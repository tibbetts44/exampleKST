/* 
	Kyle Tibbetts
	CSCI 3310 Section 1
	Fall 2014
	Novemeber 19, 2014
	
	Finds a safe path through the kingdom, of 49 regions,if one exists. The program 
	uses a stack to store path taken. A move is valid if it has not been visited and 
	the gold amount is between 0-50. This checked for before a move put on the stack. 
	The moves mmust be made in a certain order, east, south, west, north, if one doesnt 
	exist for one. The input comes from a file or can be typed in. The input starts at 
	[1][1] and goes to [7][7]. The indexes that surrond this are marked as out of bounds 
	and will not be able to be entered. The program checks to see if there is a path to 
	print and if not will print 'NO SAFE PATH'. It also checks to see if the path is 
	safe to enter before visiting the state. The program assumes the user will enter 
	info by row and not column by column.  
*/

import java.util.*;

public class CzarTrek {

public static final int WEST = CzarState.WEST;
public static final int EAST = CzarState.EAST;
public static final int NORTH = CzarState.NORTH;
public static final int SOUTH = CzarState.SOUTH;
public static final int NONE = CzarState.NONE;

public static int[][] region = new int[9][9];
public static boolean[][] visited = new boolean[9][9];
public static final int THRESH = 50;

//*********************************************************
//						initState
//*********************************************************
//initializes the region and visted data structures
//assumes all region gold indices are inputted
//*********************************************************
	public static void initState()
	{
		int i = 0;
		int j = 0;
		int gold;
		Scanner sc = new Scanner(System.in);    //gets input
		
		while(i <= 8) { //goes while in range of array 
			if (i == 0 || i == 8 || j == 0 || j == 8) {  //sets the border to -999
				region[i][j]  = -999; //mark to not be able to get in
				j++;
				if(j == 9) {  //tests if j out of range to set back to 0 
					i++;
					j = 0; 
				}
			}
			else { //in bounds on the of the kingdom
				region[i][j]  = sc.nextInt();
				visited[i][j] = false;
				j++;
			}
		}
	}
	
	
//*********************************************************
//						nextDir
//*********************************************************
//returns the next direction based on the moce order 
//requirement
//*********************************************************
	public static int nextDir(int curDir)
	{
		if(curDir == EAST) {         //EAST  -> SOUTH
			return SOUTH;
		}
		else if(curDir == SOUTH) {   //SOUTH -> WEST 
			return WEST;
		}
		else if(curDir == WEST) {    //WEST  -> NORTH
			return NORTH;
		}
		else if (curDir == NORTH) {  //NORTH -> NONE
			return NONE;
		}
		return NONE;
	}
	
	
//*********************************************************
//						markVisited
//*********************************************************
//set the visited status of the region associated with 
//state to value
//*********************************************************
	public static boolean markVisited(CzarState state, boolean value)
	{
		int i = state.getRow();  //get row
		int j = state.getCol();  //get col
		visited[i][j] = value; //updates visited
		return value;
	}


//*********************************************************
//						goalState
//*********************************************************
//returns true if the state is the final state
//*********************************************************
	public static boolean goalState(CzarState state)
	{
		int i = state.getRow();
		int j = state.getCol();
		
		if(i == 7 && j == 7) { return true; } //state is region[7][j]
		else {return false; }                 //state is not region[7][7]
	}
	
	
//*********************************************************
//						safeState
//*********************************************************
//returns true if state is legeal. Legeal meaning it hasnt
//been visited and gold amount should be within acceatable 
//limits
//*********************************************************
	public static boolean safeState(CzarState state)
	{
		int i = state.getRow();
		int j = state.getCol();
		int gold = state.getGold();
		
		if(visited[i][j] == false) { //checks visit to see if has been visited
			if(gold <= THRESH && gold >= 0) { //gold is in limits
				return true;
			}
			else {  //gold is not in limits
				return false;
			}
		}
		else { //state has been visited
			return false;
		}
	}
	
	
//*********************************************************
//							nextState
//*********************************************************
//returns a CzarState value that represents the next legal 
//move from curState; if no legal move is possible, then null
//should be returned
//*********************************************************
	public static CzarState nextState(CzarState curState)
	{
		int next = curState.getDir();
		int gold = curState.getGold();
		int i = curState.getRow();
		int j = curState.getCol();
		
		while(next != NONE) {  //stops if direction is NONE
			int row = i;
			int col = j;
		
			//advances the state accodringly
			if (next == EAST) {  
				col++;
				curState.setCol(col);
			} else if (next == SOUTH) {
				row++;
				curState.setRow(row);
			} else if (next == WEST) {
				col--;
				curState.setCol(col);
			} else if (next == NORTH) {
				row--;
				curState.setRow(row);
			}
			
			int newGold = gold + region[row][col];   //gets the new gold amount
			curState.setGold(newGold);   //sets the gold amount
			
			if (safeState(curState)) { //checks if move is legal
				curState.setDir(EAST); //sets direction to east 
				return curState;
			}
			else {
				//sets state to as it was when it first came and advances direction
				next = nextDir(next);
				curState.setRow(i);
				curState.setCol(j);
				curState.setGold(gold);
				curState.setDir(next);
				row = i;
				col = j;
			}
		}
		return null;  //no next state was found
	}


//*********************************************************
//						backTrack
//*********************************************************
//goes through the kingdom in the direction order to find a 
//safe path through the kingdom
//*********************************************************
	public static void backTrack()
	{
		Stack<CzarState> stack = new Stack<CzarState>();  //intialize stack
		int i = 1, j = 1;
		int gold = region[i][j] + 25;   //finds gold for the first state
		CzarState curState = new CzarState(i, j, gold);  //creates first state with proper amount of gold
		
		if(safeState(curState)) {  //checks if 
			 
			stack.push(new CzarState(curState));   //put intial state on stack
			
			while(!stack.empty() && !goalState(stack.peek())) { //checks if stacks is empty there is no safe path
			
				curState = stack.peek(); 		//gets current state
				CzarState next = new CzarState(curState);  //copies current state
				next = nextState(next);   	 //finds next state
				curState.setDir(nextDir(curState.getDir())); //moves orginal dir to the next direction
				
				if(next == null) {			//there is not a next state
					markVisited(curState, false); //marks curstate not visited
					stack.pop();			//removes state from stack
					curState.setDir(EAST);  //sets nextDir to EAST 
				}
				else if(safeState(next)){  //next state
					markVisited(curState, true);  //sets curState to visited
					stack.push(next);     //put the state on stack
				}
			}//end while		
		}		
		
		if (stack.empty()) {      //the stack is empty--- NO SAFE PATH
			System.out.println("NO SAFE PATH\n");
		}
		else {			//there is a path to print
			print(stack);
		}	
	}



//********************************************************
//							print	
//*********************************************************
//takes the stack and makes an iterator from it and prints
//the informaiton 
//*********************************************************
	public static void print(Stack<CzarState> stack)
	{
		Iterator<CzarState> i = stack.iterator();    //initliazes iterator
		int row;
		int col;
		int gold;
		CzarState top;
			
		while(i.hasNext()) { //goes untill iterator is empty
			top = i.next();  //gets top on iterator
			
			//getd row, col, and gold info
			row  = top.getRow();
			col  = top.getCol();
			gold = top.getGold();
				
			System.out.printf("Move to [%d,%d]  Gold: %d\n", row, col, gold);
		}
		System.out.println("\n");
	}
	

  public static void main(String[] args)
  {
		initState();  		//intializes region and visited array
		backTrack();	    //starts the path through the kingdom and intilaizes stack
  }
}	
