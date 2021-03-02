package Pacman;

import java.util.HashMap;

import javafx.geometry.Point2D;
import javafx.scene.image.WritableImage;

public class Tile extends Sprite{
	private boolean isPassage;
	private boolean hasCollectible;
	private boolean isRestricted = false;
	public static final double width = GameStage.cellWidth;
	public static final double height = GameStage.cellHeight;
	private Collectible collectible;
	private Tile UP;
	private Tile DOWN;
	private Tile LEFT;
	private Tile RIGHT;
	private HashMap <String, Tile> neighbors;
	
	public Tile (boolean isPassage, boolean hasCollectible,  int col, int row) {
		super(col, row, width, height);
		this.isPassage = isPassage;
		this.hasCollectible = hasCollectible;
		this.collectible = null;
	}
	
	public boolean isPassage() {
		return isPassage;
	}

	//Getters
	public boolean checkHasCollectible() {
		return this.hasCollectible;
	}
	
	public Collectible getCollectible() {
		return this.collectible;
	}
	
	//Setters
	public void setCollectible(String type) {
		this.collectible = new Collectible(type);
	}
	
	public void setCollectible(Collectible c) {
		this.collectible = c;
	}
	
	public void setHasCollectible(boolean b) {
		this.hasCollectible = b;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.col;
	}
	
	public void setNeighbors() {
		this.UP = Maze.tileMap[this.row-1][this.col];
		this.DOWN = Maze.tileMap[this.row+1][this.col];
		this.LEFT = Maze.tileMap[this.row][this.col-1];
		this.RIGHT = Maze.tileMap[this.row][this.col+1];
		neighbors = new HashMap <String, Tile>();
		neighbors.put("UP", this.UP);
		neighbors.put("LEFT", this.LEFT);
		neighbors.put("DOWN", this.DOWN);
		neighbors.put("RIGHT", this.RIGHT);
		
	}
	
	public Tile getNeighbor(String dir) {
		try {
			return neighbors.get(dir);
		} catch (NullPointerException e) {
			return this;
		}
	}

	public String getDirectionFromTile(Tile neighbor) {
        for (String dir: this.neighbors.keySet()) {
            if (neighbors.get(dir).equals(neighbor)) {
                return dir;
            }

	}

        return "";
	
	}
	
	public boolean accessibleToPacman() {
		return this.isPassage && !this.isRestricted();
	}

	public boolean isRestricted() {
		return isRestricted;
	}

	public void setRestricted(boolean isRestricted) {
		this.isRestricted = isRestricted;
	}
	
}

