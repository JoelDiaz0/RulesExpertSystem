package com.ai.expert.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class MainMenuControlller {

	private static final System.Logger LOGGER = System.getLogger(MainMenuControlller.class.getName());
		
	private Scene scene;
	
    public void setScene(Scene scene) {
        this.scene = scene;
    }
	 		
	@FXML
	private void startOnAction() {	
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/ai/expert/ui/backChainingView.fxml"));
            Parent root;
			root = fxmlLoader.load();
            BackChainingController backChainingController = fxmlLoader.getController();
            backChainingController.setScene(scene);

            scene.setRoot(root);	
            LOGGER.log(System.Logger.Level.INFO, "backChainingView.fxml loaded successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	@FXML
	private void exitOfProgram() {
		System.exit(0);
	}
}