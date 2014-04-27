package optimizations;

import data.DataPacket;
import data.DataSet;

public class KMeansLSHResult {
	DataPacket[] centroids;
	DataSet[] index;
	int probecount;
	
	public KMeansLSHResult(DataPacket[] centroids,DataSet[] index, int probecount) {
		this.centroids = centroids;
		this.index = index;
		this.probecount = probecount;
	}
}
