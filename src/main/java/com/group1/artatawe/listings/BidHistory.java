package com.group1.artatawe.listings;

import java.util.Iterator;
import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.group1.artatawe.accounts.Account;

/**
 * BidHistory will store all the different bids made on a Listing
 */
public class BidHistory {

	private final LinkedList<Bid> bids = new LinkedList<>();

	/**
	 * Construct a new bid history
	 */
	protected BidHistory() { }

	/**
	 * Construct a new bid history
	 * 
	 * @param jo - The JsonObject to load from
	 */
	public BidHistory(JsonObject jo) { 
		this.loadFromJson(jo);
	}

	/**
	 * Get the current (last) bid
	 * @return The current bid. Null if no bid has been made
	 */
	public Bid getCurrentBid() {
		if(this.bids.isEmpty()) {
			return null;
		}

		return this.bids.getLast();
	}

	/**
	 * Get the entire history of bids
	 * @return ListIterator containing all the bids
	 */
	public LinkedList<Bid> getAllBids() {
		return this.bids;
	}

	/**
	 * Get the last bid an account has placed
	 * 
	 * @param account - The account whose bid to get
	 * @return The last bid placed by an account
	 */
	public Bid getBid(Account account) {
		Iterator<Bid> it = this.bids.descendingIterator();
		while(it.hasNext()) {
			Bid next = it.next();
			if(next.getBidder().equals(account.getUserName())) {
				return next;
			}
		}
		return null;
	}

	/**
	 * Get the number of bids made in total
	 * @return The total number of bids
	 */
	public int getNumOfBids() {
		return this.bids.size();
	}

	/**
	 * Create a new bid
	 * Note: This will not check if the Listing should end
	 * 
	 * @param price  - The amount bid
	 * @param bidder - The Account of the bidder
	 */
	protected void createNewBid(double price, Account bidder) {
		this.bids.add(new Bid(price, bidder.getUserName(), System.currentTimeMillis()));
	}

	/**
	 * Turn the bid history into a JsonObject
	 * @return The JsonObject
	 */
	protected JsonObject toJsonObject() {
		JsonObject jo = new JsonObject();

		JsonArray bidArray = new JsonArray();

		for(Bid bid : this.bids) {
			JsonObject bidObj = new JsonObject();

			bidObj.addProperty("price", bid.getPrice());
			bidObj.addProperty("bidder", bid.getBidder());
			bidObj.addProperty("date", bid.getDate());

			bidArray.add(bidObj);
		}

		jo.add("bidhistory", bidArray);

		return jo;
	}

	/**
	 * Load the bid history from a JsonObject
	 * @param jo - The JsonObject
	 */
	protected void loadFromJson(JsonObject jo) {
		JsonArray bids = jo.get("bidhistory").getAsJsonArray();
		Iterator<JsonElement> it = bids.iterator();

		while(it.hasNext()) {
			JsonObject bidObj = it.next().getAsJsonObject();

			double price = bidObj.get("price").getAsDouble();
			String bidder = bidObj.get("bidder").getAsString();
			long date = bidObj.get("date").getAsLong();

			Bid bid = new Bid(price, bidder, date);
			this.bids.add(bid);
		}
	}
}