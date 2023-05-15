package aiproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import aiproject.Connect4Game;
import aiproject.Connect4Game;
import aiproject.Connect4Game;
public class Connect4Frame extends Parent{
    private final int DISC_SIZE = 80;
    
    private Pane discRoot;
    private Text statusText1;
    private Text statusText2;

    private Connect4Game game;

    public Connect4Frame() {

        super();
        
        Pane gamePane = new Pane();
        discRoot = new Pane();
        gamePane.getChildren().add(discRoot);
        gamePane.getChildren().add(makeGrid());
        gamePane.getChildren().addAll(makeColumns());
        
        GridPane grid = new GridPane();
        grid.setMinSize(125, 125);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));
        grid.add(gamePane, 0, 0);

        Button b0 = new Button("Human vs Computer");
        b0.setOnAction( (e) -> newGame(false,true) );
        Button b1 = new Button("Undo");
//        b1.setOnAction( (e) -> { if (game!=null) game.undo(); } );
        statusText1 = new Text();
        statusText2 = new Text();
        VBox v = new VBox(b0,b1,statusText1,statusText2);
        VBox.setMargin(b1, new Insets(10, 0, 0, 0));
        grid.add(v, 1, 0);
        getChildren().add(grid);
        
    }

    private Shape makeGrid() {
        Shape shape = new Rectangle((game.getCols() + 1) * DISC_SIZE, (game.getRows() + 1) * DISC_SIZE);
        for (int y = 0; y < game.getRows(); y++) {
            for (int x = 0; x < game.getCols(); x++) {
                Circle circle = new Circle(DISC_SIZE / 2);
                circle.setCenterX(DISC_SIZE / 2);
                circle.setCenterY(DISC_SIZE / 2);
                circle.setTranslateX(x * (DISC_SIZE + 5) + DISC_SIZE / 4);
                circle.setTranslateY(y * (DISC_SIZE + 5) + DISC_SIZE / 4);
                shape = Shape.subtract(shape, circle);
            }
        }
        Light.Distant light = new Light.Distant();
        light.setAzimuth(45.0);
        light.setElevation(30.0);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);
        shape.setEffect(lighting);
        shape.setFill(Color.DARKBLUE);
        return shape;
    }
    private List<Rectangle> makeColumns() {
        List<Rectangle> list = new ArrayList<>();
        for (int x = 0; x < game.getCols(); x++) {
            Rectangle rect = new Rectangle(DISC_SIZE, (game.getRows() + 1) * DISC_SIZE);
            rect.setTranslateX(x * (DISC_SIZE + 5) + DISC_SIZE / 4);
            rect.setFill(Color.TRANSPARENT);
            rect.setOnMouseEntered(e -> rect.setFill(Color.rgb(200, 200, 200, 0.2)));
            rect.setOnMouseExited(e -> rect.setFill(Color.TRANSPARENT));
            final int column = x;
            rect.setOnMouseClicked(e -> humanMove(column));
            list.add(rect);
        }
        return list;
    }
    private void newGame(boolean c1, boolean c2) {
        discRoot.getChildren().clear();
        game = new Connect4Game(c1, c2, 
                (Color color,boolean animated,boolean marker,int column,int row) -> placeDisc(color, animated, marker, column, row),
                (String s) -> statusText2.setText(s));
        if (c1 && c2) computerMove();
    }
    
    private void humanMove(int col) {
        if (game == null) { // Start a new human vs computer game if column is clicked 
            newGame(false,true);
        }
        if (game.humanMove(col)) {
            if (game.nextIsComputer()) { // Computer move in 1 second to complete human move animation
                Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), (e) -> computerMove()));
                timer.play();
            }
        }
    }
    private void computerMove() {
        if (game.computerMove()) {
            if (game.nextIsComputer()) { // Do another Computer move in 1 second
              Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), (e) -> computerMove()));
              timer.play();
            }
        }
    }
    
    private void placeDisc(Color color, boolean animated, boolean marked, int column, int row) {  
        Circle disc = new Circle(DISC_SIZE / (marked?4:2), color);
        disc.setCenterX(DISC_SIZE / 2);
        disc.setCenterY(DISC_SIZE / 2);
        discRoot.getChildren().add(disc);
        disc.setTranslateX(column * (DISC_SIZE + 5) + DISC_SIZE / 4);
        if (marked || !animated) {
            disc.setTranslateY((Connect4Game.getRows()-row-1) * (DISC_SIZE + 5) + DISC_SIZE / 4);            
        }
        else { // Animate drop
            TranslateTransition animation = new TranslateTransition(Duration.seconds(0.6), disc);
            animation.setToY((Connect4Game.getRows()-row-1) * (DISC_SIZE + 5) + DISC_SIZE / 4);
            animation.play();
        }
    }
}
