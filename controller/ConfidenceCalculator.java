package controller;

import java.util.ArrayList;

import data.Cluster;
import data.DataPacket;
import data.ReachabilityPoint;

/**
 * Input: training data and test data with assigned labels
 * Output: test points to be included.
 * @author Doge
 *
 */
public class ConfidenceCalculator {
	
	ArrayList<ReachabilityPoint> points;
	ArrayList<ReachabilityPoint> train_points;
	ArrayList<ReachabilityPoint> test_points;
	
	ArrayList<Cluster> clusters;
	Cluster noise;
	
	// TODO: Include 'isAGuessAssignment' attribute, move ClusterLabeling class to controller package
	// TODO If point is just a guess, NEVER INCLUDE
	// TODO if a point is not in a cluster, how to compute for entropy?

	public void setTrainAndTestPoints(ArrayList<ReachabilityPoint> points){
		this.points = points;
		train_points = new ArrayList<ReachabilityPoint>();
		test_points = new ArrayList<ReachabilityPoint>();
		for (ReachabilityPoint reachabilityPoint : points) {
			if(reachabilityPoint.hasLabel){
				train_points.add(reachabilityPoint);
			}else{
				test_points.add(reachabilityPoint);
			}
		}
	}
	
	public void setClusters(ArrayList<Cluster> clusters){
		this.clusters = clusters;
	}
	
	/**
	 * APPROACH 1:  Get the relative entropy of the test point with all the training points, then get the average.
	 */
	public double getRelativeEntropyInTrainingPoints(ReachabilityPoint testPoint){
		
		double totalEntropy = 0;		
		for (int i = 0; i < points.size(); i++) {
			ReachabilityPoint rp  = points.get(i);
			totalEntropy += computeRelativeEntropy(testPoint, rp);
		}
		return totalEntropy;
	}
	
	
	public double computeRelativeEntropy(ReachabilityPoint testPoint , ReachabilityPoint referencePoint){
		double entropy = 0;
		DataPacket tpoint = testPoint.datapacket;
		DataPacket refpoint = referencePoint.datapacket;
		//for all dimensions of point, get the probability of each dimension in the cluster
		
		
		
		
		return entropy;
	}
	
	
	//attribute a,  xi as the value, m possible values
	private double computeProbabilityInFieldValues(){
		//for each attribute
//			total packets with xi as their value in the ath attribute/  total packets 
	}
	
	
	
	/**
	 * APPROACH 2:  Get the entropy of the point wrt clusters, assigned label
	 */
	public double getEntropyInCluster(ReachabilityPoint testpoint, Cluster clusterAssigned){
		int numberOfClusters = clusters.size();
		double entropy = 0;
		
		for (int i = 0; i < numberOfClusters +1; i++) {
			double prob = getProbabilityOfPointInCluster(testpoint,clusterAssigned);
			entropy += (prob * Math.log(prob));
		}
		return entropy;
	}
	
	/**
	 * Computes the probability of point in the cluster
	 * @param testpoint
	 * @param clusterAssigned
	 * @return
	 */
	public double getProbabilityOfPointInCluster(ReachabilityPoint testpoint, Cluster clusterAssigned){
		// options: nearest neighbor with the same label?
		int startIndex = clusterAssigned.startIndex;
		int endIndex =  clusterAssigned.endIndex;
		double probability =0;
		
		for (int i = startIndex; i <= endIndex; i++) {
			ReachabilityPoint rp  = points.get(i);
			if(rp.equals(testpoint))
				continue;
			
			
			
			
		}
		
		return 0;
	}
	
	
}
