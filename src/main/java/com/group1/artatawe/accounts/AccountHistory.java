package com.group1.artatawe.accounts;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.group1.artatawe.Main;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.listings.ListingState;

/*
 * Represents the history of the account, such as Listings bid on, 
 * and Listings the user has created. This is a cache.
 */
public class AccountHistory {

	private final Account account;
	
	//Cache of all Listings that an account has bidded on.
	//This cache will also be kept in the order of newest -> oldest.
	private final LinkedList<Listing> bidded = new LinkedList<>();
	
	//Cache of all Listings created by this account. 
	//This cache will also be kept in the order of newest -> oldest.
	private final LinkedList<Listing> auctioned = new LinkedList<>();
	
	/**
	 * Constructs a new AccountHistory
	 * 
	 * @param account  - The account whose history this is
	 */
	public AccountHistory(Account account) {
		this.account = account;
	}
	
	/**
	 * Add a Listing a user has bid on to the cache
	 * Will keep the ordering off the list 
	 * 
	 * @param listing - The listing bid on
	 */
	public void addBiddedOnListing(Listing listing) {
		if(this.bidded.contains(listing)) {
			this.bidded.remove(listing);
		}
		
		this.bidded.addFirst(listing);
	}
	
	/**
	 * Add a listing a user has auctioned/auctioning
	 * Will keep the ordering off the list
	 * 
	 * @param listing - The listing that is being auctioned/auctioning
	 */
	public void addSellingListing(Listing listing) {
		if(this.auctioned.contains(listing)) {
			this.auctioned.remove(listing);
		}
		this.auctioned.addFirst(listing);
	}
	
	/**
	 * Get all Listings an Account has ever bid on
	 * @return List of all Listings an account has bid on
	 */
	public LinkedList<Listing> getBiddedOnListings() {
		return this.bidded;
	}
	
	/**
	 * Get all Listings an Account has ever bid on
	 * @return List of all Listings an account has bid on
	 */
	public List<Listing> getBiddedOnActiveListings() {
		return this.bidded.stream()
				.filter(listing -> listing.getListingState() == ListingState.ACTIVE)
				.collect(Collectors.toList());
	}
	
	/**
	 * Get all the Listings an account is selling / has sold
	 * @return All Listings this account has created
	 */
	public LinkedList<Listing> getAllCreatedListings() {
		return this.auctioned;
	}
	
	/**
	 * Get all Listings the user has ever won
	 * @return The listings a user has won
	 */
	public List<Listing> getWonListings() {
		//If a auction is FINISHED and the current bid is this user,
		//then we know they have won this Listing
		return this.bidded.stream()
			.filter (listing -> 
						listing.getListingState() == ListingState.FINISHED
						&& listing.getCurrentBid().getBidder().equals(this.account.getUserName())
			        )
			.collect(Collectors.toList());
	}
	
	/**
	 * Get all the listings that have been sold by this user
	 * That is, the Listing is FINISHED
	 * 
	 * @return All Listings that have FINISHED
	 */
	public List<Listing> getSoldListings() {
		return this.auctioned.stream()
				.filter (listing -> listing.getListingState() == ListingState.FINISHED)
				.collect(Collectors.toList());
	}
	
	/**
	 * Get all the listings that are being sold by a user
	 * 
	 * @return All Listings that a AVTIVE
	 */
	public List<Listing> getSellingListings() {
		return this.auctioned.stream()
				.filter (listing -> listing.getListingState() == ListingState.ACTIVE)
				.collect(Collectors.toList());
	}
	
	/**
	 * Load the AcountHistory back from a JsonObject
	 * @param jo - The json object to load from
	 */
	protected void loadFromJson(JsonObject jo) {
		JsonArray bidArray = jo.get("bidded").getAsJsonArray();
		Iterator<JsonElement> bidIterator = bidArray.iterator();
		
		while(bidIterator.hasNext()) {
			int nextID = bidIterator.next().getAsInt();
			Listing l = Main.listingManager.getListing(nextID);
			this.bidded.add(l);
		}
		
		JsonArray aucArray = jo.get("auctioned").getAsJsonArray();
		Iterator<JsonElement> aucIterator = aucArray.iterator();
		
		while(aucIterator.hasNext()) {
			int nextID = aucIterator.next().getAsInt();
			Listing l = Main.listingManager.getListing(nextID);
			this.auctioned.add(l);
		}
	}
	
	/**
	 * Turn the AccountHistory into a JsonObject
	 * @return The JsonObject
	 */
	protected JsonObject toJsonObject() {
		JsonObject jo = new JsonObject();
		
		JsonArray bidArray = new JsonArray();
		
		for(Listing l : this.bidded) {
			bidArray.add(l.getID());
		}
		
		JsonArray auctArray = new JsonArray();
		
		for(Listing l : this.auctioned) {
			auctArray.add(l.getID());
		}
		
		jo.add("bidded", bidArray);
		jo.add("auctioned", auctArray);
		
		return jo;
	}
}
