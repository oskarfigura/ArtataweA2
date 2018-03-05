package com.group1.artatawe.controllers;

import com.group1.artatawe.Main;
import com.group1.artatawe.utils.AlertUtil;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.accounts.Address;
import com.group1.artatawe.drawing.DrawingController;
import com.group1.artatawe.utils.ValidationUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EditAccountController {

	//Header Attributes
	@FXML StackPane topstack;
	@FXML ImageView profileimage;
	@FXML Button home;
	@FXML Button currentlistings;
	@FXML Button createlisting;
	@FXML Button logout;

	//Edit Account Attributes
	@FXML TextField firstname;
	@FXML TextField lastname;
	@FXML TextField mobilenum;
	@FXML TextField line1;
	@FXML TextField line2;
	@FXML TextField line3;
	@FXML TextField city;
	@FXML TextField postcode;

	@FXML VBox defaultavatars;
	@FXML ImageView selectedavatar;

	@FXML Button done;
	@FXML Button back;
	@FXML Button drawavatar;

	public void initialize() {
		this.initializeHeader();
		
		this.done.setOnMouseClicked(e -> this.handleDoneButton() );
		this.back.setOnMouseClicked(e -> ProfileController.viewProfile(Main.accountManager.getLoggedIn()));

		this.drawavatar.setOnMouseClicked(e -> {
			DrawingController.openDrawGUI();
			if(DrawingController.WAS_SAVED) {
				this.selectedavatar.setImage(DrawingController.LAST_IMAGE);
			}
		});

		//Add a click event to each of the default built-in avatars
		this.defaultavatars.getChildren().stream().forEach(node -> {
			if(node instanceof ImageView) {
				ImageView iv = (ImageView) node;
				iv.setOnMouseClicked(e -> {
					this.selectedavatar.setImage(iv.getImage());
				});
			}
		});
		
		this.loadDefaultValues();
	}
	
	public static void editAccount() {
		Main.switchScene("EditAccount");
	}
	
	/**
	 * Initialize the header
	 */
	private void initializeHeader() {
		this.currentlistings.setOnMouseClicked(e -> Main.switchScene("CurrentListings"));
		this.profileimage.setImage(Main.accountManager.getLoggedIn().getAvatar());
		this.createlisting.setOnMouseClicked(e -> Main.switchScene("CreateListing"));
		this.home.setOnMouseClicked(e -> Main.switchScene("Home"));
		this.logout.setOnMouseClicked(e -> Main.accountManager.logoutCurrentAccount());

		//I could not get topstack to ignore the mouse event and let the child nodes handle it, so instead
		//we check where the click happened and what should actually of been clicked.
		this.topstack.setOnMouseClicked(e -> {
			if(this.profileimage.intersects(e.getX(), e.getY(), 0, 0)) {
				ProfileController.viewProfile(Main.accountManager.getLoggedIn());
			}
		});
	}
	
	private void loadDefaultValues() {
		Account loggedIn = Main.accountManager.getLoggedIn();
		
		this.selectedavatar.setImage(loggedIn.getAvatar());
		
		this.firstname.setText(loggedIn.getFirstName());
		this.lastname.setText(loggedIn.getLastName());
		this.mobilenum.setText(loggedIn.getMobileNum());
		
		Address address = loggedIn.getAddress();
		this.line1.setText(address.getLine(1));
		this.line2.setText(address.getLine(2));
		this.line3.setText(address.getLine(3));
		this.city.setText(address.getCity());
		this.postcode.setText(address.getPostcode());
	}

	/**
	 * Handle the done button. Will try to create the account
	 */
	private void handleDoneButton() {
		//First check if all the required fields are filled
		if(! checkAllFieldsAreFilled()) {
			AlertUtil.sendAlert(AlertType.ERROR, "Form not filled", "A required field has not been filled!");
			return;
		}

		String firstName = this.firstname.getText().trim();
		String lastName = this.lastname.getText().trim();
		String mobileNum = this.mobilenum.getText().trim();
		String line1 = this.line1.getText().trim();
		String line2 = this.line2.getText().trim();
		String line3 = this.line3.getText().trim();
		String city = this.city.getText().trim();
		String postcode = this.postcode.getText().trim();

		//Validate the postcode
		if(! ValidationUtil.validatePostcode(postcode)) {
			AlertUtil.sendAlert(AlertType.ERROR, "Invalid Postcode", "The postcode entered is not valid.");
			return;
		}

		//Validate the phone number
		if(! ValidationUtil.validatePhone(mobileNum)) {
			AlertUtil.sendAlert(AlertType.ERROR, "Invalid Phone Number", "The phone number entered is not valid.");
			return;
		}

		Address address = new Address(line1, line2, line3, city, postcode);

		Account loggedIn = Main.accountManager.getLoggedIn();
		
		loggedIn.setFirstName(firstName);
		loggedIn.setLastName(lastName);
		loggedIn.setMobileNum(mobileNum);
		loggedIn.setAddress(address);
		loggedIn.setAvatar(this.selectedavatar.getImage());

		Main.accountManager.saveAccountFile();
		
		//Switch to the home page
		Main.switchScene("Home");
	}

	/**
	 * Check if all the fields are filled
	 * @return True if all required fields are filled, else False
	 */
	private boolean checkAllFieldsAreFilled() {
		return this.isFieldFilled(this.firstname) &&
				this.isFieldFilled(this.lastname) &&
				this.isFieldFilled(this.mobilenum) &&
				this.isFieldFilled(this.line1) &&
				this.isFieldFilled(this.city) &&
				this.isFieldFilled(this.postcode);
	}

	/**
	 * Check if a TextField is filled
	 * @param field - The field to check
	 * @return True if the field is filled, else False
	 */
	private boolean isFieldFilled(TextField field) {
		return ! field.getText().trim().isEmpty();
	}
}