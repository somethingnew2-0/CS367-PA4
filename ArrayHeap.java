///////////////////////////////////////////////////////////////////////////////
//Main Class File:  WordCloudGenerator.java
//File:             ArrayHeap.java
//Semester:         Fall 2011
//
//Author:           Peter Collins pmcollins2@wisc.edu
//CS Login:         pcollins
//Lecturer's Name:  Beck Hasti
//Lab Section:      NA
//
///////////////////////////////////////////////////////////////////////////////

import java.util.NoSuchElementException;

/**
 * ArrayHeap implements a Heap out of an Array for an implementation of a
 * Priority Queue.
 * 
 * <p>
 * Bugs: none known
 * 
 * @author Peter Collins
 */
public class ArrayHeap<E extends Prioritizable> implements PriorityQueueADT<E> {

	// default number of items the heap can hold before expanding
	private static final int INIT_SIZE = 100;

	private E[] data; // The data heap stored in an array
	private int size; // The representation of the current heap size

	/**
	 * Constructs the array heap with no parameters
	 */
	public ArrayHeap() {
		// Call the other constructor with the INIT_SIZE parameter
		this(INIT_SIZE);
	}

	/**
	 * Constructs the array heap with a given initial size for the array
	 * 
	 * @param initialSize
	 *            the size of the array to initialize
	 */
	public ArrayHeap(int initialSize) {
		// If the initial size is less than 0 throw an exception
		if (initialSize < 0) {
			throw new IllegalArgumentException();
		}
		// Initial the array and size
		data = (E[]) (new Prioritizable[initialSize + 1]);
		size = 0;
	}

	public E getMax() {
		// Make sure the heap has values else return the root
		if (size == 0) {
			throw new NoSuchElementException();
		}
		return data[1];
	}

	public void insert(E item) {
		// If we are at the max capacity of the array, grow the array
		if (data.length == size + 1) {
			// Create a new array twice the size
			E[] newData = (E[]) new Prioritizable[data.length * 2];
			// Copy the old array to the new bigger array
			System.arraycopy(data, 0, newData, 0, data.length);
			// Set the new array as the data reference
			data = newData;
		}
		// Put the item in the next spot in the array/heap
		data[++size] = item;

		// Heapify by swaping the value up
		int child = size;
		while (data[child / 2] != null
				&& data[child / 2].getPriority() < data[child].getPriority()) {
			// Swap the value up because the parent is less
			E temp = data[child / 2];
			data[child / 2] = data[child];
			data[child] = temp;

			// Do we need to swap again?
			child = child / 2;
		}
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public E removeMax() {
		// Make sure the heap has values
		if (size == 0) {
			throw new NoSuchElementException();
		}

		// Save the root as the value to return
		E returnedMax = data[1];

		// Set the last child as the root
		data[1] = data[size];
		// Set the old last child as null
		data[size--] = null;

		// Heapify by swapping down
		int parent = 1;
		while (parent * 2 < data.length
				&& data[parent * 2] != null
				&& data[parent * 2 + 1] != null
				&& (data[parent * 2].getPriority() > data[parent].getPriority() || data[parent * 2 + 1]
						.getPriority() > data[parent].getPriority())) {
			// Swap the parent with the child if the children are bigger
			E temp = data[parent];
			// If both children are bigger, pick the biggest and swap
			if (data[parent * 2].getPriority() > data[parent].getPriority()
					&& data[parent * 2 + 1].getPriority() > data[parent]
							.getPriority()) {
				if (data[parent * 2].getPriority() > data[parent * 2 + 1]
						.getPriority()) {
					// The left is bigger, swap with the parent
					data[parent] = data[parent * 2];
					data[parent * 2] = temp;
					parent *= 2;
				} else {
					// The right is bigger, swap with the parent
					data[parent] = data[parent * 2 + 1];
					data[parent * 2 + 1] = temp;
					parent = parent * 2 + 1;
				}

			} else if (data[parent * 2].getPriority() > data[parent]
					.getPriority()) {
				// Only the left child is bigger swap with the parent
				data[parent] = data[parent * 2];
				data[parent * 2] = temp;
				parent *= 2;
			} else {
				// Only the right child is bigger, swap with the parent
				data[parent] = data[parent * 2 + 1];
				data[parent * 2 + 1] = temp;
				parent = parent * 2 + 1;
			}
		}

		// Return the original root
		return returnedMax;
	}

	public int size() {
		return size;
	}
}
