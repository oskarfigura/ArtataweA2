package com.group1.artatawe.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import com.group1.artatawe.Main;
import com.group1.artatawe.utils.AlertUtil;
import com.group1.artatawe.utils.NumUtil;
import com.group1.artatawe.artwork.Artwork;
import com.group1.artatawe.artwork.Painting;
import com.group1.artatawe.artwork.Sculpture;
import com.group1.artatawe.utils.ImageUtil;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Controller for "CreateListing.fxml"
 */
public class CreateListingController {

	private static final String TAB_PAINTING_NAME = "Painting";
	private static final String TAB_SCULPTURE_NAME = "Sculpture";

	//Header Attributes
	@FXML StackPane topstack;
	@FXML Button currentlistings;
	@FXML Button home;
	@FXML Button logout;
	@FXML ImageView profileimage;

	//Main Attributes
	@FXML Button done;
	@FXML ImageView image;
	@FXML Button selectimage;
	@FXML TextField title;
	@FXML TextArea description;
	@FXML TextField artist;
	@FXML TextField year;
	@FXML TextField reserve;
	@FXML TextField maxbids;

	@FXML TabPane tabs;

	//Painting Tab Stuff, prefixed with p
	@FXML TextField pheight;
	@FXML TextField pwidth;

	//Sculpture Tab stuff, prefixed with s
	@FXML TextField sheight;
	@FXML TextField swidth;
	@FXML TextField sdepth;
	@FXML TextField sweight;
	@FXML TextField smaterial;
	@FXML HBox sextraimagebox;
	@FXML Button sextraimagebutton;

	private boolean isSelectingFile = false;

	public void initialize() {
		this.initializeHeader();
		
		this.done.setOnMouseClicked(e -> this.handleDoneButton());
		this.selectimage.setOnMouseClicked(e -> this.handleImageSelect());
		this.sextraimagebutton.setOnMouseClicked(e -> this.handleExtraImageSelect());
	}
	
