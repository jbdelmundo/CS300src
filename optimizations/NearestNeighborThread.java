package optimizations;

import java.util.HashMap;

import controller.IDS;

import clustering.NearestNeighborCompute;

import data.DataPacket;
import data.DataSet;

public class NearestNeighborThread extends Thread {

	DataPacket reference;
	DataSet dataset;
	int start, end;
	ParallelNearestNeighbor parent;
	double epsilon;

	/**
	 * 
	 * @param reference
	 * @param dataset
	 * @param parent
	 * @param start		//inclusive index
	 * @param end		//exclusive index
	 * @param epsilon
	 */
	public NearestNeighborThread(DataPacket reference, DataSet dataset,
			ParallelNearestNeighbor parent, int start, int end, double epsilon) {
		this.reference = reference;
		this.dataset = dataset;
		this.start = start;
		this.end = end;
		this.parent = parent;
		this.epsilon = epsilon;

	}

	@Override
	public void run() {
		int ctr;
		for (ctr = start; ctr < end; ctr++) {
			DataPacket dataPacket = dataset.elementAt(ctr);

			double dist = NearestNeighborCompute.findDistance(reference, dataPacket);
			if(IDS.useDistCache){
				dist =   IDS.distCache.findDistance(reference, dataPacket);
			}else{
				dist =   NearestNeighborCompute.findDistance(reference, dataPacket);
			}
			// System.out.println("findNeighbors:" + dist);
			if (epsilon >= dist) {
				parent.addNeighbor(dataPacket);
			}

		}
	}

}
