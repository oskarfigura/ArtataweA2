package com.group1.artatawe;

import java.io.IOException;

import com.group1.artatawe.managers.AccountManager;
import com.group1.artatawe.managers.ListingManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	public static final String WINDOW_TITLE = "Artatawe";

	public static final ListingManager listingManager = new ListingManager();
	public static final AccountManager accountManager = new AccountManager();
	
	private static Stage mainStage = null;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		mainStage = stage;
		
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/fxml/AccountSelect.fxml"));
			Scene scene = new Scene(root);

			//Stop the window being scaled down smaller than we can handle
			stage.setMinHeight(820);
			stage.setMinWidth(1280);

			stage.setScene(scene);
			stage.setTitle(WINDOW_TITLE);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Switch the scene in the main window
	 * 
	 * @param fxmlFileName - The FXML File's name to load and set the scene too.
	 *                       Will select the file out of the fxml directory. 
	 */
	public static void switchScene(String fxmlFileName) {
		try {
			Parent parent = FXMLLoader.load(Main.class.getResource("/fxml/" + fxmlFileName + ".fxml"));
			Scene newScene = new Scene(parent, mainStage.getScene().getWidth(), mainStage.getScene().getHeight());
			
			mainStage.setScene(newScene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}