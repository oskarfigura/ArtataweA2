package com.group1.artatawe.listings;

/**
 * Represents a single bid made on a Listing
 */
public class Bid {

	private final double price;
	private final String bidder;
	private final long date;
	
	/**
	 * Construct a new Bid object
	 * 
	 * @param price - The amount of money bid
	 * @param user  - The username of the user that bid
	 * @param date  - The date of the bid
	 */
	protected Bid(double price, String user, long date) {
		this.price = price;
		this.bidder = user;
		this.date = date;
	}

	/**
	 * The amount of money bid 
	 * @return Amount bid
	 */
	public double getPrice() {
		return this.price;
	}

	/**
	 * Get the username of the user that bid
	 * @return Username of bidder
	 */
	public String getBidder() {
		return this.bidder;
	}

	/**
	 * Get the date of the bid
	 * @return Date of bid
	 */
	public long getDate() {
		return this.date;
	}
}