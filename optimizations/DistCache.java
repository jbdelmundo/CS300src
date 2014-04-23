package optimizations;

import java.util.HashMap;

public class DistCache {
	static double dist[];
	
	public static long DistCache2(long elements){
//		return  ( ( (elements * elements) - elements)/ 2 ) + elements;
		return (long) (( (Math.pow(elements,2) - elements)/2)+ elements);
		
	}
	
	public static void main(String[] args) {
		int size = (int) DistCache2(52950);
		int max = 1401877725;
		System.out.println("Size:" +size);
		System.out.println("Size:" +Integer.MAX_VALUE);
		max = size;
		HashMap<String, Integer> map= new HashMap<String, Integer>(max+100, (float) 0.99999999999999999);
		for (int i = 0; i < max; i++) {
			map.put(i+"",  i);
			if(i % 10000000 ==0){
				System.out.println(i*1.0/max+ "\t"+i);
			}
		}
		dist=  new double[size];
		System.out.println("done");
	}
}
