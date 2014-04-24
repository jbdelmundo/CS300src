package optimizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import optimizations.KMeans.CentroidIndex;

import config.Config;

import clustering.NearestNeighborCompute;

import data.DataPacket;
import data.DataSet;
import data.Heap;

public class KMeans {
	
	
	
	/**
	 * Returns a list of Centroids 
	 */
	public DataPacket[] runKMeansClusteringPhase(DataSet D,int samplesize, int k,int itr){
		
		//insert samples into array samples
		Random r = new Random(System.currentTimeMillis());
		
		DataPacket samples[] = new DataPacket[samplesize];
		DataPacket randomPacket;
		for (int i = 0; i < samples.length; i++) {
			randomPacket = D.elementAt(r.nextInt(D.size()));
			
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
	
	private DataPacket[] initializeCentroids(DataPacket samples[], int k,Random r){
		DataPacket centroids[] = new DataPacket[k];
		DataPacket randomPacket;
		
		for (int i = 0; i < k; i++) {
			randomPacket = samples[r.nextInt(samples.length)];
			
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
	
	
	
	public double kmeansdist(DataPacket dp1, DataPacket dp2){
		/*** Get only the distance of continuous attributes, symbolic attributes are considered equal */
		
		double cont_dist = 0.0;
		for (int i = 0; i < dp1.ContinuousAttr.length; i++) {

			double diff = dp1.ContinuousAttr[i] - dp2.ContinuousAttr[i];
			double attr_diff = Math.pow(diff, 2);
			cont_dist += attr_diff;
		}

		return cont_dist;
	}
	
	public double kmeansIndexdist(DataPacket dp1, DataPacket dp2){
		return NearestNeighborCompute.findDistance(dp1, dp2);
	}
	
	private DataPacket addToCentroid(DataPacket centroid, DataPacket packet){
		for (int i = 0; i < centroid.ContinuousAttr.length; i++) {
			centroid.ContinuousAttr[i] += packet.ContinuousAttr[i];
		}
		return centroid;
	}
	
	private DataPacket updateCentroid(DataPacket centroid,int count){
		for (int i = 0; i < centroid.ContinuousAttr.length; i++) {
			centroid.ContinuousAttr[i] /= count;
		}
		return centroid;
	}
	
	/**
	 * Improvement: Multiple binning
	 * @return array of datasets. each dataset contains the set of points assigned to the ith centroid
	 */
	public DataSet[] index(DataSet D, DataPacket[] Centroids, int probecount){
		//initialize Index
		DataSet index[] = new DataSet[Centroids.length];
		
		
		
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
		return index;
	}
	
	class CentroidIndex implements Comparable<CentroidIndex>{
		double dist;
		int centroidID;
		public CentroidIndex(int centroidID, double dist) {
			this.centroidID = centroidID;
			this.dist = dist;
		}
		public int compareTo(CentroidIndex o) {
			if(dist > o.dist) return 1;
			if(dist < o.dist) return -1;
			return 0;
		}
	}
	
	
	/**
	 * Improvement: Multiple Indices(k means run),
	 */
	public void querry(DataPacket source, DataSet[] index, DataPacket[] Centroids, int probecount){
		
		//initialize heap to track neighbors
		
		//find the closest centroids index i
		
		
		//for each centroid index, perform a linear search
	}
	

}
