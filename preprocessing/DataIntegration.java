package preprocessing;

import data.DataPacket;
import data.DataSet;

public class DataIntegration {
	/**
	 * Read Training Set and Read Test Set, Output ExperimentFile
	 * 
	 * 
	 * Output should be DataSet data type
	 */
	
	DataSet knowledge;
	
	
	public void initKnowledge(){
		this.knowledge = new DataSet();
	}
	
	public static DataSet combine(DataSet training, DataSet test){
		DataSet ds = new DataSet();
		for (int i = 0; i < training.size(); i++) {
			ds.add(new DataPacket(training.elementAt(i)));	// a new copy for training set
		}
		for (int i = 0; i < test.size(); i++) {
			ds.add(new DataPacket(test.elementAt(i)));	// a new copy for test set
		}
		
		int size = ds.size();
		for (int i = 0; i < size; i++) {
			ds.elementAt(i).DataPacketID = i;
		}
		return ds;
	}
	
}
