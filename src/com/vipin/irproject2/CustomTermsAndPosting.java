package com.vipin.irproject2;

/**
 * Holder Class for the keep the term with its frequency and postings list
 * 
 * @author infinity
 *
 */
public class CustomTermsAndPosting {
	private String termString;
	private CustomPostingsLinkedList<Integer> customPostingsList;
	private int count;

	/*
	 * Getters and Setters
	 */
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getTermString() {
		return termString;
	}

	public void setTermString(String termString) {
		this.termString = termString;
	}

	public CustomPostingsLinkedList<Integer> getCustomPostingsList() {
		return customPostingsList;
	}

	public void setCustomPostingsList(CustomPostingsLinkedList<Integer> customPostingsList) {
		this.customPostingsList = customPostingsList;
	}
}
