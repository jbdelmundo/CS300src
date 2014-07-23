package data;

import data.HeapObject;

public class ReachabilityPoint extends HeapObject{
	public int index;
	public double reachability;
	public double core_dist;
	public int label;
	public boolean hasLabel;
	public int assignedlabel = UNDEFINED;
	public boolean isAssignedGuess = false;
	public DataPacket datapacket;
	
	public static final int UNDEFINED = -1;
	
	public static boolean useBooleanVerification = false;
	
	public double confidence = 0;
	
	@Override
	public int compareTo(HeapObject o) {
		
		System.err.println("Unimplemented");
		return 0;
	}
	
	
	public boolean verifyAssignedLabel(){
		if(this.hasLabel){
			return true;
		}
		
		 
		if(useBooleanVerification){	//if
			if (this.assignedlabel == datapacket.label && datapacket.label == 0)	//both normal
			{
				return true;
			} 
			else
			if (this.assignedlabel > 0 &&  datapacket.label >0)	//both attack
			{
				return true;
			}
			else
			{
				return false;
			}
			
		}
		
		return (this.assignedlabel == datapacket.label);		
	}
	
	public int getDataPacketLabel(){
		if(this.label != datapacket.label){
			System.err.println(this.label +"\t"+ datapacket.label);
		}
		return this.label;//datapacket.label;
		
	}
	
}
