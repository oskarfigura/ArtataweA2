package com.group1.artatawe.controllers;

import java.util.LinkedList;

import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.utils.GridUtil;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Controller for "Home.fxml"
 */
public class HomePageController {

	//Header Attributes
	@FXML StackPane topstack;
	@FXML ImageView profileimage;
	@FXML Button home;
	@FXML Button currentlistings;
	@FXML Button createlisting;
	@FXML Button logout;

	//Home Page Specific Attributes
	@FXML GridPane sellingbox;
	@FXML GridPane mybidbox;
	@FXML HBox favuserbox;

	public void initialize() {
		this.initializeHeader();
		
		this.renderMySelling();
		this.renderMyBids();
		this.renderFavUsers();
	}
	
	/**
	 * Initialize the header
	 */
	private void initializeHeader() {
		this.currentlistings.setOnMouseClicked(e -> Main.switchScene("CurrentListings"));
		this.profileimage.setImage(Main.accountManager.getLoggedIn().getAvatar());
		this.createlisting.setOnMouseClicked(e -> Main.switchScene("CreateListing"));
		//this.home.setOnMouseClicked(e -> Main.switchScene("Home"));
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
	 * Turns a Listing that is being sold, into a node
	 * 
	 * @param listing - The listing to turn into a node
	 * @return The node created
	 */
	private Node getSellingListingNode(Listing listing) {
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
	 * Turns a Listing that has been bid on, into a node
	 * 
	 * @param listing - The listing to turn into a node
	 * @return The node created
	 */
	private Node getBiddedOnListingNode(Listing listing) {
		HBox hbox = new HBox();

		Image image = listing.getArtwork().getImage();
		ImageView iv = new ImageView(image);
		iv.setPreserveRatio(true);
		iv.setFitHeight(120);
		iv.setFitWidth(120);

		double myBid = listing.getBidHistory().getBid(Main.accountManager.getLoggedIn()).getPrice();
		double currentBid = listing.getCurrentBid().getPrice();
		
		ListView<String> info = new ListView<>();
		info.getItems().addAll(listing.getArtwork().getTitle(),
				"£" + myBid,
				"£" + currentBid);
		info.getItems().add(listing.getBidsLeft() + " bids remaining");
		
		if(! listing.getCurrentBid().getBidder().equals(Main.accountManager.getLoggedIn().getUserName())) {
			info.getItems().add("You are not winning!");
		} else {
			info.getItems().add("You are winning");
		}
		
		info.setMaxHeight(125);
		
		hbox.setOnMouseClicked(e -> ViewListingController.viewListing(listing));

		hbox.getChildren().add(iv);
		hbox.getChildren().add(info);

		return hbox;
	}
	
	/**
	 * Turns a favourite Account into a node
	 * 
	 * @param account - The account to turn into a node
	 * @return The node created
	 */
	private Node getFavUserNode(Account account) {
		VBox vbox = new VBox();

		Image image = account.getAvatar();
		ImageView iv = new ImageView(image);
		iv.setPreserveRatio(true);
		iv.setFitHeight(120);
		iv.setFitWidth(120);

		Label label = new Label(account.getUserName());

		vbox.setAlignment(Pos.CENTER);
		
		vbox.setOnMouseClicked(e -> ProfileController.viewProfile(account));
		
		vbox.getChildren().add(iv);
		vbox.getChildren().add(label);

		return vbox;
	}
	
	/**
	 * Render all Listings, that are active and a user has bid on in the box
	 */
	private void renderMyBids() {
		LinkedList<Node> nodes = new LinkedList<>();
		
		Main.accountManager.getLoggedIn().getHistory().getBiddedOnActiveListings().stream()
			.forEach(listing -> nodes.add(this.getBiddedOnListingNode(listing)));
		
		GridUtil.insertList(this.mybidbox, nodes);
	}
	
	/**
	 * Render all the Listings the user is currently selling in the box
	 */
	private void renderMySelling() {
		LinkedList<Node> nodes = new LinkedList<>();
		
		Main.accountManager.getLoggedIn().getHistory().getSellingListings().stream()
			.forEach(listing -> nodes.add(this.getSellingListingNode(listing)));
		
		GridUtil.insertList(this.sellingbox, nodes);
	}
	
	/**
	 * Render the favourite users in the box
	 */
	private void renderFavUsers() {
		LinkedList<String> favAccs = Main.accountManager.getLoggedIn().getFavAccounts();
		LinkedList<Node> nodes = new LinkedList<>();
		
		for(String favAcct : favAccs) {
			Account account = Main.accountManager.getAccount(favAcct);
			Node node = this.getFavUserNode(account);
			
			node.setOnMouseClicked(e -> ProfileController.viewProfile(account));
			
			nodes.add(node);
		}
		
		this.favuserbox.getChildren().addAll(nodes);
	}
}