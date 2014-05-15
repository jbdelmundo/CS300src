package fileIO;



import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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
			// TODO Auto-generated catch block
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
			
			buffer.add(currentpoint);
			
			
			
		}//end of buffering	
		System.out.println("Buffer done:"+i+ "   " + buffer.size());
		System.out.println("\n");
		
		scan.close();
		return buffer;
	}
}
