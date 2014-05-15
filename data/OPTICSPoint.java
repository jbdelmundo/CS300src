package data;

public class OPTICSPoint extends HeapObject {
	//OPTICS VARIABLES
		public double core_dist = -1;
		public double reachability_dist = -1;
		public boolean isProcessed = false;
		
		public double distToNeighbor;
		public int neighborID;
//		public DataSet neighbors;
		
		@Override
		public int compareTo(HeapObject o) {
			if(reachability_dist > ((OPTICSPoint)o).reachability_dist)
				return 1;
			
			if(reachability_dist < ((OPTICSPoint)o).reachability_dist)
				return -1;
			
			return 0;
						
		}
}
