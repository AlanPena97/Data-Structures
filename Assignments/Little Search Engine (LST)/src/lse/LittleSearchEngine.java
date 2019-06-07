package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		File f = new File(docFile);
		if(!f.exists()) {
			//System.out.println("My name is ");
			throw new FileNotFoundException("File Not Found");
		}
		Scanner sc = new Scanner(new File(docFile));
		HashMap<String, Occurrence> newTable = new HashMap<>();
		while(sc.hasNext()) {
			String word = sc.next();
			word = getKeyword(word);
			//System.out.println("hey "+word);
			if(word == null) {
				continue;
			}
			if(newTable.containsKey(word)) {
				newTable.get(word).frequency++;
				/*Occurrence temp = newTable.get(word);
				temp.frequency++;
				newTable.put(word, temp);*/
			}else if(!(newTable.containsKey(word))) {
				newTable.put(word, new Occurrence(docFile, 1));
			}
		}
		
		sc.close();
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return newTable;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for(String word : kws.keySet()) {
			String str = word;
			Occurrence temp = kws.get(str); 
			ArrayList<Occurrence> arrayL = keywordsIndex.get(str);
			if(arrayL == null) {
				arrayL = new ArrayList<>();
				arrayL.add(temp);
				keywordsIndex.put(str, arrayL);
			}else {
				keywordsIndex.get(str).add(temp);
				insertLastOccurrence(keywordsIndex.get(str));
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		word = word.toLowerCase();
		String returnWord = "";
		for(int i = 0 ; i < word.length() ; i++) {
			if(checkPunc(word.charAt(i)) && i+1 < word.length()) {
				if(Character.isLetter(word.charAt(i+1))) {
					return null;
				}
			}
			if(!(checkPunc(word.charAt(i)))) {
				returnWord += word.charAt(i);
			}
		}
		int count = 0;
		for(int i = 0; i < returnWord.length(); i++) {
			if(Character.isLetter(returnWord.charAt(i))) {
				count++;
			}
		}
		if(!noiseWords.contains(returnWord) && count == returnWord.length()) {
			return returnWord;
		}
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return null;
	}
	
	private boolean checkPunc(char ch) {
		return (ch == '!' || ch == ';' || ch == ':' || ch == ',' || ch == '.' || ch == '?');
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs == null || occs.size() == 1)
			return null;
		
		//System.out.println("\n\n" + occs.toString());
		Occurrence oc = occs.remove(occs.size() - 1);
		
		ArrayList<Integer> visited = new ArrayList<>();
		int lo = 0, hi = occs.size() - 1;
		int mid = (lo + hi)/2;
		while(lo <= hi) {
			//System.out.println("lo " + lo + " hi " + hi);
			mid = (lo + hi)/2;
			visited.add(mid);
			
			if(occs.get(mid).frequency == oc.frequency)
				break;
			
			//Opposite of binary search since it's in descending order
			else if(occs.get(mid).frequency > oc.frequency)
				lo = mid + 1;
			else
				hi = mid - 1;
		}
		
		if(oc.frequency > occs.get(mid).frequency)
			occs.add(mid, oc);
		else //terminated at a point where mid.frequency >= oc.frequency
			occs.add(mid + 1, oc);
		
		return visited;
		/*//** COMPLETE THIS METHOD **//*
		
		ArrayList<Integer> newArray = new ArrayList<>();
		int size = occs.size();
		if(size == 1 || occs == null) {
			return null;
		} // return null if the size of an array is 1 or 0 or occs is null
		int lastNum = occs.get(size - 1).frequency; // last number in a arraylist of occurrences
		int start = 0;
		int end = size - 2;
		int mid = 0;
		boolean check = false;
		while(start <= end) {
			mid = (start+end) / 2;
			newArray.add(mid); // adding the index of every midpoint in a process of binary search (newArray - Integer Array)
			int numMid = occs.get(mid).frequency;
			if(numMid == lastNum) {
				break; // because at that mid last member of an occs can be inserted
			}else if(numMid > lastNum) {
				start = mid + 1; 
				check = true; // keeping track to see if numMid is bigger and there is nothing left on the right to 
				//search and mid is actually mid + 1 which is start.  
			}else if(numMid < lastNum){
				end = mid - 1;  // For end the mid remain the same for last member everything else in an arraylist is shifted to the right
			}
		} // lastNum 1 and numMid 7 start 
		// 12 8 7 5 3 2 1 4
		// 0  1 2 3 4 5 6 7
		Occurrence temp = occs.remove(size -1);
		if(check) {
			occs.add(mid + 1, temp);
		}else {
			occs.add(mid, temp);
		}
		return newArray;*/
	}

	// following line is a placeholder to make the program compile
	// you should modify it as needed when you write your code

	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		ArrayList<String> top5 = new ArrayList<>();
		//kw1 = kw1.toLowerCase();
		//kw2 = kw2.toLowerCase();
		ArrayList<Occurrence> list0 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> list1 = keywordsIndex.get(kw2);
		System.out.println("List0: " + list0);
		System.out.println("List1: " + list1);
		if(list0 == null && list1 == null) {
			return null;
		}
		if(list1 == null || list1.size() == 0) {
			//System.out.println(123);
			for(int i = 0; i < list0.size(); i++) {
				if(top5.size() == 5) {
					break;
				}
				//System.out.println(!top5.contains(list0.get(i).document));
				if(!top5.contains(list0.get(i).document)) {
					//System.out.println("eE");
					top5.add(list0.get(i).document);
				}
			}
		}
		if(list0 == null || list0.size() == 0) {
			for(int i = 0; i < list1.size() ; i++) {
				if(top5.size() == 5) {
					break;
				}
				if(!top5.contains(list1.get(i).document)) {
					top5.add(list1.get(i).document);
				}
			}
		}
		
		int i = 0;
		int j = 0;
		
		while((list0 != null && list1 !=null) && i < list0.size() && j < list1.size()) {
			//System.out.println("Hello");
			int zeroF = list0.get(i).frequency;
			int oneF = list1.get(j).frequency;
			String zeroS = list0.get(i).document;
			String oneS = list1.get(j).document;
			if(top5.size() == 5) {
				break;
			}
			if(zeroF == oneF) {
				if(top5.contains(zeroS) && top5.contains(oneS)) {
					i++;
					j++;
					continue;
				}else if(top5.contains(zeroS) && !top5.contains(oneS)) {
					top5.add(oneS);
					i++;
					j++;
				}else if(!top5.contains(zeroS) && top5.contains(oneS)) {
					top5.add(zeroS);
					i++;
					j++;
				}else {
					top5.add(zeroS);
					i++;
				}
			}else if(zeroF > oneF) {
				if(top5.contains(zeroS)) {
					i++;
					continue;
				}else {
					top5.add(zeroS);
					i++;
				}
			}else if(zeroF < oneF) {
				if(top5.contains(oneS)) {
					j++;
					continue;
				}else {
					top5.add(oneS);
					j++;
				}
			}
		}
		boolean checkZero = false;
		boolean checkOne = false;
		//boolean both = false;
		if(list0 != null && i < list0.size()) {
			checkZero = true;
		}else if(list1 != null && j < list1.size()) {
			checkOne = true;
		}
		if(top5.size() < 5 && checkZero) {
			while(top5.size() < 5 && i < list0.size()) {
				if(top5.contains(list0.get(i).document)) {
					i++;
					continue;
				}else {
					top5.add(list0.get(i).document);
					i++;
				}
			}
		}else if(top5.size() < 5 && checkOne) {
			while(top5.size() < 5 && j < list1.size()) {
				if(top5.contains(list1.get(j).document)) {
					j++;
					continue;
				}else {
					top5.add(list1.get(j).document);
					j++;
				}
			}
			
		}
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return top5;
	
	}
}