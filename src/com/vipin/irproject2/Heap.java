package com.vipin.irproject2;

import java.util.Comparator;

@SuppressWarnings("unchecked")
public class Heap<AnyType> {
	private static final int CAPACITY = 2;

	private int size; // Number of elements in heap
	private AnyType[] heap; // The heap array
	private Comparator<AnyType> comparator;
	private int count = 0;

	public Heap(Comparator<AnyType> comparator) {
		this.comparator = comparator;
		size = 0;
		heap = (AnyType[]) new Object[CAPACITY];
	}

	private void percolatingDown(int k) {
		AnyType tmp = heap[k];
		int child;

		for (; 2 * k <= size; k = child) {
			child = 2 * k;

			if (child != size && comparator.compare(heap[child], heap[child + 1]) > 0) {
				child++;
			}
			count++;

			if (comparator.compare(tmp, heap[child]) > 0) {
				count++;
				heap[k] = heap[child];
			} else {
				break;
			}
		}
		heap[k] = tmp;
	}

	/**
	 * Deletes the top item
	 */
	public AnyType deleteMin() throws RuntimeException {
		if (size == 0)
			throw new RuntimeException();
		AnyType min = heap[1];
		heap[1] = heap[size--];
		percolatingDown(1);
		return min;
	}

	/**
	 * checks if the heap is empty or not
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Inserts a new item
	 */
	public void insert(AnyType x) {
		if (size == heap.length - 1)
			doubleSize();

		// Insert a new item to the end of the array
		int pos = ++size;

		// Percolate up
		for (; pos > 1 && comparator.compare(x, heap[pos / 2]) < 0; pos = pos / 2) {
			heap[pos] = heap[pos / 2];
			count++;
		}
		heap[pos] = x;
	}

	public int getCount() {
		return count;
	}

	private void doubleSize() {
		AnyType[] old = heap;
		heap = (AnyType[]) new Object[heap.length * 2];
		System.arraycopy(old, 1, heap, 1, size);
	}

	/*
	 * public String toString() { String out = ""; for (int k = 1; k <= size;
	 * k++) out += heap[k] + " "; return out; }
	 */
}
