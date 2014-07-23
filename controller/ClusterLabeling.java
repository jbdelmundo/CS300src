package controller;

import java.util.ArrayList;
import java.util.PriorityQueue;

import data.Cluster;
import data.ClusterPerformance;
import data.DataPacket;
import data.DataSet;
import data.Heap;
import data.ReachabilityPoint;

public class ClusterLabeling {
	
	public static final int UNDEFINED = ReachabilityPoint.UNDEFINED;
	
	public static DataSet  ConfidentPoints;
	
	public static int correctConfident = 0;
	public static int incorrectConfident = 0;
	
	
	
	
	/**
	 * Creates label assignments to those that have no assignments. This will start with smallest clusters.
	 * @param points
	 * @param clusters
	 * @return accuracy
	 */
	public static ClusterPerformance assignLabels(ArrayList<ReachabilityPoint> points,ArrayList<Cluster> clusters, boolean skipGuessing){
		
		correctConfident = 0;
		incorrectConfident = 0;
		
		//sort clusters according to size, 
		PriorityQueue<Cluster> PQclusters = new PriorityQueue<>(clusters.size());
		
		for (Cluster cluster : clusters) {
			PQclusters.add(cluster);
		}
		
		ConfidentPoints = new DataSet(points.size());				// confident points are points that fall into a cluster
		
		while(!PQclusters.isEmpty()){
			Cluster c = PQclusters.poll();
//			System.out.println("Cluster + " + c.startIndex + " - " + c.endIndex + "\tSize:" + c.size());
			
			
			//change all labels within cluster
			
			
			ReachabilityPoint rp; 
			int previousLabel = ReachabilityPoint.UNDEFINED;
			for (int i = c.startIndex; i <= c.endIndex; i++) {
				
				rp = points.get(i);
				
				//if train data-- assign a label
				if(rp.hasLabel){
					
					
					// TODO ---- ADD NEW HERE july 21, if previous is undefined and 
					if(previousLabel == ReachabilityPoint.UNDEFINED){		//first train point encountered in cluster,
						for (int j = c.startIndex; j < i; j++) {
							ReachabilityPoint prevpt = points.get(i);
							
							if(prevpt.assignedlabel == UNDEFINED){
								prevpt.assignedlabel = rp.getDataPacketLabel();
								addToConfidentPoints(prevpt);
							}
						}
					}
					
						
						
					previousLabel = rp.getDataPacketLabel();
					rp.assignedlabel = rp.getDataPacketLabel();
					continue;
				}

				
				//already assigned previously
				if(rp.assignedlabel != UNDEFINED){
					previousLabel = rp.assignedlabel;
					continue;
				}
				
				if(previousLabel != UNDEFINED && rp.assignedlabel == UNDEFINED){
					rp.assignedlabel = previousLabel;
					
					addToConfidentPoints(rp);
					
				}
				
			}
		}//all clusters done -- not all training data and test data is included in a cluster
		//DO NOT USE assignedLabel on Train data since some are not yet changed -- use haslabel and getDataPacketLabel()
		
		
		
		//get stats
		
		
		int unassigned = 0;
		int traindata = 0;
		int guesses = 0;
		int skippguess = 0;
		
				
		// guess for remaining noise -- TEST DATA NOT ON A CLUSTER
		// TODO Find other alternatives other than left to right
		
		
		
		
		for (int i = 0; i < points.size() && !skipGuessing; i++) {
			ReachabilityPoint rp = points.get(i);
			
			if(rp.assignedlabel != UNDEFINED || rp.hasLabel){	//skip if already assigned or training data
					continue;
			}
			
			//find the nearest neighbor (priority on the optics predecessor, then optics successor)
			ReachabilityPoint rp_predecessor_neighbor, rp_successor_neighbor;
			if(i == 0){
				rp_predecessor_neighbor = points.get(i+1);
			}else{
				rp_predecessor_neighbor = points.get(i-1);
			}
			
			if(i+1 < points.size()){
				rp_successor_neighbor = points.get(i+1);
			}else{
				rp_successor_neighbor = points.get(i-1);
			}
			
			
			
			
			if(rp_predecessor_neighbor.assignedlabel != UNDEFINED){
				rp.assignedlabel = rp_predecessor_neighbor.getDataPacketLabel();
			}else{
				rp.assignedlabel = rp_successor_neighbor.getDataPacketLabel();
			}
			rp.isAssignedGuess = true;
			
			System.out.println("Guessing " + i + "\t" + rp.assignedlabel);
			if(rp.assignedlabel == UNDEFINED){
				System.out.println("Still Guessing " + i + "\t" + rp.assignedlabel);
				System.out.println("\tPredecessor" + i + "\t" + rp_predecessor_neighbor.assignedlabel + " "+rp_predecessor_neighbor.hasLabel  );
				System.out.println("\tSuccessor" + i + "\t" + rp_successor_neighbor.assignedlabel);
			}
			guesses++;
			
			
			
		}//end of guessing
		
		
		//get measurement performance
		int falsenegative = 0;		//actual : attack, classification: normal
		int falsepositive = 0;		//actual : normal, classification: attack
		int correct = 0;
		int incorrect = 0;
		int truepositive = 0;
		int truenegative = 0;
		
		
		for (int i = 0; i < points.size(); i++) {
			ReachabilityPoint rp = points.get(i);
			
			if(rp.hasLabel){
				traindata++;
				continue;
			}
			
			if(rp.assignedlabel == UNDEFINED){
				unassigned++;
//				System.out.println("Unassigned? " + i + "\t" + rp.assignedlabel + "\t"+ rp.hasLabel);
			}else {
				int label = rp.label;
				int classification = rp.assignedlabel;
				
				if(label == 0 && classification == 0){				//true positive
					truenegative++;
					correct++;
				}else if(label != 0 && classification != 0){		// true negative
					truepositive ++;
					correct++;
				}else if(label != 0 && classification == 0){		//false negative
					truepositive ++;
					falsenegative++;
					incorrect++;
//					System.out.println("False - "+i);
				}else if(label == 0 && classification != 0){		//false positive
					truenegative++;
					falsepositive++;
					incorrect++;
//					System.out.println("False + "+i);
				}
				
				
			}
			
		}
		
		int assigned = correct+incorrect;
		int testdata = points.size() - traindata;
		int certain = correct+incorrect - guesses;
		
		ClusterPerformance labelResult = new ClusterPerformance();
		labelResult.traindata = traindata;
		labelResult.testdata = testdata;
		
		labelResult.correct = correct;
		labelResult.incorrect = incorrect;
		labelResult.truepositive = truepositive;
		labelResult.truenegative = truenegative;
		labelResult.falsenegative = falsenegative;
		labelResult.falsepositive = falsepositive;
		
		labelResult.certain = certain;
		labelResult.guesses = guesses;
		labelResult.assigned = assigned;
		labelResult.unassigned = unassigned;
		
		labelResult.clustersFormed = clusters.size();
		
		
		labelResult.addedCorrect = correctConfident;
		labelResult.addedIncorrect = incorrectConfident;
		
		return labelResult;
		
	}

	
	public static void addToConfidentPoints(ReachabilityPoint rp){
		//create new confident training point
		
		if(rp.reachability <  0 || rp.reachability >=  3000 || true){		//threshold for adding
			return;	//TODO add custom tweaks here to improve performance --- add based on a reachability value maybe?
		}
		
		
		
		DataPacket confidentpoint = new DataPacket(rp.datapacket); 
		confidentpoint.label = rp.assignedlabel;							//overwrite label;
		confidentpoint.hasLabel = true;
		ConfidentPoints.add(confidentpoint);		
		
		int label = rp.label;
		int classification = rp.assignedlabel;
		
		if(label == 0 && classification == 0){				//true positive
			correctConfident++;			
		}else if(label != 0 && classification != 0){		// true negative
			correctConfident++;
		}else if(label != 0 && classification == 0){		//false negative
			incorrectConfident++;
//			System.out.println("False - "+i);
		}else if(label == 0 && classification != 0){		//false positive
			incorrectConfident++;
//			System.out.println("False + "+i);
		}
		
		
		
	}
}
