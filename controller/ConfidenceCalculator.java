package controller;

import java.util.ArrayList;


import data.Cluster;
import data.DataPacket;
import data.DataSet;
import data.ReachabilityPoint;

/**
 * APPROACH 1:  Get the relative entropy of the test point with all the training points, then get the average.	 
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

	public void setDataPointsAndClusters(ArrayList<ReachabilityPoint> points , ArrayList<Cluster> clusters){
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
		this.clusters = clusters;
	}
	
	/**
	 * 
	 * @return Confident dataset
	 */
	public DataSet retriveConfidentData(double threshold){
		DataSet confidentData = new DataSet();		
		
		for (ReachabilityPoint reachabilityPoint : test_points) {
			DataPacket datapoint = reachabilityPoint.datapacket;			
			
			double confidence  = getRelativeEntropyInTrainingPoints(reachabilityPoint);
			if(confidence >= threshold){
				confidentData.add(datapoint);
			}			
		}
		return confidentData;	
	}
	

	
	/**
	 * Get entropy relative with whom?
	 * @param testPoint
	 * @return
	 */
	public double getRelativeEntropyInTrainingPoints(ReachabilityPoint testPoint){
		
		double totalEntropy = 0;		
		for (int i = 0; i < points.size(); i++) {		// for all points? inter-cluster? inter-label?s
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
		return 0;
		//for each attribute
//			total packets with xi as their value in the ath attribute/  total packets 
	}
	
	
	

	
}
