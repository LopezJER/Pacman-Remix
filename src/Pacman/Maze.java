package Pacman;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;


public class Maze{
	public final static int MAZE_COLS = 28;
	public final static int MAZE_ROWS = 36;
	public final static int SPRITESHEET_COLS = 32;
	public final static int SPRITESHEET_ROWS = 20;
	public final static double SCALED_SPRITESHEET_WIDTH = SPRITESHEET_COLS * GameStage.cellWidth; //512px
	public final static double SCALED_SPRITESHEET_HEIGHT = SPRITESHEET_ROWS * GameStage.cellHeight; //320px
	public final static int SPRITESHEET_CELL_WIDTH = (int)GameStage.cellWidth; //16px
	public final static int SPRITESHEET_CELL_HEIGHT = (int)GameStage.cellHeight; //16px
	
	public final static String OPEN_PATH_KEY = "0-23";
	public final static String DOT_KEY = "0-16";
	public final static String ENERGIZER_KEY = "0-18";
	public final static String ENERGIZER_KEY2 = "0-20";
	
	public final static Image spritesheet = new Image("spritesheet.png", SCALED_SPRITESHEET_WIDTH, SCALED_SPRITESHEET_HEIGHT, false, false);
	public final static WritableImage life = new WritableImage(Maze.spritesheet.getPixelReader(),4*Maze.SPRITESHEET_CELL_WIDTH, 6*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2); 
	public final static WritableImage fruit = new WritableImage(Maze.spritesheet.getPixelReader(),0*Maze.SPRITESHEET_CELL_WIDTH, 10*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2); 	
	public final static WritableImage fifty = new WritableImage(Maze.spritesheet.getPixelReader(),11*Maze.SPRITESHEET_CELL_WIDTH, 4*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2); 	
	public final static WritableImage twoh = new WritableImage(Maze.spritesheet.getPixelReader(),16*Maze.SPRITESHEET_CELL_WIDTH, 12*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2); 	
	public final static WritableImage fourh = new WritableImage(Maze.spritesheet.getPixelReader(),18*Maze.SPRITESHEET_CELL_WIDTH, 12*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2); 	
	public final static WritableImage eighth = new WritableImage(Maze.spritesheet.getPixelReader(),20*Maze.SPRITESHEET_CELL_WIDTH, 12*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2); 	
	public final static WritableImage onesix = new WritableImage(Maze.spritesheet.getPixelReader(),22*Maze.SPRITESHEET_CELL_WIDTH, 12*Maze.SPRITESHEET_CELL_HEIGHT,Maze.SPRITESHEET_CELL_WIDTH*2 ,Maze.SPRITESHEET_CELL_HEIGHT*2); 	

	public static HashMap <String, WritableImage> tileImgMap;
	public static Tile[][] tileMap;
	public static ArrayList<Tile> ghostHouseTiles = new ArrayList<Tile>();
	public static boolean hasFruit = false;
	public Maze() {		
		initTileImgMap();
		initTileMap();
		setNeighboringTiles();
	}


	public void initTileImgMap() {
		Maze.tileImgMap = new HashMap <String, WritableImage>();
		
		for (int i = 0; i < SPRITESHEET_ROWS; i++) {
			for (int j = 0; j < SPRITESHEET_COLS; j++) {
				WritableImage imgTile = new WritableImage (spritesheet.getPixelReader(), j * SPRITESHEET_CELL_WIDTH, i *  SPRITESHEET_CELL_HEIGHT, SPRITESHEET_CELL_WIDTH, SPRITESHEET_CELL_HEIGHT);
				tileImgMap.put(Integer.toString(i)+"-"+Integer.toString(j), imgTile);
			}
		}
				
	}
	
	public void initTileMap() {
		tileMap = new Tile[MAZE_ROWS][MAZE_COLS];
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("maze2.txt"));
			String line;
			for (int i = 0; i < MAZE_ROWS; i++) {
				String[] tokens;
				if ((line=reader.readLine()) != null) {
					tokens = line.split("\t");
				} else break;
				for (int j = 0; j < MAZE_COLS; j++) {
					String tileKey = tokens[j];
					if (tileKey.equals(OPEN_PATH_KEY)) {
						tileMap[i][j] = new Tile (true, false, j, i);
					} else if(tileKey.equals(DOT_KEY) || tileKey.equals(ENERGIZER_KEY)){
						tileMap[i][j] = new Tile (true, true, j, i);
						tileMap[i][j].setCollectible(tileKey);
					} else {
						tileMap[i][j] = new Tile (false, false, j, i);
					}
					tileMap[i][j].loadImage(tileImgMap.get(tileKey));

				}
			}
			reader.close();
			
			tileMap[15][13].loadImage(new WritableImage(spritesheet.getPixelReader(), 16 * SPRITESHEET_CELL_WIDTH, 5 *  SPRITESHEET_CELL_HEIGHT, SPRITESHEET_CELL_WIDTH, SPRITESHEET_CELL_HEIGHT));
			tileMap[15][13].loadImage(new WritableImage(spritesheet.getPixelReader(), 16 * SPRITESHEET_CELL_WIDTH, 5 *  SPRITESHEET_CELL_HEIGHT, SPRITESHEET_CELL_WIDTH, SPRITESHEET_CELL_HEIGHT));
			
			//Create arraylist of ghosthouse tiles
			for (int i = 16; i < 19; i++) {
				for (int j = 11; j < 17; j++) {
					ghostHouseTiles.add(tileMap[i][j]);
				}
				
			}

			
			
			ghostHouseTiles.add(tileMap[15][13]);
			ghostHouseTiles.add(tileMap[15][14]);
			ghostHouseTiles.add(Maze.tileMap[17][5]);
			ghostHouseTiles.add(Maze.tileMap[17][22]);
			
		} catch (FileNotFoundException e) {} catch (IOException e) {}
	}

	public void setNeighboringTiles() {
		for (int i = 4; i < 33; i++) {
			for (int j = 1; j < 27; j++) {
				tileMap[i][j].setNeighbors();
			}
		}
	}

	public void renderMaze(GraphicsContext gc) {
		
		for (int i = 0; i < GameStage.numRows; i++) {
			for (int j = 0 ; j < GameStage.numCols; j++) {
				tileMap[i][j].render(gc);
			}

		}
		//System.out.println("CELL WIDTH: " + GameStage.cellWidth + "CELL HEIGHT" + GameStage.cellHeight + "SPRITE WIDTH" + SPRITESHEET_WIDTH + "SPRITE HEIGHT" + SPRITESHEET_HEIGHT);
		//gc.drawImage(cropped, 0, 0);
		
	}
	
	public static void placeFruit() {
		
		tileMap[Collectible.fruitCol][Collectible.fruitRow].setHasCollectible(true);
		tileMap[Collectible.fruitCol][Collectible.fruitRow].setCollectible("Fruit");
		tileMap[Collectible.fruitCol][Collectible.fruitRow].loadImage(Collectible.fruitSprites.get(new Random().nextInt(7)));
		
		hasFruit = true;
	}
	

}
	



