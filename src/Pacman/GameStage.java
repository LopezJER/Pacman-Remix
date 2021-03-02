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

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


public class GameStage {
	public static final int numRows = 36;
	public static final int numCols = 28;
	public static final int WINDOW_HEIGHT = numRows*16; //16px
	public static final int WINDOW_WIDTH = numCols*16;
	public final static Image Logo = new Image ("logo.jpg", GameStage.WINDOW_WIDTH, GameStage.WINDOW_WIDTH, true, true);
	public static final double cellWidth = WINDOW_WIDTH/numCols;
	public static final double cellHeight = WINDOW_HEIGHT/numRows;
	private Scene splashScn, playScn, instScn, abtScn;;
	private Stage stage;
	//private Group root;
	private Canvas canvas;
	private GraphicsContext gc;
	private GameTimer gametimer;
	public static boolean eaten = false; 
	private static int points;
	
	//the class constructor
	public GameStage() {
		setSplashScene();
		setPlayScene();
		setInstScene();
		setAbtScene();
		points = 0;
	}	

	//method to add the stage elements
	public void setStage(Stage stage) {
		this.stage = stage;
		//set stage elements here	     
		
		this.stage.setTitle("Pacman");
		this.stage.setScene(this.splashScn);
		
		//invoke the start method of the animation timer
		//this.gametimer.start();
		
		this.stage.show();
		
		if (this.eaten) {
			System.out.println("EATENNNNNNNNNNNNNNNNN");
	
			setPlayScene();
			this.gametimer.start();
		}
	}
	
	public void setSplashScene() {
		
		Pane layout1 = new Pane();
		layout1.setPrefWidth(100);
		layout1.setPrefHeight(100);

		//layout1.setSpacing(5);
		//layout1.setAlignment(Pos.CENTER);

        //label1.setStyle("-fx-font-family: \"Courier\"; -fx-font-size: 20; -fx-text-fill: white;");

        Button playBtn = new Button("Play");
        Button instBtn = new Button("Instructions");
        Button optBtn = new Button("Options");
        Button abtBtn = new Button("About");
        Button extBtn = new Button("Exit");
        Button bckBtn = new Button ("Back");
        
        playBtn.setOnAction(e ->{ stage.setScene(playScn); gametimer.start();});
        instBtn.setOnAction(e -> stage.setScene(instScn));
        abtBtn.setOnAction(e -> stage.setScene(abtScn));
        extBtn.setOnAction(e -> {Platform.exit();System.exit(0);});
        bckBtn.setOnAction(e -> stage.setScene(splashScn));
	        
		playBtn.setMinWidth(layout1.getPrefWidth());
		instBtn.setMinWidth(layout1.getPrefWidth());
		abtBtn.setMinWidth(layout1.getPrefWidth());
		extBtn.setMinWidth(layout1.getPrefWidth());
		bckBtn.setMinWidth(layout1.getPrefWidth());
	
		playBtn.setTranslateY(370);
		playBtn.setTranslateX(10);
		instBtn.setTranslateY(370);
		instBtn.setTranslateX(120);
		abtBtn.setTranslateY(370);
		abtBtn.setTranslateX(230);
		extBtn.setTranslateY(370);
		extBtn.setTranslateX(340);
		
		playBtn.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		instBtn.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		abtBtn.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		extBtn.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		
		playBtn.setStyle("-fx-color:White;");
		instBtn.setStyle("-fx-color:White;");
		abtBtn.setStyle("-fx-color:White;");
		extBtn.setStyle("-fx-color:White;");
		
   	 BackgroundImage background = new BackgroundImage(Logo,
             BackgroundRepeat.NO_REPEAT,
             BackgroundRepeat.NO_REPEAT,
             BackgroundPosition.DEFAULT,
             BackgroundSize.DEFAULT);
	 layout1.setBackground(new Background(background));
      

        layout1.getChildren().addAll( playBtn, instBtn, abtBtn, extBtn); //options button to be added

        splashScn = new Scene(layout1, GameStage.WINDOW_WIDTH, GameStage.WINDOW_WIDTH, Color.BLACK);
        splashScn.setFill(Color.YELLOW);
//        return splashScn;
        
	}
	
