package com.group1.artatawe.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.group1.artatawe.Main;
import com.group1.artatawe.utils.AlertUtil;
import com.group1.artatawe.utils.NumUtil;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.artwork.Painting;
import com.group1.artatawe.artwork.Sculpture;
import com.group1.artatawe.listings.Bid;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.listings.ListingState;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;

/**
 * Controller for "ViewListing.fxml"
 */
public class ViewListingController {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private static final double MAX_POPUP_IMG_SIZE = 600;

	//The listing that is being viewed
	private static Listing viewing = null;

	//If the account is viewing their own listing
	private static boolean viewingOwnListing = false;

	//Header Attributes
	@FXML StackPane topstack;
	@FXML ImageView profileimage;
	@FXML Button home;
	@FXML Button currentlistings;
	@FXML Button createlisting;
	@FXML Button logout;

	//Listing Attributes
	@FXML ImageView image;
	@FXML Label title;
	@FXML VBox infobox;
	@FXML Label currentbid;
	@FXML Label bidsleft;
	@FXML TextField amount;
	@FXML Button placebid;
	@FXML VBox bidhistorybox;

	//Seller Attributes
	@FXML ImageView selleravatar;
	@FXML Label sellername;

	public void initialize() {
		this.initializeHeader();

		this.title.setText(viewing.getArtwork().getTitle());

		this.placebid.setOnMouseClicked(e -> this.handleBid());

		//Display the Artwork's main image
		this.image.setImage(viewing.getArtwork().getImage());
		this.image.setOnMouseClicked(e -> this.viewLargeImage(viewing.getArtwork().getImage()));

		//If a user is viewing their own Listing, hide the bidding UI
		if(viewingOwnListing) {
			this.amount.setVisible(false);
			this.placebid.setVisible(false);
		}

		if(viewing.getListingState() != ListingState.ACTIVE) {
			this.amount.setVisible(false);
			this.placebid.setVisible(false);
			this.bidsleft.setText("Auction Has Ended.");
		}

		//Set seller information
		Account seller = Main.accountManager.getAccount(viewing.getSeller());

		this.selleravatar.setImage(seller.getAvatar());
		this.sellername.setText(seller.getFirstName() + " " + seller.getLastName());

		this.selleravatar.setOnMouseClicked(e -> ProfileController.viewProfile(seller));
		this.sellername.setOnMouseClicked(e -> ProfileController.viewProfile(seller));

		//Render the different parts of the Listing view
		this.displayCurrentBid();
		this.displayBidHistory();
		this.renderInfo();
	}

	/**
	 * View a listing
	 * @param listing - The listing to view
	 */
	public static void viewListing(Listing listing) {
		viewing = listing;
		viewingOwnListing = Main.accountManager.getLoggedIn().getUserName().equals(listing.getSeller());

		Main.switchScene("ViewListing");
	}

