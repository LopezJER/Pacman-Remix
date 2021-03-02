package Pacman;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Collectible{
	public final static String DOT = "Dot";
	public final static String ENERGIZER = "Energizer";
	public final static String FRUIT = "Fruit";
	private final static String ENERGIZER2 = "Energizer2";
	
	private int points;
	private String type;
	
	public final static int fruitRow = 14;
	public final static int fruitCol = 20;
	public final static ArrayList<Image> fruitSprites = new ArrayList<Image>();
	
	Collectible(String type){
		switch(type) {
			case Maze.DOT_KEY:
				this.points = 10;
				this.type = DOT;
				break;
			case Maze.ENERGIZER_KEY:
				this.points = 50;
				this.type = ENERGIZER;
				break;
			case FRUIT:
				this.points = 200;
				this.type = FRUIT;
				break;
		}
		if(fruitSprites.isEmpty()) {
			for(int i=0;i<6;i++) {
				fruitSprites.add(new WritableImage(Maze.spritesheet.getPixelReader(),i*2*Maze.SPRITESHEET_CELL_WIDTH,10*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2));
			}
		}
	}
	
	
	
	public static void animateDots() {
		for(int i=0;i < Maze.MAZE_ROWS;i++) {
			for(int j=0;j < Maze.MAZE_COLS;j++) {
				if(Maze.tileMap[i][j].checkHasCollectible() && (Maze.tileMap[i][j].getCollectible().getType().equals(ENERGIZER) || Maze.tileMap[i][j].getCollectible().getType().equals(ENERGIZER2))) {
					
					if(Maze.tileMap[i][j].getCollectible().getType().equals(ENERGIZER)) {
						Maze.tileMap[i][j].loadImage(Maze.tileImgMap.get(Maze.ENERGIZER_KEY2));
						Maze.tileMap[i][j].getCollectible().type = ENERGIZER2;
					}						
					else {
						Maze.tileMap[i][j].loadImage(Maze.tileImgMap.get(Maze.ENERGIZER_KEY));
						Maze.tileMap[i][j].getCollectible().type = ENERGIZER;
					}
						
				}
			}
		}
	}
	
	//Getter
	public int getPoints() {
		return this.points;
	}
	
	public String getType() {
		return this.type;
	}
}
