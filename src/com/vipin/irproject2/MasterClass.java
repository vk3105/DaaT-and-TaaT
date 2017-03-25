package com.vipin.irproject2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

/**
 * This is a big big class. This is where everything tries to go wrong and you
 * have to fix them.
 * 
 * @author infinity
 *
 */
public class MasterClass {

	private static final boolean DEBUG = true;
	private Path indexFilePath;
	private FileSystem fileSystem;
	private int maxDocCount;
	private int comparisionCount;
	private File indexFile;
	private File outputFile;
	private File inputFile;
	private BufferedWriter indexOutputWriter = null;
	private BufferedWriter outputFileWriter = null;
	private BufferedReader inputFileBufferedReader = null;
	private IndexReader reader;

	private String args[] = new String[3];

	private HashMap<String, CustomTermsAndPosting> customTermsAndPostingMap = new HashMap<String, CustomTermsAndPosting>();

	/**
	 * Constructor of this class
	 * 
	 * @param args
	 */
	public MasterClass(String[] args) {
		if (DEBUG) {
			this.args[0] = "/home/infinity/Desktop/IRP2/index";
			this.args[1] = "/home/infinity/Desktop/output.txt";
			this.args[2] = "/home/infinity/Desktop/input.txt";
		} else {
			this.args = args;
		}
	}

	/**
	 * Initialize the variables used by this program
	 * 
	 * @throws IOException
	 */
	public void getIngredients() throws IOException {
		fileSystem = FileSystems.getDefault();

		indexFilePath = fileSystem.getPath(args[0]);
		reader = DirectoryReader.open(FSDirectory.open(indexFilePath));
		maxDocCount = reader.maxDoc();

		if (DEBUG) {
			indexFile = new File("/home/infinity/Desktop/example.txt");
			indexOutputWriter = new BufferedWriter(new FileWriter(indexFile));
		}

		outputFile = new File(args[1]);
		outputFileWriter = new BufferedWriter(new FileWriter(outputFile));

		inputFile = new File(args[2]);
		inputFileBufferedReader = new BufferedReader(new FileReader(inputFile));
	}

	/**
	 * Retrieves the terms and their postings list and makes a HashMap
	 * 
	 * @throws IOException
	 */
	public void cookRecipe() throws IOException {

		// Get all fields
		Fields fields = MultiFields.getFields(reader);
		Iterator<String> itr = fields.iterator();

		// Iterate thru all the fields one by one
		while (itr.hasNext()) {

			String field = itr.next();
			Terms terms = fields.terms(field);
			TermsEnum termsItr = terms.iterator();

			// Iterates the terms under a field
			if (termsItr != null) {
				if (DEBUG) {
					indexOutputWriter.write("-----------------START-----------------\n");
					indexOutputWriter.write("Field = " + field + "\n");
				}
				if (!field.equals("id") && !field.equals("_version_")) {

					while (true) {

						try {

							BytesRef termString = termsItr.next();
							PostingsEnum postingsEnum = MultiFields.getTermDocsEnum(reader, field, termString);
							if (DEBUG) {
								indexOutputWriter.write(termString.utf8ToString() + " ");
							}
							List<Integer> postingsList = createAndGetPostingsList(postingsEnum);

							addTermAndPostingList(termString.utf8ToString(), postingsList);
							if (DEBUG) {
								indexOutputWriter.write("\n");
							}
						} catch (Exception e) {
							break;
						}
					}
					if (DEBUG) {
						indexOutputWriter.write("\n-----------------END-----------------\n");

					}
				}
			}
		}
	}

	public static String removeUTF8BOM(String s) {
		if (s.startsWith("\uFEFF")) {
			s = s.substring(1);
		}
		return s;
	}

