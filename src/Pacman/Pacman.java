/********************************************************************************************************************************************************
* 
* In creating the Pacman game, inheritance was used through the Sprite and Entity classes to model shared movement characteristics between Pacman and the ghosts. 
* Some examples of these characteristics are current tile, xy-positions, and open tile neighbors--information which allow them to move according to the bounds of the maze. 
* Both entities share a target tile system when traversing the maze but the mechanics for each entity differs to reflect their distinct personalities. 
* But while the 'chase' movements of the ghosts, for example are different from each other, we attempted to abstract it by creating a general method for it in the 
* Ghost class which acted as an external interface for other classes but the method itself calls more specific chase methods and mechanics 
* depending on the name of the ghost calling the method. The same principle of abstraction was used in the collectibles and how they spawn, animate, and
* disappear relative to the maze and Pacman.
*
* @authors Paul Allen B. Asumen  || Jose Enrique R. Lopez
* @date created 2019-12-11 17:30
*********************************************************************************************************************************************************/






package Pacman;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Line;

public class Pacman extends Entity{
	
	public final static Image PACMAN_IMAGE = new Image ("pacman1.png", GameStage.cellWidth, GameStage.cellHeight, true, true);
	public static final double width = GameStage.cellWidth;
	public static final double height = GameStage.cellHeight;
	public final static double initCol = 14;
	public final static double initRow = 26;	
	public static boolean  eatenFruit = false;
	public static int lives = 3;
    public String tempDirection = "LEFT";
    public ArrayList <String> dirs = new ArrayList<String>();
	public HashMap <String, ArrayList <WritableImage>> sprites = new HashMap<String, ArrayList <WritableImage>>();
	private boolean isMoving;
	private boolean foundTargetTile = false;
	public Pacman() {
		super(initCol, initRow, width, height);

		this.direction = "LEFT";
		this.row = this.currentTile.row;
		this.col = this.currentTile.col;
		restrictedTiles.add(Maze.tileMap[5][16]);
		restrictedTiles.add(Maze.tileMap[5][17]);
		initSprite();
		
		// TODO Auto-generated constructor stub
	}
	
