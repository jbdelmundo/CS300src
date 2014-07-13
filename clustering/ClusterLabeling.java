package clustering;

import java.util.ArrayList;
import java.util.PriorityQueue;

import data.Cluster;
import data.Heap;
import data.ReachabilityPoint;

public class ClusterLabeling {
	
	public static final int UNDEFINED = ReachabilityPoint.UNDEFINED;
	
	/**
	 * Creates label assignments to those that have no assignments. This will start with smallest clusters.
	 * @param points
	 * @param clusters
	 */
	public static void assignLabels(ArrayList<ReachabilityPoint> points,ArrayList<Cluster> clusters, boolean skipGuessing){
		
		//sort clusters according to size, 
		PriorityQueue<Cluster> PQclusters = new PriorityQueue<>(clusters.size());
		
		for (Cluster cluster : clusters) {
			PQclusters.add(cluster);
		}
		
		
		while(!PQclusters.isEmpty()){
			Cluster c = PQclusters.poll();
			System.out.println("Cluster + " + c.startIndex + " - " + c.endIndex + "\tSize:" + c.size());
			
			
			//change all labels within cluster
			
			
			ReachabilityPoint rp; 
			int previousLabel = ReachabilityPoint.UNDEFINED;
			for (int i = c.startIndex; i <= c.endIndex; i++) {
				
				rp = points.get(i);
				
				//if train data-- assign a label
				if(rp.hasLabel){
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
				}
				
				
				
			}
		}//all clusters done -- not all training data and test data is included in a cluster
		//DO NOT USE assignedLabel on Train data since some are not yet changed -- use haslabel and getDataPacketLabel()
		
		
		
		//get stats
		
		int correct = 0;
		int incorrect = 0;
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
			
			
			System.out.println("Guessing " + i + "\t" + rp.assignedlabel);
			if(rp.assignedlabel == UNDEFINED){
				System.out.println("Still Guessing " + i + "\t" + rp.assignedlabel);
				System.out.println("\tPredecessor" + i + "\t" + rp_predecessor_neighbor.assignedlabel + " "+rp_predecessor_neighbor.hasLabel  );
				System.out.println("\tSuccessor" + i + "\t" + rp_successor_neighbor.assignedlabel);
			}
			guesses++;
			
			
			
		}//end of guessing
		
		
		
		
		for (int i = 0; i < points.size(); i++) {
			ReachabilityPoint rp = points.get(i);
			
			if(rp.hasLabel){
				traindata++;
				continue;
			}
			
			if(rp.assignedlabel == UNDEFINED){
				unassigned++;
//				System.out.println("Unassigned? " + i + "\t" + rp.assignedlabel + "\t"+ rp.hasLabel);
			}else if(rp.verifyAssignedLabel()){
				correct++;
			}else{
				incorrect++;
			}
			
		}
		
		int assigned = correct+incorrect;
		int testdata = points.size() - traindata;
		int certain = correct+incorrect - guesses;
		
		System.out.println("Stats:");
		System.out.println("TrainData " + traindata);
		System.out.println("TestData " + testdata);
		System.out.println("DataSize " + (traindata+testdata));
		System.out.println();
		System.out.println("Correct " + correct  + "\tout of "+ testdata + "\t" + (correct*1.0/testdata));
		System.out.println("InCorrect " + incorrect  + "\tout of "+ testdata + "\t" + (incorrect*1.0/testdata));
		System.out.println("Certain " + certain  + "\tout of "+ testdata + "\t" + (certain*1.0/testdata));
		System.out.println();
		System.out.println("Assigned " + assigned  + "\tout of "+ testdata + "\t" + (assigned*1.0/testdata));
		System.out.println("Unassigned " + unassigned  + "\tout of "+ testdata + "\t" + (unassigned*1.0/testdata));
		System.out.println("Guesses " + guesses  + "\tout of "+ testdata + "\t" + (guesses*1.0/testdata));
		System.out.println("Skippedguesses " + skippguess  + "\tout of "+ testdata + "\t" + (skippguess*1.0/testdata));
		
		
		
		
		
	}

}
