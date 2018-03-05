package com.group1.artatawe.listings;

/**
 * Represents all the states a Listing can be in.
 */
public enum ListingState {
	/** The Auction is currently underway */
	ACTIVE,
	
	/** The Auction has finished */
	FINISHED,
	
	/** The Auction has been cancelled without finishing (not currently implemented) */
	CANCELLED
}