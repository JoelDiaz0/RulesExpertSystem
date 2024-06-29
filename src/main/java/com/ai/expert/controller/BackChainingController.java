package com.ai.expert.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.ai.expert.system.BackChainingRulesEngine;
import static com.ai.expert.ui.Utils.generateResultView;
import static com.ai.expert.system.RulesEngine.getAllIndicators;
import static com.ai.expert.system.RulesEngine.convertIndicatorValueToOriginalValue;
import static com.ai.expert.system.RulesEngine.convertOriginalIndicatorToView;
import static com.ai.expert.system.RulesEngine.getProblemAndIndicators;
import static com.ai.expert.ui.Utils.createTree;
import static com.ai.expert.system.RulesEngine.getPercentageOfSimilarity;
import static com.ai.expert.system.RulesEngine.convertIndicatorsListToView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class BackChainingController {
	
	private static final System.Logger LOGGER = System.getLogger(BackChainingController.class.getName());
	
	private Scene scene;
	
	private TreeItem<String> rootAnswersItem;
	
	private BackChainingRulesEngine rulesEngine;
	
	@FXML private GridPane indicatorNames;
				
	@FXML private TreeView<String> userAnswersTreeView;
		
    public void setScene(Scene scene) {
        this.scene = scene;
    }
	   
    @FXML
    private void initialize() {
    	
    	try {		 		
			rulesEngine = new BackChainingRulesEngine();
    		
    		rootAnswersItem = new TreeItem<>("Answers");
    		rootAnswersItem.setExpanded(true);
    		userAnswersTreeView.setRoot(rootAnswersItem);
    		
			var indicatorList = getAllIndicators();	
					
			final var NUMBER_OF_INDICATORS = indicatorList.size();
			final var NUMBER_OF_ROWS = NUMBER_OF_INDICATORS / 2;
			final var NUMBER_OF_COLUMNS = 2;
						
			indicatorNames.getRowConstraints().clear();
			indicatorNames.getColumnConstraints().clear();
			
			for (int row = 0; row < NUMBER_OF_ROWS; row++) {	
				indicatorNames.getRowConstraints().add(new RowConstraints());
			}
			
			for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
				indicatorNames.getColumnConstraints().add(new ColumnConstraints());
			}
			
			var indicatorListIndex = 0;
			for (int row = 0; row < NUMBER_OF_ROWS; row++) {	
				for (int col = 0; col < 2; col++) {
									
					if (indicatorListIndex < NUMBER_OF_INDICATORS) {	
						var indicatorCheckBox = new CheckBox(convertOriginalIndicatorToView(indicatorList.get(indicatorListIndex)));
						indicatorCheckBox.getStyleClass().add("whiteText");						
						indicatorNames.add(indicatorCheckBox, col, row);
						indicatorListIndex++;
					}			
				}
			}	
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}    	
    }
       
	@FXML
	private void switchToMainMenu() throws IOException {		
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/ai/expert/ui/mainMenuView.fxml"));
        Parent root = fxmlLoader.load();	
		
        MainMenuControlller mainMenuControlller = fxmlLoader.getController();
        mainMenuControlller.setScene(scene);

        scene.setRoot(root);	
        
        rulesEngine.deletePrologFile();
        
        LOGGER.log(System.Logger.Level.INFO, "mainMenuView.fxml loaded successfully");     
	}
	
	@FXML
	private void checkResponse() {			
		try {		
			var listCheckBoxes = indicatorNames.getChildren();
			var userIndicators = new ArrayList<String>();
			
			for (final var checkBoxNode : listCheckBoxes) {
				var indicatorCheckBox = (CheckBox) checkBoxNode;
				
				if (indicatorCheckBox.isSelected()) {				
					var indicatorPrologName = convertIndicatorValueToOriginalValue(indicatorCheckBox.getText());			
					userIndicators.add(indicatorPrologName);
					indicatorCheckBox.setSelected(false);
				}		
			}
			
			var result = rulesEngine.identifyProblem(userIndicators);
				
			if (userIndicators.isEmpty()) {
				var alertResult = new Alert(AlertType.ERROR, "Empty" , ButtonType.CLOSE);
				alertResult.setTitle("Empty");
				alertResult.setHeaderText("No indicator was selected");	
				alertResult.setContentText("Select at least one indicator");
				
				alertResult.show();	
			} else if (result.isEmpty()) {
				var alertResult = new Alert(AlertType.ERROR, "Problem not found" , ButtonType.CLOSE);
				alertResult.setHeaderText("Find a technical");
				alertResult.setContentText("The computer problem could not be identified");
				alertResult.show();	
			} else {			
				var resultNames = result.get();			
				var map = getProblemAndIndicators();
				var treeList = new ArrayList<TreeView<String>>();
				var percentageList = new ArrayList<Double>();
				
				for (final var problemName : resultNames) {					
					var problemIndicators = map.get(problemName);		
					treeList.add(createTree(convertOriginalIndicatorToView(problemName), convertIndicatorsListToView(problemIndicators)));	
					
					var bd = BigDecimal.valueOf(getPercentageOfSimilarity(userIndicators, problemIndicators));
					bd = bd.setScale(2, RoundingMode.HALF_UP);
					
					percentageList.add(bd.doubleValue());
				}
				generateResultView(convertIndicatorsListToView(userIndicators), treeList, percentageList);		
			} 
			updateTreeView(convertIndicatorsListToView(userIndicators));								
		} catch (IOException e) {
			e.printStackTrace();
		} 			
	}
	
	private void updateTreeView(List<String> indicatorsList) {		       	
        var childrenRootAnswer = rootAnswersItem.getChildren();
        childrenRootAnswer.clear();        
        for (final var VALUE : indicatorsList) {
            var answer = new TreeItem<String>(VALUE);        
            childrenRootAnswer.add(answer);
        }          
	}	
	
	@FXML
    private void resetSearch() {				
		rulesEngine.clearIndicators();
		
		var childrenRootAnswer = rootAnswersItem.getChildren();
		childrenRootAnswer.clear();
		
		var confimationAlert = new Alert(AlertType.INFORMATION, "Successful reboot", ButtonType.OK);
		confimationAlert.setTitle("Reset");
		confimationAlert.show();	
    }
}