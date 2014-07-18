package optimizations;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;


import preprocessing.DataNormalization;

import clustering.NearestNeighborCompute;

import data.DataPacket;
import data.DataSet;
import fileIO.DataSetReader;

public class ParallelKMeans {
	
	int threadCount = 1;
	ParallelKMeansThread threads[]; 
	
	public ParallelKMeans(int threadCount ) {
		this.threadCount = threadCount;
	}
	
	private void initThreads(){
		threads = new ParallelKMeansThread[threadCount];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ParallelKMeansThread();
		}
		ParallelKMeansThread.monitor = this;
	}
	
 	private void setThreadBounds(int datasize, int threadcount){
		int size = datasize/threadcount;
		
		int bounds[] = new int[threadcount];
		for (int i = 0; i < bounds.length; i++) {
			bounds[i] = size + (size*i);
		}
		bounds[bounds.length-1] = datasize;

		for (int i = 0; i < bounds.length; i++) {
			threads[i].startIndex = (i==0)? 0 : bounds[i-1];
			threads[i].endIndex = bounds[i];
		}
	}
	
	private void joinThreads(){
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startThreads(){
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
	}	
	
	public static void main(String[] args) {
		System.out.println("KMeans-Parallel - UNITEST");
		
		int threadcount = 3;
		ParallelKMeans pkm = new ParallelKMeans(threadcount);
		
		int trainItem = 3;
		
//		String InputTrainingFilePath = "Data" + File.separatorChar + "ids.data";	

		String InputTrainingFilePath = "RandomPieces_10" + File.separatorChar + "ids10_"+trainItem+".data";		;


		DataSet trainData = DataSetReader.readTrainingSet(InputTrainingFilePath);		
		DataSet normalizedData =  DataNormalization.normalizeIntegerScaling(trainData);		
		System.out.println("DataSize:" + normalizedData.size());
		
		
		double samplePercent = 1.00;
		int k = 100;
		int itr =5;
		double epsilon = 1000;
		int probecount = 3;
		int indexcount = 3;
		//Percentage overhead 0.5256419861788397 - k= 200
		
		//probecount :AR 96
		
		
		System.out.println("Sampling...");
		DataPacket samples[] = pkm.sampleDatabase(normalizedData, samplePercent);
		
		KMeansLSHResult[] kmeansResults =  new KMeansLSHResult[indexcount];
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
		
		
		
		long indexedSearchTime = 0, linearSearchTime = 0, recallTime = 0;
		double TotalRecall = 0;
		for (int i = 0; i < normalizedData.size(); i++) {
			DataPacket source = normalizedData.elementAt(i);
			
			long startIndexSearch = System.currentTimeMillis();
			DataSet indexedNeighbors = pkm.query(source, normalizedData, epsilon, kmeansResults, probecount);
			long endIndexSearch = System.currentTimeMillis();
			
			indexedSearchTime+= (endIndexSearch - startIndexSearch);
			
			long startLinearSearch = System.currentTimeMillis();
			DataSet linearNeighbors = NearestNeighborCompute.findNeighbors(normalizedData, source,  epsilon);
			long endLinearSearch = System.currentTimeMillis();
			
			linearSearchTime += (endLinearSearch-startLinearSearch);
			
			long startRecall = System.currentTimeMillis();
			double recall = computeRecall(linearNeighbors, indexedNeighbors);
			long endRecall = System.currentTimeMillis();
			
			recallTime += recall;
			
			TotalRecall += recall;
			System.out.println(i+"\tR:"+recall+"\tL:" + linearNeighbors.size()+ "\tI:"+indexedNeighbors.size() + "\tIT:" +(endIndexSearch - startIndexSearch) +  
					"\tLT"+(endLinearSearch-startLinearSearch) + "\n\t\tAR:" + (TotalRecall/(i+1.0)) + "\tITT:"+indexedSearchTime + "\tLTT:"+linearSearchTime);
			System.out.println("\tTotalSave:" + (linearSearchTime-indexedSearchTime) + "\tRT:"+ recall + "\tRTT: " +recallTime);
			
		}
		
		System.out.println("END BENCHMARK");
		System.out.println("Ave Recall: " + TotalRecall*1.0/normalizedData.size() );
		System.out.println("Index Search: " + indexedSearchTime);
		System.out.println("Linear Search: " + linearSearchTime);
		
		System.out.println("End");
		
	}
	
	public DataPacket[]  sampleDatabase(DataSet D,double percentage){
		Random r = new Random(System.currentTimeMillis());
		
		int estSize = (int) (D.size() * (percentage+0.10*percentage));
		DataSet samples = new DataSet(estSize);
		
		for (int i = 0; i < D.size(); i++) {
			if(r.nextDouble() < percentage){
				samples.add(D.elementAt(i));
			}	
		}
		DataPacket arr[] = new DataPacket[samples.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = samples.elementAt(i);
		}
		return 	arr;
	}
	
	/**
	 * Returns a array of Centroids 
	 */
	public  DataPacket[] runKMeansClusteringPhase(DataPacket[] samples , int k,int itr){
		System.out.println("Searching centroids");
		
		System.out.println("SampleSize "+ samples.length);
		
	
		Random r = new Random(System.currentTimeMillis());
		//initialize array C with kPoints Randomly Picked from samples;
		DataPacket centroids[] = initializeCentroids(samples,k,r);
		
		
		
		/*initialize array N with k Zeroes*/
		int N[] = new int[k];
		Arrays.fill(N, 0);
		
		
		
		//iterations of k means
		for (int i = 0; i < itr; i++) {
//			System.out.println("Kmeans Iteration:"+i);
			
			
			DataPacket adjustmentCentroids[] = new DataPacket[centroids.length];
			
			//for all samples, find nearest centroid
			initThreads();
			setThreadBounds(samples.length, this.threadCount);
			ParallelKMeansThread.setClusterParameters(samples, centroids,adjustmentCentroids, N);
			ParallelKMeansThread.threadTask = ParallelKMeansThread.NEAREST_CENTROID;
			startThreads();
			joinThreads();
			
//			System.out.println("End of threads");
			
			
//			for (int j = 0; j < samples.length; j++) {
//				
//				int centroidIndex = -1;
//				double centroidDistance = Double.MAX_VALUE;
//				//find the nearest centroid
//				for (int j2 = 0; j2 < centroids.length; j2++) {
////					double cdist = ParallelKMeans.kmeansdist(centroids[j2], sample[j]);
//					double cdist = ParallelKMeans.kmeansdist(centroids[j2], samples[j]);
//					if(cdist < centroidDistance){
//						centroidIndex = j2;
//						centroidDistance = cdist;
//					}
//				}
//				
//				//assign to the nearest centroid		BUT DO NOT UPDATE CENTROID YET
//				addToCentroid(adjustmentCentroids, centroidIndex, samples[j]);
//				N[centroidIndex]++;
//			}
//			
			
			//for all centroids
			for (int j = 0; j < centroids.length; j++) {
				
				
				
				//no points assigned to centroid
				if(N[j] == 0){					
					centroids[j] = new DataPacket(samples[r.nextInt(samples.length)]);	//random centroid
				}else{		
					centroids[j] = updateCentroid(centroids[j],adjustmentCentroids[j], N[j]);		
				}
				
				
//				System.out.println("Centroid " + j + " Count: " +N[j]);
				
							
				N[j] = 0;
//				centroids[j].printDataPacketInfo();
																
			}
		}//end of iterations
		
		return centroids;
		
	}
	
	private void addToCentroid(DataPacket newCentroids[], int index ,DataPacket packet){
		if(newCentroids[index] == null){
			newCentroids[index] = new DataPacket(packet);
		}else{
			DataPacket centroid = newCentroids[index];
			for (int i = 0; i < centroid.ContinuousAttr.length; i++) {
				centroid.ContinuousAttr[i] += packet.ContinuousAttr[i];
			}
		}
	}

	
	private static DataPacket[] initializeCentroids(DataPacket samples[], int k,Random r){
		DataPacket centroids[] = new DataPacket[k];
		DataPacket randomPacket;
		
		for (int i = 0; i < k; i++) {
			randomPacket = samples[r.nextInt(samples.length)];
			
			centroids[i] = new DataPacket(randomPacket);
			
			for (int j = 0; j < i; j++) {
				if(centroids[i].equals(randomPacket)){
					randomPacket = samples[r.nextInt(samples.length)];
					j=0;
				}
			}
			//copy random packet			
			centroids[i] = new DataPacket(randomPacket);		
		}
		return centroids;				
	}
	
	
	
	public static double kmeansdist(DataPacket dp1, DataPacket dp2){
		/*** Get only the distance of continuous attributes, symbolic attributes are considered unequal */
		
		double cont_dist = 0.0;
		for (int i = 0; i < dp1.ContinuousAttr.length; i++) {

			double diff = dp1.ContinuousAttr[i] - dp2.ContinuousAttr[i];
			double attr_diff = Math.pow(diff, 2);
			cont_dist += attr_diff;
		}
		
		for (int i = 0; i < dp1.SymbolicAttr.length; i++) {
			cont_dist += Math.pow(NearestNeighborCompute.PREFERRED_RANGE, 2);
		}

		return cont_dist;
	}
	
	public static double kmeansIndexdist(DataPacket dp1, DataPacket dp2){
		return NearestNeighborCompute.findDistance(dp1, dp2);
	}
	
	
	
	private static DataPacket updateCentroid(DataPacket centroid,DataPacket adjustment,int count){
		for (int i = 0; i < centroid.ContinuousAttr.length; i++) {
			centroid.ContinuousAttr[i] =( centroid.ContinuousAttr[i] + adjustment.ContinuousAttr[i])/count;
		}
		return centroid;
	}
	
	/**
	 * Improvement: Multiple binning
	 * @return array of datasets. each dataset contains the set of points assigned to the ith centroid
	 */
	public DataSet[] index(DataSet D, DataPacket[] centroids, DataPacket samples[]){
			
				
		//initialize Index/Buckets
		DataSet index[] = new DataSet[centroids.length];
		int expectedBucketSize = (int) (D.size()*1.2/centroids.length);
		
		for (int i = 0; i < index.length; i++) {
			index[i] = new DataSet(expectedBucketSize);					//initialize buckets
		}
		
				
		
	
//		System.out.println("\n");
		
		/**********************************************************/
		
		
		
		initThreads();
		setThreadBounds(D.size(), this.threadCount);
		ParallelKMeansThread.setIndexParameters(D, centroids, index);
		ParallelKMeansThread.threadTask = ParallelKMeansThread.INDEX;		
		startThreads();
		joinThreads();
		
//		System.out.println("End of threads");
		boolean showstats = false;
		if(showstats){
			int overhead = 0;
			int saved = 0;
			for (int i = 0; i < index.length; i++) {
				int ideal = D.size()/index.length;
				int indexsize = index[i].size();
				String remark = "     Bad";
				if(ideal >= indexsize){
					remark = "GOOD";
					saved += D.size()-indexsize;
				}else{
					overhead += indexsize-ideal;
				}
						
				System.out.println("Index Centroid "+i+ " member count: " + indexsize + "\t ideal:" + ideal + " " + remark );
			}
			
			System.out.println("Centroid Indices:" + index.length + " Total Overhead " + overhead + "saved " + saved);
			System.out.println("Percentage overhead " +overhead*1.0/D.size());
			System.out.println("Percentage saved " +saved*1.0/D.size());
			
			System.out.println("IMPROVEMENT: " + (saved - overhead)*1.0/D.size());
				
		}
	
		return index;
	}
	
	
	
	
	/**
	 * Improvement: Multiple Indices(k-means run),
	 * Input: Each run N
	 * Class KMeansLSHResults: 
	 * 		DataPacket[k] Centroids
	 * 		DataSet[k] index
	 */
	public  DataSet query(DataPacket source, DataSet data, double epsilon, KMeansLSHResult[] kmeansResults, int probecount){
		
		HashMap<DataPacket,Boolean> neighbors = new HashMap<DataPacket, Boolean>();		//use hashmap to remove duplicates
		
		int searchspace = 0;
		
		
		for (int i = 0; i < kmeansResults.length; i++) {
			
			//find the nearest centroids
			PriorityQueue<CentroidIndex> nearestCentroids = new PriorityQueue<CentroidIndex>(probecount+1);			
			for (int j = 0; j < kmeansResults[i].centroids.length; j++) {
				double cdist = kmeansIndexdist(source, kmeansResults[i].centroids[j]);				
				
				nearestCentroids.add(new CentroidIndex(j, cdist));
				if(nearestCentroids.size() > probecount){
					nearestCentroids.remove();
				}
			}
			
			
			while(nearestCentroids.size() != 0){
				CentroidIndex ci = nearestCentroids.remove();
				DataSet compareTo = kmeansResults[i].index[ci.centroidID];
				
//				System.out.println("\tSearchspace: i="+ i+" Centroid = "+ci.centroidID+"\t "+ compareTo.size());
				searchspace += compareTo.size();
				
				
//				initThreads();
//				setThreadBounds(compareTo.size(), this.threadCount);
//				ParallelKMeansThread.setQueryParameters(compareTo, source, epsilon, neighbors);
//				ParallelKMeansThread.threadTask = ParallelKMeansThread.INDEX;		
//				startThreads();
//				joinThreads();
				
				for (DataPacket dataPacket : compareTo) {
					if (source.equals(dataPacket))
						continue;
					double dist = kmeansIndexdist(source, dataPacket);
					// System.out.println("findNeighbors:" + dist);
					if (epsilon >= dist) {
						neighbors.put(dataPacket,true);
					}
				}
			}
			
		}
		
		
		
		
	
//		System.out.println("\tpercent:" +(searchspace*100.0/data.size()));
//		System.out.println("");
	
		//transfer keys to DataSet;
		
		Object dp[]  =  (neighbors.keySet().toArray());
		//initialize heap to track neighbors
		DataSet nearestNeighbors = new DataSet(dp.length);
		for (int i = 0; i < dp.length; i++) {
			nearestNeighbors.add((DataPacket) dp[i]);
		}
		
		
		return nearestNeighbors;
	}
	
	
	
	
	
	/**
	 * Percentage of Actual Neighbors included in the query 
	 * Computed as intersection(actual,indexed)/ actual.size
	 * @param actual
	 * @param indexed
	 * @return
	 */
	public static double computeRecall(DataSet actual, DataSet indexed){
		
		int included_count = 0;
		for (int i = 0; i < indexed.size(); i++) {
			//check if included in actual
			
			DataPacket indexedNeighbor = indexed.elementAt(i);
			if(actual.contains(indexedNeighbor)){
				included_count++;
			}
			
		}
		
//		System.out.println("Included " + included_count + " / " + actual.size() + " = " + included_count*1.0/actual.size());
		if(actual.size() == 0) return 1;
		
		return included_count*1.0/actual.size();
		
	}
	
	

}
