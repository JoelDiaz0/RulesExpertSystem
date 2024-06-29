package com.ai.expert.controller;

import java.lang.System.Logger.Level;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clase que proporciona metodos que responden a eventos que respoden
 * a la vista generada por "resultView.fxml". 
 */

public class ResultController {

	/*
	 * Un logger para mostrar mensajes en consola sobre el funcionamiento del controlador.
	 */
	
	private static final System.Logger LOGGER = System.getLogger(ResultController.class.getName());
	
	/**
	 * Stage indipendiente para la generacion de otra ventana
	 */
	
	private Stage stage;

	@FXML private HBox posibleResponsesTree;
	
	@FXML private TextField userAnswerTextField;
	
	/**
	 * Evento que cierra la ventana "resultView.fxml".
	 */
	
	@FXML
	private void handleCloseResultViewOnAction() {
		stage.close();
	}
	
	/**
	 * Establecer una nueva stage.
	 * @param stage.
	 */
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setUserIndicators(List<String> userIndicators) {
		userAnswerTextField.setText(userIndicators.toString());
	}
	
	public void setPossibleAnswers(List<TreeView<String>> treesList, List<Double> percentagesList) {		
		
		if (treesList.size() != percentagesList.size()) {
			throw new IllegalArgumentException();
		}
		
		var children = posibleResponsesTree.getChildren();
		children.clear();	
		
		for (var i = 0; i < treesList.size(); i++){		
			var vbox = new VBox();
			vbox.setAlignment(Pos.TOP_CENTER);
			var labelPercentage = new Label(String.format("%s%%", percentagesList.get(i)));
			labelPercentage.setAlignment(Pos.TOP_CENTER);
			labelPercentage.getStyleClass().add("defaultText");
			
			var percentage = percentagesList.get(i);
			if (percentage < 33 && percentage >= 0) {
				labelPercentage.getStyleClass().add("redText");
			} else if (percentage < 66 && percentage >= 33) {
				labelPercentage.getStyleClass().add("orangeText");
			} else if (percentage <= 100 && percentage >= 66) {
				labelPercentage.getStyleClass().add("greenText");
			} else {
				labelPercentage.getStyleClass().add("blueText");
			}
							
			vbox.getChildren().addAll(labelPercentage, treesList.get(i));				
			children.add(vbox);		
			
			if (i != treesList.size() - 1) {
				children.add(new Separator(Orientation.VERTICAL));
			}			
		}
		
		LOGGER.log(Level.INFO, true);
	}	
}