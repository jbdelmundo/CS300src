package data;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public  class Heap  {

	/**
	 * Abstact class of Max-Heap
	 */
	protected List<HeapObject> heap;

    public Heap() {
        heap = new ArrayList<HeapObject>();
    }

    public void push(HeapObject obj) {
    	obj.heapPosition = heap.size();			//add position upon pushing
        heap.add(obj);
        pushUp(heap.size()-1);
    }

    public HeapObject pop() {
        if (heap.size() > 0) {
            swap(0, heap.size()-1);
            HeapObject result = heap.remove(heap.size()-1);
            pushDown(0);
            result.heapPosition = -1;
            return result;
        } else {
            return null;
        }
    }

    public HeapObject getFirst() {
        return heap.get(0);
    }

    public HeapObject get(int index) {
        return heap.get(index);
    }

    public int size() {
        return heap.size();
    }
    
       
    
    /**
     * 
     * @param first
     * @param last
     * @return Returns true if the first parameter is greater than or equal to the last
     */
    protected boolean isGreaterOrEqual(int first, int last){
    	return heap.get(first).compareTo(heap.get(last)) > 0;
    }

    protected int parent(int i) {
        return (i - 1) / 2;
    }

    protected int left(int i) {
        return 2 * i + 1;
    }

    protected int right(int i) {
        return 2 * i + 2;
    }

    protected void swap(int i, int j) {
        HeapObject obj_i = heap.get(i);
        HeapObject obj_j = heap.get(j);
        
        //dont forget to swap the heapPositon variables too! :)
        obj_i.heapPosition = i;
        obj_j.heapPosition = j;
        
        heap.set(i, obj_j);
        heap.set(j, obj_i);
        
    }
    
   
    public void pushDown(int i) {
        int left = left(i);
        int right = right(i);
        int largest = i;

        if (left < heap.size() && !isGreaterOrEqual(largest, left)) {
            largest = left;
        }
        if (right < heap.size() && !isGreaterOrEqual(largest, right)) {
            largest = right;
        }

        if (largest != i) {
            swap(largest, i);
            pushDown(largest);
        }
    }

    public void pushUp(int i) {
        while (i > 0 && !isGreaterOrEqual(parent(i), i)) {
            swap(parent(i), i);
            i = parent(i);
        }
    }
    
    public boolean isEmpty(){
    	return heap.isEmpty();
    }
    
    public void pushDown(HeapObject o){
		pushDown(o.heapPosition);
	}
    public void pushUp(HeapObject o){
		pushUp(o.heapPosition);
	}

    public String toString() {
        StringBuffer s = new StringBuffer("Heap:\n");
        int rowStart = 0;
        int rowSize = 1;
        for (int i = 0; i < heap.size(); i++) {
            if (i == rowStart+rowSize) {
                s.append('\n');
                rowStart = i;
                rowSize *= 2;
            }
            s.append(get(i));
            s.append(" ");
        }
        return s.toString();
    }

    public static void main(String[] args){
//        Heap h = new Heap() {
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
