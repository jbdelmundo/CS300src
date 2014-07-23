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

import com.sun.org.apache.bcel.internal.generic.LSTORE;

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
 * Attempted Optimizations: Distance Cache - Store distance between 2 points :
 * Minimal improvement, Huge space requirement n^2 Parallel Search - Parallel
 * linear search : Minimal improvement: Scales with the dataset Precomputed core
 * dist when finding neighbor : IMPLEMENTED Parallel K-means Local Sensitive
 * Hashing : IMPLEMENTED
 * 
 * 1) DataSetReader - > Reads CSV file (Data file and Attributes) 2)
 * DataNormalization - > Accepts DataSet and Normalizes it 3) OpticsAlgo - >
 * Accepts Normalized Data and process it 4) DatapacketWritter - > Writes
 * Reachability Value and Attributes
 * 
 * @author Test
 * 
 */
public class IDS {

	long timeParallel; // 1540 - 1540316
	long timeSolo; // 1606 milis

	/* ==============KLSH static Variables================= */
	public static DistanceCache distCache;
	public static boolean useDistCache = false;
	public static boolean useKLSH = true;

	public static KMeansLSHResult[] kmeansResults;
	public static ParallelKMeans pkm;
	public static int probecount = 15;

	public static int DataSize = 0;
	public DataSet knowledge;

	public static int run = 1;
	public static int iteration = 1;

	public DataSet combineDataSets(String DataDirectory, int items[],
			boolean isTestItems) {

		String FileNamePrefix = "ids" + DataSize + "_";
		DataSet combination = new DataSet();
		for (int i = 0; i < items.length; i++) {
			int item = items[i];

			String itemFilename = FileNamePrefix + item + ".data";
			String inputPath = DataDirectory + File.separatorChar
					+ itemFilename;

			DataSet itemData;
			if (!isTestItems) {
				itemData = DataSetReader.readTrainingSet(inputPath);
			} else {
				itemData = DataSetReader.readTestSet(inputPath);
			}

			combination = DataIntegration.combine(combination, itemData);
			System.gc();
		}
		return combination;
	}

	public void initializeKnowledge(int trainItems[]) {
		String DataDirectory = "SequentialPieces_" + DataSize;
		String FileNamePrefix = "ids" + DataSize + "_";
		knowledge = combineDataSets(DataDirectory, trainItems, false);
	}

	public void addConfidentDataToKnowledge(DataSet confidentData) {
		knowledge = DataIntegration.combine(knowledge, confidentData);
	}

