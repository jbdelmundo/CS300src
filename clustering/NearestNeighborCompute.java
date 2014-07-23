package clustering;


import java.util.HashMap;
import config.Config;
import controller.IDS;

import data.DataPacket;
import data.DataSet;

import data.ReachabilityPoint;

/**
 * NearestNeighborCompute performs N-N Search based on distance function
 * 
 * 
 * @author Test
 * 
 */

public class NearestNeighborCompute {

	public static double includedMaxAttr[] = null;
	public static double includedMinAttr[] = null;
	public static final double PREFERRED_RANGE = Math.sqrt(Integer.MAX_VALUE);
	static boolean displaystats = false;
	
	public static boolean useParallelSearch = false;
	

	static HashMap<Integer, Boolean> isInDB = new HashMap<Integer, Boolean>();

	/**
	 * Finds the neighbors of the object and updates its core dist.
	 * 
	 * @param dataset
	 *            Set of objects
	 * @param object
	 *            Object of reference
	 * @param epsilon
	 *            Distance threshold
	 * @return Unprocessed Data points whose distance is less than or equal to the threshold
	 */
	public static DataSet findNeighborsAndCoreDist(DataSet dataset, DataPacket object,
			int minpts, double epsilon) {

				
		double core_dist = Double.MAX_VALUE;
		int neighborcount = 0;
		
		DataSet neighbors = new DataSet();
		for (DataPacket dataPacket : dataset) {

			if (object.equals(dataPacket))
				continue;
			
			double dist;
			if(IDS.useDistCache){
				dist = IDS.distCache.findDistance(object, dataPacket);
			}else{
				dist = NearestNeighborCompute.findDistance(object, dataPacket);
			}
				
			
			
			// System.out.println("findNeighbors:" + dist);
			if (epsilon >= dist) {
				neighborcount++;
				
				if( !dataPacket.isProcessed){
					neighbors.add(dataPacket);			//add only unprocessed points but keep track of neighbor count
				}
				
				dataPacket.distToNeighbor = dist;
				dataPacket.neighborID = object.DataPacketID;
				core_dist = Math.min(core_dist, dist);	//pre-compute core distance			
			}
			
			
		}
		
		
		//if core object, update core dist
		if(neighborcount < minpts){
			object.core_dist = ReachabilityPoint.UNDEFINED;
		}else{
			object.core_dist = core_dist;
		}

		return neighbors;
	}
	
	public static DataSet findNeighbors(DataSet dataset, DataPacket object,	 double epsilon) {
		
//		if(epsilon == Double.MAX_VALUE){									//Override if will include all
//			DataSet nearestNeighbors = new DataSet(dataset.size());
//			for (DataPacket dataitem : dataset) {
//				if(object.equals(dataitem))
//					continue;
//				
//				nearestNeighbors.add(dataitem);
//			}
//			return nearestNeighbors;
//		}
		

		if(IDS.useKLSH){
			return IDS.pkm.query(object, dataset, epsilon, IDS.kmeansResults, IDS.probecount);
		}
		
		DataSet neighbors = new DataSet();
		for (DataPacket dataPacket : dataset) {

			if (object.equals(dataPacket))
				continue;
			
			double dist = NearestNeighborCompute.findDistance(object, dataPacket);
						
			// System.out.println("findNeighbors:" + dist);
			if (epsilon >= dist) {				
				neighbors.add(dataPacket);						
			}	
		}
		
		return neighbors;
	}
	
		
	public static double findDistance(DataPacket dp1, DataPacket dp2) {
		//used in neighbor search, OPTICS compute distance
		return HOEM(dp1, dp2);			
	}

	public static double HOEM(DataPacket dp1, DataPacket dp2) {

		double cont_dist = 0.0;

		for (int i = 0; i < dp1.ContinuousAttr.length; i++) {

			double diff = dp1.ContinuousAttr[i] - dp2.ContinuousAttr[i];
			double attr_diff = Math.pow(diff, 2);

			// System.out.println("dist cont:" +
			// ConfigAttributes.IncludedContinuousNames[i]+ "\t" +
			// dp1.ContinuousAttr[i] +" (-) "+ dp2.ContinuousAttr[i]
			// +" " +diff +"\n"+attr_diff);
			cont_dist += attr_diff;
		}

		double symb_dist = 0.0;

		for (int i = 0; i < dp1.SymbolicAttr.length; i++) {

			double weight = Config.getIntegerRangeNormalizationValue();
			double attr_diff = 0.0;

			if (dp1.SymbolicAttr[i] != dp2.SymbolicAttr[i]) {
				attr_diff = Math.pow(weight, 2);
			}

			symb_dist += attr_diff;
		}

		// System.out.println(symb_dist + " " + cont_dist);
		return Math.sqrt(cont_dist + symb_dist);

	}

	public static void main(String[] args) {
		System.out.println("Main: Nearest Neighbor");

		System.out.println("Main: Nearest Neighbor - End");
	}

}
