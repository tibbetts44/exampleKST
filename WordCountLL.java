/*  
	Kyle Tibbetts
	CSCI 3310 Section 1
	Fall 2014
	Program 5, part 1
	December 8, 2014	
	
	This program creates a hash table using linked list collision solution.
	The input is assumed to be the name of the file that wants to be stored
	into a hashtable followed by an integer refering to the the amount of the 
	top most frequent words to be printed out with the amount of occurence. The 
	The output will "The (amount of words wanted to be displayed) most 
	frequently occuring words are:" this followed by the most word being displayed 
	with a rank starting at 1 to the amoutn wanted.
*/

import java.io.*;
import java.util.*;

public class WordCountLL {
	
	public static final int TABLESIZE = 587;
	public static LinkedList<WordItem>[] hashTable = new LinkedList [TABLESIZE];

//**************************************************************
//							initializeHash
//**************************************************************
//initializes the hastable with default WordItem constructor
//**************************************************************
	public static void initializeHash()
	{
		for(int i = 0; i < TABLESIZE; i++) {//goes through the whole array
			hashTable[i] = new LinkedList<WordItem>(); //sets WordItem word to null, and count to 0
			hashTable[i].add(0, new WordItem());
		}	
	}

//***************************************************************
//							hash
//***************************************************************
//creates the hash index for the string word
//***************************************************************
	public static int hash(String word)
	{
	   	int i;
	   	int addr = 0;
	   	for (i = 0; i < word.length(); i++)
	       addr += (int) word.charAt(i); 

    	return addr % TABLESIZE;
	}

//**************************************************************
//							duplicate
//**************************************************************
//returns true if the word has been previously stored and false
//if it has not, also increaments the count for that word
//**************************************************************
	public static boolean duplicate(String word, int hash)
	{
		//goes through has list untill a null word is found
		for(int i = 0; hashTable[hash].get(i).getWord() != null; i++) { 
			String stored = hashTable[hash].get(i).getWord();  //records stored word
			if (stored.equals(word)) {  //checks for equality
				hashTable[hash].get(i).incCount();  //increaments counter of that word
				return true;
			}
		}
		return false;
	}
	
//**************************************************************
//							retrieve
//**************************************************************
//returns the index in the list for the a word at its hash index
//in the hastable
//**************************************************************
	public static int retieve(String word, int hash) 
	{
		//intialize ListIterator
		ListIterator<WordItem> list = hashTable[hash].listIterator();
		for(int i = 0; list.hasNext(); i++) { //goes untill the list is empty
			//stores stored word
			String stored = list.next().getWord();
			if (stored.equals(word)) { //checks for equality
				return i; 
			}
		}
		return 0;
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
		//record info of the most frequent word
		WordItem most = new WordItem();
		for(int i = 0; i < TABLESIZE; i++) { //goes through whole hash table
			//creates listIterator
			ListIterator<WordItem> list = hashTable[i].listIterator();
			while(list.hasNext()) { //goes to the end of the list
				//checks if the next count is higher than the current most
				if(most.compareTo(list.next()) < 0) { 
					int count = list.previous().getCount();  //gets count of from the one just tested
					most = new WordItem(list.next().getWord()); //updates most
					most.setCount(count); //updates most count
				}	
			}
		}
		//prints out the most frequent word
		System.out.printf("%d. %s occurs %d times\n", rank, most.getWord(), most.getCount());
		//finds the hashTable index and position in list 
		String word = most.getWord();
		int hash = hash(word);
		int spot = retieve(word, hash);
		//removes the most frequent word from the hashtable
		hashTable[hash].remove(spot); 
	}

//**************************************************************
//							print
//**************************************************************
//prints out the top amount(numwords) of most frequently used words
//and the amount of unique words found
//**************************************************************
	public static void print(int numWords, int unique)
	{
		System.out.printf("The %d most frequently occuring words were:\n", numWords);
		for(int rank = 1; rank <= numWords; rank++) { //goes for numWord times
			printMost(rank);
		}
		System.out.printf("\nThere were a total of %d unique words\n", unique);
	}

//**************************************************************
//							reader
//**************************************************************
//stores words in the hashtable appropriately untill the book has
//nothing left to look at, and returns the number of unique words
//found
//**************************************************************
	public static int reader(BookIterator book) 
	{
		String word;
		int hash;
		int unique = 0;
		
		while(book.hasNext()){     //goes untill all words have been read
			word = book.next();    //gets next word
			hash = hash(word);   //gets hash index for the word
				
			if(hashTable[hash].peek().getWord() == null) {  //list is empty
				hashTable[hash].addFirst(new WordItem(word));
				unique++; //new word, uniwue count increamented
			}
			else { //list is not empty */
				if(duplicate(word, hash)) { 	//word has been stored before
					//if duplicate is true the count for that word is increamented 
				}
				else {  //word has not been seen
					hashTable[hash].addFirst(new WordItem(word));//store in hash table
					unique++;	//new word, uniwue count increamented
				}
			}
		}
		return unique;
	}

/**************************************************************/
	public static void main(String[] args)
	{
		//initialize hashTable
    	initializeHash();
		if (args.length < 2)
    	{
			System.out.print("ERROR: insufficient number of command line ");
       		System.out.println("arguments. Program aborted.");
       		return;
    	}
    	int numWords = Integer.parseInt(args[1]);
		//Call book iterator with name of file (specified in args[0])
		BookIterator book = new BookIterator();
		book.readBook(args[0]);
		// Go through iterator storing words into hashtable
		int unique = reader(book);
		//Code for producing output goes here
		print(numWords, unique);
}