	public void setPlayScene() {
		Group root = new Group();
		
		this.playScn = new Scene(root, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);	
		
		
		GridPane layout2 = new GridPane();
		//layout2.setStyle("-fx-background-color: #000000;");
		layout2.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		layout2.setGridLinesVisible(true);
		Canvas canvas = new Canvas(GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		layout2.setPrefSize(GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
		//set the map to x and y location; add border color to see the size of the gridpane/map  
	    //layout2.setStyle("-fx-border-color: red ;");
	    //layout2.setLayoutY(GameStage.WINDOW_WIDTH);
		
        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / numCols);
            layout2.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / numRows);
            layout2.getRowConstraints().add(rowConst);         
        }
        

		this.canvas = new Canvas(GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);	
		this.gc = canvas.getGraphicsContext2D();
		layout2.getChildren().add(canvas);
		//instantiate an animation timer
		
		Label lbl = new Label("Score: ");
		lbl.setTextFill(Color.WHITE);
		 
		Button bckBtn = new Button("Back");
		bckBtn.setOnAction(e -> stage.setScene(splashScn));
		
		bckBtn.setLayoutX(0);
		bckBtn.setLayoutY(0);
		
		lbl.setLayoutX(WINDOW_WIDTH-100);
		lbl.setLayoutY(20);

		
		//root.getChildren().add(lbl);
		//root.getChildren().add(bckBtn);
		
		root.getChildren().add(layout2);
		root.getChildren().add(canvas);

        


		this.gametimer = new GameTimer(stage, playScn, layout2, gc, playScn);
		


	}
	
	public void setInstScene() {
        VBox layout3 = new VBox();
        layout3.setAlignment(Pos.CENTER);
        layout3.setStyle("-fx-background-color: black; -fx-padding: 10;");
	    
        final String text = "~\nInstructions:\n" + "Help Pacman eat all the dots in the maze "
        		+ "\r\nwhile dodging the 4 creepy ghosts!\r\n" + 
				"\r\n" + 
				"Pacman continuously moves in a single direction until he hits a wall.\r\n" + 
				"You can make Pacman turn around corners by using the arrow keys.\r\n\n" + 
				"Eating dots, energizers, and fruits earns you points.\r\n" + 
				"Eating an energizer turns ghosts blue and makes them flee from Pacman for a limited amount of time.\r\n" + 
				"You can eat a ghost in this state which also adds to your points.\r\n Beware though! The ghosts will respawn" +
				" upon being eaten.\n~\n"; 
        
        Label instructionsText = new Label (text);
        instructionsText.setWrapText(true);
        instructionsText.setTextAlignment(TextAlignment.CENTER);
        instructionsText.setStyle("-fx-font-family: \"Courier\"; -fx-font-size: 20; -fx-text-fill: white;");
        

        layout3.getChildren().add(instructionsText);
        Button bckBtn = new Button ("Back");
        bckBtn.setOnAction(e -> stage.setScene(splashScn));
        layout3.getChildren().add(bckBtn);
        instScn = new Scene(layout3, 600, 600);
//        return instScn;
	}
	
	public void setAbtScene() {
		VBox layout5 = new VBox();
		layout5.setAlignment(Pos.CENTER);
        layout5.setStyle("-fx-background-color: black; -fx-padding: 10;");
	    
        final String text2 = "~\nThis is a Pacman game created by Paul Allen B. Asumen and Jose Enrique Lopez for the project submission in the course CMSC 22."
				+ "\nThis was developed to apply the lessons taught about Object Oriented Programming through a software made using Java. We hope you enjoy playing this game!\n~\n";
        
        Label aboutText = new Label (text2);
        aboutText.setWrapText(true);
        aboutText.setTextAlignment(TextAlignment.CENTER);
        aboutText.setStyle("-fx-font-family: \"Courier\"; -fx-font-size: 20; -fx-text-fill: white;");

		layout5.getChildren().add(aboutText); 
        Button bckBtn = new Button ("Back");
        bckBtn.setOnAction(e -> stage.setScene(splashScn));
        layout5.getChildren().add(bckBtn);
		abtScn = new Scene (layout5, 500, 500);
//		return abtScn;
	}
	
	public static int getPoints() {
		return GameStage.points;
	}
	
	public static void setPoints(int points) {
		GameStage.points = points;
	}
	

}

