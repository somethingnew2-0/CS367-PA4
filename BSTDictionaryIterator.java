///////////////////////////////////////////////////////////////////////////////
//Main Class File:  WordCloudGenerator.java
//File:             BSTDictionaryIterator.java
//Semester:         Fall 2011
//
//Author:           Peter Collins pmcollins2@wisc.edu
//CS Login:         pcollins
//Lecturer's Name:  Beck Hasti
//Lab Section:      NA
//
///////////////////////////////////////////////////////////////////////////////

import java.util.*;

/**
 * BSTDictionaryIterator implements an iterator for a binary search tree (BST)
 * implementation of a Dictionary. The iterator iterates over the tree in order
 * of the key values (from smallest to largest).
 * 
 * <p>
 * Bugs: none known
 * 
 * @author Peter Collins
 */
public class BSTDictionaryIterator<K extends Comparable<K>> implements
		Iterator<K> {
	private Stack<BSTnode<K>> stack; // The stack containing the BSTDictionary
										// tree so in is iterated inorder

	/**
	 * Constructs an iterator for the BSTDictionary
	 * 
	 * @param root
	 *            the root the BSTDictionary to iterate
	 */
	public BSTDictionaryIterator(BSTnode<K> root) {
		stack = new Stack<BSTnode<K>>();

		// Push nodes from the root to the farthest left node
		BSTnode<K> n = root;
		while (n != null) {
			stack.push(n);
			n = n.getLeft();
		}

	}

	public boolean hasNext() {
		// If the stack is empty, there are no nodes left
		return !stack.isEmpty();
	}

	public K next() {
		BSTnode<K> returnNode = stack.pop();

		// Push nodes from the popped node to it's inorder successor
		if (returnNode.getRight() != null) {
			BSTnode<K> n = returnNode.getRight();
			while (n != null) {
				stack.push(n);
				n = n.getLeft();
			}
		}

		return returnNode.getKey();
	}

	public void remove() {
		// DO NOT CHANGE: you do not need to implement this method
		throw new UnsupportedOperationException();
	}
}
