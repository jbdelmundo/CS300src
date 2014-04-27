package data;

import java.util.Collection;
import java.util.Vector;

public class DataSet extends Vector<DataPacket> {

	private String label = "DEFAULT";
	private static final long serialVersionUID = 1L;
	
	public DataSet() {
		
	}
	
	public DataSet(int initialCapacity){
		super(initialCapacity);
	}
	
	public DataSet(Collection<DataPacket> arg0){
		super(arg0);
	}
	
	public void setLabel(String labels){
		this.label = labels;
	}
	
	public String getLabel(){
		return this.label;
	}
	
}
