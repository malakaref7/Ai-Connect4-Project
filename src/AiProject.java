package aiproject;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import aiproject.Connect4Frame;
import java.util.*;

public class AiProject extends Application {
    @Override
    public void start(Stage stage) throws Exception {
                
        //Create GUI
        final Connect4Frame frame = new Connect4Frame(); 
        stage.setScene(new Scene(frame)); 
        stage.show();     
        System.out.println("Connect 4");
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
