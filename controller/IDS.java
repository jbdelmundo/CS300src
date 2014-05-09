package controller;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	
	public void IDS_run(boolean useParallelSearch,int trainItem, int testItem) {
		long startTime = System.currentTimeMillis();
		
		String DataDirectory = "RandomPieces_200";
		String trainFilename = "ids200_"+trainItem+".data";
		String testFilename = "ids200_"+testItem+".data";
		String OpticsFilename = "ids200_" + trainItem + "-" + testItem + ".optics";
		
		
		String InputTrainingFilePath = DataDirectory + File.separatorChar + trainFilename;
		String InputTestFilePath = DataDirectory + File.separatorChar + testFilename;
		String OutputFilePath = DataDirectory + File.separatorChar +  OpticsFilename;
		
		double epsilon = 7000; //7000 originally
		int minPts = 6;
		
		
		
		
		
		
		DataSet trainData = DataSetReader.readTrainingSet(InputTrainingFilePath);
		DataSet testData = DataSetReader.readTestSet(InputTestFilePath);
		
		DataSet experimentData = DataIntegration.combine(trainData, testData);
		
		DataSet normalizedData =  DataNormalization.normalizeIntegerScaling(experimentData);		
		
		OPTICSAlgorithm opticsalgo = new OPTICSAlgorithm();
		DataPacketWriter opticsOutputWriter = new DataPacketWriter(OutputFilePath);
		
		
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
		System.out.println("Total Runtime: " + totalTime);
		
		if(useParallelSearch){
			timeParallel = totalTime;
		}else{
			timeSolo = totalTime;
		}
		
	}
	
	public static void main(String[] args) {
		IDS ids = new IDS();
//		ids.IDS_run(false);
		
		
		ids.IDS_run(true,34,125);
		
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
