package com.ai.expert.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.ai.expert.controller.MainMenuControlller;
import static com.ai.expert.ui.Utils.SCENE_HEIGHT;
import static com.ai.expert.ui.Utils.SCENE_WIDTH;

public class MainMenuView extends Application {
	
	protected static final System.Logger LOGGER = System.getLogger(MainMenuView.class.getName());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
             
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ai/expert/ui/mainMenuView.fxml"));
        Parent root = loader.load();        
        Scene mainScene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
           
        MainMenuControlller mainMenuControlller = loader.getController();   
        mainMenuControlller.setScene(mainScene);
        
        var imageIcon = new Image(MainMenuView.class.getResourceAsStream("/com/ai/expert/ui/icon.jpg"));
          
        stage.getIcons().add(imageIcon);    
        stage.setTitle("Computer Problems System Expert");
        stage.setScene(mainScene);
        stage.setResizable(false);     
        stage.show();
        
        LOGGER.log(System.Logger.Level.INFO, "mainMenuView.fxml loaded successfully");
    }
}