package data;

public class MinHeap extends Heap {

	
	
	public void pushDown(int i) {
        int left = left(i);
        int right = right(i);
        int largest = i;

        if (left < heap.size() && isGreaterOrEqual(largest, left)) {
            largest = left;
        }
        if (right < heap.size() && isGreaterOrEqual(largest, right)) {
            largest = right;
        }

        if (largest != i) {
            swap(largest, i);
            pushDown(largest);
        }
    }

    public void pushUp(int i) {
        while (i > 0 && isGreaterOrEqual(parent(i), i)) {
            swap(parent(i), i);
            i = parent(i);
        }
    }

	public static void main(String[] args) {
//		MinHeap h = new MinHeap() {
//            protected boolean isGreaterOrEqual(int first, int last) {
//                return ((Integer)get(first)).intValue() >= ((Integer)get(last)).intValue();
//            }
//        };
//
//        for (int i = 0; i < 100; i++) {
//            h.push(new Integer((int)(100 * Math.random())));
//        }
//
//        System.out.println(h+"\n");
//
//        while (h.size() > 0) {
//            System.out.println(h.pop());
//        }
	}

	
}
