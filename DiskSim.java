/*
   Kyle Tibbetts 
   CSCI 3310 Section 1
   Fall 2014

   This program simulates manging files on a disk. The input comes from the console but the 
   input may be redirected to come from a file. The program checks to see if the file exists 
   when creating, deleteing, and printing and will print and error if the file does exist for 
   creating and if it does not for deleteing or printing. The program checks to make sure all 
   file sizes are equal to or smaller than the MAX_SIZE. It also checks to see if there is
   enough room to store the file. It is assumed the input will be a character followed by two
   integers, even if the input character does nothing for the simulation. 
*/


import java.io.*;
import java.util.*;


public class DiskSim
{
    public static final int NUM_SECTORS = DiskParam.NUM_SECTORS;  //refrence to DiskParm NUM_SECTORS
    public static final int NUM_FILES   = DiskParam.NUM_FILES;    //refrence to DiskParm NUM_FILES
    public static final int MAX_SIZE    = DiskParam.MAX_SIZE;     //refrence to DiskParm MAX_SIZE
    public static final int SECTOR_SIZE = DiskParam.SECTOR_SIZE;  //refrence to DiskParm SECTOR_SIZE


//************************************************************************
//                        intializeFreeSectors
//************************************************************************
//intializes the freeSectors list to 0 - NUM_SECTORS - 1
//************************************************************************
	public static List<SectorUsage> intializeFreeSectors(int NUM_SECTORS)
	{
		List<SectorUsage> freeSectors = new List<SectorUsage>();    //creates list of sector usages
		freeSectors.add(1, new SectorUsage(0, NUM_SECTORS - 1));   
		return freeSectors; 
	}

//************************************************************************
//                        intializeFilesArray
//************************************************************************
//creates the filesArray array and intializes every index's numchars to -1
//and file_usage list to null
//************************************************************************
	public static FileInfo[] intializeFilesArray()
	{
		FileInfo[] filesArray = new FileInfo[NUM_FILES];  //creates filesArray of type FileInfo

		for(int i = 0; i < NUM_FILES; i++)	//goes through intiating the fields to empty 
		{
			filesArray[i] = new FileInfo();  //creates fileInfo class for each index
			filesArray[i].file_usage = null; //sets file_usage to null
			filesArray[i].numchars = -1;     //sets num chars to empty
		}
		return filesArray;
	}


//************************************************************************
//                          amtFree
//************************************************************************
//counts the amount free in the freeSectors list
//************************************************************************
	public static int amtFree(List<SectorUsage> freeSectors)
	{
		int size = freeSectors.size();
		int freeSects = 0;

		for(int i = 1; i <= size; i++)              //totals the number of free sectors in the list
		{
			try 
			{
				int start = freeSectors.get(i).getStart();
				int end   = freeSectors.get(i).getEnd();
				int temp  = end - start + 1;
        	    freeSects = freeSects + temp;
			} catch (Exception ArrayIndexOutOfBoundsException) 
			{
				break;
			}
			
		}
		return freeSects;
	}


//************************************************************************
//								fileSearch
//************************************************************************
//looks to see a fileId already exists in filesArray
//************************************************************************
	public static boolean fileSearch(FileInfo[] filesArray, int fileId)   
	{
		if(filesArray[fileId].numchars != -1) //checks if files exists
		{
			return true;
		}	
		return false;		
	}

//************************************************************************
//							amtSectors
//************************************************************************
	public static int amtSectors(int numSects)
	{
		if(numSects % SECTOR_SIZE == 0)       //finds the amount of sectors needed
		{
			numSects = numSects / SECTOR_SIZE ;
		}
		else   //finds the amount of sectors needed 
		{			
			numSects = numSects / SECTOR_SIZE + 1;
		}
		return numSects;
	}
	
//************************************************************************
//							     merge
//************************************************************************
	public static void merge(List<SectorUsage> freeSectors, FileInfo[] filesArray, int fileId, int delegate)
	{
		int size;
		int start;
		int end;
		
		if(delegate == 1) // merges filesArray list
		{
			
			size = filesArray[fileId].file_usage.size();
			if(size > 1)
			{
				for(int i = 1; i < size; i++)//goes through filesArray list
				{
					try //try the following code if there is no error
					{
						if(size > 1 ) //since size gets reduced will only go forawrd is sixe > 1
						{
							
							start = filesArray[fileId].file_usage.get(i).getEnd();
							end   = filesArray[fileId].file_usage.get(i+1).getStart();
				
							if(start + 1 == end)//if start + 1 = end the two sectors will be merged
							{
								int newEnd = filesArray[fileId].file_usage.get(i + 1).getEnd();
								filesArray[fileId].file_usage.get(i).setEnd(newEnd);
								filesArray[fileId].file_usage.remove(i + 1);
								size--;
							}
						} 
					}
					catch (Exception ArrayIndexOutOfBoundsException) 
					{
						break;
					}
				}
			}
		}
		else //merges freeSectors list
		{
			size = freeSectors.size();
			if(size > 1)
			{
				for(int i = 1; i < size ; i++)
				{
					try 
					{
						
						if(size > 1 )
						{
							start = freeSectors.get(i).getEnd();
							end   = freeSectors.get(i + 1).getStart();
							
							if(start + 1 == end)
							{
								int newEnd = freeSectors.get(i + 1).getEnd();
								freeSectors.get(i).setEnd(newEnd);
								freeSectors.remove(i + 1);
								size--;
								
							}
						}
					} 
					catch (Exception ArrayIndexOutOfBoundsException) 
					{
						break;
					}
				}
			}
		}
	}
	
//************************************************************************
//                               grow
//************************************************************************
// grow is the amount the fileId in filesArray will get bigger by. The 
// correct amount of sectors will get taken away from freeSectors
//************************************************************************
	public static void grow(List<SectorUsage> freeSectors, FileInfo[] filesArray, int fileId, int grow)
	{
		if(fileSearch(filesArray, fileId)) //makes sure the fileId exists
		{
			if (filesArray[fileId].numchars + grow <= MAX_SIZE) //makes sure with the grow the numchars is less than
										                         //the MAX_SIZE
			{
				int fileSize = filesArray[fileId].numchars;
				int numSects; 
			
				if(fileSize == 0)//if fileSize is 0 the the grow operation will be simpler
				{
					numSects = amtSectors(grow);
					if(amtFree(freeSectors) >= numSects)//checks if there is enough room to store
					{				
						filesArray[fileId].numchars = grow;

						while(numSects != 0) //updates filesArray untill all sectors are stored
						{
							numSects = updater(freeSectors, filesArray, fileId, numSects);
						}
						System.out.printf("File %d increased size by %d characters, new size = %d\n", fileId, grow, grow);
						
					}
					else   //not enough freesectors to store file
					{
						System.out.printf("There was not enough room for file %d to be grown\n",fileId);
					}			
				}		
				else // if fileSize is greater than 0
				{
					int notUsed = fileSize % SECTOR_SIZE; //amount used by a sector on the fileId
					if(notUsed != 0)//find the amount not used by a sector
					{
						notUsed = SECTOR_SIZE - notUsed;
					}	
									
					if(grow == 0)//if grow = 0 than no new sectors need to be added
					{
						numSects = 0;
					}
					else //grow > 0
					{
						if(grow < notUsed) //if grow is less notUsed no new sectors needed
						{
							numSects = 0;
						}
						else// grow is bigger than not used than it fins the amount of secotrs needed
						{
							numSects = amtSectors(grow - notUsed);
						}
						filesArray[fileId].numchars = filesArray[fileId].numchars + grow;
					}
					
					if(amtFree(freeSectors) >= numSects)
					{
			 			while(numSects != 0)
						{
							numSects = updater(freeSectors, filesArray, fileId, numSects);
						}
					
						merge(freeSectors, filesArray, fileId, 1);
						
						System.out.printf("File %d increased size by %d characters, new size = %d\n", fileId, grow, filesArray[fileId].numchars);
					}
					else   //not enough freesectors to store file
					{
						System.out.printf("There was not enough room for file %d to be grown\n",fileId);
					}
				}
			} 
			else 
			{
				System.out.printf("File %d exceeds max size after grow, grow ignored\n", fileId);			}
		}
		else 
		{
			System.out.printf("File %d does not exisits to be grown\n", fileId);
		}
	}
	
//***********************************************************************
//							  shrinkDeleter
//***********************************************************************
	public static void shrinkDeleter(List<SectorUsage> freeSectors, FileInfo[] filesArray, int fileId, int amtDelete)
	{
		int listIndex = filesArray[fileId].file_usage.size();
		int start;
		int end;
		int amtSects;
		
		while (amtDelete > 0 && listIndex >= 1) //goes untill the all need sectors are deleted from filesArray
		{ 
			try 
			{
				start = filesArray[fileId].file_usage.get(listIndex).getStart();
				end   = filesArray[fileId].file_usage.get(listIndex).getEnd();
				amtSects = end - start + 1;
				
				if(amtDelete == amtSects)//deletes sectors from filesArray if the sectors to delete are equal
				{
					deleteHelper(freeSectors, start, end);
					filesArray[fileId].file_usage.remove(listIndex);
					amtDelete = 0;
				}
				else if (amtDelete > amtSects)//when the amt to delete is bigger than the size of 
											   //sectors the filesArray list index is delete and
											   // amt to delete is changed accordingly
				{
					deleteHelper(freeSectors, start, end);
					filesArray[fileId].file_usage.remove(listIndex);
					amtDelete = amtDelete - amtSects;
					listIndex--;
				}
				else if (amtDelete < amtSects) //deletes sectors from filesArray and gives back to freeSectors
				{
					filesArray[fileId].file_usage.get(listIndex).setEnd(end-amtDelete);
					deleteHelper(freeSectors, end - amtDelete + 1, end);
					amtDelete = 0;
				}	
			}
			catch (Exception ArrayIndexOutOfBoundsException) 
			{
				listIndex--;
			}
			
		}
	}
	
//***********************************************************************
//								shrink
//***********************************************************************
	public static void shrink(List<SectorUsage> freeSectors, FileInfo[] filesArray, int fileId, int shrink)
	{
		if(fileSearch(filesArray, fileId))//make sures the file exists
		{
			int fileSize = filesArray[fileId].numchars;
			
			if(fileSize - shrink >= 0) //make sures after shrink that the file will be 0 or bigger
			{
				int sizeSects = amtSectors(fileSize);  //amt of sectors being used by the file
				
				int newSize = fileSize - shrink;       //newsize of the file
				int newSects = amtSectors(newSize);    //amt of the new sectors
				int delete = sizeSects - newSects;     //determines amount of sectors to delete
			
				shrinkDeleter(freeSectors,filesArray, fileId, delete);
				merge(freeSectors, filesArray, fileId, 2);
				filesArray[fileId].numchars = newSize; //sets numchars to correct new value
				System.out.printf("File %d was shrunk by %d = new size = %d \n", fileId, shrink, filesArray[fileId].numchars);
			}
			else //if file after shrink is below 0
			{
				System.out.printf("File %d numchars is less than 0 after shrink = %d, shrink ignored\n", fileId, shrink);
			}
		}
		else //if files doesnt exists
		{
			System.out.printf("File %d does not exisits to be shrunk\n", fileId);
		}
	}	
	
	
//************************************************************************
//                               updater
//************************************************************************
//makes the filesArray list for a particular fileId of the size numSects
//in filesArray and updates the freeSectors by removing the newly taken 
//sectors
//************************************************************************
	public static int updater(List<SectorUsage> freeSectors, FileInfo[] filesArray, int fileId, int numSects)
	{
		int start = freeSectors.get(1).getStart(); //gets start of sector
		int end   = freeSectors.get(1).getEnd();   //gets end of sectos
		int freeSize  = end - start + 1;           //determines free size
		int fileSize;
		
		if(filesArray[fileId].file_usage == null)///creates a file_usage list if no list exists
		{
			filesArray[fileId].file_usage = new List<SectorUsage>();
			fileSize  = 0;
		}
		else //gets size of file_usage list if one exists
		{
			fileSize = filesArray[fileId].file_usage.size();
		}
		
		if(freeSize == numSects)    //numSects equals free space 
		{
			filesArray[fileId].file_usage.add(fileSize + 1, new SectorUsage(start, end));
			freeSectors.remove(1);
			return 0;
		}
		else if(freeSize < numSects)  //numSects is bigger than free space
		{
			filesArray[fileId].file_usage.add(fileSize + 1, new SectorUsage(start, end));
			freeSectors.remove(1);
			numSects = numSects - freeSize;

			return numSects;
		}
		else if(freeSize > numSects)  //numSects is smaller than free space
		{
		 	filesArray[fileId].file_usage.add(fileSize + 1, new SectorUsage(start, start + numSects - 1)); 
			freeSectors.get(1).setStart(start + numSects );
			return 0;
		}
		return numSects;
	}


//************************************************************************
//                                create
//************************************************************************
//creates a file(fileId) of a fileSize if the file does not exist and in
//filesArray uses freeSectors to see the open sectors so it can be stores
//************************************************************************
	public static void create(List<SectorUsage> freeSectors, FileInfo[] filesArray, int fileId, int fileSize)
	{
		if(!fileSearch(filesArray, fileId))    //checks if file exists
		{				

			filesArray[fileId].numchars = fileSize ;  //set filesArray numchars 
			int numSects = amtSectors(fileSize);

			if(amtFree(freeSectors) >= numSects)  //looks to see if there is enough space thorughout freeSectors
			{
				while(numSects != 0)   //goes untill all of the files needed sectors are stored.
				{
					numSects = updater(freeSectors, filesArray, fileId, numSects);
				}
				System.out.printf("File %d created, size = %d\n", fileId, fileSize);
			}
			else   //not enough freesectors to store file
			{
				System.out.printf("There was not enough room for file %d to be stored\n",fileId);
			}

		}
		else         //file already exists
		{
			System.out.printf("File %d already exisits\n", fileId);
		}

	}


//************************************************************************
//                            deleteHelper
//************************************************************************
//gives sectors(start,end) back to the freeSectors list and keeps the list 
//in order fromleast to greatest when the file is deleted.
//************************************************************************
	public static void deleteHelper(List<SectorUsage> freeSectors, int start, int end)
	{
		int listStart;
		int listSize = freeSectors.size();
		
		if(listSize == 0)//go through 
		{
			freeSectors.add(1, new SectorUsage(start,end));
		}
		else 
		{
			for(int i = 1; i <= listSize; i++) //goes through the freeSectors list
			{
		//checks if the deleted start is smaller then the start at that index
				listStart = freeSectors.get(i).getStart();

				if(start < listStart)
				{
					int listEnd;
					
					freeSectors.add(listSize+1, new SectorUsage(freeSectors.get(listSize).getStart(), freeSectors.get(listSize).getEnd()));
				
					while(i != listSize) //goes backwards through the list moving the start and end one index forward
					{
						listStart = freeSectors.get(listSize-1).getStart();
						listEnd   = freeSectors.get(listSize-1).getEnd();
						freeSectors.get(listSize).setStart(listStart);
						freeSectors.get(listSize).setEnd(listEnd);
						listSize--;
					}
					freeSectors.get(i).setStart(start);
					freeSectors.get(i).setEnd(end);
				}
			}
		}
	}


//************************************************************************
//                               delete
//************************************************************************
//deletes the requested fileId from the filesArray and gives those previously
//taken sectors back to freeSectors
//************************************************************************
	public static void delete(List<SectorUsage> freeSectors, FileInfo[] filesArray, int fileId)
	{
		if(fileSearch(filesArray, fileId))    //checks if file exists
		{
			int start;
			int end;
			int size;
			int num = 0;
			
			if(filesArray[fileId].numchars == 0)
			{
				filesArray[fileId].numchars = -1;				System.out.printf("File %d deleted, 0 sectors freed\n\n",fileId);
			}
			else 
			{
				size = filesArray[fileId].file_usage.size();

				for(int i = 1; i <= size ; i++) //goes through the filesArray
				{

					start = filesArray[fileId].file_usage.get(i).getStart();
					end   = filesArray[fileId].file_usage.get(i).getEnd();
					num   = num + (end - start + 1);
					deleteHelper(freeSectors, start, end);
				}
				System.out.printf("File %d deleted, %d sectors freed\n",fileId, num );
				filesArray[fileId].file_usage.removeAll();
				filesArray[fileId].file_usage = null;
				filesArray[fileId].numchars = -1;
			}
		}
		else    //file does not exists
		{
			System.out.printf("File %d does not exist\n",fileId);
		}
	}


//************************************************************************
//								  print
//************************************************************************
//prints out the fileId in filesArray requested or the freeSectors if the 
//fileId is -1 
//************************************************************************
	public static void print(List<SectorUsage> freeSectors, FileInfo[] filesArray, int fileId)
	{
		if(fileId >= 0 ) //prints the fileId information 
		{
			if(!fileSearch(filesArray,fileId))
			{
				System.out.printf("ERROR: File %d non-exsitent, print command ignored\n\n", fileId);
			}
			else
			{
				if(filesArray[fileId].numchars > 0)				{
					System.out.printf("Sector usage for file %d --- size = %d characters\n",fileId, filesArray[fileId].numchars);
					int size = filesArray[fileId].file_usage.size();
					int start = 0;
					int end   = 0;
					System.out.print("Start Sector    End Sector\n");
					for(int i = 1; i <= size; i++)   //go through the filesArray fileusage list
					{
						try 
						{
							start = filesArray[fileId].file_usage.get(i).getStart();
							end   = filesArray[fileId].file_usage.get(i).getEnd();
							System.out.printf("%6d%14d\n", start, end);
						} 
						catch (Exception ArrayIndexOutOfBoundsException) 
						{
							size--;
						}
						
					}
					System.out.print("\n");
				}
				else
				{
					System.out.printf("File %d exists but is empty\n\n", fileId);
				}
			}
		}
		else //prints the freeSectors information
		{
			if (!freeSectors.isEmpty())
			{
				System.out.print("Contents of of Sectors List\n");
				System.out.print("Start Sector   End Sector\n");
				int sectSize = freeSectors.size();
				int sectStart = 0;
				int sectEnd   = 0;
				for(int j = 1; j <= sectSize; j++) //goes through every index in freeSectors
				{
					try 
					{
						sectStart = freeSectors.get(j).getStart(); 
						sectEnd   = freeSectors.get(j).getEnd();
						System.out.printf("%6d%14d\n",sectStart, sectEnd);
					} 
					catch (Exception ArrayIndexOutOfBoundsException) 
					{
						sectSize--;
					}
					
				}
				System.out.print("\n");	
			}
			else
			{
				System.out.println("There are no free sectors available.\n");
			}
		}
	}


//************************************************************************
//								   exit
//************************************************************************
//prompts the user that the simulation is done and states the amount files
//in filesArray and the amount of sectors used in freeSectors
//************************************************************************
	public static void exit(List<SectorUsage> freeSectors, FileInfo[] filesArray)
	{
		int free = NUM_SECTORS - amtFree(freeSectors);
		int size = 0;
		for(int i = 0; i < NUM_FILES; i++) //goes through the filesArray
		{
			if(fileSearch(filesArray,i))//increases size if the file exists
			{
				size++;
			}
		}
		System.out.printf("SIMULATOR EXIT:  %d files exist, occupying %d states\n", size, free);
	}


//************************************************************************
//								delegater
//************************************************************************
//reads the input from Scanner and updates the freeSectors as needed
//************************************************************************
	public static void delegater(List<SectorUsage> freeSectors, Scanner sc)
	{
		FileInfo[] filesArray = intializeFilesArray();
		for(char opcode = sc.next().charAt(0); opcode != 'x'; opcode = sc.next().charAt(0)) {
		//keep going till an 'x' meaning exit, program stops.
			int fileId;
			int fileSize;
			if(opcode == 'c') //create 
			{
				fileId   = sc.nextInt();
				fileSize = sc.nextInt();

				if(fileSize > MAX_SIZE)	//error check to see if file is smaller than maxsize
				{
					System.out.printf("ERROR: The current file size is %d exceeds the max size of %d\n", fileSize, MAX_SIZE);
				}
				else
				{
					create(freeSectors, filesArray, fileId, fileSize);
				}
			}
			else if(opcode == 'p')//print
			{
				fileId   = sc.nextInt();
				fileSize = sc.nextInt();
				print(freeSectors, filesArray, fileId);
			}
			else if(opcode == 'd')// delete
			{
				fileId   = sc.nextInt();
				fileSize = sc.nextInt();
				delete(freeSectors, filesArray, fileId);
			}
			else if (opcode == 'g') 
			{
				fileId   = sc.nextInt();
				fileSize = sc.nextInt(); 
				grow(freeSectors, filesArray, fileId, fileSize);
			}
			else if (opcode == 's') 
			{
				fileId   = sc.nextInt();
				fileSize = sc.nextInt();	
				shrink(freeSectors, filesArray, fileId, fileSize);
			}
			else // any other character that is not recongized = error
			{
				fileId   = sc.nextInt();
				fileSize = sc.nextInt();
				System.out.printf("ERROR: The chararcter %c is not recognized.\n", opcode);
			}
		}
		exit(freeSectors, filesArray);  //exit
	}

//************************************************************************
//								   main
//************************************************************************
	public static void main(String[] args)
	{
		List<SectorUsage> freeSectors = intializeFreeSectors(NUM_SECTORS);
		Scanner sc = new Scanner(System.in);
		delegater(freeSectors, sc);
	}
}
