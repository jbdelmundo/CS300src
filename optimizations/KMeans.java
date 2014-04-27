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
				if(N[j] == 0){					
					centroids[j] = new DataPacket(samples[r.nextInt(samples.length)]);	//random centroid
				}else{
					centroids[j] = updateCentroid(centroids[j], N[j]);
					N[j] = 0;
				}
				
				
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
		/*** Get only the distance of continuous attributes, symbolic attributes are considered equal */
		
		double cont_dist = 0.0;
		for (int i = 0; i < dp1.ContinuousAttr.length; i++) {

			double diff = dp1.ContinuousAttr[i] - dp2.ContinuousAttr[i];
			double attr_diff = Math.pow(diff, 2);
			cont_dist += attr_diff;
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
	public static DataSet[] index(DataSet D, DataPacket[] Centroids, int probecount){
		//initialize Index
		DataSet index[] = new DataSet[Centroids.length];
		for (int i = 0; i < index.length; i++) {
			index[i] = new DataSet();
		}
		
		
		
		for (int i = 0; i < D.size(); i++) {
			DataPacket p = D.elementAt(i);
			
			//find top nearest centroids of point p
			PriorityQueue<CentroidIndex> nearestCentroids = new PriorityQueue<CentroidIndex>(probecount+1);			
			for (int j = 0; j < Centroids.length; j++) {
				double cdist = kmeansIndexdist(p, Centroids[j]);				
				
				nearestCentroids.add(new CentroidIndex(j, cdist));
				if(nearestCentroids.size() > probecount){
					nearestCentroids.remove();
				}
			}
			
			//add point p to the index[j] where j is the nearest centroids.
			while(!nearestCentroids.isEmpty()){
				CentroidIndex ci = nearestCentroids.remove();
				index[ci.centroidID].add(p);
			}
			
		}
		
		for (int i = 0; i < index.length; i++) {
			System.out.println("Cenroid "+i+ " member count: " + index[i].size());
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
	public static DataSet query(DataPacket source, DataSet data, double epsilon, KMeansLSHResult[] kmeansResults){
		
		
		
		//find the closest centroids index i
		int[] nearestCentroids = new int[kmeansResults.length];
		
		for (int i = 0; i < nearestCentroids.length; i++) {
			
			int cindex = -1;
			double dist = Double.MAX_VALUE;
			for (int j = 0; j < kmeansResults[i].centroids.length; j++) {
				
				double distToCentroid =  kmeansIndexdist(source,  kmeansResults[i].centroids[j]);
				if(dist > distToCentroid){
					cindex = j;
					dist = distToCentroid;
				}
			}
			
			nearestCentroids[i] = cindex;
		}
		
		
		HashMap<DataPacket,Boolean> neighbors = new HashMap<DataPacket, Boolean>();		//use hashmap to remove duplicates
		
		int searchspace = 0;
		
		//for each centroid index, perform a linear search among the indices
		for (int i = 0; i < nearestCentroids.length; i++) {
			
			//
			for (int j = 0; j < kmeansResults[i].index.length; j++) {
				DataSet compareTo = kmeansResults[i].index[i];
				
				System.out.println("\tSearchspace: i="+ i+" J = "+j+"\t "+ compareTo.size());
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
		System.out.println("Total Searchspace: "+ searchspace);
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
		
		int testItem = 13, trainItem = 10;
		
		String DataDirectory = "RandomPieces_100";
		String trainFilename = "ids100_"+trainItem+".data";
		String testFilename = "ids100_"+testItem+".data";
		String OpticsFilename = "ids100_" + trainItem + "-" + testItem + ".optics";
		
		
		String InputTrainingFilePath = DataDirectory + File.separatorChar + trainFilename;
		String InputTestFilePath = DataDirectory + File.separatorChar + testFilename;
		String OutputFilePath = DataDirectory + File.separatorChar +  OpticsFilename;
		
		double epsilon = 70;//00; //7000 originally
		int minPts = 6;
		
		
		
		
		
		
//		DataSet trainData = DataSetReader.readTrainingSet("Data"+File.separatorChar+"ids.data");
		
		DataSet trainData = DataSetReader.readTrainingSet(InputTrainingFilePath);
		DataSet testData = DataSetReader.readTestSet(InputTestFilePath);		
		DataSet experimentData = DataIntegration.combine(trainData, testData);		
		DataSet normalizedData =  DataNormalization.normalizeIntegerScaling(experimentData);
//		DataSet normalizedData =  DataNormalization.normalizeIntegerScaling(trainData);
		
		
		System.out.println("DataSize:" + normalizedData.size());
		DataSet dataset = normalizedData;
		double perf = 0.0;
		
		
		// ******************* START OF INDEXING *********************
		System.out.println("Indexing...");
		int runs = 1;
		int samplesize = dataset.size()/2;
		int k = 500;//500;
		int itr = 4;
		int probecount = 1;
		
		KMeansLSHResult result[] = new KMeansLSHResult[runs];
		
		double LSHDuration = System.currentTimeMillis();
		for (int j = 0; j < runs; j++) {
			
			double LSHRunDuration = System.currentTimeMillis();
			
			DataPacket[] centroids = KMeans.runKMeansClusteringPhase(dataset, samplesize, k, itr);
			DataSet indices[]  = KMeans.index(dataset, centroids, probecount);
			result[j] = new KMeansLSHResult(centroids,indices,probecount);			
			
			
			System.out.println("\t Run Index Time: " + (System.currentTimeMillis()-LSHDuration)/1000.0);
			
		}
		
		System.out.println("Total Indexing Time:" + (System.currentTimeMillis()-LSHDuration)/1000.0);
		
		// ******************* END OF INDEXING *********************
		
		for (int i = 0; i < dataset.size(); i++) {
			DataPacket object = dataset.elementAt(i);
			
			System.out.println("Actual Start...");
			double ActualDuration = System.currentTimeMillis();
			DataSet actualNeighbors = NearestNeighborCompute.findNeighbors(dataset,  object, epsilon);		
			System.out.println("Linear Search Time :" + (System.currentTimeMillis()-ActualDuration)/1000.0);
			
			
			
			
			
			
			double QueryDuration = System.currentTimeMillis();			
			DataSet indexedNeighbors = KMeans.query(object, dataset, epsilon, result);			
			System.out.println("Total QueryTime:" + (System.currentTimeMillis()-QueryDuration)/1000.0);
			
			
			System.out.println("Actual Neighbor Size" + actualNeighbors.size());
			System.out.println("Esimate Neighbor Size" + indexedNeighbors.size());
			
			
			
			
			
			
			break; //no loop
		}
		
		System.out.println("FIX Some CENTROIDS have no members");
		System.out.println("FIX INDEXING");
		
		System.out.println("End");
		
	}
	
	
	

}
