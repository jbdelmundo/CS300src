package fileIO;

import data.SteepArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import data.ConfigAttributes;
import data.DataPacket;
import data.MinHeap;


/**
 * Writes a file from the OPTICS algorithm.
 * Represents the OPTICS ordering.
 * @author Test
 *
 */
public class DataPacketWriter {
	PrintWriter pw;
	public int test = 0, train = 0;
	
	public DataPacketWriter() {
		
	}
	
	public DataPacketWriter(String filename){
		this();		
		try {
			pw = new PrintWriter(new File(filename));
		} catch (FileNotFoundException e) {
			
		}
	}
	
	//
	/**
	 * Writes reachability Packet, used by OPTICSAlgorithm
	 *  -id
	 *  -reachability
	 *  -label
	 *  -haslabel
	 * @param datapacket
	 */
	public void printReachabilityPacketToFile(DataPacket datapacket){
		double reachability_dist = datapacket.reachability_dist;
		double core_dist = datapacket.core_dist;
		int id = datapacket.DataPacketID;
		int label = datapacket.label;
		boolean hasLabel = datapacket.hasLabel;
		String details = "";
		for(int i = 0; i < datapacket.SymbolicAttr.length; i++){
			details += ","+datapacket.SymbolicAttr[i];
		}
		for(int i = 0; i < datapacket.ContinuousAttr.length; i++){
			details += ","+datapacket.ContinuousAttr[i];
		}
				
		pw.println(id+","+reachability_dist+","+core_dist+","+label +","+hasLabel+ details);
		
		if(hasLabel){
			train++;
		}else{
			test++;
		}
	}
	
	
	
	public void printEvaluatedOpticsPackatToFile(MinHeap SteepAreaList){
		MinHeap temp =  new MinHeap();
		
		int lastindex = -1;
		
		while(SteepAreaList.size() != 0){
			SteepArea steeparea = (SteepArea) SteepAreaList.pop();
			
			int Sstart = steeparea.startIndex, Send = steeparea.endIndex;
			if(lastindex > Sstart){
				System.err.println(Sstart);
			}
			
			
			lastindex = Send;
			if(steeparea.isSteepUp){
				System.out.println("U^^\t" + Sstart+"\t"+Send+"\t" + steeparea.size());
				pw.println(Sstart+","+Send+","+1);
			}else{
				System.out.println("D__\t" + Sstart+"\t"+Send+"\t" + steeparea.size());
				pw.println(Sstart+","+Send+","+0);
			}						
			
			temp.push(steeparea);
		}
		SteepAreaList = temp;
		pw.close();
	}
	
	//plain Packet to file
	public void printPacket(DataPacket datapacket){		
		
		int label = datapacket.label;
		
		String details = "";
		for(int i = 0; i < datapacket.SymbolicAttr.length; i++){
			details += ","+datapacket.SymbolicAttr[i];
		}
		for(int i = 0; i < datapacket.ContinuousAttr.length; i++){
			details += ","+datapacket.ContinuousAttr[i];
		}
		
		
		pw.println(details+","+label);
	}
	
	
	public void printHyothesisPacket(MinHeap SteepAreaList){
		MinHeap temp =  new MinHeap();
		
		int lastindex = -1;
		
		while(SteepAreaList.size() != 0){
			SteepArea steeparea = (SteepArea) SteepAreaList.pop();
			
			int Sstart = steeparea.startIndex, Send = steeparea.endIndex;
			if(lastindex > Sstart){
				System.err.println(Sstart);
			}
			
			
			lastindex = Send;
			if(steeparea.isSteepUp){
				System.out.println("U^^\t" + Sstart+"\t"+Send+"\t" + steeparea.size());
				pw.println(Sstart+","+Send+","+1);
			}else{
				System.out.println("D__\t" + Sstart+"\t"+Send+"\t" + steeparea.size());
				pw.println(Sstart+","+Send+","+0);
			}						
			
			temp.push(steeparea);
		}
		SteepAreaList = temp;
		pw.close();
	}
	
	
	public void close(){
		pw.close();
	}
}
