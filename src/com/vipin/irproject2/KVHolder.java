package com.vipin.irproject2;

/**
 * Holder class for holding the value and index of the list and value
 * 
 * @author infinity
 *
 */
public class KVHolder {
	private int index;
	private int value;

	/**
	 * Constructor
	 * 
	 * @param value
	 * @param index
	 */
	public KVHolder(Integer value, int index) {
		this.index = index;
		this.value = value;
	}

	/*
	 * Getters and Setters
	 */

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
