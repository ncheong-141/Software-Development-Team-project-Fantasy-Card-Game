package structures.basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;

/**
 * 
 * @author Chiara Pascucci
 * 	this class builds the board that the player will play on
 	this class stores a 2D array of Tile objects that represent the board
	this class contains methods to access different tiles on the board
 *
 */


public class Board {

	//class variables
	private Tile [][] gameBoard;
	//since the board for this version of the game is set to given constant values
	//the board length on the X and Y axis is represented by constant integer values
	private final int Y;
	private final int X;
	
	private int numUnitsOnBoard;
	private final int boardCapacity;

	private Monster unitSelected;

	private final int[] rangeH = {0,0,1,-1,1,-1,1,-1};
	private final int[] rangeW = {1,-1,0,0,-1,1,1,-1};

	public Board() {
		X = 9;
		Y = 5;
		this.boardCapacity = X*Y;
		this.numUnitsOnBoard = 0;
		gameBoard = new Tile[Y][X];
		for (int i = 0; i<Y; i++) {
			for (int k = 0; k<X; k++) {
				gameBoard [i][k] = BasicObjectBuilders.loadTile(k, i);
				gameBoard [i][k].free = true; 	
				gameBoard[i][k].unitOnTile = null;
			}
		}
	}
	
	public int getBoardWidth() {
		return this.X;
	}
	
	public int getBoardLength() {
		return this.Y;
	}

	//getter method to access the 2D array of Tiles
	public Tile[][] getGameBoard() {
		return gameBoard;
	}

	public int getBoardCapacity() {
		return this.boardCapacity;
	}
	
	public void updateUnitCount(int delta) {
		this.numUnitsOnBoard += delta;
	}

	public ArrayList<Tile> getAllTilesList(){
		ArrayList<Tile> fullTileList = new ArrayList<Tile>();
		
		for (int i=0; i<gameBoard.length; i++) {
			for (int k = 0; k<gameBoard[0].length; k++) {
				fullTileList.add(gameBoard[i][k]);
			}
		}
		
		return fullTileList;
	}

	public void setGameBoard(Tile[][] gameBoard) {
		this.gameBoard = gameBoard;
	}

	public void setUnitSelected(Monster m){
		this.unitSelected = m;
	}

	public Monster getUnitSelected(){
		return this.unitSelected;
	}

	//Method to access a specific tile on the board given the X and Y coordinates
	public Tile getTile(int x, int y) {
		return gameBoard[y][x];
	}


	//=====================PLAYABLE TILES METHODS SECTION==================//

	/**
	 * @param Player objects
	 * @return it returns a list of tiles where a given Player can summon a standard unit
	 */

