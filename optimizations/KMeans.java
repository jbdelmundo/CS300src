package optimizations;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

import preprocessing.DataIntegration;
import preprocessing.DataNormalization;

import clustering.NearestNeighborCompute;

import data.DataPacket;
import data.DataSet;
import fileIO.DataSetReader;

public class KMeans {
	
	
	
	/**
	 * Returns a array of Centroids 
	 */
	public static DataPacket[] runKMeansClusteringPhase(DataSet D,int samplesize, int k,int itr){
		System.out.println("Searching centroids");
		
		
		//insert samples into array samples
		Random r = new Random(System.currentTimeMillis());
		
		DataPacket samples[] = new DataPacket[samplesize];
		DataPacket randomPacket;
		for (int i = 0; i < samples.length; i++) {
			randomPacket = D.elementAt(r.nextInt(D.size()));
			
			samples[i] = randomPacket;
			
			//check if already inserted
			for (int j = 0; j < i; j++) {
				if(samples[i].equals(randomPacket)){
					randomPacket = D.elementAt(r.nextInt(D.size()));
					j=0;
				}
			}
			samples[i] = randomPacket;
			
		}
		
//		DataPacket samples[] = new DataPacket[D.size()];
//		for (int i = 0; i < samples.length; i++) {
//			samples[i] = D.elementAt(i);
//		}
		
		
		//initialize array C with kPoints Randomly Picked from samples;
		DataPacket centroids[] = initializeCentroids(samples,k,r);
		
		
		
		/*initialize array N with k Zeroes*/
		int N[] = new int[k];
		Arrays.fill(N, 0);
		
		//iterations of k means
		for (int i = 0; i < itr; i++) {
			System.out.println("Kmeans Iteration:"+i);
			for (int j = 0; j < samples.length; j++) {
				
				int centroidIndex = -1;
				double centroidDistance = Double.MAX_VALUE;
				//find the nearest centroid
				for (int j2 = 0; j2 < k; j2++) {
					double cdist = kmeansdist(centroids[j2], samples[j]);
					if(cdist < centroidDistance){
						centroidIndex = j2;
						centroidDistance = cdist;
					}
				}
				
				//assign to the nearest centroid
				centroids[centroidIndex] = addToCentroid(centroids[centroidIndex],samples[j]);
				N[centroidIndex]++;
			}
			
			//for all centroids
			for (int j = 0; j < centroids.length; j++) {
				
				
				
				//no points assigned to centroid
				while(N[j] == 0){					
					centroids[j] = new DataPacket(samples[r.nextInt(samples.length)]);	//random centroid
					
					int centroidIndex = -1;
					double centroidDistance = Double.MAX_VALUE;
					//find the nearest centroid
					for (int j2 = 0; j2 < k; j2++) {
						double cdist = kmeansdist(centroids[j2], samples[j]);
						if(cdist < centroidDistance){
							centroidIndex = j2;
							centroidDistance = cdist;
						}
					}
					
					//assign to the nearest centroid
					centroids[centroidIndex] = addToCentroid(centroids[centroidIndex],samples[j]);
					N[centroidIndex]++;
					
					
				}
				System.out.println("Centroid " + j + " Count: " +N[j]);
				
				centroids[j] = updateCentroid(centroids[j], N[j]);					
				N[j] = 0;
//				centroids[j].printDataPacketInfo();
																
			}
		}//end of iterations
		
		return centroids;
		
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
	
	private static DataPacket addToCentroid(DataPacket centroid, DataPacket packet){
		for (int i = 0; i < centroid.ContinuousAttr.length; i++) {
			centroid.ContinuousAttr[i] += packet.ContinuousAttr[i];
		}
		return centroid;
	}
	
	private static DataPacket updateCentroid(DataPacket centroid,int count){
		for (int i = 0; i < centroid.ContinuousAttr.length; i++) {
			centroid.ContinuousAttr[i] /= count;
		}
		return centroid;
	}
	
	/**
	 * Improvement: Multiple binning
	 * @return array of datasets. each dataset contains the set of points assigned to the ith centroid
	 */
	public static DataSet[] index(DataSet D, DataPacket[] Centroids){
		System.out.println("Indexing...");
		//initialize Index
		DataSet index[] = new DataSet[Centroids.length];
		for (int i = 0; i < index.length; i++) {
			index[i] = new DataSet();
		}
		
		
		
		for (int i = 0; i < D.size(); i++) {
			DataPacket p = D.elementAt(i);
			
			
			int centroidIndex = -1;
			double centroidDistance = Double.MAX_VALUE;
			//find the nearest centroid
			for (int j2 = 0; j2 < Centroids.length; j2++) {
				double cdist = kmeansIndexdist(Centroids[j2], p);
				if(cdist < centroidDistance){
					centroidIndex = j2;
					centroidDistance = cdist;
				}
			}
			
			index[centroidIndex].add(p);
			
		}
		
		for (int i = 0; i < index.length; i++) {
			System.out.println("Centroid "+i+ " member count: " + index[i].size());
		}
		
		System.out.println("IndexSize:" + index.length);
		return index;
	}
	
	
	
	
	/**
	 * Improvement: Multiple Indices(k-means run),
	 * Input: Each run N
	 * Class KMeansLSHResults: 
	 * 		DataPacket[k] Centroids
	 * 		DataSet[k] index
	 */
	public static DataSet query(DataPacket source, DataSet data, double epsilon, KMeansLSHResult[] kmeansResults, int probecount){
		
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
		
		
		
		
		System.out.println("\tSize "+ data.size()+" Total Searchspace: "+ searchspace + "\tNeighbor Count: " + neighbors.size());		
		System.out.println("\tpercent:" +(searchspace*100.0/data.size()));
		
		//transfer keys to DataSet;
		Object dp[]  =  (neighbors.keySet().toArray());
		//initialize heap to track neighbors
		DataSet nearestNeighbors = new DataSet(dp.length);
		for (int i = 0; i < dp.length; i++) {
			nearestNeighbors.add((DataPacket) dp[i]);
		}
		
		
		return nearestNeighbors;
	}
	
	
	public static void main(String[] args) {
		System.out.println("KMeansLSH-Unit Test:");
		
		int testItem = 5, trainItem = 9;
		
		String DataDirectory = "RandomPieces_10";
		String trainFilename = "ids10_"+trainItem+".data";
		String testFilename = "ids10_"+testItem+".data";
		String OpticsFilename = "ids10_" + trainItem + "-" + testItem + ".optics";
		
		
		String InputTrainingFilePath = DataDirectory + File.separatorChar + trainFilename;
		String InputTestFilePath = DataDirectory + File.separatorChar + testFilename;
		String OutputFilePath = DataDirectory + File.separatorChar +  OpticsFilename;
		
		double epsilon = 7000;//00; //7000 originally
		int minPts = 6;
		
		
		
		
		
		DataSet normalizedData;
		boolean partialSet = true;
		if(partialSet){
			DataSet trainData = DataSetReader.readTrainingSet(InputTrainingFilePath);
//			DataSet testData = DataSetReader.readTestSet(InputTestFilePath);		
//			DataSet experimentData = DataIntegration.combine(trainData, testData);		
//			normalizedData =  DataNormalization.normalizeIntegerScaling(experimentData);
			normalizedData =  DataNormalization.normalizeIntegerScaling(trainData);
		}else{
			DataSet trainData = DataSetReader.readTrainingSet("Data"+File.separatorChar+"ids.data");
			normalizedData =  DataNormalization.normalizeIntegerScaling(trainData);
		}
		
		
		
		
		
		System.out.println("DataSize:" + normalizedData.size());
		DataSet dataset = normalizedData;
		double perf = 0.0;
		
		
		// ******************* START OF INDEXING *********************
		int runs = 1;
		int samplesize = dataset.size()/2;
		int k = (int) Math.sqrt(dataset.size()/2);
		int itr = 4;
		int probecount = 1;		//MUST BE LESS THAN K (set a percentage of k, consider # of runs)
		System.out.println("Centroids:"+k);
		KMeansLSHResult result[] = new KMeansLSHResult[runs];
		
		double LSHDuration = System.currentTimeMillis();
		for (int j = 0; j < runs; j++) {
			
			double LSHRunDuration = System.currentTimeMillis();
			
			DataPacket[] centroids = KMeans.runKMeansClusteringPhase(dataset, samplesize, k, itr);
			DataSet indices[]  = KMeans.index(dataset, centroids);
			result[j] = new KMeansLSHResult(centroids,indices,probecount);			
			
			
			System.out.println("\t Run Index Time: " + (System.currentTimeMillis()-LSHDuration)/1000.0);
			
		}
		
		System.out.println("Total Indexing Time:" + (System.currentTimeMillis()-LSHDuration)/1000.0);
		
		// ******************* END OF INDEXING *********************
		
		
		double totalLinearSearch = 0, totalIndexedSearch = 0, totalRecall = 0;
		
		
		for (int i = 0; i < dataset.size(); i++) {
			DataPacket object = dataset.elementAt(i);
			
			
			double ActualDuration = System.currentTimeMillis();
			DataSet actualNeighbors = NearestNeighborCompute.findNeighbors(dataset,  object, epsilon);	
//			DataSet actualNeighbors = ParallelNearestNeighbor.findNeighbors(dataset,  object, epsilon);	
			double LinearSearchTime =  (System.currentTimeMillis()-ActualDuration)/1000.0;
			
			
			
			
			
			
			
			double QueryDuration = System.currentTimeMillis();			
			DataSet indexedNeighbors = KMeans.query(object, dataset, epsilon, result,probecount);	
			double IndexedSearchTime = (System.currentTimeMillis()-QueryDuration)/1000.0;
			
			
//			System.out.println("LinearSearch: " + LinearSearchTime + "\tIndexed Time:" + IndexedSearchTime);	
//			System.out.println("Actual Neighbor Size " + actualNeighbors.size());
//			System.out.println("Esimate Neighbor Size " + indexedNeighbors.size());
//			
//			
//			
//			System.out.println();
			
			totalLinearSearch += LinearSearchTime;
			totalIndexedSearch += IndexedSearchTime;
			totalRecall += computeRecall(actualNeighbors, indexedNeighbors);
			
			if(i % 10 == 0){
				System.out.println("Completed: " + i*1.0/dataset.size()+ "\t"+i+"/"+ dataset.size());
				
				System.out.println("Total LinearSearch: " + totalLinearSearch + "\t Total Indexed Time:" + totalIndexedSearch);	
				System.out.println("Total Recall: " + totalRecall + "\tAverage Recall: " + totalRecall/ i);
			}
		}
		System.out.println("***************************");
		System.out.println("Total LinearSearch: " + totalLinearSearch + "\t Total Indexed Time:" + totalIndexedSearch);	
		System.out.println("Average Recall: " + totalRecall/ dataset.size());
		
		System.out.println("End");
		
	}
	
	
	public static double computeRecall(DataSet actual, DataSet indexed){
		
		int included_count = 0;
		for (int i = 0; i < actual.size(); i++) {
			//check if included in actual
			
			DataPacket actualNeighbor = actual.elementAt(i);
			if(indexed.contains(actualNeighbor)){
				included_count++;
			}
			
		}
		
//		System.out.println("Included " + included_count + " / " + actual.size() + " = " + included_count*1.0/actual.size());
		if(actual.size() == 0) return 0;
		
		return included_count*1.0/actual.size();
		
	}
	
	

}
