package Pacman;

import java.awt.geom.Line2D;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Sprite {
	//Constants
	public final static int MAZE_COLS = 28;
	public final static int MAZE_ROWS = 36;
	public final static int SPRITESHEET_COLS = 32;
	public final static int SPRITESHEET_ROWS = 20;
	public final static double SCALED_SPRITESHEET_WIDTH = SPRITESHEET_COLS * GameStage.cellWidth; //512px
	public final static double SCALED_SPRITESHEET_HEIGHT = SPRITESHEET_ROWS * GameStage.cellHeight; //320px
	public final static int SPRITESHEET_CELL_WIDTH = (int)(SCALED_SPRITESHEET_WIDTH)/SPRITESHEET_COLS; //16px
	public final static int SPRITESHEET_CELL_HEIGHT = (int)(SCALED_SPRITESHEET_HEIGHT)/SPRITESHEET_ROWS; //16px
	public final static String OPEN_PATH_KEY = "0-23";
	public final static Image spritesheet = new Image("spritesheet.png", SCALED_SPRITESHEET_WIDTH, SCALED_SPRITESHEET_HEIGHT, false, false);
	public static final int SPRITE_SHEET_ROWS = 20;
	public static final int SPRITE_SHEET_COLS = 32;

	//Variables
	protected Image img;
	protected int row;
	protected int col;
	protected Tile tile;
	protected Point2D point;
	protected double x, y, dx, dy;
	protected boolean visible;
	protected double width;
	protected double height;
	private String direction;


	public Sprite(int col, int row, double width, double height){
		this.col = col;
		this.row = row;
		this.width = width;
		this.height = height;
		this.point = locateXY(col, row);
		this.tile = locateTile(col, row);
		this.x = (double) (this.point.getX());
		this.y = (double) this.point.getY();
		this.visible = true;
	}
	
	public Sprite(double col, double row, double width, double height){
		this.width = width;
		this.height = height;
		this.point = locateXY(col, row);
		this.tile = locateTile(this.point.getX(), this.point.getY());
		//this.col = this.tile.col;
		//this.row = this.tile.row;
		this.x = (double) (this.point.getX());
		this.y = (double) this.point.getY();
		this.visible = true;
	}
	
	//method to set the object's image
	
	
	protected Point2D locateXY(double col, double row) {
		return new Point2D (col * GameStage.cellWidth + this.width/2, row * GameStage.cellHeight + this.height/2);
	}
	
	
	protected Tile locateTile(int col, int row) {
		return Maze.tileMap[row][col];
	}
	
	protected Tile locateTile (double x, double y) {
		try {
		System.out.println(x + " --- " + y);
		int col = (int) ((x - (GameStage.cellWidth/2))/(GameStage.cellWidth));
		int row = (int) ((y - (GameStage.cellHeight/2))/(GameStage.cellHeight));
		
		return Maze.tileMap[row][col];
		} catch (ArrayIndexOutOfBoundsException e){

			if (col < 0) {
			return Maze.tileMap[row][-col];
			} else {
				return Maze.tileMap[row][+col];
			}
				
		}
		
	}
	
	protected void loadImage(Image img){
		try{
			this.img = this.scale(img, (int)GameStage.cellWidth, (int) GameStage.cellHeight, true);
	        this.setSize();
		} catch(Exception e){}
	}
	
	//method to set the image to the image view node
	public void render(GraphicsContext gc) {
	
		gc.drawImage(this.img, this.getCanvasPoint().getX(), this.getCanvasPoint().getY());

		
	}
	
	//method to set the object's width and height properties
	private void setSize(){
		this.width = this.img.getWidth();
	    this.height = this.img.getHeight();
	}
	
	public double findDistance (Sprite sprite) {
		double xDist = this.x - sprite.x;
		double yDist = this.y - sprite.y;
		double hyp = Math.sqrt( xDist*xDist + yDist*yDist );
		
		return hyp;
	}
	//method that will check for collision of two sprites
	public boolean collidesWith(Sprite rect2)	{
		
		/*Rectangle2D rectangle1 = this.getBounds();
		Rectangle2D rectangle2 = rect2.getBounds();

		return rectangle1.intersects(rectangle2);*/
		
		return this.row==rect2.row && this.col==rect2.col;
		
	}
	
	public boolean occupies (Sprite sprite) {
		
		return (this.x==sprite.x) && (this.y==sprite.y);
		
	}
	
	public Image scale(Image image, int targetWidth, int targetHeight, boolean preserveRatio) {
	    ImageView imageView = new ImageView(image);
	    imageView.setPreserveRatio(preserveRatio);
	    imageView.setFitWidth(targetWidth);
	    imageView.setFitHeight(targetHeight);
	    return imageView.snapshot(null, null);
	}
	
	public boolean collidesWith(Line line)	{
		Rectangle2D rectangle1 = this.getBounds();
		System.out.println (line.getStartX() + " " + line.getStartY() + " " + line.getEndX() + " " + line.getEndY());
		System.out.println(rectangle1.getMinX() + " " + rectangle1.getMinY() + " " + rectangle1.getMaxX() + " " + rectangle1.getMaxY() );
		return line.intersects(rectangle1.getMinX(), rectangle1.getMinY(), rectangle1.getWidth(), rectangle1.getHeight());
	}
	//method that will return the bounds of an image
	protected Rectangle2D getBounds(){
		return new Rectangle2D(this.getCanvasPoint().getX(), this.getCanvasPoint().getY(), this.width, this.height);
	}
	
	//method to return the image
	Image getImage(){
		return this.img;
	}
	//getters
	public double getX() {
    	return this.x;
	}

	public double getY() {
    	return this.y;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.col;
	}
	public boolean getVisible(){
		return visible;	
	}
	public boolean isVisible(){
		if(visible) return true;
		return false;
	}
	
	//setters
	public void setDX(int dx){
		this.dx = dx;
	}
	
	public void setDY(int dy){
		this.dy = dy;
	}
	
	public void setWidth(double val){
		this.width = val;
	}

	public void setHeight(double val){
		this.height = val;
	}
		
	public void setVisible(boolean value){
		this.visible = value;
	}
	
	
	
	public Point2D getCenter() {
		return new Point2D ((2*this.x + GameStage.cellWidth)/2, (2*this.y + GameStage.cellHeight)/2);
		
	}
	
	public Point2D getCanvasPoint() {
		return new Point2D ((this.x - 8), (this.y - 8));
	}
	
	protected Line getFaceOutline (String dir) {
		switch (dir) {
		case "UP":
			return new Line (this.x+1, this.y, this.x+this.width-1, this.y);

		case "DOWN":
			return new Line (this.x+1, this.y + this.height , this.x+this.width-1, this.y + this.height);

		case "LEFT":
			//System.out.println(this.getBounds());
			return new Line (this.x, this.y+1, this.x, this.y+this.height-1);
			
		case "RIGHT":
			return new Line (this.x+this.width, this.y+1, this.x+this.width, this.y+this.height-1);

		default:
			return new Line();
		}
	}
	

		
	
}
	
