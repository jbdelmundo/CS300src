package controller;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import optimizations.DistanceCache;

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
	
	public void IDS_run(boolean useParallelSearch,int trainItem, int testItem) {

		long startTime = System.currentTimeMillis();
		
		String DataDirectory = "RandomPieces_200";
		String trainFilename = "ids200_"+trainItem+".data";
		String testFilename = "ids200_"+testItem+".data";
		String OpticsFilename = "ids200_" + trainItem + "-" + testItem + ".optics";
		
		
		String InputTrainingFilePath = DataDirectory + File.separatorChar + trainFilename;
		String InputTestFilePath = DataDirectory + File.separatorChar + testFilename;
		String OutputFilePath = DataDirectory + File.separatorChar +  OpticsFilename;
		
		double epsilon = 15000; //7000 originally
		int minPts = 200;//20;//6;
		
		
		
		
		
		
		DataSet trainData = DataSetReader.readTrainingSet(InputTrainingFilePath);
		DataSet testData = DataSetReader.readTestSet(InputTestFilePath);
		
		DataSet experimentData = DataIntegration.combine(trainData, testData);
		
		DataSet normalizedData =  DataNormalization.normalizeIntegerScaling(experimentData);		
		
		OPTICSAlgorithm opticsalgo = new OPTICSAlgorithm();
		DataPacketWriter opticsOutputWriter = new DataPacketWriter(OutputFilePath);
		
		long endRead  = System.currentTimeMillis();
		
		if(useDistCache){
			trainData = null;
			testData = null;
			experimentData = null;
			distCache = new DistanceCache(normalizedData);
		}
			
		
		long endcache  = System.currentTimeMillis();
		/**
		 * use jocl.org
		 */
		NearestNeighborCompute.useParallelSearch = useParallelSearch;
		
		
		opticsalgo.OPTICS(normalizedData, epsilon, minPts, opticsOutputWriter);
		
		
		
		System.out.println("OPTICS Done");
		long endTime   = System.currentTimeMillis();
		
		long totalTime = endTime - startTime;
		System.out.println("Start Time: " + startTime);
		System.out.println("End Time: " + endTime);
		System.out.println("Cache Runtime: " +( endcache-endRead));
		System.out.println("OPTICS Runtime: " +( endTime- endcache));
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
		//1142693minpts from 50 to 200
	}
	
	public static void main(String[] args) {
		IDS ids = new IDS();
//		ids.IDS_run(false);
		
		
		ids.IDS_run(true,1,2);
		
//		for (int i = 1; i < 10; i++) {
//			ids.IDS_run(true,i,i+1);
//		}
		
		System.out.println("Time with parallel : " + ids.timeParallel);
		System.out.println("Time solor : " + ids.timeSolo);
		System.out.println("Time Difference : " + Math.abs(ids.timeParallel- ids.timeSolo));
		
		long a = (long) 1457016.0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date(a);
		System.out.println(dateFormat.format(date));
	}

}
