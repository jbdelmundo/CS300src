package clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import config.Config;

import data.ConfigAttributes;
import data.DataPacket;
import data.DataSet;
import optimizations.DBConnection;
import optimizations.ParallelNearestNeighbor;
import preprocessing.DataNormalization;

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
	public static final double PREFERRED_RANGE = 46340.0; // sqrt (int range)
	static boolean displaystats = false;
	
	public static boolean useParallelSearch = false;
	
	static DBConnection dbconn;
	static HashMap<Integer, Boolean> isInDB = new HashMap<Integer, Boolean>();

	/**
	 * Nearest Neighbor Search using bruteforce
	 * 
	 * @param dataset
	 *            Set of objects
	 * @param object
	 *            Object of reference
	 * @param epsilon
	 *            Distance threshold
	 * @return datapoints whose distance is less than or equal to the threshold
	 */
	public static DataSet findNeighbors(DataSet dataset, DataPacket object,
			double epsilon) {

		if(useParallelSearch){
			return ParallelNearestNeighbor.findNeighbors(dataset, object, epsilon);	
		}
		
		
		
		DataSet neighbors = new DataSet();
		for (DataPacket dataPacket : dataset) {

			if (object.equals(dataPacket))
				continue;
			double dist = findDistance(object, dataPacket);
			// System.out.println("findNeighbors:" + dist);
			if (epsilon >= dist) {
				neighbors.add(dataPacket);
			}
		}

		return neighbors;
	}
	
		
	public static double findDistance(DataPacket dp1, DataPacket dp2) {
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
