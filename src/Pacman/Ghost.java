package Pacman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Ghost extends Entity{
	//Constants
	public int initRow = 17;
	public int initCol = 11;
	//Ghost Names
	public final static String BLINKY = "Blinky";
	public final static String INKY = "Inky";
	public final static String PINKY = "Pinky";
	public final static String CLYDE = "Clyde";
	//Ghost modes
	public final static String SCATTER = "Scatter";
	public final static String CHASE = "Chase";
	public final static String FRIGHTENED = "Frightened";
	public final static String EATEN = "Eaten";
	//Ghost positions
	public final static Point2D InitPosI = new Point2D (11, 17);
	public final static Point2D InitPosP = new Point2D (13, 17);
	public final static Point2D InitPosC = new Point2D  (15, 17);
	public final static Point2D InitPosB = new Point2D  (14, 14);
	//Ghost characteristics
	public static int SPEED = 2;
	public static final double width = GameStage.cellWidth;
	public static final double height = GameStage.cellHeight;
	private String name;
	private String mode = "";
	//Ghost images
	public final static WritableImage frightenedImage = new WritableImage(Maze.spritesheet.getPixelReader(),16*Maze.SPRITESHEET_CELL_WIDTH,8*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2);
	public final static WritableImage eatenImage = new WritableImage(Maze.spritesheet.getPixelReader(),16*Maze.SPRITESHEET_CELL_WIDTH,18*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2);
	//booleans
	private boolean targetTileSet = false;
	private boolean turnedAround = false;
	public final static HashMap <String, Tile> scatterTargetTiles = new HashMap <String, Tile>();
	public boolean isFrightened = false;
	public static boolean isNotified=false;
	private ArrayList<Image> sprites = new ArrayList<Image>();
	private int spriteIndex;
	private boolean enteredTunnel = false;


	
	public Ghost(String name, Point2D initPos) {
		super(initPos.getX(), initPos.getY(), width, height);
		// TODO Auto-generated constructor stub
//		WritableImage ghostImg = new WritableImage();
		this.name = name;
		int x = 0,y = 0;
		switch(name) {
			case BLINKY:
				x=0;
				y=12;
				this.direction = "LEFT";
				break;
			case INKY:
				x=16;
				y=16;
				this.direction = "RIGHT";
				break;
			case PINKY:
				x=0;
				y=16;
				this.direction = "UP";
				break;
			case CLYDE:
				x=0;
				y=18;
				this.direction = "LEFT";
				break;
		}
		this.inGhostHouse = isInGhostHouse();
		this.targetTileSet = false;
		this.turnedAround = false;
		this.entityMovementSpeedPx = SPEED;
		this.setMode(CHASE);
		
		//Loading of regular ghost sprites to ArrayList sprites 
		for(int i=0;i<8;i++) {
			this.sprites.add(new WritableImage(Maze.spritesheet.getPixelReader(),(x+i*2)*Maze.SPRITESHEET_CELL_WIDTH,y*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		}
		//Loading of eaten ghost sprites to ArrayList sprites
		for(int i=0;i<8;i++) {
			this.sprites.add(new WritableImage(Maze.spritesheet.getPixelReader(),(16+i*2)*Maze.SPRITESHEET_CELL_WIDTH,18*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		}
		//Loading of frightened ghost sprites to ArrayList sprites
		for(int i=0;i<4;i++) {
			this.sprites.add(new WritableImage(Maze.spritesheet.getPixelReader(),(12+i*2)*Maze.SPRITESHEET_CELL_WIDTH,8*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		}
		
		//WritableImage wi = new WritableImage(Maze.spritesheet.getPixelReader(),x*Maze.SPRITESHEET_CELL_WIDTH,y*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2);

		this.loadImage(this.sprites.get(this.spriteIndex));
		
		scatterTargetTiles.put(BLINKY, Maze.tileMap[0][0]);
		scatterTargetTiles.put(INKY, Maze.tileMap[31][27]);
		scatterTargetTiles.put(PINKY, Maze.tileMap[0][27]);
		scatterTargetTiles.put(CLYDE, Maze.tileMap[0][27]);
	
}
	

	public void roam(Pacman pacman) {
		
		switch (this.mode) {
		case CHASE:
			this.chase(pacman);
			break;
		case SCATTER:
			this.scatter();
			break;
		case FRIGHTENED:
			this.loadImage(Ghost.frightenedImage);
			this.runAway();
			break;
		case EATEN:
			this.loadImage(Ghost.eatenImage);
			this.goHome();
			break;
		default:
			break;
		}
	}
	
	public void chase (Pacman pacman) {
		if (this.isInGhostHouse()) {
			this.escapeGhostHouse();	
		} 
		else {

			this.restrictedTiles.addAll(Maze.ghostHouseTiles);
			switch(this.name) {
			case BLINKY: this.blinkyChase(pacman.getCurrentTile());
			break;
			case CLYDE: 
				this.clydeChase(pacman.getCurrentTile());
			break;
			case INKY:
				this.inkyChase(pacman, GameTimer.ghosts.get(0));
			case PINKY: this.pinkyChase(pacman); break;
			}
		}
	}
	
	public void inkyChase(Pacman pacman, Ghost blinky) {
		if (!this.targetTileSet) {
			setInkyTargetTile(pacman, blinky);
		this.targetTileSet=true;
		this.choosePath();
		} else {
			setInkyTargetTile(pacman, blinky);
			if (this.currentTile.equals(this.pivotTile)) {
				this.choosePath();
			}
			this.move();
			this.updateCurrentTile();	
		}
		


		
	}
	
	private void setInkyTargetTile(Pacman pacman, Ghost blinky) {
		Tile tempTile = Maze.tileMap[0][0];
		switch(pacman.getDirection()) {
		case "UP":
			try {
			tempTile = Maze.tileMap[pacman.getRow()-2][pacman.getCol()-2];
			} catch(ArrayIndexOutOfBoundsException e) {}
			break;
		case "DOWN":
			try {
			tempTile = Maze.tileMap[pacman.getRow()+2][pacman.getCol()];
			break; }catch(ArrayIndexOutOfBoundsException e) {}
		case "LEFT":
			try {
			tempTile = Maze.tileMap[pacman.getRow()][pacman.getCol()-2];
			break; }catch(ArrayIndexOutOfBoundsException e) {}
		case "RIGHT":
			try {
			tempTile = Maze.tileMap[pacman.getRow()][pacman.getCol()+2];
			break;}catch(ArrayIndexOutOfBoundsException e) {}
		default:
			break;
		}
		

		int targetTileRow = GameStage.numRows - blinky.getRow();
		int targetTileCol = GameStage.numCols - blinky.getCol();
		this.targetTile = Maze.tileMap[targetTileRow][targetTileCol];

		
	}
	
	
	public void scatter() {
		this.mode = SCATTER;
		if (this.isInGhostHouse()) {
			this.escapeGhostHouse();			
		}else {
			this.restrictedTiles.addAll(Maze.ghostHouseTiles);
			if (!this.targetTileSet) {
				this.targetTileSet = true;
				this.targetTile = scatterTargetTiles.get(this.name);
				this.choosePath();
			} else {
				if (this.currentTile.equals(pivotTile)) {
					this.choosePath();
				}
				this.move();
				this.updateCurrentTile();			
			}
		}		
	}
	

	
	
	public void goHome() {
		SPEED = 16;
		System.out.println("GO HOME");
		this.restrictedTiles.clear();
		if (!this.targetTileSet) {
			this.targetTile = Maze.tileMap[16][13];
			this.targetTileSet=true;
			System.out.println("this.turnedAROUND????? " + this.turnedAround );
			this.choosePath();
		}
		else {
			this.targetTile =  Maze.tileMap[16][13];
			if (this.currentTile.equals(this.pivotTile)) {
				this.choosePath();
			}

			this.move();
			this.updateCurrentTile();	
			
		}
			
			System.out.println("HIYAAAAAAA" + this.targetTile.row + " " + this.targetTile.col);
			
		if(this.currentTile.equals(Maze.tileMap[16][13]))
			this.mode = CHASE;
		
	}
	public void runAway() {
		this.loadImage(eatenImage);
		SPEED = 4;
		if (Ghost.isNotified) {
			this.turnAround();
			isNotified=false;
		} else if (this.currentTile.equals(pivotTile) ) {
				ArrayList <Tile> neighbors = new ArrayList<Tile>();
				neighbors = getNeighbors();
				Random r = new Random();
				int choice = r.nextInt(neighbors.size());
				this.pivotTile = neighbors.get(choice);
				this.setDirectionFromPivot(this.currentTile, this.pivotTile);
			}

			
		
		System.out.println(this.direction);
		this.move();
		this.updateCurrentTile();
		
		

	}
		
		
	
	
	public boolean checkIfFrightened() {
		return isFrightened;
	}


	
	
	
	public void blinkyChase(Tile pacmanTile) {
		this.updateCurrentTile();
		if (!this.targetTileSet || this.enteredTunnel) {
		this.targetTile = pacmanTile;
		this.targetTileSet=true;
		this.choosePath();
		} else {
			if (!this.enteredTunnel) {
				this.targetTile = pacmanTile;
			}
	
			if (this.currentTile.occupies(this.pivotTile) && this.x % 16!=0 && this.y%16!=0 &&this.x%8==0 &&this.y%8==0) {
				this.choosePath();
				if(this.enteredTunnel) {
					this.enteredTunnel = false;
				}
			}
			

		}
		this.move();
		this.updateCurrentTile();	
		
	

		
	}
	
	private void turnAround () {

		SPEED = -SPEED;
		switch(this.direction) {
		case "UP":
			this.direction = "DOWN";
		case "DOWN":
			this.direction = "UP";
		case "LEFT":
			this.direction = "RIGHT";
		case "RIGHT":
			this.direction = "LEFT";
		}

		//this.setDirectionFromPivot(currentTile, pivotTile);
		this.turnedAround = true;
	}
	
	private void setPinkyTargetTile(Pacman pacman) {
		switch(pacman.getDirection()) {
		case "UP":
			try{
			this.targetTile = Maze.tileMap[pacman.getRow()-4][pacman.getCol()-4];
			break; } catch(ArrayIndexOutOfBoundsException e) {this.targetTile = Maze.tileMap[pacman.getRow()-1][pacman.getCol()-1];}
		case "DOWN":
			try {
			this.targetTile = Maze.tileMap[pacman.getRow()+4][pacman.getCol()];
			break; } catch(ArrayIndexOutOfBoundsException e) {this.targetTile = Maze.tileMap[pacman.getRow()+1][pacman.getCol()];}
		case "LEFT":
			try {
			this.targetTile = Maze.tileMap[pacman.getRow()][pacman.getCol()-4];
			break;  } catch(ArrayIndexOutOfBoundsException e) {this.targetTile = Maze.tileMap[pacman.getRow()][pacman.getCol()-1];}
		case "RIGHT":
			try {
			this.targetTile = Maze.tileMap[pacman.getRow()][pacman.getCol()+4];
			break; } catch (ArrayIndexOutOfBoundsException e) {this.targetTile = Maze.tileMap[pacman.getRow()][pacman.getCol()+1];}
		default:
			break;
		}
		

		
	}
	public void pinkyChase (Pacman pacman) {
		if (!this.targetTileSet) {
			setPinkyTargetTile(pacman);
		this.targetTileSet=true;
		this.choosePath();
		} else {
			setPinkyTargetTile(pacman);
			if (this.currentTile.equals(this.pivotTile)) {
				this.choosePath();
			}
			this.move();
			this.updateCurrentTile();	
		}
		

	}
	
	
	public void clydeChase (Tile pacmanTile) {
		if(this.currentTile.findDistance(pacmanTile) > 8*GameStage.cellWidth) {
			this.blinkyChase(pacmanTile);
//			this.scatter();
			System.out.println("Clyde is currently in chase mode.");
		}
		else {
			this.scatter();
			System.out.println("Clyde is currently in scatter mode.");
			//this.setMode(CHASE);
		}
		
	}

		
	
	private void choosePath() {
		ArrayList <Tile> neighbors = this.getNeighbors();
	
		for (Tile tile: neighbors) {

		}
		this.pivotTile = pickClosestTileToTarget(neighbors);
		
		this.setDirectionFromPivot(this.currentTile, this.pivotTile);
	}
	public boolean isInGhostHouse() {
		for (Tile tile : Maze.ghostHouseTiles) {
			if (tile.equals(this.currentTile)) {

				return true;
			}
		}
		return false;
	}
	
	public boolean hasTurnedAround() {
		return this.turnedAround;
	}
	
	
	private void escapeGhostHouse() {
		if (!this.targetTileSet || this.targetTile.equals(Maze.tileMap[16][13])) {
			this.targetTileSet = true;
			this.targetTile = Maze.tileMap[14][13];
			this.choosePath();
		} else {
			if (this.currentTile.equals(pivotTile)) {
				this.choosePath();
			}
			this.move();
			this.updateCurrentTile();
		}
		if (!this.isInGhostHouse()) {
			targetTileSet = false;
		}

		System.out.println("CLYDE " + this.direction + " " + this.targetTile.getRow() + " " + this.targetTile.getCol());

	}
	
	private ArrayList <Tile> getNeighbors() {
		ArrayList <Tile> neighbors = new ArrayList<Tile>();
		
		if (!this.hasTurnedAround()) {
		switch (this.direction) {
		case "UP":
			neighbors = new ArrayList <Tile> (Arrays.asList(this.currentTile.getNeighbor("UP"), this.currentTile.getNeighbor("LEFT"), this.currentTile.getNeighbor("RIGHT")));
		break;
		case "DOWN":
			neighbors = new ArrayList <Tile> (Arrays.asList(this.currentTile.getNeighbor("DOWN"), this.currentTile.getNeighbor("LEFT"), this.currentTile.getNeighbor("RIGHT")));
		break;
		case "LEFT":
			try {
			neighbors = new ArrayList <Tile> (Arrays.asList(this.currentTile.getNeighbor("LEFT"), this.currentTile.getNeighbor("UP"), this.currentTile.getNeighbor("DOWN")));
			}catch(Exception e) {return neighbors;}
			break;
			
		case "RIGHT":
			try {
			neighbors = new ArrayList <Tile> (Arrays.asList(this.currentTile.getNeighbor("RIGHT"), this.currentTile.getNeighbor("UP"), this.currentTile.getNeighbor("DOWN")));
			} catch(Exception e) {return neighbors;}
		break;
			
		default:
			neighbors = new ArrayList <Tile>();
		}}

		//}
		
		else {
			neighbors =  new ArrayList <Tile> (Arrays.asList(this.currentTile.getNeighbor("UP"), this.currentTile.getNeighbor("LEFT"), this.currentTile.getNeighbor("RIGHT"), this.currentTile.getNeighbor("DOWN")));
			this.turnedAround = false;
		}
		for (Tile tile: neighbors) {
			System.out.print(tile.row+ " " + tile.col + "|");
		}
		
		System.out.println();
		

	Iterator <Tile> iter = neighbors.iterator();
			
		while (iter.hasNext()) {
	
		Tile tile = iter.next();
	
		if (!tile.isPassage() || restrictedTiles.contains(tile)) {
			iter.remove();
		}
		}
		return neighbors;
	}
		
		
		
		
	
	
	private Tile pickClosestTileToTarget(ArrayList <Tile> neighbors) {

		double minDist = Math.sqrt(GameStage.WINDOW_HEIGHT*GameStage.WINDOW_HEIGHT + GameStage.WINDOW_WIDTH*GameStage.WINDOW_WIDTH);
		Tile closestTile = currentTile;
		for (Tile neighbor : neighbors) {
			double neighborDist = neighbor.findDistance(this.targetTile);
			if (neighborDist<minDist) {
				minDist = neighborDist;
				closestTile = neighbor;
			}
		}
		
		
		return closestTile;
	
	}
	
	private void setDirectionFromPivot (Tile currentTile, Tile pivotTile) {
		try {
			this.setDirection(this.currentTile.getDirectionFromTile(pivotTile));
		}catch(Exception e) {}
	}
	
	
	private void updateCurrentTile() {
		Tile tile = this.locateTile(this.x, this.y);
		if (this.x%8==0 &&this.x%16!=0 && this.y%8==0 && this.y%16!=0){
			this.currentTile = this.locateTile(this.x, this.y);
			this.row = this.currentTile.row;
			this.col = this.currentTile.col;

		} else {

		}

	}
	
	public void move() {
		System.out.println("GHOST DIRECTION: " + this.direction);
		if (this.x<-16) {
			this.setDirection("LEFT");
			this.x = GameStage.WINDOW_WIDTH+16;
			this.y=17*GameStage.cellHeight+0.5*GameStage.cellHeight;
			this.targetTile = Maze.tileMap[17][27];
			this.enteredTunnel  = true;
		} else if (this.x > GameStage.WINDOW_WIDTH + 16) {
			this.setDirection("RIGHT");
			this.x = -16;
			this.y=17*GameStage.cellHeight+0.5*GameStage.cellHeight;
			this.targetTile = Maze.tileMap[17][0];
			this.enteredTunnel = true;
		} else {
		
			
			if (this.x<-16) {
				this.setDirection("LEFT");
				this.x = GameStage.WINDOW_WIDTH+16;
			} else if (this.x > GameStage.WINDOW_WIDTH + 16) {
				this.setDirection("RIGHT");
				this.x = -16;
			} else {
			
			this.changeSprite();
				
			if(this.direction == "UP") {
//				if(this.spriteIndex == 7)
//					this.setSpriteIndex(6);
//				else
//					this.setSpriteIndex(7);
				
				this.setDY(-this.entityMovementSpeedPx);   
				this.setDX(0);
			}

			else if(this.direction == "LEFT") {
//				if(this.spriteIndex == 4)
//					this.setSpriteIndex(5);
//				else
//					this.setSpriteIndex(4);
				
				this.setDX(-this.entityMovementSpeedPx);
				this.setDY(0); 
			}

			else if(this.direction ==  "DOWN") {
//				if(this.spriteIndex == 2)
//					this.setSpriteIndex(3);
//				else
//					this.setSpriteIndex(2);
				
				this.setDY(this.entityMovementSpeedPx);
				this.setDX(0);
			}
			
			else if(this.direction == "RIGHT") {
//				if(this.spriteIndex == 0)
//					this.setSpriteIndex(1);
//				else
//					this.setSpriteIndex(0);
				
				this.setDX(this.entityMovementSpeedPx);
				this.setDY(0); 
			}
			// TODO Auto-generated method stub
			
//			this.loadImage(this.sprites.get(this.spriteIndex));
			this.x+=this.dx;
			this.y+=this.dy;
			}
		
		}
	}
	
	void changeSprite() {
		if(this.mode.equals(SCATTER) || this.mode.equals(CHASE)) {
			switch(this.direction) {
				case "UP":
					if(this.spriteIndex == 7)
						this.setSpriteIndex(6);
					else
						this.setSpriteIndex(7);
					break;
				case "LEFT":
					if(this.spriteIndex == 4)
						this.setSpriteIndex(5);
					else
						this.setSpriteIndex(4);
					break;
				case "DOWN":
					if(this.spriteIndex == 2)
						this.setSpriteIndex(3);
					else
						this.setSpriteIndex(2);
					break;
				case "RIGHT":
					if(this.spriteIndex == 0)
						this.setSpriteIndex(1);
					else
						this.setSpriteIndex(0);
					break;
			}
		} 
		else if(this.mode.equals(FRIGHTENED)) {
			if(this.spriteIndex == 18)
				this.setSpriteIndex(19);
			else if(this.spriteIndex == 19)
				this.setSpriteIndex(16);
			else if(this.spriteIndex == 16)
				this.setSpriteIndex(17);
			else
				this.setSpriteIndex(18);
		}
		else if(this.mode.contentEquals(EATEN)) {
			switch(this.direction) {
				case "UP":
					if(this.spriteIndex == 15)
						this.setSpriteIndex(14);
					else
						this.setSpriteIndex(15);
					break;
				case "LEFT":
					if(this.spriteIndex == 12)
						this.setSpriteIndex(13);
					else
						this.setSpriteIndex(12);
					break;
				case "DOWN":
					if(this.spriteIndex == 10)
						this.setSpriteIndex(11);
					else
						this.setSpriteIndex(10);
					break;
				case "RIGHT":
					if(this.spriteIndex == 8)
						this.setSpriteIndex(9);
					else
						this.setSpriteIndex(8);
					break;
			}
		}
		
		this.loadImage(this.sprites.get(this.spriteIndex));
	}
	
	
	void setMode(String mode) {
		this.mode = mode;
		if (this.mode.equals (FRIGHTENED)) {
			if (this.turnedAround == true){
			this.turnedAround=false;
			}
		}
	}
	String getMode () {
		return this.mode;
	}
	
	public void setSpriteIndex(int spriteIndex) {
		this.spriteIndex = spriteIndex;
	}
}
