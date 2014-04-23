package data;

import data.HeapObject;

public class ReachabilityPoint extends HeapObject{
	public int index;
	public double reachability;
	public int label;
	public boolean hasLabel;
	public int assignedlabel;
	
	@Override
	public int compareTo(HeapObject o) {
		// TODO Auto-generated method stub
		System.err.println("Unimplemented");
		return 0;
	}
	
}
