package fileIO;



import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import config.Config;

import data.DataPacket;
import data.ReachabilityPoint;

public class OpticsOrderingReader {
	
	
	
	
	public static ArrayList<ReachabilityPoint> readFile(String pathname){		
		// index 0 - packetid
		// index 1 - reachability
		// index 2 - label
		// index 3 - haslabel
		Scanner scan = null;
		try {
			scan = new Scanner(new File(pathname));			
		} catch (FileNotFoundException e) {
	
			e.printStackTrace();
		}
		
		ArrayList<ReachabilityPoint> buffer;
		buffer = new ArrayList<ReachabilityPoint>();
		System.out.println("Buffering....");		
		int i;
		for(i = 0; scan.hasNext() ; i++){
			String str =scan.nextLine(); 
			String read[] =str.split(",");			
				
			ReachabilityPoint currentpoint = new ReachabilityPoint();
			currentpoint.index = i;
			currentpoint.reachability = Double.parseDouble(read[1]);
			currentpoint.core_dist = Double.parseDouble(read[2]);
			currentpoint.label = Integer.parseInt(read[3]);
			currentpoint.hasLabel = Boolean.parseBoolean(read[4]);
			boolean includeLabel = true;
			currentpoint.datapacket = OpticsOrderingReader.createPacketFromOpticsFile(5, read, includeLabel,currentpoint.label);
			
			buffer.add(currentpoint);
			
			
			
			
		}//end of buffering	
		System.out.println("Buffer done:"+i+ "   " + buffer.size());
		System.out.println("\n");
		
		scan.close();
		return buffer;
	}
	
	public static DataPacket createPacketFromOpticsFile(int startIndex, String[] line,boolean includeLabel, int label){
		int isIncluded[] = Config.getIncludedAttributes();		
		
		DataPacket datapacket = new DataPacket(Config.symbiolicAttributeCount, Config.continuousAttibuteCount);
		
		int sIndex = startIndex, cIndex = 0;
		for (int i = 0; i < Config.symbiolicAttributeCount; i++) {
			
			char attributeValue = line[sIndex].charAt(0);
			
			datapacket.SymbolicAttr[i] = (char)( attributeValue +65 );
			
			sIndex++;
		}
		
		for (int i = 0; i < Config.continuousAttibuteCount; i++) {
			long attributeValue = Long.parseLong(line[sIndex]);
			
			datapacket.ContinuousAttr[i] = attributeValue;
			
			sIndex++;
		}
		

		datapacket.label = label;
		datapacket.hasLabel = includeLabel;
		
		return datapacket;
	}
}