	public ClusterPerformance IDS_run(int testItems[], double epsilon,
			int minPts) {
		String DataDirectory = "SequentialPieces_" + DataSize;
		String FileNamePrefix = "ids" + DataSize + "_";

		long startTime = System.currentTimeMillis();

		String OutputFilePath = DataDirectory + File.separatorChar
				+ "test.optics";

		DataSet testData = combineDataSets(DataDirectory, testItems, true);
		DataSet experimentData = DataIntegration.combine(knowledge, testData);
		DataSet normalizedData = DataNormalization
				.normalizeIntegerScaling(experimentData);
		OPTICSAlgorithm opticsalgo = new OPTICSAlgorithm();
		DataPacketWriter opticsOutputWriter = new DataPacketWriter(
				OutputFilePath);

		System.gc(); // free memory for old dataset
		System.out.println("Test Data Points: " + testData.size());
		System.out.println("Train Data Points: " + knowledge.size());

		if (useKLSH) {
			int threadcount = 3;
			int indexcount = 1;
			double samplePercent = 1.0;
			int k = 100;
			int itr = 5;
			int probecount = 15;

			pkm = new ParallelKMeans(threadcount);
			System.out.println("Sampling...");
			DataPacket samples[] = pkm.sampleDatabase(normalizedData,
					samplePercent);

			kmeansResults = new KMeansLSHResult[indexcount];
			for (int i = 0; i < indexcount; i++) {
				System.out.println("Clustering K-Means...");
				long startCluster = System.currentTimeMillis();
				DataPacket centroids[] = pkm.runKMeansClusteringPhase(samples,
						k, itr);
				long endCluster = System.currentTimeMillis();

				System.out.println("Indexing...");
				long startIndex = System.currentTimeMillis();
				DataSet indices[] = pkm.index(normalizedData, centroids,
						samples);
				long endIndex = System.currentTimeMillis();

				System.out.println("ClusterTime:" + (endCluster - startCluster)
						/ 1000.0);
				System.out.println("IndexTime:" + (endIndex - startIndex)
						/ 1000.0);
				kmeansResults[i] = new KMeansLSHResult(centroids, indices,
						probecount);
			}

		}// end if KLSH

		System.out.println("Starting OPTICS clustering...");
		opticsalgo.OPTICS(normalizedData, epsilon, minPts, opticsOutputWriter);

		/*********************** EVALUATE *************************************/

		OpticsEvaluation2 opticsEval = new OpticsEvaluation2();
		ClusterPerformance result = opticsEval.evaluate(DataDirectory,
				"test.optics");

		DataSet confidentData = ClusterLabeling.ConfidentPoints;
		addConfidentDataToKnowledge(confidentData);

		System.out.println("Train DATA: " + knowledge.size());
		System.out.println("Test DATA:  " + testData.size());

		try {
			PrintWriter pw = new PrintWriter(new File("Iteration- " + iteration
					+ "-Retrain# " + run + "-train-" + knowledge.size()
					+ "-test-" + testData.size() + ".txt"));
			result.printToFile(pw);
			pw.close();
			run++;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("OPTICS Done " + totalTime + " ms");
		result.showstats();

		return result;

		// Start Time: 1400037244192
		// End Time: 1400045681069
		// Cache Runtime: 508483
		// OPTICS Runtime: 7927910
		// Total Runtime: 8436877
		// Time with parallel : 8436877-cached
		// Time with parallel : 3050173 - linear
		// Time Difference : 1933983
		// 1760539 - Initializes Heap Capacity
		// 1135444 computes core dist with neighborsearch, adds only unprocessed
		// neighbors, adds precomputed distNeighbor: 10 mins better
		// 1137281 - 7000 to 15k epsilon
		// 1142693 minpts from 50 to 200
		// 483291 KLSH

		// 2% data: 1$ train, 1%test

	}

	public static void SingleRun() {
		System.out.println("SINGLE RUN ONLY");
		
		IDS ids = new IDS();

		double epsilon = Double.MAX_VALUE; // 7000 originally
		int minPts = 50;// 20;//6;
		IDS.DataSize = 10000;
		

		int testitems[] = { 8778};// 8779, 8780, 8781, 8782};
		int trainitems[] = { 5494  };

		ids.initializeKnowledge(trainitems);
		ClusterPerformance run_result = ids.IDS_run(testitems,	epsilon, minPts); // retrain j times

		
		System.out.println("END OF SINGLE RUN");
	}

	public static void main(String[] args) throws FileNotFoundException {

		SingleRun();
		 System.exit(0);

		IDS ids = new IDS();

		double epsilon = Double.MAX_VALUE; // 7000 originally
		int minPts = 50;// 20;//6;
		IDS.DataSize = 10000;
		int limit = 10000;

		int maxIterations = 2;

		int initTrainSize = 1;

		int trainsize = initTrainSize;

		int testSize = 5; // knowledge to be absorbed per retraining
		int retrainLimit = 10; // number of retraining

		ArrayList<ClusterPerformance> averagePerformance[] = new ArrayList[maxIterations];

		PrintWriter combinedpw = new PrintWriter(new File(
				"Performance_combined.csv"));
		PrintWriter overallpw = new PrintWriter(new File(
				"Performance_overall.csv"));

		for (int i = 0; i < maxIterations; i++) { // perform multiple benchmarks
			iteration = i + 1;
			run = 1;

			ArrayList<ClusterPerformance> performanceList = new ArrayList<>();
			PrintWriter pw = new PrintWriter(new File(
					"Performance- Iteration-withThreshold_small" + i + " of"
							+ maxIterations + ".csv"));

			int testitems[][] = new int[retrainLimit][testSize];
			int trainitems[] = new int[trainsize];
			generateSequentialDataIndices(trainitems, testitems, limit);

			System.out.println("Train DATA: " + trainitems.length
					+ Arrays.toString(trainitems));

			ids.initializeKnowledge(trainitems);

			for (int j = 0; j < testitems.length; j++) {
				System.out.println("Test DATA:  " + testitems.length
						+ Arrays.toString(testitems[j]));
				System.out
						.println("======================= ITERATION #" + i
								+ " RUN " + run
								+ " ==================================");
				ClusterPerformance run_result = ids.IDS_run(testitems[j],
						epsilon, minPts); // retrain j times
				performanceList.add(run_result);
			}

			ClusterPerformance.trackImprovement(performanceList, pw);
			pw.close();
			ClusterPerformance.trackImprovement(performanceList, combinedpw);
			averagePerformance[i] = performanceList;

		}

		ClusterPerformance.trackAverageImprovement(averagePerformance,
				overallpw);
		overallpw.close();
		combinedpw.close();

	}

	public static void benchmarkTrainVSTest() throws FileNotFoundException {
		IDS ids = new IDS();
		//
		double epsilon = Double.MAX_VALUE; // 7000 originally
		int minPts = 50;// 20;//6;
		IDS.DataSize = 10000;

		int testsize = 10;

		int trainSizeStart = 50;
		int trainSizeEnd = 101;
		int trainSizeIncrement = 10;

		int limit = 10000;

		for (int trainsize = trainSizeStart; trainsize < trainSizeEnd; trainsize += trainSizeIncrement) { // loop
																											// for
																											// each
																											// trainsize
			int iterations = 10;

			ArrayList<ClusterPerformance> performanceList = new ArrayList<>();
			PrintWriter pw = new PrintWriter(new File("Performance  "
					+ trainsize + "-" + iterations + "X.txt"));

			for (int i = 0; i < iterations; i++) { // perform multiple
													// benchmarks

				int testitems[] = new int[testsize];
				int trainitems[] = new int[trainsize];
				generateDataIndices(trainitems, testitems, limit);

				System.out.println("RUN #" + i);
				System.out.println("Train DATA: " + trainitems.length
						+ Arrays.toString(trainitems));
				System.out.println("Test DATA:  " + testitems.length
						+ Arrays.toString(testitems));

				ids.initializeKnowledge(trainitems);
				ClusterPerformance run_result = ids.IDS_run(testitems, epsilon,
						minPts);
				performanceList.add(run_result);
			}

			ClusterPerformance.computeAverageStats(performanceList, pw);
			pw.close();
		}

	}

	// pass by reference yo!!
	public static void generateDataIndices(int train[], int test[], int limit) {

		int choices[] = new int[limit];
		for (int i = 0; i < choices.length; i++) {
			choices[i] = i + 1;
		}

		// shuffle
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < choices.length; i++) {
			int ri = r.nextInt(choices.length);

			// swao choices[ri] and choices[i]
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

	}

	// pass by reference yo!!
	/**
	 * Uses 2D array for test Data
	 * 
	 * @param train
	 * @param test
	 * @param limit
	 */
	public static void generateDataIndices2(int train[], int test[][], int limit) {

		int choices[] = new int[limit];
		for (int i = 0; i < choices.length; i++) {
			choices[i] = i + 1;
		}

		// shuffle
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < choices.length; i++) {
			int ri = r.nextInt(choices.length);

			// swao choices[ri] and choices[i]
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
			for (int j = 0; j < test[i].length; j++) {
				test[i][j] = choices[ind];
				ind++;
			}

		}

	}