	/**
	 * Reads the input.txt file and Writes to output.txt file and do the various
	 * queries
	 * 
	 * @throws IOException
	 */
	public void serveRecipe() throws IOException {

		String termsString;

		// Read input.txt file line by line and process all the DAAT and TAAT
		// operations

		while ((termsString = inputFileBufferedReader.readLine()) != null) {

			termsString = removeUTF8BOM(termsString);

			ArrayList<CustomTermsAndPosting> termsList = sortTermsListByFrequency(termsString);

			int listSize = termsList.size();

			printTermsPostingsList(termsList);

			CustomPostingsLinkedList<Integer> temp = new CustomPostingsLinkedList<Integer>();

			// AND TAAT
			comparisionCount = 0;
			outputFileWriter.write("TaatAnd\n");
			printTerms(termsList);

			if (listSize == 1) {
				temp = termsList.get(0).getCustomPostingsList();
				temp.setSize(termsList.get(0).getCount());
			} else {
				temp = intersectListWithANDTAAT(termsList.get(0).getCustomPostingsList(),
						termsList.get(1).getCustomPostingsList());
			}
			for (int i = 2; i < listSize; i++) {
				if (temp.isEmpty()) {
					break;
				} else {
					temp = intersectListWithANDTAAT(temp, termsList.get(i).getCustomPostingsList());
				}
			}

			if (temp.getSize() != 0) {
				outputFileWriter.write("\nResults: " + temp.toString() + "\n");
				outputFileWriter.write("Number of documents in results: " + temp.getSize() + "\n");
			} else {
				outputFileWriter.write("\nResults: empty" + "\n");
				outputFileWriter.write("Number of documents in results: 0\n");
			}

			outputFileWriter.write("Number of comparisons: " + comparisionCount + "\n");

			// OR TAAT
			comparisionCount = 0;
			outputFileWriter.write("TaatOr\n");
			printTerms(termsList);

			if (listSize == 1) {
				temp = termsList.get(0).getCustomPostingsList();
				temp.setSize(termsList.get(0).getCount());
			} else {
				temp = intersectListWithORTAAT(termsList.get(0).getCustomPostingsList(),
						termsList.get(1).getCustomPostingsList());
			}
			for (int i = 2; i < listSize; i++) {
				temp = intersectListWithORTAAT(temp, termsList.get(i).getCustomPostingsList());
			}

			if (temp.getSize() != 0) {
				outputFileWriter.write("\nResults: " + temp.toString() + "\n");
			} else {
				outputFileWriter.write("\nResults: empty" + "\n");
			}
			outputFileWriter.write("Number of documents in results: " + temp.getSize() + "\n");
			outputFileWriter.write("Number of comparisons: " + comparisionCount + "\n");

			// AND DAAT
			comparisionCount = 0;
			outputFileWriter.write("DaatAnd\n");
			printTerms(termsList);

			if (listSize == 1) {
				temp = termsList.get(0).getCustomPostingsList();
				temp.setSize(termsList.get(0).getCount());
			} else {
				temp = intersectListWithDAATAND(termsList);
			}

			if (temp.getSize() != 0) {
				outputFileWriter.write("\nResults: " + temp.toString() + "\n");
				outputFileWriter.write("Number of documents in results: " + temp.getSize() + "\n");
			} else {
				outputFileWriter.write("\nResults: empty" + "\n");
				outputFileWriter.write("Number of documents in results: 0\n");
			}

			outputFileWriter.write("Number of comparisons: " + comparisionCount + "\n");

			// OR DAAT
			comparisionCount = 0;
			outputFileWriter.write("DaatOr\n");
			printTerms(termsList);

			if (listSize == 1) {
				temp = termsList.get(0).getCustomPostingsList();
				temp.setSize(termsList.get(0).getCount());
			} else {
				temp = intersectListWithDAATOR(termsList);
			}

			if (temp.getSize() != 0) {
				outputFileWriter.write("\nResults: " + temp.toString() + "\n");
			} else {
				outputFileWriter.write("\nResults: empty" + "\n");
			}
			outputFileWriter.write("Number of documents in results: " + temp.getSize() + "\n");
			outputFileWriter.write("Number of comparisons: " + comparisionCount + "\n");
		}
	}

	/**
	 * Clears all the declared fields and file input reader and output readers
	 * 
	 * @throws IOException
	 */
	public void cleanDishes() throws IOException {
		if (DEBUG) {
			indexOutputWriter.close();
		}
		outputFileWriter.close();
		inputFileBufferedReader.close();
	}

