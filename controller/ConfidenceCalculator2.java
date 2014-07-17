package controller;

import java.util.ArrayList;


import data.Cluster;
import data.DataPacket;
import data.DataSet;
import data.ReachabilityPoint;

/**
 * /**
 * APPROACH 2: Compare entropy with new point and without new point, 
 * 				Compute Probability that the test point will fall into the assigned category
 * 				- Estimate probability on k-nearest neighbors in training set
 * 				- Rationale: clustering will be based on KNN and will most likely to cluster with the nearest neighbor
	 
 * Input: training data and test data with assigned labels
 * Output: test points to be included.
 * @author Doge
 *
 */
public class ConfidenceCalculator2 {
	
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
			
			double confidence  = compareEntropyWithDataSet(reachabilityPoint);
			if(confidence >= threshold){
				confidentData.add(datapoint);
			}			
		}
		return confidentData;	
	}
	
	
	
	public double compareEntropyWithDataSet(ReachabilityPoint testPoint){
		
		double trainingEntropy = getEntropyOfDataSet(train_points);
		
		ArrayList<ReachabilityPoint> trainingWithTestPoint = new ArrayList<>(train_points);
		trainingWithTestPoint.add(testPoint);
		
		
		double testEntropy = getEntropyOfDataSet(trainingWithTestPoint);
		
		return trainingEntropy - testEntropy ;
	}
	
	
	
	/**
	 * Probability is based on KNN, OPTICS will find neighbors within epsilon
	 * @param points
	 * @return
	 */
	public double getEntropyOfDataSet(ArrayList<ReachabilityPoint> points){

		int k = 6;
		
		for (ReachabilityPoint reachabilityPoint : points) {
			
		}
		
		
		
		
		return 0;
	}
	
	
	
	
}