	/**
	 * Initialize the header
	 */
	private void initializeHeader() {
		this.currentlistings.setOnMouseClicked(e -> Main.switchScene("CurrentListings"));
		this.profileimage.setImage(Main.accountManager.getLoggedIn().getAvatar());
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
	
	/**
	 * Handle the done button click event
	 */
	private void handleDoneButton() {
		if(! checkAllFieldsAreFilled()) {
			AlertUtil.sendAlert(AlertType.ERROR, "Invalid Arguments!", "Not all of the required fields are filled!");
			return;
		}

		//Load default attributes
		String title = this.title.getText().trim();
		String desc = this.description.getText().trim().isEmpty() ? "" : this.description.getText().trim();
		
		//Remove any new lines
		desc = desc.replaceAll("\\n", " ");
		
		String artist = this.artist.getText().trim();
		Image img = this.image.getImage();

		int year = 0;
		double reserve = 0;
		int maxBids = 0;

		//Validate number input
		try {
			year = NumUtil.toInt(this.year.getText(), NumUtil.POSITIVE);
			reserve = NumUtil.toDouble(this.reserve.getText(), NumUtil.POSITIVE_OR_ZERO);
			maxBids = NumUtil.toInt(this.maxbids.getText(), NumUtil.POSITIVE);
		} catch (Exception e) {
			AlertUtil.sendAlert(AlertType.ERROR, "Invalid Arguments!", "Wrong values have been provided");
			return;
		}

		Artwork artwork = null;

		//Load artwork specific attributes, depending on the selected tab
		String tabName = this.getSelectedTab();
		if(tabName.equalsIgnoreCase(TAB_PAINTING_NAME)) {
			double height = 0;
			double width = 0;

			//Validate number input
			try {
				height = NumUtil.toDouble(this.pheight.getText(), NumUtil.POSITIVE);
				width = NumUtil.toDouble(this.pwidth.getText(), NumUtil.POSITIVE);
			} catch (Exception e) {
				AlertUtil.sendAlert(AlertType.ERROR, "Invalid Arguments!", "Wrong values have been provided");
				return;
			}

			artwork = new Painting(title, desc, img , artist, year, height, width);

		} else if(tabName.equalsIgnoreCase(TAB_SCULPTURE_NAME)) {
			//Load basic attributes
			double height = 0;
			double width = 0;
			double weight = 0;
			double depth = 0;

			//Validate number input
			try {
				height = NumUtil.toDouble(this.sheight.getText(), NumUtil.POSITIVE);
				width = NumUtil.toDouble(this.swidth.getText(), NumUtil.POSITIVE);
				weight = NumUtil.toDouble(this.swidth.getText(), NumUtil.POSITIVE);
				depth = NumUtil.toDouble(this.swidth.getText(), NumUtil.POSITIVE);
			} catch (Exception e) {
				AlertUtil.sendAlert(AlertType.ERROR, "Invalid Arguments!", "Wrong values have been provided");
				return;
			}

			String material = this.smaterial.getText();

			List<Image> extraImages = new LinkedList<>();

			//Loop through every image in the extra image box and add it to extra images
			for(Node child : this.sextraimagebox.getChildren()) {
				if(child instanceof ImageView) {
					extraImages.add(((ImageView) child).getImage());
				}
			}

			artwork = new Sculpture(title, desc, img, artist, year, height, width, depth, weight, material, extraImages);
		}

		if(artwork == null) {
			throw new NullPointerException("The artwork has not loaded! The tab: " + this.getSelectedTab() + " is not recognised?");
		}

		Main.listingManager.addListing(artwork, reserve, maxBids, Main.accountManager.getLoggedIn());
		Main.switchScene("Home");
	}

	/**
	 * Handle the image select button
	 */
	private void handleImageSelect() {
		if(! this.isSelectingFile) {
			Image image = this.chooseImage();
			if(image != null) {
				this.image.setImage(image);
			}
		}
	}
	
	/**
	 * Handle the extra image select button
	 */
	private void handleExtraImageSelect() {
		if(! this.isSelectingFile) {
			Image image = this.chooseImage();
			if(image != null) {
				ImageView iv = new ImageView(image);
				
				iv.setFitHeight(80);
				iv.setFitWidth(80);
				
				this.sextraimagebox.getChildren().add(iv);
				
				iv.setOnMouseClicked(e -> this.sextraimagebox.getChildren().remove(iv));
			}
		}
	}

	/**
	 * Open the GUI allowing the user to select a image file
	 * @return The selected Image. Will be <b>null</b> if no Image was selected
	 */
	private Image chooseImage() {
		this.isSelectingFile = true;

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a Image");
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("Image Files", "*jpg", "*png"));

		File selectedFile = fileChooser.showOpenDialog(null);
		
		Image image = null;
		if (selectedFile != null) {
			try {
				image = ImageUtil.loadImage(selectedFile);
			} catch (FileNotFoundException e) { 
				AlertUtil.sendAlert(AlertType.ERROR, "Error", "We could not load that image, try again.");
			}
		}

		this.isSelectingFile = false;
		return image;
	}

	/**
	 * Get the tab that is currently selected
	 * @return The name of the tab that is selected
	 */
	private String getSelectedTab() {
		return this.tabs.getSelectionModel().getSelectedItem().getText();
	}

	/**
	 * Check if all the fields are filled
	 * @return True if all required fields are filled, else False
	 */
	private boolean checkAllFieldsAreFilled() {
		if(this.image.getImage() == null) {
			return false;
		}

		//I know this breaks the coding convention, but the layout would be very unreadable otherwise :/

		//Check main attributes
		if(this.title.getText().trim().isEmpty()) { return false; }
		if(this.artist.getText().trim().isEmpty()) { return false; }
		if(this.year.getText().trim().isEmpty()) { return false; }
		if(this.reserve.getText().trim().isEmpty()) { return false; }
		if(this.maxbids.getText().trim().isEmpty()) { return false; }

		//Check the attributes in the selected tab
		String tabName = this.getSelectedTab();
		if(tabName.equalsIgnoreCase(TAB_PAINTING_NAME)) {

			if(this.pheight.getText().trim().isEmpty()) { return false; }
			if(this.pwidth.getText().trim().isEmpty()) { return false; }

		} else if(tabName.equalsIgnoreCase(TAB_SCULPTURE_NAME)) {

			if(this.sheight.getText().trim().isEmpty()) { return false; }
			if(this.swidth.getText().trim().isEmpty()) { return false; }
			if(this.sdepth.getText().trim().isEmpty()) { return false; }
			if(this.smaterial.getText().trim().isEmpty()) { return false; }
			if(this.sweight.getText().trim().isEmpty()) { return false; }
		}

		//Everything is filled!
		return true;
	}
}