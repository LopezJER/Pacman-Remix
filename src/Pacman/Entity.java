package Pacman;

import java.util.ArrayList;

import javafx.scene.shape.Line;

//interface Entity{
//	float initRow = 0;
//	float initCol = 0;
//	float initX = initRow * GameStage.cellWidth;
//	float initY = initCol * GameStage.cellHeight;
//	void move();
//}



public abstract class Entity extends Sprite{
	

	protected Tile currentTile;
	protected Tile targetTile;
	protected Tile pivotTile;
	protected String direction;
	protected boolean inGhostHouse;
	protected boolean canEnterGhostHouse;
	protected int entityMovementSpeedPx = 4;
	protected ArrayList<Tile> restrictedTiles = new ArrayList <Tile>();

	public Entity(int col, int row, double width, double height) {
		super (col, row, width, height);
		this.currentTile = this.locateTile(this.x, this.y);
		//this.pivotTile = new Tile(currentTile.isPassage(), currentTile.checkHasCollectible(), currentTile.col, currentTile.row);
		this.targetTile = new Tile(currentTile.isPassage(), currentTile.checkHasCollectible(), currentTile.col, currentTile.row);

		
		// TODO Auto-generated constructor stub
	}

	public Entity(double col, double row, double width, double height) {
		super (col, row, width, height);
		this.currentTile = this.locateTile(this.x, this.y);
		this.pivotTile = new Tile(currentTile.isPassage(), currentTile.checkHasCollectible(), currentTile.col, currentTile.row);
		System.out.println(pivotTile.row + " " + pivotTile.col);
		this.targetTile = new Tile(currentTile.isPassage(), currentTile.checkHasCollectible(), currentTile.col, currentTile.row);
		// TODO Auto-generated constructor stub
	}

	//Methods
	abstract void move();
	


	
	//Getters
	protected static double getInitX(double initRow) {
		return initRow * GameStage.cellWidth;
	}
	
	protected static double getInitY(double initCol) {
		return initCol * GameStage.cellHeight;
	}
	
	protected void setDirection(String direction) {
		this.direction = direction;
	}
	protected String getDirection() {
		return this.direction;
	}	
	
	protected void stop() {
		this.dx=0;
		this.dy=0;
	}
	}

