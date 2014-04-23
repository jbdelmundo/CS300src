package data;

public abstract class HeapObject {
	public int heapPosition = -1;
	
	/**
	 * 
	 * @param o
	 * @return returns true if this object is greater than object o
	 */
	public abstract int compareTo(HeapObject o);
}
