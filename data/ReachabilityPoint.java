package data;

import data.HeapObject;

public class ReachabilityPoint extends HeapObject{
	public int index;
	public double reachability;
	public double core_dist;
	public int label;
	public boolean hasLabel;
	public int assignedlabel;
	
	public static final int UNDEFINED = -1;
	
	@Override
	public int compareTo(HeapObject o) {
		// TODO Auto-generated method stub
		System.err.println("Unimplemented");
		return 0;
	}
	
}
