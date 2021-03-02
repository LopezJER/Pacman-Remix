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
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/*
 * The GameTimer is a subclass of the AnimationTimer class. It must override the handle method. 
 */
 
public class GameTimer extends AnimationTimer{
	
	
	private GridPane gridpane;
	private Scene theScene;
	private Stage stage;
	private Scene splashScn;
	private GraphicsContext gc;
	private Button bckBtn;
	private Label lbl;
	private Maze maze;
	private Pacman pacman;
	private int score;
    private ArrayList<String> input = new ArrayList<String>();
    private boolean timerOn = false;
    private int timeSecPassed;
	static int timeNanoPassed;
    static int secNanoTime;
    private static boolean shownReady = false;
    private int frightModeSecsPassed;
    private int frightNanoTime;
    private boolean frightMode = false;
    private int frightModeTimeLimit = 8;
    private int randomTimeLimit = 10 + new Random().nextInt(21);
    
  
	public static ArrayList<Ghost> ghosts = new ArrayList<Ghost>();
	public static int ghostsEaten = 0;
	
	GameTimer(Stage stage, Scene splashScn, GridPane gridpane, GraphicsContext gc, Scene theScene){
		this.gridpane = gridpane;
		this.stage =stage;
		this.splashScn =splashScn;
		this.theScene = theScene;
		this.gc = gc;
		this.maze = new Maze();	
		this.pacman = new Pacman();
		
		this.ghosts.add(new Ghost(Ghost.BLINKY, Ghost.InitPosB));
		this.ghosts.add(new Ghost(Ghost.INKY, Ghost.InitPosI));
		this.ghosts.add(new Ghost(Ghost.PINKY, Ghost.InitPosP));
		this.ghosts.add(new Ghost(Ghost.CLYDE, Ghost.InitPosC));
	}

