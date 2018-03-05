package com.group1.artatawe.controllers;

import java.util.LinkedList;

import com.group1.artatawe.Main;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.utils.GridUtil;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Controller for "CurrentListings.fxml"
 */
public class CurrentListingController {

	//Header Attributes
	@FXML StackPane topstack;
	@FXML ImageView profileimage;
	@FXML ImageView logo;
	@FXML Button home;
	@FXML Button currentlistings;
	@FXML Button createlisting;
	@FXML Button logout;

	//Current Listing Specific Attributes
	@FXML GridPane alllistings;

	public void initialize() {
		this.initializeHeader();
		
		this.renderListings();
	}
	
	/**
	 * Initialize the header
	 */
	private void initializeHeader() {
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

	/**
	 * Turns a Listing into a node
	 * 
	 * @param listing - The listing to turn into a node
	 * @return The node created
	 */
	private Node getListingNode(Listing listing) {
		HBox hbox = new HBox();

		Image image = listing.getArtwork().getImage();
		ImageView iv = new ImageView(image);
		iv.setPreserveRatio(true);
		iv.setFitHeight(120);
		iv.setFitWidth(120);

		ListView<String> info = new ListView<>();
		info.getItems().addAll(listing.getArtwork().getTitle(), 
				listing.getArtwork().getArtist());
		
		if(listing.hasDescription()) {
			info.getItems().add(listing.getArtwork().getDescription());
		}
		
		info.setMaxHeight(125);

		if(listing.getCurrentBid() == null) {
			info.getItems().add("£" + listing.getReserve());
		} else {
			info.getItems().add("£" + listing.getCurrentBid().getPrice());
		}

		info.getItems().add(listing.getBidsLeft() + " bids remaining");
		
		hbox.setOnMouseClicked(e -> ViewListingController.viewListing(listing));
		
		hbox.getChildren().add(iv);
		hbox.getChildren().add(info);

		return hbox;
	}
	
	/**
	 * Render all the active listings into the box
	 */
	private void renderListings() {
		LinkedList<Node> nodes = new LinkedList<>();
		
		for(Listing listing : Main.listingManager.getAllActiveListings()) {
			nodes.add(this.getListingNode(listing));
		}
		
		GridUtil.insertList(this.alllistings, nodes);
	}
}