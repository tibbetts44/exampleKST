/* 
	Kyle Tibbetts
	CSCI 3310 Section 1
	Fall 2014
	Program 5, part 2
	December 8, 2014
	
	This program creates a hash table using HashTable class.The input is 
	assumed to be the name of the file that wants to be stored into a 
	hashtable followed by an integer refering to the the amount of the 
	top most frequent words to be printed out with the amount of occurence. 
	The output will "The (amount of words wanted to be displayed) most 
	frequently occuring words are:" this followed by the most word being 
	displayed with a rank starting at 1 to the amoutn wanted.
*/

import java.io.*;
import java.util.*;

public class WordCountHT
{

// Declarations and initialization for hash table.  Using Java API.
	public static final int TABLESIZE = 11003;
	public static Hashtable<String,WordItem> hashTable = 
    			new Hashtable<String,WordItem>(TABLESIZE);


//**************************************************************
//							fill
//**************************************************************
//stores the input in the hash table appropiately, and returns the
//number of unique words found.
//**************************************************************
	public static int fill(BookIterator book)
	{
		String word;
		int hash;
		int unique = 0;
		while(book.hasNext()){
			word = book.next();	
			if(hashTable.containsKey(word)){ //seen before
				hashTable.get(word).incCount();
			} else {
				unique++;
				hashTable.put(word, new WordItem(word));
			}
		}
		return unique;
	}
	
//**************************************************************
//						printMost
//**************************************************************
//finds most frequently occuring word prints out its rank and the
//amount of times it is used and then removes the word from the 
//hashtable
//**************************************************************
	public static void printMost(int rank)
	{
		Collection c = hashTable.values();
		Vector<WordItem> v = new Vector<WordItem>(c);
		String mostKey = null;
		int most = 0;
		int next;
		for(int i = 0; i < v.capacity(); i++) {
			next = v.get(i).getCount();
			if(next > most) {
				most = next;
				mostKey = v.get(i).getWord();
			}
		}
		//prints out the most frequent word
		System.out.printf("%d. %s occurs %d times\n", rank, mostKey,  most);
		//removes the most frequent word from the hashtable
		hashTable.remove(mostKey);
	}	
	
//**************************************************************
//							print
//**************************************************************
//prints out the top numWords(amount) of most frequently occuring
//and the amount of unique words 
//**************************************************************
	public static void print(int numWords, int unique)
	{
		System.out.printf("The %d most frequently occuring words were:\n", numWords);
		int rank = 1;
		while(rank <= numWords) {
			printMost(rank);
			rank++;
		}
		System.out.printf("\nThere were a total of %d unique words\n", unique);
	}

/**************************************************************/
	public static void main(String[] args)
	{
		if (args.length < 2)
    	{
			System.out.print("ERROR: insufficient number of command line ");
			System.out.println("arguments. Program aborted.");
			return;
    	}
    	int numWords = Integer.parseInt(args[1]);

    	// Call book iterator with name of file (specified in args[0])
		BookIterator book = new BookIterator();
		book.readBook(args[0]);
    	// Go through iterator storing words into hashtable
		int unique = fill(book);
    	// Code for producing output goes here
		print(numWords, unique)	;
	}
}
