package com.vipin.irproject2;

import java.util.*;

/**
 * A Linked List class with a private static inner Node class
 * 
 * @author infinity
 *
 * @param <Integer>
 */
public class CustomPostingsLinkedList<Integer> implements Iterable<Integer> {
	private Node<Integer> head;

	private int size;

	/**
	 * Constructs an empty list
	 */
	public CustomPostingsLinkedList() {
		head = null;
	}

	/**
	 * Add a list of items
	 * 
	 * @param list
	 */
	public void addArrayOfElements(List<Integer> list) {
		for (int i = 0; i < list.size(); i++) {
			addLast(list.get(i));
		}
	}

	/**
	 * Returns true if the list is empty
	 *
	 */
	public boolean isEmpty() {
		return head == null;
	}

	/**
	 * Inserts a new node at the beginning of this list.
	 *
	 */
	public void addFirst(Integer item) {
		head = new Node<Integer>(item, head);
	}

	/**
	 * Returns the first element in the list.
	 *
	 */
	public Integer getFirst() {
		if (head == null)
			throw new NoSuchElementException();

		return head.data;
	}

	/**
	 * Removes the first element in the list.
	 *
	 */
	public Integer removeFirst() {
		Integer tmp = getFirst();
		head = head.next;
		return tmp;
	}

	/**
	 * Inserts a new node to the end of this list.
	 *
	 */
	public void addLast(Integer item) {
		if (head == null)
			addFirst(item);
		else {
			Node<Integer> tmp = head;
			while (tmp.next != null)
				tmp = tmp.next;

			tmp.next = new Node<Integer>(item, null);
		}
	}

	/**
	 * Returns the last element in the list.
	 *
	 */
	public Integer getLast() {
		if (head == null)
			throw new NoSuchElementException();

		Node<Integer> tmp = head;
		while (tmp.next != null)
			tmp = tmp.next;

		return tmp.data;
	}

	/**
	 * Removes all nodes from the list.
	 *
	 */
	public void clear() {
		head = null;
	}

	/**
	 * Returns true if this list contains the specified element.
	 *
	 */
	public boolean contains(Integer x) {
		for (Integer tmp : this)
			if (tmp.equals(x))
				return true;

		return false;
	}

	/**
	 * Returns the data at the specified position in the list.
	 *
	 */
	public Integer get(int pos) {
		if (head == null)
			throw new IndexOutOfBoundsException();

		Node<Integer> tmp = head;
		for (int k = 0; k < pos; k++)
			tmp = tmp.next;

		if (tmp == null)
			throw new IndexOutOfBoundsException();

		return tmp.data;
	}

	/**
	 * Returns a string representation
	 *
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (Object x : this)
			result.append(x + " ");

		return result.toString().trim();
	}

	/**
	 * Inserts a new node after a node containing the key.
	 *
	 */
	public void insertAfter(Integer key, Integer toInsert) {
		Node<Integer> tmp = head;

		while (tmp != null && !tmp.data.equals(key))
			tmp = tmp.next;

		if (tmp != null)
			tmp.next = new Node<Integer>(toInsert, tmp.next);
	}

	/**
	 * Inserts a new node before a node containing the key.
	 *
	 */
	public void insertBefore(Integer key, Integer toInsert) {
		if (head == null)
			return;

		if (head.data.equals(key)) {
			addFirst(toInsert);
			return;
		}

		Node<Integer> prev = null;
		Node<Integer> cur = head;

		while (cur != null && !cur.data.equals(key)) {
			prev = cur;
			cur = cur.next;
		}
		// insert between cur and prev
		if (cur != null)
			prev.next = new Node<Integer>(toInsert, cur);
	}

	/**
	 * Removes the first occurrence of the specified element in this list.
	 *
	 */
	public void remove(Integer key) {
		if (head == null)
			throw new RuntimeException("cannot delete");

		if (head.data.equals(key)) {
			head = head.next;
			return;
		}

		Node<Integer> cur = head;
		Node<Integer> prev = null;

		while (cur != null && !cur.data.equals(key)) {
			prev = cur;
			cur = cur.next;
		}

		if (cur == null)
			throw new RuntimeException("cannot delete");

		// delete cur node
		prev.next = cur.next;
	}

	/**
	 * Returns a deep copy of the list Complexity: O(n^2)
	 */
	public CustomPostingsLinkedList<Integer> copy1() {
		CustomPostingsLinkedList<Integer> twin = new CustomPostingsLinkedList<Integer>();
		Node<Integer> tmp = head;
		while (tmp != null) {
			twin.addLast(tmp.data);
			tmp = tmp.next;
		}

		return twin;
	}

	/**
	 * Returns a deep copy of the list Complexity: O(n)
	 */
	public CustomPostingsLinkedList<Integer> copy2() {
		CustomPostingsLinkedList<Integer> twin = new CustomPostingsLinkedList<Integer>();
		Node<Integer> tmp = head;
		while (tmp != null) {
			twin.addFirst(tmp.data);
			tmp = tmp.next;
		}

		return twin.reverse();
	}

	/**
	 * Returns size of List
	 * 
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets size of List
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Reverses the list Complewxity: O(n)
	 */
	public CustomPostingsLinkedList<Integer> reverse() {
		CustomPostingsLinkedList<Integer> list = new CustomPostingsLinkedList<Integer>();
		Node<Integer> tmp = head;
		while (tmp != null) {
			list.addFirst(tmp.data);
			tmp = tmp.next;
		}
		return list;
	}

	/**
	 * Returns a deep copy of the immutable list It uses a tail reference.
	 * Complexity: O(n)
	 */
	public CustomPostingsLinkedList<Integer> copy3() {
		CustomPostingsLinkedList<Integer> twin = new CustomPostingsLinkedList<Integer>();
		Node<Integer> tmp = head;
		if (head == null)
			return null;
		twin.head = new Node<Integer>(head.data, null);
		Node<Integer> tmpTwin = twin.head;
		while (tmp.next != null) {
			tmp = tmp.next;
			tmpTwin.next = new Node<Integer>(tmp.data, null);
			tmpTwin = tmpTwin.next;
		}

		return twin;
	}

	/*******************************************************
	 *
	 * The Node class
	 *
	 ********************************************************/
	private static class Node<Integer> {
		private Integer data;
		private Node<Integer> next;

		public Node(Integer data, Node<Integer> next) {
			this.data = data;
			this.next = next;
		}
	}

	/*******************************************************
	 *
	 * The Iterator class
	 *
	 ********************************************************/

	public Iterator<Integer> iterator() {
		return new LinkedListIterator();
	}

	private class LinkedListIterator implements Iterator<Integer> {
		private Node<Integer> nextNode;

		public LinkedListIterator() {
			nextNode = head;
		}

		public boolean hasNext() {
			return nextNode != null;
		}

		public Integer next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Integer res = nextNode.data;
			nextNode = nextNode.next;
			return res;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}