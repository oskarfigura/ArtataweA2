package com.group1.artatawe.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.utils.GridUtil;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Controller for "Profile.fxml"
 */
public class ProfileController {

	private static Account viewing = null;
	private boolean viewingOwnProfile = false;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

	//Header Attributes
	@FXML StackPane topstack;
	@FXML ImageView profileimage;
	@FXML Button home;
	@FXML Button currentlistings;
	@FXML Button createlisting;
	@FXML Button logout;

	//Profile Specific Attributes
	@FXML ImageView avatar;
	@FXML Button favbutton;
	@FXML Label firstname;
	@FXML Label lastname;
	@FXML Label username;
	@FXML Label lastseen;
	@FXML Button editaccount;

	@FXML GridPane selling;
	@FXML GridPane sold;
	@FXML GridPane wonauctions;

	public void initialize() {
		Account loggedIn = Main.accountManager.getLoggedIn();
		viewingOwnProfile = loggedIn.getUserName().equals(viewing.getUserName());
		
		this.initializeHeader();
		this.initializeFavButton();

		this.renderSellingListings();
		this.renderSoldListings();
		this.renderWonListings();

		this.avatar.setImage(viewing.getAvatar());
		this.firstname.setText(viewing.getFirstName());
		this.lastname.setText(viewing.getLastName());
		this.username.setText(viewing.getUserName());
		this.lastseen.setText(DATE_FORMAT.format(new Date(viewing.getLastLogin())));

		if(! viewingOwnProfile) {
			this.editaccount.setVisible(false);
		}

		this.editaccount.setOnMouseClicked(e -> EditAccountController.editAccount());

		this.favbutton.setOnMouseClicked(e -> {
			if(! viewingOwnProfile) {
				if(loggedIn.isFavAccount(viewing)) {
					loggedIn.removeFavAccounts(viewing);
				} else {
					loggedIn.addFavAccount(viewing);
				}
				this.initializeFavButton();
			}
		});
	}

	/**
	 * Open the profile GUI on a specific account
	 * @param account - The account to show the GUI of
	 */
	public static void viewProfile(Account account) {
		viewing = account;
		Main.switchScene("Profile");
	}

	/**
	 * Initialize the header
	 */
	private void initializeHeader() {
		this.currentlistings.setOnMouseClicked(e -> Main.switchScene("CurrentListings"));
		this.profileimage.setImage(Main.accountManager.getLoggedIn().getAvatar());
		this.profileimage.setOnMouseClicked(e -> viewProfile(Main.accountManager.getLoggedIn()));
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
	 * Initialize the fav account button. Will set the formatting of the favourite button (gold or grey) 
	 * depending on if the viewing account is favourite.
	 */
	private void initializeFavButton() {
		if(! viewingOwnProfile) {
			if(Main.accountManager.getLoggedIn().isFavAccount(viewing)) {
				this.favbutton.setStyle("-fx-background-color: #ffb938;");
			} else {
				this.favbutton.setStyle("-fx-background-color: lightgrey;");
			}
		} else {
			this.favbutton.setVisible(false);
		}
	}

	/**
	 * Turns a Listing that is currently being sold, into a node
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
	 * Turns a Listing that has been sold, into a node
	 * 
	 * @param listing - The listing to turn into a node
	 * @return The node created
	 */
	private Node getSoldListingNode(Listing listing) {
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

		if(listing.getCurrentBid() != null) {
			info.getItems().add("Sold for: £" + listing.getCurrentBid().getPrice());
			info.getItems().add("Winner: " + listing.getCurrentBid().getBidder());
		}

		hbox.setOnMouseClicked(e -> ViewListingController.viewListing(listing));
		hbox.getChildren().add(iv);
		hbox.getChildren().add(info);

		return hbox;
	}

	/**
	 * Turns a Listing that has been won, into a node
	 * 
	 * @param listing - The listing to turn into a node
	 * @return The node created
	 */
	private Node getWonListingNode(Listing listing) {
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

		if(listing.getCurrentBid() != null) {
			info.getItems().add("Purchased for: £" + listing.getCurrentBid().getPrice());
		}

		hbox.setOnMouseClicked(e -> ViewListingController.viewListing(listing));
		hbox.getChildren().add(iv);
		hbox.getChildren().add(info);

		return hbox;
	}

	/**
	 * Get all the listings the viewing account is selling and add it to the box
	 */
	private void renderSellingListings() {
		LinkedList<Node> nodes = new LinkedList<>();

		viewing.getHistory().getSellingListings().stream()
			.forEach(listing -> nodes.add(this.getSellingListingNode(listing)));

		GridUtil.insertList(this.selling, nodes);
	}

	/**
	 * Get all the listings the viewing account has sold and add it to the box
	 */
	private void renderSoldListings() {
		LinkedList<Node> nodes = new LinkedList<>();

		viewing.getHistory().getSoldListings().stream()
		.forEach(listing -> nodes.add(this.getSoldListingNode(listing)));

		GridUtil.insertList(this.sold, nodes);
	}

	/**
	 * Get all the listings the viewing account has one and add it to the box
	 */
	private void renderWonListings() {
		LinkedList<Node> nodes = new LinkedList<>();

		viewing.getHistory().getWonListings().stream()
		.forEach(listing -> nodes.add(this.getWonListingNode(listing)));

		GridUtil.insertList(this.wonauctions, nodes);
	}
}