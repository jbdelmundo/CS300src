package controller;

import java.util.ArrayList;

import data.ReachabilityPoint;

public class ConfidenceCalculator {
	
	/**
	 * Computes the confidence of each point based on entropy
	 * @param points	Array of points
	 */
	public static void computeConfidence(ArrayList<ReachabilityPoint> trainingpoints, ArrayList<ReachabilityPoint> testpoints){
		
		for (ReachabilityPoint reachabilityPoint : testpoints) {
			getEntropyOfPoint(trainingpoints, reachabilityPoint);
		}
		
	}
	
	public static double getEntropyOfPoint(ArrayList<ReachabilityPoint> trainingpoints, ReachabilityPoint testpoint){
		
		//H(X)
		int numberOfClasses = 5; //normal, dos, u2r, probe, r2l
		double sum = 0;
		
		for (int i = 0; i < numberOfClasses; i++) {
			
			
			//get entropy for each
			double prob = getProbabilityOfPoint(trainingpoints, testpoint, testpoint.assignedlabel);
			sum += get
			
		}
		
		return 0;	// TODO change
	}
	
	public static double getProbabilityOfPoint(ArrayList<ReachabilityPoint> trainingpoints, ReachabilityPoint testpoint , int label){
		
		
		
		
		return 0;	//TODO change
	}

}
