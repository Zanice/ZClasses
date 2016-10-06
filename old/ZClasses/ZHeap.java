package ZClasses;

public class ZHeap<T extends Comparable<T>> {
	T[] heap;
	int size;
	boolean maxheap;
	
	public ZHeap(boolean maximumHeap) {
		this(maximumHeap, 10);
	}
	
	public ZHeap(boolean maximumHeap, int startsize) {
		heap = (T[]) new Comparable[10];
		size = 0;
		maxheap = maximumHeap;
	}
	
	public void add(T element) {
		if (size == heap.length)
			resize(size * 2);
		
		heap[size] = element;
		siftUp(size++);
	}
	
	public boolean canPull() {
		return heap[0] != null;
	}
	
	public T pull() {
		T pulled = heap[0];
		heap[0] = null;
		floatUp(0);
		size--;
		return pulled;
	}
	
	private int parentIndexOf(int index) {
		return ((index + 1) / 2) - 1;
	}
	
	private int leftChildIndexOf(int index) {
		return (index * 2) + 1;
	}
	
	private int rightChildIndexOf(int index) {
		return (index * 2) + 2;
	}
	
	private void siftUp(int index) {
		if (index == 0)
			return;
		
		int comparison = maxheap ? heap[index].compareTo(heap[parentIndexOf(index)]) : heap[parentIndexOf(index)].compareTo(heap[index]);
		if (comparison > 0) {
			T temp = heap[parentIndexOf(index)];
			heap[parentIndexOf(index)] = heap[index];
			heap[index] = temp;
			
			siftUp(parentIndexOf(index));
		}
	}
	
	private void floatUp(int index) {
		if (leftChildIndexOf(index) > size - 1) {
			if (index < size - 1) {
				heap[index] = heap[size - 1];
				heap[size - 1] = null;
				siftUp(index);
			}
		}
		else if (leftChildIndexOf(index) == size - 1) {
			heap[index] = heap[leftChildIndexOf(index)];
			heap[leftChildIndexOf(index)] = null;
			floatUp(leftChildIndexOf(index));
		}
		else {
			int comparison = maxheap ? heap[leftChildIndexOf(index)].compareTo(heap[rightChildIndexOf(index)]) : heap[rightChildIndexOf(index)].compareTo(heap[leftChildIndexOf(index)]);
			int floatingIndex = comparison > 0 ? leftChildIndexOf(index) : rightChildIndexOf(index);
			heap[index] = heap[floatingIndex];
			heap[floatingIndex] = null;
			
			floatUp(floatingIndex);
		}
	}
	
	private void resize(int i) {
		T[] temp = heap;
		heap = (T[]) new Comparable[i];
		int j = 0;
		while ((j < i)&&(j < temp.length)) {
			heap[j] = temp[j];
			j++;
		}
		
		if (size >= heap.length)
			size = heap.length - 1;
	}
	
	private int find(T element) {
		for (int i = 0; i < size; i++) {
			if (heap[i].equals(element))
				return i;
		}
		return -1;
	}
}