	private void initSprite() {
		sprites.put ("UP", new ArrayList<WritableImage>());
		sprites.put ("DOWN", new ArrayList<WritableImage>());
		sprites.put ("LEFT", new ArrayList<WritableImage>());
		sprites.put ("RIGHT", new ArrayList<WritableImage>());
		sprites.put ("EATEN", new ArrayList<WritableImage>());
		
		sprites.get("LEFT").add(new WritableImage(Maze.spritesheet.getPixelReader(),0*Maze.SPRITESHEET_CELL_WIDTH,6*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("LEFT").add(new WritableImage(Maze.spritesheet.getPixelReader(),4*Maze.SPRITESHEET_CELL_WIDTH,6*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("LEFT").add(new WritableImage(Maze.spritesheet.getPixelReader(),0*Maze.SPRITESHEET_CELL_WIDTH,14*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("UP").add(new WritableImage(Maze.spritesheet.getPixelReader(),2*Maze.SPRITESHEET_CELL_WIDTH,6*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("UP").add(new WritableImage(Maze.spritesheet.getPixelReader(),6*Maze.SPRITESHEET_CELL_WIDTH,6*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("UP").add(new WritableImage(Maze.spritesheet.getPixelReader(),0*Maze.SPRITESHEET_CELL_WIDTH,14*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("DOWN").add(new WritableImage(Maze.spritesheet.getPixelReader(),10*Maze.SPRITESHEET_CELL_WIDTH,6*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("DOWN").add(new WritableImage(Maze.spritesheet.getPixelReader(),14*Maze.SPRITESHEET_CELL_WIDTH,6*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("DOWN").add(new WritableImage(Maze.spritesheet.getPixelReader(),0*Maze.SPRITESHEET_CELL_WIDTH,14*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("RIGHT").add(new WritableImage(Maze.spritesheet.getPixelReader(),8*Maze.SPRITESHEET_CELL_WIDTH,6*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("RIGHT").add(new WritableImage(Maze.spritesheet.getPixelReader(),12*Maze.SPRITESHEET_CELL_WIDTH,6*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		sprites.get("RIGHT").add(new WritableImage(Maze.spritesheet.getPixelReader(),0*Maze.SPRITESHEET_CELL_WIDTH,14*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
	}
		


	
	public boolean hasValidDirection () {
		
		if (!dirs.isEmpty()) {
			for (String dir: dirs) {
				if (this.currentTile.getNeighbor(dir).accessibleToPacman() && this.currentTile.getX() == this.x &&   this.currentTile.getY()==this.y) {
					
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
		return false;
	}
	
	public void updateTargetTile() {
		int row = currentTile.row;
		int col = currentTile.col;
		while (!foundTargetTile) {
		
			switch(this.direction) {
			case "UP":
				if (!Maze.tileMap[this.targetTile.row-1][this.targetTile.col].accessibleToPacman()) {
					foundTargetTile = true;
				} else {
					this.targetTile = Maze.tileMap[row--][col];
				}
				break;
			case "DOWN":
				if (!Maze.tileMap[this.targetTile.row+1][this.targetTile.col].accessibleToPacman()) {
					foundTargetTile = true;
				} else {
					this.targetTile = Maze.tileMap[row++][col];
				}
				break;
			case "LEFT":
				try {
					System.out.println("Hello");
					if (!Maze.tileMap[this.targetTile.row][this.targetTile.col-1].accessibleToPacman()) {
						foundTargetTile = true;
					} else {
						
						this.targetTile = Maze.tileMap[row][col--];
					
					}} catch(ArrayIndexOutOfBoundsException e) {this.targetTile = Maze.tileMap[row][27]; foundTargetTile = true;}
					break;
				
			case "RIGHT":
				try {
				System.out.println("Looking for target");
				if (!Maze.tileMap[this.targetTile.row][this.targetTile.col+1].accessibleToPacman()) {
					foundTargetTile = true;
				} else {
					this.targetTile = Maze.tileMap[row][col++];
				}} catch(ArrayIndexOutOfBoundsException e) {this.targetTile = Maze.tileMap[row][0]; foundTargetTile = true;}
				break;
			default:
				foundTargetTile = true;
				break;
			}

		}
	}
	public void move() {
			
			this.foundTargetTile = false;
			this.updateCurrentTile();
			this.updateTargetTile();
		
			if(currentTile.checkHasCollectible()) {
			
				if(currentTile.getCollectible().getType().equals("Energizer") || currentTile.getCollectible().getType().equals("Energizer2")) {
					for (Ghost g: GameTimer.ghosts) {
						if(g.getMode().equals(Ghost.CHASE) || g.getMode().equals(Ghost.SCATTER)) {
							g.setMode(Ghost.FRIGHTENED);
							Ghost.isNotified=true;
							
						}
					}					
				}
				this.eatCollectible(currentTile);
			}
			
			if (!this.currentTile.equals(this.targetTile)) {
				
				this.x += 4*this.dx;
				this.y += 4*this.dy;
			} else {
				this.setDirection("");
				this.dx = 0;
				this.dy = 0;
				foundTargetTile=false;
			}
			this.updateCurrentTile();

		}
		
		

		/*int currRow = currentTile.getRow();
		int currCol = currentTile.getCol();
		Tile[] neighbors = {Maze.tileMap[currRow-1][currCol-1],Maze.tileMap[currRow-1][currCol], Maze.tileMap[currRow-1][currCol+1],
							Maze.tileMap[currRow][currCol-1],	Maze.tileMap[currRow][currCol+1],
							Maze.tileMap[currRow+1][currCol-1], Maze.tileMap[currRow+1][currCol], Maze.tileMap[currRow+1][currCol+1]};
		*/
		
		//System.out.println("Pacman Center XY: " + this.x + " " + this.y);
		//System.out.println("Pacman Edge XY: " + this.getCanvasPoint().getX() + " " + this.getCanvasPoint().getY());
		//System.out.println("Pacman XY: " + this.row + " " + this.col);
	
	
	public void move(ArrayList<String> dirs) {

	}
	
	public Tile getCurrentTile() {
		return this.currentTile;
	}
				


	private void updateCurrentTile() {
		Tile tile = this.locateTile(this.x, this.y);
		if (this.x%8==0 &&this.x%16!=0 && this.y%8==0 && this.y%16!=0){
			this.currentTile = this.locateTile(this.x, this.y);
			this.row = this.currentTile.row;
			this.col = this.currentTile.col;
		}
		
	}
	
	private void eatCollectible(Tile collectible) {
		collectible.setVisible(false);
		collectible.loadImage(Maze.tileImgMap.get(OPEN_PATH_KEY));
		collectible.setHasCollectible(false);
		
		//Addition of points
		GameStage.setPoints(GameStage.getPoints() + collectible.getCollectible().getPoints());
		if (collectible.getCollectible().getType().equals(Collectible.ENERGIZER)) {
			try {
				this.loadImage(Maze.fifty);
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
				
		}
		else if (collectible.getCollectible().getType().equals(Collectible.FRUIT)) {
				this.loadImage(Maze.twoh);
		}
			
		

	
	}
	
	public boolean isMoving() {
		return this.isMoving;
	}

	public void loadImage() {
		ArrayList <WritableImage> faces = this.sprites.get(this.direction);
		
		try {
		for (Image face: faces) {
			this.loadImage(faces.get(GameTimer.timeNanoPassed%2));
		}} catch (NullPointerException e){}
	}
	
	public int getLives() {
		return this.lives;
	}
	public void decreaseLives() {
		this.lives--;
	}
	
	public boolean isAlive() {
		return this.lives>0;
	}
}
