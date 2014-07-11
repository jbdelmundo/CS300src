package data;

import java.util.ArrayList;

import data.HeapObject;

public class SteepArea extends HeapObject{
	public int startIndex, endIndex;
	public double mib = 0;
	public int mibIndex = -1;
	public boolean negativeStart = false;
	public boolean isSteepUp;
	public boolean isFlat = false;
	
	public static final boolean UP = true;
	public static final boolean DOWN = false;
	
	public SteepArea(){
		super();
	}
	
	public SteepArea(int start, int end, boolean isSteepUp) {
		this.startIndex = start;
		this.endIndex = end;
		this.isSteepUp = isSteepUp;
	}
	
		
	@Override
	public int compareTo(HeapObject o) {
		if(startIndex < ((SteepArea)o).startIndex){
			return -1;
		}
		return 1;
	}
	
	public int size(){
		return endIndex - startIndex +1;
	}
	
	
	
	
	/**
	 * Updates the MIB attribute of this steep area
	 * @param update
	 */
	public void updateMIB(double reachability, int index){		
		if(this.mib < reachability){
			mibIndex = index;
			this.mib = reachability;
		}
	}
}