	/**
	 * Initialize the header
	 */
	private void initializeHeader() {
		this.currentlistings.setOnMouseClicked(e -> Main.switchScene("CurrentListings"));
		this.profileimage.setImage(Main.accountManager.getLoggedIn().getAvatar());
		this.profileimage.setOnMouseClicked(e -> ProfileController.viewProfile(Main.accountManager.getLoggedIn()));
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
	 * Handle the bid button
	 */
	private void handleBid() {
		//The UI allowing users to bid should be invisible to the Listings creator.
		if(viewingOwnListing) {
			throw new IllegalStateException("A user cannot bid on their own listing!");
		}

		if(viewing.getListingState() != ListingState.ACTIVE) {
			throw new IllegalStateException("Tried to bid on a listing that is not active!");
		}

		if(viewing.getCurrentBid() != null) {
			if(viewing.getCurrentBid().getBidder().equalsIgnoreCase(Main.accountManager.getLoggedIn().getUserName())) {
				AlertUtil.sendAlert(AlertType.INFORMATION, "Wow Buddy!", "You are already the highest bidder");
				return;
			}
		}

		double prevAmt = viewing.getCurrentBid() == null ? viewing.getReserve() : viewing.getCurrentBid().getPrice();
		double amount = 0;

		try {
			amount = NumUtil.toDouble(this.amount.getText(), num -> num.doubleValue() > prevAmt);
		} catch(Exception e) {
			AlertUtil.sendAlert(AlertType.ERROR, "Invalid bid", "The amount you have entered is either invalid, or not high enough. Try again.");
			return;
		}

		viewing.createBid(amount, Main.accountManager.getLoggedIn());

		this.displayBidHistory();
		this.displayCurrentBid();

		if(viewing.getListingState() == ListingState.FINISHED) {
			this.placebid.setVisible(false);
			this.amount.setVisible(false);
			this.bidsleft.setText("Auction Has Ended");
			AlertUtil.sendAlert(AlertType.INFORMATION, "Success!", "You have won the auction!");
		} else {
			AlertUtil.sendAlert(AlertType.INFORMATION, "Success!", "Your bid has been placed. Good luck!");
		}
	}

	/**
	 * Update the current bid
	 */
	private void displayCurrentBid() {
		Bid current = viewing.getCurrentBid();

		if(viewing.getListingState() != ListingState.ACTIVE) {
			this.bidsleft.setText("Auction Has Ended");
		} else {
			this.bidsleft.setText(viewing.getBidsLeft() + "");
		}


		if(current != null) {
			this.currentbid.setText("£" + current.getPrice() + " (" + current.getBidder() + ")");
			this.currentbid.setOnMouseClicked(e -> {
				ProfileController.viewProfile(Main.accountManager.getAccount(current.getBidder()));
			});
		} else {
			this.currentbid.setText("£" + viewing.getReserve());
		}
	}

	/**
	 * Render all the bids into the bid history box.
	 * 
	 * Is the creator of the Listing is viewing it, then the entire history will be displayed.
	 * If another user is viewing the Listing, then they will only see their bids.
	 */
	private void displayBidHistory() {
		this.bidhistorybox.getChildren().clear();

		for(Bid bid : viewing.getBidHistory().getAllBids()) {
			if(viewingOwnListing) {
				//Creator of listing can see complete history
				this.renderBidIntoHistory(bid);

			} else if(Main.accountManager.getLoggedIn().getUserName().equalsIgnoreCase(bid.getBidder())) {
				//Otherwise users can only see their bid history
				this.renderBidIntoHistory(bid);
			}
		}
	}

	/**
	 * Render a single bid into the history box
	 * @param bid - The bid to render
	 */
	private void renderBidIntoHistory(Bid bid) {
		Account bidder = Main.accountManager.getAccount(bid.getBidder());

		VBox vbox = new VBox(2);

		vbox.setAlignment(Pos.CENTER);
		vbox.setStyle("-fx-border-color: lightgrey;");

		ImageView avatar = new ImageView(bidder.getAvatar());
		avatar.setFitHeight(70);
		avatar.setFitWidth(70);

		Label name = new Label("Name: " + bidder.getFirstName());
		Label amount = new Label("Bid: £" + bid.getPrice());
		Label time = new Label("Date: " + DATE_FORMAT.format(new Date(bid.getDate())));

		vbox.getChildren().addAll(avatar, name, amount, time);

		vbox.setOnMouseClicked(e -> ProfileController.viewProfile(bidder));

		this.bidhistorybox.getChildren().add(0, vbox);
	}

	/**
	 * Get a label for displaying a header
	 * @param text - The text to display
	 * @return The label
	 */
	private Label getHeaderLabel(String text) {
		Label label = new Label();
		label.setFont(new Font(label.getFont().getName(), 18));
		label.setUnderline(true);
		label.setText(text);
		return label;
	}

	/**
	 * Get a label for displaying text / information
	 * @param text - The text to display
	 * @return The label
	 */
	private Label getTextLabel(String text) {
		Label label = new Label(text);
		label.setFont(new Font(label.getFont().getName(), 18));
		return label;
	}

	/**
	 * Add information on to the info box. 
	 * Will format the header and value, and but them together.
	 * 
	 * @param header - The header
	 * @param value  - The value
	 */
	private void displayInfo(String header, String value) {
		HBox hbox = new HBox();
		hbox.setSpacing(10);

		Label headerLbl = this.getHeaderLabel(header + ":");
		Label textLbl = this.getTextLabel(value);

		hbox.getChildren().addAll(headerLbl, textLbl);
		this.infobox.getChildren().add(hbox);
	}

	/**
	 * Add the Artwork's description to the info box
	 */
	private void displayDescription() {
		this.infobox.getChildren().add(this.getHeaderLabel("Description"));

		//We use a Hbox just so we can apply a border to make the description stand out
		HBox descBox = new HBox();

		descBox.setMinWidth(this.infobox.getWidth());
		descBox.setStyle("-fx-border-color: lightgrey;");

		Label desc = this.getTextLabel(viewing.getArtwork().getDescription());
		desc.setWrapText(true);
		descBox.getChildren().add(desc);

		this.infobox.getChildren().add(descBox);
	}

	/**
	 * Populate the info box
	 */
	private void renderInfo() {
		//Load default information
		this.displayInfo("Artist", viewing.getArtwork().getArtist());
		this.displayInfo("Year", viewing.getArtwork().getYear() + "");
		this.displayDescription();

		//Load specific info to a type of Artwork
		if(viewing.getArtwork() instanceof Painting) {
			Painting painting = (Painting) viewing.getArtwork();

			this.displayInfo("Width", painting.getWidth() + "");
			this.displayInfo("Height", painting.getWidth() + "");
		} else if(viewing.getArtwork() instanceof Sculpture) {
			Sculpture sculpture = (Sculpture) viewing.getArtwork();

			this.displayInfo("Width", sculpture.getWidth() + "");
			this.displayInfo("Height", sculpture.getWidth() + "");
			this.displayInfo("Depth", sculpture.getDepth() + "");
			this.displayInfo("Weight", sculpture.getWeight() + "");
			this.displayInfo("Material", sculpture.getMaterial() + "");

			if(! sculpture.getExtraImages().isEmpty()) {
				HBox extraImages = new HBox(10);

				ScrollPane sp = new ScrollPane(extraImages);
				sp.setVbarPolicy(ScrollBarPolicy.NEVER);
				sp.setHbarPolicy(ScrollBarPolicy.ALWAYS);

				for(Image image : sculpture.getExtraImages()) {
					ImageView iv = new ImageView(image);
					iv.setFitHeight(70);
					iv.setFitWidth(70);

					extraImages.getChildren().add(iv);

					iv.setOnMouseClicked(e -> this.viewLargeImage(image));
				}

				this.infobox.getChildren().add(sp);
			}
		}
	}

	/**
	 * View an image as large as possible
	 * 
	 * @param image - The image to view
	 */
	private void viewLargeImage(Image image) {
		Popup popup = new Popup();

		ImageView iv = new ImageView(image);
		iv.setPreserveRatio(true);

		//Crop the image if it's too large.
		if(image.getWidth() > MAX_POPUP_IMG_SIZE) {
			iv.setFitWidth(MAX_POPUP_IMG_SIZE);
		}

		if(image.getHeight() > MAX_POPUP_IMG_SIZE) {
			iv.setFitHeight(MAX_POPUP_IMG_SIZE);
		}

		popup.getContent().add(iv);

		popup.setHeight(image.getHeight());
		popup.setWidth(image.getWidth());

		popup.setHideOnEscape(true);
		popup.setAutoHide(true);

		popup.show(this.image.getScene().getWindow());
	}
}