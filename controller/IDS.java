package controller;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import optimizations.DistanceCache;
import optimizations.KMeansLSHResult;
import optimizations.ParallelKMeans;

import clustering.NearestNeighborCompute;
import clustering.OPTICSAlgorithm;
import preprocessing.DataIntegration;
import preprocessing.DataNormalization;
import data.DataPacket;
import data.DataSet;
import fileIO.DataPacketWriter;
import fileIO.DataSetReader;

/**
 * Dummy class to run the 
 * 
 * 1) DataSetReader 			- > Reads CSV file (Data file and Attributes)
 * 2) DataNormalization 		- > Accepts DataSet and Normalizes it
 * 3) OpticsAlgo				- > Accepts Normalized Data and process it
 * 4) DatapacketWritter 		- > Writes Reachability Value and Attributes
 * 
 * @author Test
 *
 */
public class IDS {
	
	long timeParallel; //1540 - 1540316
	long timeSolo; //1606 milis
	public static DistanceCache distCache;
	public static boolean useDistCache = false;
	public static boolean useKLSH = true;
	
	public static KMeansLSHResult[] kmeansResults;
	public static ParallelKMeans pkm;
	public static int probecount = 15;
	
	public DataSet initTrain(String DataDirectory, int trainItems[]){
		DataSet knowledge =  new DataSet();
		for (int i = 0; i < trainItems.length; i++) {
			int trainItem = trainItems[i];
			
			String trainFilename = "ids10000_"+trainItem+".data";
			String InputTrainingFilePath = DataDirectory + File.separatorChar + trainFilename;
			DataSet trainData = DataSetReader.readTrainingSet(InputTrainingFilePath);
			
			knowledge = DataIntegration.combine(knowledge, trainData);
		}
		return knowledge;
	}
	
	public void IDS_run(boolean useParallelSearch,int trainItems[], int testItems[], double epsilon, int minPts) {

		long startTime = System.currentTimeMillis();
		
		String DataDirectory = "RandomPieces_10000";
		
		//build trainset
		
		
		
		
		
		
		
		DataSet knowledge = initTrain(DataDirectory, trainItems);
		
		System.out.println(Arrays.toString(trainItems));
		 
		
		
		
		//loop for multiple test items
		for (int testItem = 0; testItem < testItems.length; testItem++) {
			
			
			//load testDataFile
			String testFilename = "ids10000_"+testItems[testItem]+".data";
//			String OpticsFilename = "ids10000_"  + "-" + testItems[testItem] + ".optics";
			String OpticsFilename = "test.optics";
			String InputTestFilePath = DataDirectory + File.separatorChar + testFilename;
			
			
			String OutputFilePath = DataDirectory + File.separatorChar +  OpticsFilename;
			
			
			
			DataSet testData = DataSetReader.readTestSet(InputTestFilePath);
			DataSet experimentData = DataIntegration.combine(knowledge, testData);	
			
			
			DataSet normalizedData =  DataNormalization.normalizeIntegerScaling(experimentData);		
			OPTICSAlgorithm opticsalgo = new OPTICSAlgorithm();
			DataPacketWriter opticsOutputWriter = new DataPacketWriter(OutputFilePath);
			
			long endRead  = System.currentTimeMillis();
			
			if(useKLSH){
				int threadcount = 3;
				int indexcount = 1;
				double samplePercent = 1.0;
				int k = 100;
				int itr = 5;
				int probecount = 15;
				
				pkm = new ParallelKMeans(threadcount);
				System.out.println("Sampling...");
				DataPacket samples[] = pkm.sampleDatabase(normalizedData, samplePercent);
				
				kmeansResults =  new KMeansLSHResult[indexcount];
				for (int i = 0; i < indexcount; i++) {
					System.out.println("Clustering...");
					long startCluster = System.currentTimeMillis();
					DataPacket centroids[] = pkm.runKMeansClusteringPhase(samples, k, itr);
					long endCluster = System.currentTimeMillis();
					
					
					System.out.println("Indexing...");
					long startIndex = System.currentTimeMillis();		
					DataSet indices[] = pkm.index(normalizedData, centroids, samples);		
					long endIndex = System.currentTimeMillis();
					
					
					System.out.println("ClusterTime:" + (endCluster-startCluster)/1000.0);
					System.out.println("IndexTime:" + (endIndex-startIndex)/1000.0);
					kmeansResults[i] = new KMeansLSHResult(centroids, indices, probecount);
				}
				
				
			}// end if
						
			
			
			opticsalgo.OPTICS(normalizedData, epsilon, minPts, opticsOutputWriter);
			
			
			//read OpticsFile
			
			//Calculate predictions
			
			//Measure Entropy
			
			
			//merge confident data to knowledge
			
			
		}//loop for the next testItem
		
		
		/**
		 * Future Improvements: JOCL
		 */
		
		
		
		
		System.out.println("OPTICS Done");
		long endTime   = System.currentTimeMillis();
		
		long totalTime = endTime - startTime;
		System.out.println("Start Time: " + startTime);
		System.out.println("Total Runtime: " + totalTime);
		
		if(useParallelSearch){
			timeParallel = totalTime;
		}else{
			timeSolo = totalTime;
		}
		
//		Start Time: 1400037244192
//		End Time: 1400045681069
//		Cache Runtime: 508483
//		OPTICS Runtime: 7927910
//		Total Runtime: 8436877
//		Time with parallel : 8436877-cached
//		Time with parallel : 3050173 - linear
//		Time Difference : 1933983
		//1760539 - Initializes Heap Capacity
		//1135444 computes core dist with neighborsearch, adds only unprocessed neighbors, adds precomputed distNeighbor: 10 mins better
		//1137281 - 7000 to 15k epsilon
		//1142693 minpts from 50 to 200
		//483291 KLSH
		
		//2% data: 1$ train, 1%test
	}
	
	public static void main(String[] args) {
		IDS ids = new IDS();
//		
		double epsilon = 5000; //7000 originally
		int minPts = 50;//20;//6;
		
		int testitems[] = {100,123,453,125,637,23,543,23};
		int trainitems[] = {10,34,645};//,432,87};
		ids.IDS_run(true,trainitems,testitems, epsilon,minPts);
		
		
		System.out.println("Time with parallel : " + ids.timeParallel);
		System.out.println("Time solor : " + ids.timeSolo);
		System.out.println("Time Difference : " + Math.abs(ids.timeParallel- ids.timeSolo));
		
		long a = (long) 1457016.0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date(a);
		System.out.println(dateFormat.format(date));
	}

}
