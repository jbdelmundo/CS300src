package clustering;

import java.io.FileNotFoundException;

import controller.IDS;
import data.DataPacket;
import data.DataSet;
import data.MinHeap;
import fileIO.DataPacketWriter;


/**
 * Receives the dataset and writes a file based on the given DataPacketWriter 
 * @author Test
 *
 */
public class OPTICSAlgorithm {
	
	public static final int UNDEFINED = -1;
	int processed = 0;

	public void OPTICS(DataSet setOfObjects, double epsilon, int minPts, DataPacketWriter OrderedFile){
		
		
				
		for (int i = 0; i < setOfObjects.size(); i++) {
			DataPacket obj = setOfObjects.elementAt(i);
			
			if(!obj.isProcessed){				
				ExpandClusterOrder(setOfObjects,obj, epsilon, minPts,OrderedFile);
				
//				if(i % 1000 == 0 )
//					System.out.println((processed*1.0/setOfObjects.size())+"%\t Point " + i + " of " +setOfObjects.size() + ": " +processed);
			}
			
		}
		OrderedFile.close();
		
		System.out.println("Optics Algorithm: DONE");
	}
	
	
	public void ExpandClusterOrder(DataSet setOfObjects, DataPacket obj, double epsilon, int minPts, DataPacketWriter OrderedFile){
		
		
//		DataSet neighbors = NearestNeighborCompute.findNeighborsAndCoreDist(setOfObjects, obj, minPts,epsilon);
		DataSet neighbors = NearestNeighborCompute.findNeighbors(setOfObjects, obj, epsilon);
		
//		if(neighbors.size() != setOfObjects.size()-1){
//			System.err.println("Neighborhood " + neighbors.size());
//			System.err.println("Data " + setOfObjects.size());
//			
//			System.out.println("Eps " + epsilon);
//			
//			
//			System.exit(1);
//		}
		
		obj.isProcessed = true;
		processed++;
		obj.reachability_dist = UNDEFINED *1000;
		obj.core_dist = getCoreDistance(neighbors,obj, epsilon, minPts);
		
		// OrderedFile.append(obj.toString());
		OrderedFile.printReachabilityPacketToFile(obj);
		
		
		MinHeap orderedSeeds = new MinHeap(setOfObjects.size());	
	
		
//		System.out.println("NeighborhoodSize:" + neighbors.size() );
		

		if(obj.core_dist != UNDEFINED){
			updateSeeds(orderedSeeds,neighbors, obj);
			
			while(!orderedSeeds.isEmpty()){
//				System.out.println("Seedlist " + orderedSeeds.size() + "\t Procesed:"+processed+"/"+setOfObjects.size());
				
				
				DataPacket currentSeed = (DataPacket) orderedSeeds.pop() ;// get the least element
//				DataSet neighbors2 = NearestNeighborCompute.findNeighborsAndCoreDist(setOfObjects, currentSeed,minPts, epsilon);	//be sure to set
				DataSet neighbors2 = NearestNeighborCompute.findNeighbors(setOfObjects, currentSeed, epsilon);	//be sure to set
				currentSeed.isProcessed = true;
				processed++;
				currentSeed.core_dist = getCoreDistance(neighbors2, currentSeed, epsilon, minPts);
				
				OrderedFile.printReachabilityPacketToFile(currentSeed);
				
				
				if(currentSeed.core_dist != UNDEFINED){
					updateSeeds(orderedSeeds,neighbors2, currentSeed);
				}
				
				if(processed % 1000 == 0)System.out.println((processed*1.0/setOfObjects.size())+"%\t"+"   Seedlist" + orderedSeeds.size() + "\t Procesed:"+processed+"/"+setOfObjects.size());
			}
			
		}else{
//			System.err.println("Undefined core dist");
		}
	}//end expandCluster
	

	/**
	 * Updates the reachability values of the seeds based on the current center object and adds its unprocessed neighbors 
	 * @param orderedSeeds
	 * @param neighbors
	 * @param centerObj
	 */
	public void updateSeeds(MinHeap orderedSeeds, DataSet neighbors, DataPacket centerObj){
		double c_dist = centerObj.core_dist;
		
		for (DataPacket obj : neighbors) {
			if(!obj.isProcessed){
				double new_r_dist;
				
				if(obj.neighborID == centerObj.DataPacketID && false){
					new_r_dist = Math.max(c_dist, obj.distToNeighbor);
				}else{
					new_r_dist =  Math.max(c_dist, computeDistance(centerObj,obj));
				}
				
				if(obj.reachability_dist < 0 ){		//undefined
					obj.reachability_dist = new_r_dist;
					orderedSeeds.push(obj);
				}else{
					if(new_r_dist < obj.reachability_dist){
						obj.reachability_dist = new_r_dist;
						orderedSeeds.pushDown(obj);
					}
				}
			}
		}
		//check
	}
	
	
	public double getCoreDistance(DataSet neighbors, DataPacket obj, double epsilon, int minPts){
		
		if(neighbors.size() < minPts)
			return UNDEFINED;
		
		//return nearest neighbor distance
		double core_dist = UNDEFINED;
		
		for (DataPacket neighbor : neighbors) {
			double neighbor_dist = computeDistance(obj, neighbor);
			if(core_dist == UNDEFINED ){
				core_dist = neighbor_dist;
			}else{
				core_dist = Math.min(core_dist, neighbor_dist);
			}
		}
		return core_dist;		
	}
	
	public double computeDistance(DataPacket obj1, DataPacket obj2){
		
		if(IDS.useDistCache){
			return  IDS.distCache.findDistance(obj1, obj2);
		}else{
			return  NearestNeighborCompute.findDistance(obj1, obj2);
		}
		
	}
	
	
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
	}
	
	
}
