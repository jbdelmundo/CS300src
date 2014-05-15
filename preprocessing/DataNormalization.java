package preprocessing;

import java.util.Arrays;

import config.Config;
import data.DataPacket;
import data.DataSet;

public class DataNormalization {
	
	
	
	/**
	 * Normalizes the data based on the Integer data type range
	 * 
	 * vi' = vi   * preferredRange/ (max-min)
	 * 
	 * */
	
	static double preferredRangeINTEGER = Config.getIntegerRangeNormalizationValue();
	
	public static DataSet normalizeIntegerScaling(DataSet inputData){
		System.out.println("Normalizing based on Integer DataType");
		
		
		long max[] = new long[Config.continuousAttibuteCount];
		long min[] = new long[Config.continuousAttibuteCount];
		Arrays.fill(min, -1);
		
		//compute min and max for continuous attributes;
		for (DataPacket datapacket : inputData) {			
			for (int i = 0; i < datapacket.ContinuousAttr.length; i++) {
				max[i] = Math.max(max[i], datapacket.ContinuousAttr[i]);
				if(min[i] != -1) 
					min[i] = Math.min(min[i], datapacket.ContinuousAttr[i]);
				else
					min[i] =  datapacket.ContinuousAttr[i];
			}			
		}
		
		//compute weights
		double weight[] = new double[Config.continuousAttibuteCount];
		for (int i = 0; i < weight.length; i++) {
			if(max[i]-min[i] != 0)
				weight[i] =   preferredRangeINTEGER / (max[i]-min[i]) ;
			else
				weight[i] = 0;
		}
		
		
		//transform
		for (DataPacket datapacket : inputData) {			
			for (int i = 0; i < datapacket.ContinuousAttr.length; i++) {
				datapacket.ContinuousAttr[i] = (int) (datapacket.ContinuousAttr[i] * weight[i]);				
			}			
		}
		
		return inputData;
	}

}
