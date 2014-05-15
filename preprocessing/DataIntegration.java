package preprocessing;

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
		ds.addAll(training);
		ds.addAll(test);
		training.removeAllElements();
		test.removeAllElements();
		int size = ds.size();
		for (int i = 0; i < size; i++) {
			ds.elementAt(i).DataPacketID = i;
		}
		return ds;
	}

}
