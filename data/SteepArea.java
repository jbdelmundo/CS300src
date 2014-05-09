package data;

import data.HeapObject;

public class SteepArea extends HeapObject{
	public int startIndex, endIndex;
	public double mib;
	public boolean negativeStart = false;
	public boolean isSteepUp;
	public boolean isFlat = false;
	
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
}
