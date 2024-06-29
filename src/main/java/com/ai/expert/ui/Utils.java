package com.ai.expert.ui;

import java.io.IOException;
import java.util.List;

import com.ai.expert.controller.ResultController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public abstract class Utils {

	public static final int SCENE_WIDTH = 1024;
	
	public static final int SCENE_HEIGHT = 640;
	
	private Utils() {}
	
	public static void generateResultView(List<String> userIndicators, List<TreeView<String>> tree, List<Double> percentage) throws IOException {		
		var fxmlLoader = new FXMLLoader(Utils.class.getResource("/com/ai/expert/ui/resultView.fxml"));
		Parent root = fxmlLoader.load();
		
		ResultController resultController = fxmlLoader.getController();
        
		var stage = new Stage();     
	
        stage.setTitle("Result");
        stage.setScene(new Scene(root));
        
		resultController.setUserIndicators(userIndicators);
		resultController.setPossibleAnswers(tree, percentage);
     
        resultController.setStage(stage);    
        
        stage.show();
	}	
	
    public static TreeView<String> createTree(String rootName, List<String> childrenName) {
    	
    	var tree = new TreeView<String>();	
    	tree.getStyleClass().add("defaultText");
    	tree.getStyleClass().add("treeViewText");
    	
		var root = new TreeItem<>(rootName);				
		root.setExpanded(true);
		tree.setRoot(root);
				
        var childrenRoot = root.getChildren();
	    
		for (var value : childrenName) {		
			childrenRoot.add(new TreeItem<>(value));
		}
  		
    	return tree;
    }
}