	@Override
	public void handle(long currentNanoTime) {
		System.out.println("Seconds " +this.timeSecPassed);
		
		//i. SET THE TIMERS
		
		if(timerOn == false && Maze.hasFruit==false) {
			timerOn = true;
			this.timeSecPassed = 0;
			secNanoTime = (int)(currentNanoTime/Math.pow(10, 9));
			this.timeNanoPassed=0;

		} else {
			if(secNanoTime != (int)(currentNanoTime/Math.pow(10, 9))) {
				secNanoTime = (int)(currentNanoTime/Math.pow(10, 9));
				this.timeSecPassed++;
			}
			if(this.timeSecPassed == randomTimeLimit && Maze.hasFruit==false) {
				Maze.placeFruit();
				timerOn = false;
			}
			this.timeNanoPassed++;
		
		}
		
		for(Ghost g:ghosts) {
			if(g.getMode().equals(Ghost.FRIGHTENED)) {
					if(this.frightMode==false) {
						this.frightMode = true;
						frightNanoTime = (int)(currentNanoTime/Math.pow(10, 9));
						this.frightModeSecsPassed = 0;
					}
					else {
						if(frightNanoTime != (int)(currentNanoTime/Math.pow(10, 9))) {
							frightNanoTime = (int)(currentNanoTime/Math.pow(10, 9));
							this.frightModeSecsPassed++;
						}
						
					}
			}
		}
		
		if(this.frightModeSecsPassed >= this.frightModeTimeLimit && this.frightMode==true) {
			for(Ghost g:ghosts) {
				if(g.getMode().equals(Ghost.FRIGHTENED))
					g.setMode(Ghost.CHASE);
			}
			this.frightMode = false;
		}
		

		
		
		//ii. CHECK PRECONDITIONS (IF SHOWN READYSCREEN, EATEN, LOST, WON)
		
		if (GameStage.eaten && timeNanoPassed<24) {
			showPacmansInevitableDemise(timeNanoPassed%12);
			
		} else if (this.timeSecPassed < 4 && !this.shownReady) {
				this.maze.renderMaze(gc);
				this.showReadyPrompt();
				this.maze.renderMaze(gc);
		} else if (!this.pacman.isAlive()) {
				this.maze.renderMaze(gc);
				this.showGameOverPrompt();
				Platform.exit();
				System.exit(0);
		} else if (this.playerWon()) {
				this.maze.renderMaze(gc);
				this.showEndPrompt();
				this.maze.renderMaze(gc);
		} else {
			
		//iii. RENDER MAZE AND CHARACTERS
			this.shownReady = true;
			
			for (Ghost g: ghosts) {
				checkGhostCollision(g);
			}
			this.gc.clearRect(0, 0, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
			this.maze.renderMaze(gc);
			this.pacman.render(gc);
			
			
				for (Ghost g : ghosts) {
					g.render(gc);
					g.roam(this.pacman);
				}
			
			
			
			/*
			this.gc.clearRect(0, 0, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
			this.maze.renderMaze(gc);
			this.pacman.render(gc);
			*/
			
			if(this.timeNanoPassed%20 == 0)
				Collectible.animateDots();
			
			
			this.handleKeyPress(this.pacman);
			this.movePacman();
			this.updateScore(GameStage.getPoints());
			this.updateLives();
			this.updateFruit();

		}

	}
	
		
		
		
		

	
	
	

	private void handleKeyPress(Pacman pacman) {
			this.theScene.setOnKeyPressed(new EventHandler<KeyEvent>(){
				public void handle(KeyEvent e){
				
					if (e.getCode() == KeyCode.ESCAPE) {
						stage.setScene(splashScn);
						stage.show();
					}
	            	String code = e.getCode().toString();
	            	
	            	pacman.dirs.add(0, code);
		            

					
		            
				}	
	});
	}
			/*
			this.theScene.setOnKeyReleased(new EventHandler<KeyEvent>(){
			            public void handle(KeyEvent e){
			            	KeyCode code = e.getCode();
			                stopPacman(code);
			            }
			        });*/

		    
	
	private void movePacman() {
		
		if (pacman.x<-16) {
			pacman.setDirection("LEFT");
			this.pacman.x = GameStage.WINDOW_WIDTH+16;
			this.pacman.y=17*GameStage.cellHeight+GameStage.cellHeight *0.5;
		} else if (pacman.x > GameStage.WINDOW_WIDTH + 16) {
			pacman.setDirection("RIGHT");
			this.pacman.x = -16;
			this.pacman.y=17*GameStage.cellHeight +GameStage.cellHeight *0.5 ;
		} else if (pacman.hasValidDirection()) {
    		pacman.setDirection(pacman.dirs.get(0));
			pacman.dirs.clear();
    	}
		
		if(this.pacman.getDirection() == "UP") {
			this.pacman.setDY(-1);   
			this.pacman.setDX(0);
		}

		else if(this.pacman.getDirection() == "LEFT") {
			this.pacman.setDX(-1);
			this.pacman.setDY(0); 
		}

		else if(this.pacman.getDirection() == "DOWN") {
			this.pacman.setDY(1);
			this.pacman.setDX(0);
		}
		
		else if(this.pacman.getDirection() == "RIGHT") {
			this.pacman.setDX(1);
			 this.pacman.setDY(0); 
		}
		
		//this.pacman.setDirection(ke);
		this.pacman.move();
		this.pacman.loadImage();
   	}
	
	private void stopPacman(){
		this.pacman.setDX(0);
		this.pacman.setDY(0);
	}
	
	
	private void checkPoints() {
		if (ghostsEaten < 5)
		this.ghostsEaten++;
		else {
			this.ghostsEaten = 0;
		}
		
		int points = (int)Math.pow(2, ghostsEaten) * 100;
		
		GameStage.setPoints(GameStage.getPoints()+points);
		

	}
	
	private void checkGhostCollision(Ghost g) {
		System.out.println("Mode " + g.getMode());
		if ((this.pacman.collidesWith(g) && g.getMode().equals(Ghost.FRIGHTENED))){
			g.isFrightened = false;
			g.setMode(Ghost.EATEN);
			this.checkPoints();
			switch(ghostsEaten) {
				case 1: g.loadImage(Maze.twoh);
				case 2: g.loadImage(Maze.fourh);
				case 3: g.loadImage(Maze.eighth);
				case 4: g.loadImage(Maze.onesix);
			}
				g.render(gc);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} else if ((this.pacman.collidesWith(g) && g.getMode().equals(Ghost.CHASE)) || (this.pacman.collidesWith(g) && g.getMode().equals(Ghost.SCATTER))){
			this.timerOn=false;
			this.shownReady = false;
			Maze.hasFruit = false;
			GameStage.eaten = true;
				stopEveryone(this.pacman, ghosts);
				if (pacman.getLives() !=0) {
					gc.clearRect(0,0,GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
					restartGame();
					this.pacman.decreaseLives();


				}
			
		}
		
	}
	
	private void showPacmansInevitableDemise(int count) {
		ArrayList <WritableImage> deathFrames = new ArrayList<WritableImage>(); 
		
		for (int i = 0; i < 11; i++) {
			deathFrames.add(new WritableImage(Maze.spritesheet.getPixelReader(),(i*2+8)*Maze.SPRITESHEET_CELL_WIDTH,14*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
			deathFrames.add(new WritableImage(Maze.spritesheet.getPixelReader(),(i*2+8)*Maze.SPRITESHEET_CELL_WIDTH,14*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		}
		deathFrames.add(0, new WritableImage(Maze.spritesheet.getPixelReader(),0*Maze.SPRITESHEET_CELL_WIDTH,14*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
		
		
		gc.clearRect(0,0,GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
		this.pacman.loadImage(deathFrames.get(count));
		this.pacman.render(gc);
		
		
			


			
	
	}
	private void restartGame() {
		this.gc.clearRect(0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
		this.maze = new Maze();	
		this.maze.renderMaze(gc);
		this.pacman = new Pacman();
		this.ghosts.clear();
		this.ghosts.add(new Ghost(Ghost.BLINKY, Ghost.InitPosB));
		this.ghosts.add(new Ghost(Ghost.INKY, Ghost.InitPosI));
		this.ghosts.add(new Ghost(Ghost.PINKY, Ghost.InitPosP));
		this.ghosts.add(new Ghost(Ghost.CLYDE, Ghost.InitPosC));
	}
	private void updateScore(int score) {
		String[] hsKey = {"2-8", "2-9", "2-7", "2-8", "0-23", "2-19", "2-3", "2-15", "2-18" ,"2-5"};
		this.score = GameStage.getPoints();
		String scoreString = Integer.toString(GameStage.getPoints());
		for (int col = 9; col < 19; col++) {
			Maze.tileMap[0][col].loadImage(Maze.tileImgMap.get(hsKey[col-9]));
		}
		int strlen = scoreString.length();
		int col = GameStage.numCols/2 - strlen/2;
		for(char c : scoreString.toCharArray()) {
			  int ssCol = Character.getNumericValue(c); 
			  String tileKey = "0-" + ssCol;
			  Maze.tileMap[1][col++].loadImage(Maze.tileImgMap.get(tileKey));
			}
		
	}
	
	private void showReadyPrompt() {


		String[] readyKey = {"2-18", "2-5", "2-1", "2-4", "2-25", "2-27"}; //2011
		
		if(secNanoTime%2==0) {
			for (int col = 0; col<6; col++) {
			Maze.tileMap[20][11+col].loadImage(Maze.tileImgMap.get(readyKey[col]));
			}
		} else {
			for (int col = 0; col<6; col++) {
				Maze.tileMap[20][11+col].loadImage(Maze.tileImgMap.get(Maze.OPEN_PATH_KEY));
			}
		}
		
	}
	
	private void showEndPrompt() {
		this.gc.clearRect(0, 0, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		this.maze.renderMaze(gc);
		this.pacman.render(gc);
		
		long start = System.nanoTime();        
	    long elapsed = System.nanoTime() - start;
	    TimeUnit.SECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
		String[] endKey = {"2-3", "2-15", "2-14", "2-7", "2-18", "2-1", "2-20", "2-19", "2-27"}; //2011
		
		if(secNanoTime%2==0) {
			for (int col = 0; col<9; col++) {
			Maze.tileMap[20][10+col].loadImage(Maze.tileImgMap.get(endKey[col]));
			}
		} else {
			for (int col = 0; col<9; col++) {
				Maze.tileMap[20][10+col].loadImage(Maze.tileImgMap.get(Maze.OPEN_PATH_KEY));
			}
		}
		
	}
	
	private void showGameOverPrompt() {
		this.gc.clearRect(0, 0, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		this.maze.renderMaze(gc);
		this.pacman.render(gc);
		
		long start = System.nanoTime();        
	    long elapsed = System.nanoTime() - start;
	    TimeUnit.SECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
		String[] endKey = {"2-7", "2-1", "2-13", "2-5", "2-15", "2-22", "2-5", "2-18", "2-27"}; //2011
		
		if(secNanoTime%2==0) {
			for (int col = 0; col<9; col++) {
			Maze.tileMap[20][10+col].loadImage(Maze.tileImgMap.get(endKey[col]));
			}
		} else {
			for (int col = 0; col<9; col++) {
				Maze.tileMap[20][10+col].loadImage(Maze.tileImgMap.get(Maze.OPEN_PATH_KEY));
			}
		}
		
	}
	private void updateLives() {
		for (int i = 0; i < Pacman.lives-1; i++) {
			gc.drawImage(Maze.life, Maze.tileMap[34][2+i*2].getCanvasPoint().getX(), Maze.tileMap[34][2+i*2].getCanvasPoint().getY());
		}
	}
	
	
	private void updateFruit() {
		if (!this.pacman.eatenFruit){
			gc.drawImage(Maze.fruit, Maze.tileMap[34][24].getCanvasPoint().getX(), Maze.tileMap[34][24].getCanvasPoint().getY());
		}
	}
	
	public Tile getPacmanTile() {
		return this.pacman.getCurrentTile();
	}
	
	public static void stopEveryone (Pacman p, ArrayList<Ghost> ghosts) {
		p.stop();
		for (Ghost g: ghosts) {
			g.stop();
		}
	}
	
	private boolean playerWon() {
		for (Ghost g: ghosts) {
			if (!g.getMode().equals(Ghost.EATEN)){
				return false;
			}
		}
		return true;
	}
	
}
