package optimizations;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import sun.security.util.BigInt;

import clustering.NearestNeighborCompute;
import data.DataPacket;
import data.DataSet;
import fileIO.DataSetReader;

public class DistanceCache {
	
	
	int MAX = 49000;
	//48984
	double dist[];
	
	public DistanceCache(DataSet data) {
		build(data);
	}
	
	private void build(DataSet data){
		double capacity = 0 ;
		System.gc();
		try {
			 System.out.println("Free memory (bytes): " +   Runtime.getRuntime().freeMemory());
			int size = data.size();
			capacity = (size / 2.0 * size) + size / 2.0;
			System.out.println("DataSize " + size+ " Size " + (int) capacity);
			dist = new double[(int) capacity];
		} catch (OutOfMemoryError e) {
			 System.err.println("Free memory (bytes): " +   Runtime.getRuntime().freeMemory());
			System.err.println("Error Creating Dist Cache: Data too large: " + capacity);
			e.printStackTrace();
		}
		
		int size = data.size();
		
		int index = 0;
		for (int i = 0; i < size; i++) {
			DataPacket obj1 = data.elementAt(i);
			DataPacket obj2;
			
			
			for (int j = 0; j <= i; j++) {
				obj2 = data.elementAt(j);
				dist[index] = compute_dist(obj1, obj2);
//				System.out.println( (getIndex(i, j) == index)+"\tIndex " + index + "\t"+i+ "\t" + j + "\t est " + getIndex(i, j));
				index++;
			}	
			if(index % 1000 == 0){
				System.out.println("i " +i + " / " + data.size() + " \t"+ index+" " +dist.length);
			}
		}		
	}
	

	
	public double findDistance(DataPacket obj1,DataPacket obj2){
		int o1 = obj1.DataPacketID; 
		int o2 = obj2.DataPacketID;
		
		int index = getIndex(o1, o2);	
		
		return dist[index];
	}
	
	public static double compute_dist(DataPacket obj1, DataPacket obj2){
		
		return NearestNeighborCompute.findDistance(obj1, obj2);
	}
	
	public static void debug(){
		int size = 48000;
		double capacity = (size / 2.0 * size) + size / 2.0;
		System.out.println("DataSize " + size+ " Size " + (int) capacity);
		double dist[] = new double[(int) capacity];
		
		int index = 0;
		
		System.out.println(getIndex(size,size));
		
//		int len = size- i;
		for (int i = 0; i < size; i++) {
						
			
			for (int j = 0; j <= i; j++) {
				if(getIndex(i, j) <0){
					System.out.println(i+ "  " + j  + " " +index);;
				}
				
				if(index % 1000000 == 0){
					System.out.println(" "+index/capacity);
				}
//				System.out.println("Index" + index + "\t"+i+ "\t" + j + "\t est " + getIndex(i, j));
				index++;
			}	
			
		}
		
	}
	
	public static void main(String[] args) {
//	if(true){
////		System.out.println(getIndex(48000, 48000));
//		debug();
//		return;
//	}
		
		int trainItem = 32;
		String DataDirectory = "RandomPieces_100";
		String trainFilename = "ids100_"+trainItem+".data";
		
		String InputTrainingFilePath = DataDirectory + File.separatorChar + trainFilename;
		DataSet data = DataSetReader.readTrainingSet(InputTrainingFilePath);
		
		System.out.println("Precalc...");
		
		long startTime   = System.currentTimeMillis();
		
		DistanceCache dc = new DistanceCache(data);
		
		long endCache   = System.currentTimeMillis();
		
		long totalCacheTime = endCache - startTime;
		long timea = System.currentTimeMillis();
		
		

		System.out.println("Total Cache: " + totalCacheTime);
		double error = 0.0;
		for (int i = 0; i < data.size(); i++) {
			DataPacket obj1 = data.elementAt(i);
			
			for (int j = 0; j < data.size(); j++) {		
				DataPacket obj2 = data.elementAt(j);
				
				double dist = compute_dist(obj1, obj2);
				double precalc = dc.findDistance(obj1, obj2);
				
				if(dist != precalc ){
					System.out.println(i+" -  "+j + " -> " + (int)error + " dist" + dist + " -> " + precalc) ;
					error += Math.abs(dist-precalc);
//					break;
				}
				
			}
			
			if(i % 1000 == 0){
				long timeb = System.currentTimeMillis();
				System.out.println((i/data.size())+"\ti " +i + " / " + data.size() + "\t int: " + (timeb-timea));
				timea=timeb;
			}
		}
		
		long endTime   = System.currentTimeMillis();
		
		long totalTime = endTime - startTime;
		
		System.out.println("Total Cache: " + totalCacheTime);
		System.out.println("Total Runtime: " + (endTime - endCache));
		
		System.out.println("Done " + error);
	}
	
	
	public static int getIndex(int row, int col){
	
		if(row < col){
			int temp = row;
			row = col;
			col = temp;
		}

		row = row+1;
		col = col+1;
		long side = row-1;
		
		long unused = ((side)*(side)/2)- side/2;	
		int ind = (int) (side*side - unused + col);
		return ind-1;
	}

}
