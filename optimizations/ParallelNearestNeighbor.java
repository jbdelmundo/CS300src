package optimizations;

import java.util.HashMap;

import clustering.NearestNeighborCompute;

import data.DataPacket;
import data.DataSet;

/**
 * Distance is not efficient. Out of memory error encountered
 * Neighborbuffer is not efficient. Out of memory error encountered
 * 
 *
 */
public class ParallelNearestNeighbor {
	
	DataSet neighbors;
	
	public static DataSet findNeighbors(DataSet dataset, DataPacket object,
			double epsilon){
		ParallelNearestNeighbor par = new ParallelNearestNeighbor();
		Runtime.getRuntime().gc(); 	//garbage collect
		return par.findNeighborsParallel(dataset, object, epsilon);
	} 
	
	public  DataSet findNeighborsParallel(DataSet dataset, DataPacket object,
			double epsilon) {

		neighbors = new DataSet();
		
		int processors = Runtime.getRuntime().availableProcessors();
		
		NearestNeighborThread workers[] = new NearestNeighborThread[processors];
		
		int tasksize = dataset.size()/processors;
		
		for (int i = 0; i < workers.length; i++) {
			int start = i*tasksize;
			int end = start+tasksize;
			if(i == processors -1 )
				end = dataset.size();
			workers[i] = new NearestNeighborThread(object, dataset, this, start, end, epsilon);
		}
		
		
		
		for (int i = 0; i < workers.length; i++) {
			workers[i].start();		
		}
		
		for (int i = 0; i < workers.length; i++) {
			try {
				workers[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		return neighbors;
	}
	
	public synchronized void addNeighbor(DataPacket dataPacket){
		neighbors.add(dataPacket);
	}
	
	

}