	/**
	 * Finds Intersection of two Linked List : AND operation for TAAT
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	private CustomPostingsLinkedList<Integer> intersectListWithANDTAAT(CustomPostingsLinkedList<Integer> list1,
			CustomPostingsLinkedList<Integer> list2) {
		List<Integer> finalList = new ArrayList<Integer>();
		CustomPostingsLinkedList<Integer> finalLList = new CustomPostingsLinkedList<Integer>();

		Iterator<Integer> itr1 = list1.iterator();
		Iterator<Integer> itr2 = list2.iterator();

		int doc1ID, doc2ID;
		if (list1.isEmpty() && list2.isEmpty()) {
			return null;
		} else {
			doc1ID = (int) itr1.next();
			doc2ID = (int) itr2.next();
		}
		while (true) {
			if (itr1.hasNext() && itr2.hasNext()) {
				if (doc1ID == doc2ID) {
					finalList.add(doc1ID);
					doc1ID = itr1.next();
					doc2ID = itr2.next();
				} else if (doc1ID > doc2ID) {
					doc2ID = itr2.next();
				} else {
					doc1ID = itr1.next();
				}
			} else if (!itr1.hasNext() && itr2.hasNext()) {
				if (doc2ID <= doc1ID) {
					if (doc1ID == doc2ID) {
						finalList.add(doc1ID);
						comparisionCount++;
						break;
					}
				} else {
					break;
				}
				doc2ID = itr2.next();
			} else if (itr1.hasNext() && !itr2.hasNext()) {
				if (doc1ID <= doc2ID) {
					if (doc1ID == doc2ID) {
						finalList.add(doc1ID);
						comparisionCount++;
						break;
					}
				} else {
					break;
				}
				doc1ID = itr1.next();
			} else {
				if (doc1ID == doc2ID) {
					finalList.add(doc1ID);
				}
				comparisionCount++;
				break;
			}
			comparisionCount++;
		}
		finalLList.addArrayOfElements(finalList);
		finalLList.setSize(finalList.size());
		return finalLList;
	}

	/**
	 * Finds Union of two Linked List : OR operation for TAAT
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	private CustomPostingsLinkedList<Integer> intersectListWithORTAAT(CustomPostingsLinkedList<Integer> list1,
			CustomPostingsLinkedList<Integer> list2) {
		List<Integer> finalList = new ArrayList<Integer>();

		CustomPostingsLinkedList<Integer> finalLList = new CustomPostingsLinkedList<Integer>();

		Iterator<Integer> itr1 = list1.iterator();
		Iterator<Integer> itr2 = list2.iterator();

		int doc1ID, doc2ID;
		if (list1.isEmpty() && list2.isEmpty()) {
			return null;
		} else {
			doc1ID = (int) itr1.next();
			doc2ID = (int) itr2.next();
		}
		while (true) {
			if (itr1.hasNext() && itr2.hasNext()) {
				if (doc1ID == doc2ID) {
					finalList.add(doc1ID);
					doc1ID = itr1.next();
					doc2ID = itr2.next();
				} else if (doc1ID > doc2ID) {
					finalList.add(doc2ID);
					doc2ID = itr2.next();
				} else {
					finalList.add(doc1ID);
					doc1ID = itr1.next();
				}
			} else if (!itr1.hasNext() && itr2.hasNext()) {
				if (doc1ID == doc2ID) {
					finalList.add(doc1ID);
				} else if (doc1ID > doc2ID) {
					finalList.add(doc2ID);
					finalList.add(doc1ID);
				} else {
					finalList.add(doc1ID);
					finalList.add(doc2ID);
				}
				while (itr2.hasNext()) {
					doc2ID = itr2.next();
					finalList.add(doc2ID);
				}
				break;
			} else if (itr1.hasNext() && !itr2.hasNext()) {
				if (doc1ID == doc2ID) {
					finalList.add(doc1ID);
				} else if (doc1ID > doc2ID) {
					finalList.add(doc2ID);
					finalList.add(doc1ID);
				} else {
					finalList.add(doc1ID);
					finalList.add(doc2ID);
				}
				while (itr1.hasNext()) {
					doc1ID = itr1.next();
					finalList.add(doc1ID);
				}
				break;
			} else if (!itr1.hasNext() && !itr2.hasNext()) {
				if (doc1ID == doc2ID) {
					finalList.add(doc1ID);
				} else {
					if (doc1ID > doc2ID) {
						finalList.add(doc2ID);
						finalList.add(doc1ID);
					} else {
						finalList.add(doc1ID);
						finalList.add(doc2ID);
					}
				}
				comparisionCount++;
				break;
			}
			comparisionCount++;
		}
		finalLList.addArrayOfElements(finalList);
		finalLList.setSize(finalList.size());
		return finalLList;
	}

	/**
	 * AND operation for DAAT
	 * 
	 * @param termsList
	 * @return
	 */

