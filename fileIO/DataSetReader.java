package fileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import config.Config;

import data.ConfigAttributes;
import data.DataPacket;
import data.DataSet;

public class DataSetReader {
	/**
	 * Reads a training set file (.data) format into DataSet dataType
	 * 
	 * .data format: 7 symbolic attr, 34 cont, 1 label
	 * 
	 * 
	 */
	
	public static DataSet readTestSet(String FilePath){
		return readDataSet(FilePath, false);
	}
	
	public static DataSet readTrainingSet(String FilePath){
		return readDataSet(FilePath, true);
	}
	
	public static DataSet readDataSet(String FilePath, boolean includeLabel){
		try {
			Scanner scan = new Scanner(new File(FilePath));
			DataSet ds = new DataSet();			
			
			int id = 0;
			while(scan.hasNext()){
				String packetString = scan.nextLine();
				DataPacket dp = createPacket(packetString,includeLabel);
				dp.DataPacketID = id;
				ds.add(dp);
				id++;
			}
			scan.close();
			return ds;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.err.println("Returning NULL");
		return null;
	}
	
	public static DataPacket createPacket(String inputString,boolean includeLabel){
		int isIncluded[] = Config.getIncludedAttributes();
		String[] line = inputString.split(",");
		
		DataPacket datapacket = new DataPacket(Config.symbiolicAttributeCount, Config.continuousAttibuteCount);
		
		int sIndex = 0, cIndex = 0;
		for (int i = 0; i < line.length-1; i++) {
			if(isIncluded[i] == 1){
				int attributeValue = Integer.parseInt(line[i]);
				
				if(i<=6){
					datapacket.SymbolicAttr[sIndex] = (char)( attributeValue +65 );
					sIndex++;
				}else{
					datapacket.ContinuousAttr[cIndex] = attributeValue;
					cIndex++;
				}
			}
		}
		int label = Integer.parseInt(line[line.length-1]);
		datapacket.label = label;
		datapacket.hasLabel = includeLabel;
		
		return datapacket;
	}
	

	
	
	public static void main(String[] args) {
		Config.getIncludedAttributes();
		DataSet ds = readTrainingSet("RandomPieces_200" + File.separatorChar + "ids200_23.data");
		System.out.println(ds.size());
		for (int i = 0; i< ds.size();i++) {
			DataPacket dp = ds.elementAt(i);
			if(dp == null)
				System.err.println("FUUUU");
			dp.printDataPacketInfo();
		}
	}
}
