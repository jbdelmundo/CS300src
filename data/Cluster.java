package data;

public class Cluster extends SteepArea implements Comparable<Cluster> {
	
	public Cluster(int start, int end){
		super();
		super.startIndex = start;
		super.endIndex = end;
	}

	@Override
	public int compareTo(Cluster o) {
//		int mysize = this.endIndex - this.startIndex + 1;
//		int othersize = o.endIndex - o.startIndex + 1;
		
		int mysize = this.size();
		int othersize = o.size();
		
		if(mysize == othersize)
			return 0;
		else if(mysize > othersize)
			return 1;
		else
			return -1;
	}
	
	public int size(){
		return this.endIndex - this.startIndex +1;
	}
}