	// pass by reference yo
	public static void generateSequentialDataIndices(int train[], int test[][],
			int limit) {

		Random r = new Random(System.currentTimeMillis());
		int trainsize = train.length;
		int testCases = test.length;
		int testsize = test[0].length;

		int trainoffset = r.nextInt(limit - (testCases * testsize)-trainsize);

		for (int i = 0; i < train.length; i++) {
			train[i] = trainoffset + i; // fillout train indices
		}

		int lastindex = trainoffset + trainsize - 1;
		System.out.println(trainoffset+"las"+lastindex + "   " + limit);
		int testOffsets[] = new int[testCases];
		
		for (int i = 0; i < testOffsets.length; i++) {
			int range = limit - lastindex - (testsize*(testCases-i+1));
			System.out.println("ranbge " + range + "   " + lastindex);
			testOffsets[i] = lastindex + r.nextInt(range);

			lastindex = testOffsets[i] + testsize - 1;
		}

		for (int i = 0; i < testCases; i++) {
			for (int j = 0; j < testsize; j++) {
				test[i][j] = testOffsets[i] + j;
			}
		}
		
		System.out.println("TrainData " + Arrays.toString(train));
		for (int i = 0; i < test.length; i++) {
			System.out.println("Test " +i+ Arrays.toString(test[i]));
		}

	}

}