	public ArrayList<Tile> allSummonableTiles(Player p){
		ArrayList <Tile> tileList = new ArrayList<Tile>();
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if ((gameBoard[i][k].getUnitOnTile() != null)&& gameBoard[i][k].getUnitOnTile().getOwner()==p) {
					tileList.addAll(this.calcRange(gameBoard[i][k]));
				}
			}
		}
		return tileList;
	}

	/**
	 * @param Tile Object
	 * @return helper method to allSummonableTiles
		for any given tile it returns a list of tile in range
		the range here is based on game specifications (any free tile adjacent to a friendly unit)
	 */
	
	ArrayList<Tile> calcRange(Tile t){
		ArrayList<Tile> tileRange = this.adjTiles(t);
		tileRange.removeIf(tile -> !(tile.getFreeStatus()));
		return tileRange;
		
	}
	
	/**
	 * @param Tile object
	 * @return a list of tiles adjacent to the given tile
	 * all tiles are returned regardless of free status
	 */
	public ArrayList<Tile> adjTiles(Tile t){
		ArrayList<Tile> tileRange = new ArrayList<Tile>();
		int xPos = t.getTilex();
		int yPos = t.getTiley();


		for (int i = 0; i<rangeH.length; i++) {
			if (xPos + rangeW[i] <0 || xPos + rangeW[i] > X-1 || yPos + rangeH[i]<0 || yPos + rangeH[i] > Y-1) continue;
			else {
				
					Tile posTile = this.getTile(xPos+rangeW[i], yPos+rangeH[i]);
					tileRange.add(posTile);	
				
			}
		}
		return tileRange;
	}


	/**
	 * @param Player p
	 * @return Method returns all tiles where a ENEMY unit is present (excl. avatar)
	 */
	public ArrayList<Tile> enemyTile(Player p){
		ArrayList<Tile> tileRange = new ArrayList<Tile>();
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() != null && gameBoard[i][k].getUnitOnTile().getClass() != Avatar.class  && gameBoard[i][k].getUnitOnTile().getOwner()!=p) {
					tileRange.add(gameBoard[i][k]);
				}
			}	
		}
		return tileRange;
	}

	/**
	 * @param Player p
	 * @return Method returns all tiles where a FRIENDLY unit is present (excl. avatar)
	 */
	public ArrayList<Tile> friendlyTile(Player p) {	
		ArrayList<Tile> tileRange = new ArrayList<Tile>();
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() != null && gameBoard[i][k].getUnitOnTile().getClass() != Avatar.class && gameBoard[i][k].getUnitOnTile().getOwner()==p) {
					tileRange.add(gameBoard[i][k]);
				}
			}	
		}
		return tileRange;
	}


	/**
	 * @param player p
	 * @return method return player's avatar position as a tile
	 */
	//3)Method returns player's avatar tile position 
	public Tile ownAvatarTile (Player p) {
		Tile avatarTile = null;
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() != null && (gameBoard[i][k].getUnitOnTile() instanceof Avatar) && gameBoard[i][k].getUnitOnTile().getOwner()==p) {
					avatarTile = gameBoard[i][k];
				}
			}	
		}
		
		return avatarTile;
	}

	/**
	 * @param Player p
	 * @return method returns enemy's avatar position as a tile
	 */
	public Tile enemyAvatarTile (Player p) {
		Tile avatarTile = null;
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() != null && (gameBoard[i][k].getUnitOnTile() instanceof Avatar) && gameBoard[i][k].getUnitOnTile().getOwner()!=p) {
					avatarTile = gameBoard[i][k];
				}
			}	
		}
		
		return avatarTile;
	}

	/**
	 * @param Player p and GameState
	 * @return method returns enemy's avatar position as a tile
	 */
	public Tile enemyAvatarTile (Player p, GameState g) { 
		if (p instanceof HumanPlayer) { 
			int x = g.getComputerAvatar().getPosition().getTilex(); int y =
			g.getComputerAvatar().getPosition().getTiley(); return this.getTile(x, y); }
	else { int x = g.getHumanAvatar().getPosition().getTilex(); int y =
			g.getHumanAvatar().getPosition().getTiley(); return this.getTile(x, y); } }
	
	
	/**
	 * @param Player p 
	 * @return method returns all friendly units, including avatar
	 */
	public ArrayList<Monster> friendlyUnitsWithAvatar(Player p) {	
		ArrayList<Monster> tileRange = new ArrayList<Monster>();
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() != null && gameBoard[i][k].getUnitOnTile().getOwner()==p) {
					tileRange.add(gameBoard[i][k].getUnitOnTile());
				}
			}	
		}
		return tileRange;
	}

	/**
	 * @param xPos
	 * @param yPos
	 * @param Player p
	 * @return list of tiles adjacent to specified position that contain an enemy unit
	 */
	public ArrayList <Tile> adjEnemyTiles(int xPos, int yPos, Player p){
		ArrayList<Tile> tileRange = this.adjTiles(this.getTile(xPos, yPos));
		tileRange.removeIf(tile -> (tile.getFreeStatus()||tile.getUnitOnTile().getOwner()==p));
		return tileRange;
	}

	//================= UNIT MOVEMENTS METHODS ========================//
	
	/**
	 * alternative approach
	 * 
	 * trying to create method that does not allow for moving through enemies
	 * 	
	 * NOT WORKING DELETE IF NOT USED
	 */
	
	public ArrayList<Tile> moves(int xpos, int ypos, int moves, Player p){
		HashSet <Tile> tileList = new HashSet<Tile>();
		
		boolean[][][] visited = new boolean[this.Y][this.X][1];
		
		Tile startTile = this.getTile(xpos, ypos);
		
		State startState = new State(startTile, startTile.getUnitOnTile().getMovesLeft());
		
		Queue<State> queue = new LinkedList<State>();
		
		queue.add(startState);
		
		while(! queue.isEmpty()) {
			State current = queue.poll();
			if (current.moves == 0) {
				
				//tileList.add(current.t);
				continue;
			}
			
			else {
				ArrayList<Tile> reachTiles = this.adjTiles(current.t);
				reachTiles.removeIf(tile ->(tile.getFreeStatus()==false && tile.getUnitOnTile().getOwner()!=p));
				tileList.add(current.t);
				System.out.println("added tile: " + current.t + "to reach tiles, moves left " + current.moves); 
				visited[current.t.getTiley()][current.t.getTilex()][0] = true;
				for (Tile t : reachTiles) {
					if (visited[t.getTiley()][t.getTilex()][0] != true) {
		
						State nextState = new State(t, current.moves-1);
						queue.add(nextState);
						
					}	
				}
			}
		}
		
		ArrayList <Tile> list = new ArrayList<Tile>(tileList);
		return list;
	}
	
	//======INNNER CLASS=====//
	//for method above
	class State {
		int xpos, ypos, moves;
		Tile t;
		
		public State(Tile t, int moves) {
			this.moves = moves;
			this.t= t;
			this.xpos = t.getTilex();
			this.ypos = t.getTiley();
		}
		
		public String toString() {
			return "I'm on tile: "+ t +" with " + moves + " left";
		}
	}

	/**
	 * 
	 * @param xpos
	 * @param ypos
	 * @param moveRange
	 * @return this method returns a list of all tiles a selected unit can move to
		within a given range based on the specified position
	 */
	
	public ArrayList<Tile> unitMovableTiles (int xpos, int ypos, int moveRange ){
		Player p = this.getTile(xpos, ypos).getUnitOnTile().getOwner();
		ArrayList <Tile> tileList = this.reachableTiles(xpos, ypos, moveRange);
		tileList.removeIf(t -> !(t.getFreeStatus()));

		return tileList;
	}
	
	
	
	/**
	 * 
	 * @param xpos
	 * @param ypos
	 * @param moveRange
	 * @returnall tiles a unit can reach based on position and movement range of unit
		includes both occupied and unoccupied tiles
	 */

	
	public ArrayList<Tile> reachableTiles (int xpos, int ypos, int moveRange){
		ArrayList<Tile> reachTile = new ArrayList<Tile>();

		for (int i = xpos - moveRange; i <= (xpos + moveRange); i++) {

			for (int j = ypos - moveRange; j <= (ypos + moveRange); j++) {

				// Check if indices are within limits of the board
				if ( (i <= (this.X - 1) && i >= 0) && (j <= (this.Y - 1) && j >= 0)) { 

					// Check each tile index combination is adds up to the range 
					// (abs(i -x) is the distance the current index is away from the monster position)
					if ( (Math.abs(i - xpos) + Math.abs(j - ypos)) <=moveRange) {
						reachTile.add(this.getTile(i, j));
					}
				}
			}  
		}
		return reachTile;
	}
	//====================ATTACK RANGE METHOD SECTION=====================//

	/**
	 * 
	 * @param xpos
	 * @param ypos
	 * @param attackRange
	 * @param moveRange
	 * @return set of all tiles that a unit located at xpos and ypos can attack based on its attack and move range
		the result is returned as a set to eliminate duplicate values within the set
	 */
	
	public ArrayList<Tile> unitAttackableTiles (int xpos, int ypos, int attackRange, int moveRange ){
		Player p = this.getTile(xpos, ypos).getUnitOnTile().getOwner();
		
		ArrayList<Tile> reachTiles;
		HashSet <Tile> tileList = new HashSet<Tile>();
		
		if (moveRange == 0) {
			ArrayList<Tile> list = new ArrayList<Tile>(this.calcAttackRange(xpos, ypos, attackRange, p));
			return list;
		}

		//get a list of all tiles that the unit can reach given their position and move range
		//this includes both free and occupied tiles
		reachTiles = this.reachableTiles(xpos, ypos, moveRange);

		//iterate over the list of tiles that can be reached 
		//if the tile has an enemy unit it is added to the set (no duplicate values)
		for (Tile t : reachTiles) {
	
			if (!(t.getFreeStatus()) && t.getUnitOnTile().getOwner()!=p) tileList.add(t);
		}

		//remove all occupied tiles (enemy or friendly) from reachable tiles list
		reachTiles.removeIf(t -> !(t.getFreeStatus()));		

		//the reachable tile list now only contains unoccupied tiles
		//for each of these unoccupied tiles (that the unit could move to)
		//the attack range (with that tile as origin) is calculated as a set 
		//the set is added to the set to returned (no duplicated values)
		for(Tile t : reachTiles) {
			
			HashSet <Tile> attRange = calcAttackRange(t.getTilex(), t.getTiley(), attackRange, p);
			tileList.addAll(attRange);
		}	
		ArrayList<Tile> list = new ArrayList<Tile>(tileList);
		return list;	
	}		  

	/**
	 * 
	 * @param xpos
	 * @param ypos
	 * @param attackRange
	 * @param p
	 * @return set of all tiles containing an enemy unit within a specified range
	 */
	
	public HashSet<Tile> calcAttackRange(int xpos, int ypos, int attackRange, Player p){
		HashSet<Tile> tileList = new HashSet<Tile>();

		for (int i = xpos - attackRange; i <= (xpos + attackRange); i++) {

			for (int j = ypos - attackRange; j <= (ypos + attackRange); j++) {

				
				// Check if indices are within limits of the board
				if ( (i <= (this.X - 1) && i >= 0) && (j <= (this.Y - 1) && j >= 0)) { 
					

					 // This gets all tiles in a square around the centre Monster, up to a width-length of the attack range
				    if(this.getTile(i, j).getUnitOnTile() != null && this.getTile(i, j).getUnitOnTile().getOwner() != p) {
						 tileList.add(this.getTile(i, j));
					}
					
				}
			}
		}
		return tileList;
	}
	//====================accessors methods==========================//


	/**
	 * 
	 * @param player p
	 * @return list of all monsters (including avatars) on the board which have onCooldow == true
		this variable signal that the monster cannot attack/move in the current turn (right after summoning)
	 */
	public ArrayList<Monster> coolDownCheck (Player p){
		ArrayList<Monster> monsterList = new ArrayList<Monster>();

		for (int i = 0; i <gameBoard.length; i++) {

			for (int k =0; k<gameBoard[0].length; k++) {
				
				if ((gameBoard[i][k].getUnitOnTile() != null)&& gameBoard[i][k].getUnitOnTile().getOnCooldown() && gameBoard[i][k].getUnitOnTile().getOwner()==p) {
					monsterList.add(this.gameBoard[i][k].getUnitOnTile()); 
				}
				 
			}
		}
		return monsterList;

	}
		
	/**
	 * 
	 * @param Player p
	 * @return list of all friendly units on the board returned as monster reference
	 */
	public ArrayList<Monster> friendlyUnitList (Player p){
		ArrayList<Monster> monsterList = new ArrayList<Monster>();
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() != null && gameBoard[i][k].getUnitOnTile().getOwner()==p) 							{
					monsterList.add(gameBoard[i][k].getUnitOnTile());
				}
			}	
		}
		return monsterList;
	}
		
	/**
	 * 
	 * @return all free tiles on the board
	 */
	
	public ArrayList<Tile> allFreeTiles(){
		ArrayList<Tile> freeTilesList = new ArrayList<Tile>();
		for (int i = 0; i <gameBoard.length; i++) {
			for (int k =0; k<gameBoard[0].length; k++) {
				if (gameBoard[i][k].getUnitOnTile() == null) 							{
					freeTilesList.add(gameBoard[i][k]);
				}
			}	
		}
		return freeTilesList;
	}
	

	//======= OTHER METHODS======== \\
	public ArrayList<Tile> unitAllActionableTiles (int xpos, int ypos, int attackRange, int moveRange ){
		
		
		HashSet <Tile> tileList = new HashSet<Tile>();
		
		// Get a list of all tiles that the unit can reach given their position and move range
		ArrayList<Tile> reachTiles = this.reachableTiles(xpos, ypos, moveRange);

		//the reachable tile list now only contains unoccupied tiles
		//for each of these unoccupied tiles (that the unit could move to)
		//the attack range (with that tile as origin) is calculated as a set 
		//the set is added to the set to returned (no duplicated values)
		for(Tile t : reachTiles) {
			
			// Add movement tile
			tileList.add(t);

			HashSet <Tile> attRange = new HashSet<Tile>();
			
			// Find tiles around tile t respective of the units attack range
			for (int i = t.getTilex() - attackRange; i <= (t.getTilex()+ attackRange); i++) {
				for (int j = t.getTiley() - attackRange; j <= (t.getTiley() + attackRange); j++) {

					// Check if indices are within limits of the board
					if ( (i <= (this.X - 1) && i >= 0) && (j <= (this.Y - 1) && j >= 0)) { 
							tileList.add(this.getTile(i, j));				
					}
				}
			}	
			
			tileList.addAll(attRange);
		}	
		
		ArrayList<Tile> list = new ArrayList<Tile>(tileList);
		return list;	
	}		
	

	// Cardinally adjectent tiles to tile t
	public ArrayList<Tile> cardinallyAdjTiles(Tile t) {
		
		// Set up
		ArrayList<Tile> returnArr = new ArrayList<Tile>(4); 
		int xpos = t.getTilex(); 
		int ypos = t.getTiley();

		// Find tiles around tile t
		for (int i = xpos - 1; i <= (xpos + 1); i++) {
			for (int j = ypos - 1; j <= (ypos + 1); j++) {

				// Check if indices are within limits of the board
				if ( (i <= (this.X - 1) && i >= 0) && (j <= (this.Y - 1) && j >= 0)) { 
					
					// Only add cardinally adjacent
					if (i != xpos && j == ypos) {
						returnArr.add(this.getTile(i, j));
					}
					
					if (i == xpos && j != ypos) {
						returnArr.add(this.getTile(i, j));
					}
				}
			}
		}
		
		return returnArr;
	}
	
	
}


	
	