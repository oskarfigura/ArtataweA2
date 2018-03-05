package com.group1.artatawe.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.artwork.Artwork;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.listings.ListingState;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Stores all Listings (ever created)
 */
public class ListingManager {

	private static final String LISTING_FILE = "listings.json";

	private final LinkedList<Listing> listings = new LinkedList<>();

	/**
	 * Construct a new ListingManager
	 * Will load all the Listings out of the file
	 */
	public ListingManager() { 
		this.loadListings();
	}
	
	/**
	 * Add a listing to the system. Will automatically be in the state ACTIVE
	 * 
	 * @param artwork - The artwork being sold
	 * @param reserve - The minimum amount this item can sell for
	 * @param maxBids - The number of bids before the listing is over
	 * @param seller  - The seller of the item
	 * @return The listing created
	 */
	public Listing addListing(Artwork artwork, double reserve, int maxBids, Account seller) {
		int id = listings.size() + 1;
		
		Listing l = new Listing(id, artwork, reserve, maxBids, seller.getUserName(), System.currentTimeMillis());
		this.listings.add(l);
		
		seller.getHistory().addSellingListing(l);
		
		this.saveListingsFile();
		return l;
	}

	/**
	 * Get all the Listings in the system, regardless of state
	 * @return All listings
	 */
	public List<Listing> getAllListings() {
		return this.listings;
	}

	/**
	 * Get all active Listings in the system
	 * @return All the active listings
	 */
	public List<Listing> getAllActiveListings() {
		return this.listings.stream()
				.filter(listing -> listing.getListingState() == ListingState.ACTIVE)
				.collect(Collectors.toList());
	}

	/**
	 * Get a listing with a specific ID
	 * 
	 * @param id - The ID to search for
	 * @throws NoSuchElementException if a Listing with that ID does not exist
	 * @return The Listing with that ID.
	 */
	public Listing getListing(int id) throws NoSuchElementException {
		return this.listings.stream()
				.filter(listing -> listing.getID() == id)
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("A listing with the id " + id + " doesn't exist"));
	}
	
	/**
	 * Save all the Listings back to the file
	 */
	public void saveListingsFile() {
		StringBuilder data = new StringBuilder();
		
		this.listings.forEach(listing -> data.append(listing.toJsonObject().toString() + "\n"));
		
		File listingFile = new File(LISTING_FILE);
		
		try {
			Files.write(listingFile.toPath(), data.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Open file and load all the listings out of the file
	 */
	private void loadListings() {
		File file = new File(LISTING_FILE);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);

			while(scanner.hasNextLine()) {
				String nextLine = scanner.nextLine().trim();

				if(! nextLine.isEmpty()) {
					this.loadListing(nextLine);
				}
			}
		} catch (FileNotFoundException e) {
			try {
				new File(LISTING_FILE).createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			if(scanner != null) { 
				scanner.close(); 
			}
		}
	}


	/**
	 * Loads a Listing from a Json String into the system
	 * 
	 * @param jsonString - The Json String that is turned into a Listing
	 */
	private void loadListing(String jsonString) {
		JsonParser jp = new JsonParser();
		try {
			JsonObject jo = jp.parse(jsonString).getAsJsonObject();

			Listing listing = new Listing(jo);

			this.listings.add(listing);
		} catch(Exception e) {
			System.out.println("Parse error on string: \n" + jsonString + "\nThe listing has not been loaded.");
		}
	}
}