	/**
	 * OR operation for DAAT
	 * 
	 * @param termsList
	 * @return
	 */
	private CustomPostingsLinkedList<Integer> intersectListWithDAATOR(ArrayList<CustomTermsAndPosting> termsList) {

		int length = termsList.size();

		CustomPostingsLinkedList<Integer> finalLList = new CustomPostingsLinkedList<Integer>();
		ArrayList<Iterator<Integer>> itrList = new ArrayList<Iterator<Integer>>();
		Heap<KVHolder> pqueue = new Heap<KVHolder>(valueComparator);

		for (int i = 0; i < length; i++) {
			itrList.add(i, termsList.get(i).getCustomPostingsList().iterator());
			pqueue.insert(new KVHolder(itrList.get(i).next(), i));
		}

		int previous = -1;

		while (!pqueue.isEmpty()) {

			// Pop the minimum element
			KVHolder holder = pqueue.deleteMin();
			int index = holder.getIndex();

			// If the element is popped out length-1 times we have the element
			// which is common to all so ignore it.
			if (previous != holder.getValue()) {
				previous = holder.getValue();
				if (finalLList.isEmpty()) {
					finalLList.setSize(1);
				} else {
					finalLList.setSize(finalLList.getSize() + 1);
				}
				finalLList.addLast(holder.getValue());
			}

			// Add the element from the list from where the lowest element is
			// popped out
			if (itrList.get(index).hasNext()) {
				KVHolder holderTemp = new KVHolder(itrList.get(index).next(), index);
				pqueue.insert(holderTemp);

			}
			comparisionCount = pqueue.getCount();
		}
		return finalLList;

	}

	/**
	 * Adding Items of CustomTermsAndPosting to HashMap
	 * 
	 * @param termString
	 * @param postingsList
	 */
	private void addTermAndPostingList(String termString, List<Integer> postingsList) {
		CustomTermsAndPosting customTermsAndPosting = new CustomTermsAndPosting();

		CustomPostingsLinkedList<Integer> customPostingsLinkedList = new CustomPostingsLinkedList<Integer>();
		customPostingsLinkedList.addArrayOfElements(postingsList);
		customPostingsLinkedList.setSize(postingsList.size());

		customTermsAndPosting.setTermString(termString);
		customTermsAndPosting.setCount(postingsList.size());
		customTermsAndPosting.setCustomPostingsList(customPostingsLinkedList);

		customTermsAndPostingMap.put(termString, customTermsAndPosting);
	}

	/**
	 * Iterates the postings in PostingsEnum for a term and returns a list of
	 * postings found
	 * 
	 * @param postingsEnum
	 * @return
	 */
	private List<Integer> createAndGetPostingsList(PostingsEnum postingsEnum) {
		// int postingsList[];
		List<Integer> postingsList = new ArrayList<Integer>();
		while (true) {
			try {
				int docID = postingsEnum.nextDoc();
				if (docID != -1 && docID != PostingsEnum.NO_MORE_DOCS) {
					if (DEBUG) {
						indexOutputWriter.write(docID + " ");
					}
					postingsList.add(docID);
				} else {
					break;
				}
			} catch (Exception e) {
				break;
			}
		}
		return postingsList;
	}

