package optimizations;

import java.util.Random;

import data.DataPacket;
import data.DataSet;

public class ParallelKMeansThread extends Thread {

	
	public static Object monitor;
	
	
	int id; 
	static int idCtr = 1;


	//cluster params
	public static DataPacket sample[];
	public static DataPacket centroids[];
	public static DataPacket adjustmentCentroids[];
	public static int N[];
	
	
	//index params
	public static DataSet D;
	public static DataSet[] index;
	public static int ver[];
	
	

	public int startIndex;
	public int endIndex;
	
	
	public static double samplePercentage;
	
	public static int threadTask;

	public static final int SAMPLE = 1;
	public static final int NEAREST_CENTROID = 2;
	public static final int INDEX = 3;
	public static final int QUERY = 4;

	public ParallelKMeansThread() {
		this.id = idCtr;
		idCtr++;
	}
	
	public static void setClusterParameters(DataPacket sample[],DataPacket centroids[],DataPacket adjustmentCentroids[],int N[]){
		ParallelKMeansThread.sample = sample;
		ParallelKMeansThread.centroids = centroids;
		ParallelKMeansThread.N = N;
		ParallelKMeansThread.adjustmentCentroids = adjustmentCentroids;
	}
	
	public static void setIndexParameters(DataSet D,DataPacket centroids[],DataSet[] index){
		ParallelKMeansThread.D = D;
		ParallelKMeansThread.centroids = centroids;
		ParallelKMeansThread.index = index;
	}



	@Override
	public void run() {
		System.out.println(id+" Thread Start");
		switch (threadTask) {
		case SAMPLE:
			
			break;

		case NEAREST_CENTROID:
			nearestCentroid();
			break;

		case INDEX:
			index();
			break;
		case QUERY:

			break;
		default:
			break;
		}
		System.out.println(id+ "Thread End");
		this.interrupt();
	}
	
	
	public void index(){
		for (int i = startIndex; i < endIndex; i++) {
			DataPacket p = D.elementAt(i);
			
			
			int centroidIndex = -1;
			double centroidDistance = Double.MAX_VALUE;
			//find the nearest centroid
			for (int j2 = 0; j2 < centroids.length; j2++) {
//				double cdist = ParallelKMeans.kmeansIndexdist(centroids[j2], p);
				double cdist = ParallelKMeans.kmeansdist(centroids[j2], p);				
				if(cdist < centroidDistance){
					centroidIndex = j2;
					centroidDistance = cdist;
				}
			}
			
			synchronized (index[centroidIndex]) {
				index[centroidIndex].add(p);
				
			}
			
		}
	}
	

	
	
	/**
	 * Finds nearest centroid of each sample
	 */
	public void nearestCentroid(){
//		System.out.println("search "+ startIndex + "  " + endIndex);
		for (int j = startIndex; j < endIndex; j++) {
			
			int centroidIndex = -1;
			double centroidDistance = Double.MAX_VALUE;
			//find the nearest centroid
			for (int j2 = 0; j2 < centroids.length; j2++) {
//				double cdist = ParallelKMeans.kmeansdist(centroids[j2], sample[j]);
				double cdist = ParallelKMeans.kmeansdist(centroids[j2], sample[j]);
				if(cdist < centroidDistance){
					centroidIndex = j2;
					centroidDistance = cdist;
				}
			}
			
			//assign to the nearest centroid			but do not update yet
			addToCentroid(adjustmentCentroids, centroidIndex,sample[j]);
			
		}
		
	}
	
	private void addToCentroid(DataPacket adjustments[], int centroidIndex ,DataPacket adjustment){
		synchronized (monitor) {
			if(adjustments[centroidIndex] == null){
				adjustments[centroidIndex] = new DataPacket(adjustment);
			}else{
				DataPacket centroid = adjustments[centroidIndex];
				for (int i = 0; i < centroid.ContinuousAttr.length; i++) {
					centroid.ContinuousAttr[i] += adjustment.ContinuousAttr[i];
				}
				
			}
			N[centroidIndex]++;
		}
		
	}
	
	
}
