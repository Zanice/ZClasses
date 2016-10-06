package ZClasses;

public class ZStack<T> {
	T[] stack;
	int size;
	
	public ZStack() {
		this(10);
	}
	
	public ZStack(int initialSize) {
		stack = (T[]) new Object[initialSize];
	}
	
	public void add(T element) {
		if (size == stack.length)
			resize(size * 2);
		
		stack[size++] = element;
	}
	
	public T peek() {
		if (size == 0)
			return null;
		return stack[size - 1];
	}
	
	public boolean canPop() {
		return peek() != null;
	}
	
	public T pop() {
		T element = stack[size - 1];
		stack[--size] = null;
		return element;
	}
	
	public boolean contains(T t) {
		for (int i = 0; i < size; i++) {
			if (stack[i].equals(t))
				return true;
		}
		return false;
	}
	
	private void resize(int i) {
		T[] temp = stack;
		stack = (T[]) new Comparable[i];
		int j = 0;
		while ((j < i)&&(j < temp.length)) {
			stack[j] = temp[j];
			j++;
		}
		
		if (size >= stack.length)
			size = stack.length - 1;
	}
}
