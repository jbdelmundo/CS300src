package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import optimizations.DistanceCache;
import optimizations.KMeansLSHResult;
import optimizations.ParallelKMeans;

import clustering.NearestNeighborCompute;
import clustering.OPTICSAlgorithm;
import clustering.OpticsEvaluation2;
import preprocessing.DataIntegration;
import preprocessing.DataNormalization;
import data.ClusterPerformance;
import data.DataPacket;
import data.DataSet;
import fileIO.DataPacketWriter;
import fileIO.DataSetReader;

/**
 * Attempted Optimizations:
 *  Distance Cache - Store distance between 2 points : Minimal improvement, Huge space requirement n^2
 *  Parallel Search - Parallel linear search : Minimal improvement: Scales with the dataset
 *  Precomputed core dist when finding neighbor : IMPLEMENTED
 *  Parallel K-means Local Sensitive Hashing : IMPLEMENTED 
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
	
	
	/*  ==============KLSH static Variables=================*/
	public static DistanceCache distCache;
	public static boolean useDistCache = false;
	public static boolean useKLSH = true;
	
	public static KMeansLSHResult[] kmeansResults;
	public static ParallelKMeans pkm;
	public static int probecount = 15;
	
	public static int DataSize = 0;
	
	
	
	
	public DataSet combineDataSets(String DataDirectory, int items[], boolean isTestItems){
		
		String FileNamePrefix = "ids"+DataSize+"_";
		DataSet combination =  new DataSet();
		for (int i = 0; i < items.length; i++) {
			int item = items[i];
			
			String itemFilename = FileNamePrefix+item+".data";
			String inputPath = DataDirectory + File.separatorChar + itemFilename;
			
			
			DataSet itemData;
			if(!isTestItems){
				itemData = DataSetReader.readTrainingSet(inputPath);
			}else{
				itemData = DataSetReader.readTestSet(inputPath);
			}
			
			
			combination = DataIntegration.combine(combination, itemData);
			System.gc();
		}
		return combination;
	}
	
	
	public ClusterPerformance IDS_run(boolean useParallelSearch,int trainItems[], int testItems[], double epsilon, int minPts) {
		String DataDirectory = "RandomPieces_"+DataSize;
		String FileNamePrefix = "ids"+DataSize+"_";
		
		long startTime = System.currentTimeMillis();
		
		
		DataSet knowledge = combineDataSets(DataDirectory, trainItems, false);
		
		
		
		//loop for multiple test items
//		for (int testItem = 0; testItem < testItems.length; testItem++) {
			
			
			
			String OutputFilePath = DataDirectory + File.separatorChar +  "test.optics";
			
			
			
			DataSet testData = combineDataSets(DataDirectory, testItems, true); 			
			DataSet experimentData = DataIntegration.combine(knowledge, testData);				
			DataSet normalizedData =  DataNormalization.normalizeIntegerScaling(experimentData);		
			OPTICSAlgorithm opticsalgo = new OPTICSAlgorithm();
			DataPacketWriter opticsOutputWriter = new DataPacketWriter(OutputFilePath);
			
			System.gc();	// free memory for old dataset
			System.out.println("Test Data Points: " + testData.size());
			System.out.println("Train Data Points: " + knowledge.size());
			

			
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
					System.out.println("Clustering K-Means...");
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
						
			
			
			System.out.println("Starting OPTICS clustering...");
			opticsalgo.OPTICS(normalizedData, epsilon, minPts, opticsOutputWriter);
			
			
			/***********************EVALUATE*************************************/
			
			OpticsEvaluation2 opticsEval = new OpticsEvaluation2();
			ClusterPerformance result = opticsEval.evaluate(DataDirectory,"test.optics");
			
			//Measure Entropy
			
			
			//merge confident data to knowledge
			
			
//			System.out.println("TrainPoins:" +opticsOutputWriter.train);
//			System.out.println("TestPoints:" +opticsOutputWriter.test);
//			System.out.println("Saved:" + OutputFilePath);
			
//		}//END LOOP
		
		
		/**
		 * Future Improvements: JOCL
		 */
		
		System.out.println("Train DATA: " + trainItems.length + Arrays.toString(trainItems)  );
		System.out.println("Test DATA:  " + testItems.length + Arrays.toString(testItems)  );
		
			
		
		long endTime   = System.currentTimeMillis();		
		long totalTime = endTime - startTime;
		System.out.println("OPTICS Done " + totalTime + " ms");
		result.showstats();
		
		return result;
	
		
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
	
	public void evaluate(){
		
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		IDS ids = new IDS();
//		
		double epsilon = Double.MAX_VALUE; //7000 originally
		int minPts = 50;//20;//6;
		IDS.DataSize = 10000;
		
		
		
		int iterations = 10;		
		int testsize  = 100;
		
		int trainSizeStart = 10;
		int trainSizeEnd = 150;
		int trainSizeIncrement = 10;
		
		int limit = 10000;
		
		
		
		for (int trainsize = trainSizeStart; trainsize < trainSizeEnd; trainsize +=trainSizeIncrement) {		//loop for each trainsize
			
			ArrayList<ClusterPerformance> performanceList = new ArrayList<>();
			PrintWriter pw = new PrintWriter(new File("Performance  " +  trainsize + ".txt"));
			
			for (int i = 0; i < iterations; i++) {	//perform multiple benchmarks
				
				
				int testitems[] = new int[testsize];
				int trainitems[] = new int[trainsize];
				generateDataIndices(trainitems, testitems, limit);
				
				System.out.println("RUN #" + i);
				System.out.println("Train DATA: " + trainitems.length + Arrays.toString(trainitems)  );
				System.out.println("Test DATA:  " + testitems.length + Arrays.toString(testitems)  );
				
				ClusterPerformance run_result = ids.IDS_run(true,trainitems,testitems, epsilon,minPts);
				performanceList.add(run_result);
			}
			
			
			ClusterPerformance.computeAverageStats(performanceList,pw);
			pw.close();
		}
		
		
		
		
		
		
		
	}
	
	
	//pass by reference yo!!
	public static void generateDataIndices( int train[], int test[],int limit){
		
		int choices[] = new int[limit];
		for (int i = 0; i < choices.length; i++) {
			choices[i] = i+1;
		}
		
		
		//shuffle
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < choices.length; i++) {
			int ri = r.nextInt(choices.length);
			
			
			//swao choices[ri] and choices[i]
			int temp = choices[ri];
			choices[ri] = choices[i];
			choices[i] = temp;
		}
		
		int ind = 0;
		for (int i = 0; i < train.length; i++) {
			train[i] = choices[ind];
			ind++;
		}
		
		for (int i = 0; i < test.length; i++) {
			test[i] = choices[ind];
			ind++;
		}
		
		
		
//		return data;
	}

}
