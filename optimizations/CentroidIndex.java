package optimizations;




public class CentroidIndex implements Comparable<CentroidIndex>{
		double dist;
		int centroidID;
		public CentroidIndex(int centroidID, double dist) {
			this.centroidID = centroidID;
			this.dist = dist;
		}
		public int compareTo(CentroidIndex o) {
			if(dist > o.dist) return -1;
			if(dist < o.dist) return 1;
			return 0;
		}
	
}