	/**
	 * Bubble Sort for sorting the terms as per the postings counts
	 * 
	 * @param line
	 * @return
	 */
	private ArrayList<CustomTermsAndPosting> sortTermsListByFrequency(String line) {
		ArrayList<CustomTermsAndPosting> sortList = new ArrayList<CustomTermsAndPosting>();

		String[] termArr = line.split(" ");

		int length = termArr.length;

		for (int i = 0; i < length; i++) {
			sortList.add(customTermsAndPostingMap.get(termArr[i]));
		}

		CustomTermsAndPosting temp;
		for (int i = 0; i < length; i++) {
			for (int j = 1; j < (length - i); j++) {

				if (sortList.get(j - 1).getCount() > sortList.get(j).getCount()) {
					// swap the elements!
					temp = sortList.get(j - 1);
					sortList.set(j - 1, sortList.get(j));
					sortList.set(j, temp);
				}
			}
		}
		return sortList;
	}

	/**
	 * Printing the read terms from the input.txt file
	 * 
	 * @param termsList
	 * @throws IOException
	 */
	private void printTerms(ArrayList<CustomTermsAndPosting> termsList) throws IOException {
		for (int i = 0; i < termsList.size(); i++) {
			outputFileWriter.write(termsList.get(i).getTermString() + " ");
		}
	}

	/**
	 * Printing the posting list in the output.txt file
	 * 
	 * @param termsList
	 * @throws IOException
	 */
	private void printTermsPostingsList(ArrayList<CustomTermsAndPosting> termsList) throws IOException {

		for (int i = 0; i < termsList.size(); i++) {
			outputFileWriter.write("GetPostings \n");
			outputFileWriter.write(termsList.get(i).getTermString() + "\n");
			outputFileWriter.write("Postings list: " + termsList.get(i).getCustomPostingsList().toString() + "\n");
		}
	}

	/**
	 * Comparator for the PriorityQueue
	 */
	private static Comparator<KVHolder> valueComparator = new Comparator<KVHolder>() {

		@Override
		public int compare(KVHolder c1, KVHolder c2) {
			return (int) (c1.getValue() - c2.getValue());
		}
	};

	private CustomPostingsLinkedList<Integer> intersectListWithDAATAND(ArrayList<CustomTermsAndPosting> termsList) {

		int length = termsList.size();

		CustomPostingsLinkedList<Integer> finalLList = new CustomPostingsLinkedList<Integer>();
		ArrayList<Iterator<Integer>> itrList = new ArrayList<Iterator<Integer>>();
		Heap<KVHolder> pqueue = new Heap<KVHolder>(valueComparator);

		for (int i = 0; i < length; i++) {
			itrList.add(i, termsList.get(i).getCustomPostingsList().iterator());
			pqueue.insert(new KVHolder(itrList.get(i).next(), i));
		}

		int previous = -1;
		int counter = 0;

		while (!pqueue.isEmpty()) {

			// Pop the lowest element from the PriorityQueue
			KVHolder holder = pqueue.deleteMin();
			int index = holder.getIndex();

			// when the same element is popped out length-1 types the element is
			// added to the list
			if (previous == holder.getValue()) {
				counter++;
			} else {
				previous = holder.getValue();
				counter = 0;
			}
			if (counter == length - 1) {
				if (finalLList.isEmpty()) {
					finalLList.setSize(1);
				} else {
					finalLList.setSize(finalLList.getSize() + 1);
				}
				finalLList.addLast(holder.getValue());
			}

			// Add the element from the list from where the lowest element was
			// popped out.
			if (itrList.get(index).hasNext()) {
				KVHolder holderTemp = new KVHolder(itrList.get(index).next(), index);
				pqueue.insert(holderTemp);

			} else {
				while (!pqueue.isEmpty()) {
					if (previous == pqueue.deleteMin().getValue()) {
						counter++;
					}
				}
				if (counter == length - 1) {
					if (finalLList.isEmpty()) {
						finalLList.setSize(1);
					} else {
						finalLList.setSize(finalLList.getSize() + 1);
					}
					finalLList.addLast(holder.getValue());
				} else {
					break;
				}
			}
		}
		comparisionCount = pqueue.getCount();
		return finalLList;
	}
}
