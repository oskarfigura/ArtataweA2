package com.group1.artatawe.controllers;

import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller for "AccountSelect.fxml" 
 */
public class SelectAccountController {

	@FXML HBox accountbox;
	@FXML Button createaccount;
	
	public void initialize() {
		this.loadAccounts();
		
		this.createaccount.setOnMouseClicked(e -> this.handleAccountCreate());
	}
	
	/**
	 * Load all the accounts into the box
	 */
	private void loadAccounts() {
		this.accountbox.getChildren().clear();
		
		for(Account account : Main.accountManager.getAccounts()) {
			VBox vbox = new VBox();
			vbox.setSpacing(10);
			
			ImageView avatar = new ImageView(account.getAvatar());
			avatar.setPreserveRatio(true);
			avatar.setFitHeight(120);
			avatar.setFitWidth(120);
			
			Label label = new Label(account.getUserName());
			
			vbox.setAlignment(Pos.CENTER);
			
			vbox.getChildren().add(avatar);
			vbox.getChildren().add(label);
			
			vbox.setPickOnBounds(true);
			avatar.setOnMouseClicked(e -> { handleAccountSwitch(account); });
			
			this.accountbox.getChildren().add(vbox);
		}
	}
	
	/**
	 * Log an account in
	 * @param account - The account to log in
	 */
	private void handleAccountSwitch(Account account) {
		Main.accountManager.login(account);
		Main.switchScene("Home");
	}
	
	/**
	 * Open the account creation gui
	 */
	private void handleAccountCreate() {
		try {
			Main.switchScene("CreateAccount");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}