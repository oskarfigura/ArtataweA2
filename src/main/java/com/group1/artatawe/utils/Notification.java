package com.group1.artatawe.utils;

import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.listings.Bid;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.managers.AccountManager;
import com.group1.artatawe.managers.ListingManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Oskar Figura and Nikolina Antoniou
 * @version 1.0
 * created on: 05/03/2018.
 */
/*
TODO:
1. New auctions notification. (DONE???)
2. Seller can see new bids on the items.
3. Auctions lost since last login.
4. Auctions user bid on, which are approaching their bid limit.
 */
public class Notification {
    private List<Listing> newListings;
    private List<Bid> newBids;
    private List<Listing> lostListings;
    private List<Listing> endingListings;
    private ListingManager listingManager;
    private AccountManager accountManager;
    private Account currentUser;
    private long lastLoginDate; //Time since 1/1/1970 in milliseconds

    public Notification() {
        this.listingManager = Main.listingManager;
        this.accountManager = Main.accountManager;
        this.currentUser = accountManager.getLoggedIn();
        this.lastLoginDate = currentUser.getLastLogin();
        this.newListings = getNewListings();    //Working??
        //this.newBids = getNewBids(); //Needs more work
        //this.lostListings = getLostListings(); //Need more data from lecturer Won/Lost????
        this.endingListings = getEndingListings();  //Working??
    }

    /**
     * Get a List of new artworks that are now on auction since last login
     * @return List of new artworks since last login
     */
    private List<Listing> getNewListings() {
        return listingManager.getAllActiveListings()
                .stream().filter(x -> x.getCurrentBid().getDate() < lastLoginDate)
                .collect(Collectors.toList());
    }

    /**
     * Get a list of new bids on sellers auctions since last login
     * @return List of new bids on sellers auctions
     */
    private List<Listing> getNewBids() {
        List<Listing> sellersListings = listingManager.getAllActiveListings()
                .stream().filter(x -> x.getCurrentBid().getDate() < lastLoginDate).filter(a -> a.getSeller().equals(currentUser))
                .collect(Collectors.toList());
        return null;
        //Main.accountManager.getLoggedIn().getUserName().equals(listing.getSeller());
        //return null;
    }

    /**
     * Get a list of lost auctions since last login
     * @return List of lost auctions
     */
    private List<Listing> getLostListings() {
        //TODO - In other words get a list of auctions that have ended on which user has bid
        return listingManager.getAllActiveListings()
                .stream().filter(x -> x.getCurrentBid().getDate() < lastLoginDate).filter(a -> a.getSeller().equals(currentUser))
                .collect(Collectors.toList());
    }

    /**
     * List of auctions a user bid on that are coming to a close
     * so they have less than 2 bids left
     * @return List of auctions close to their bid limit
     */
    private List<Listing> getEndingListings() {
        return listingManager.getAllActiveListings()
                .stream().filter(x -> x.getBidsLeft() < 2)
                .filter(a -> a.getBidHistory().getBid(currentUser) != null)
                .collect(Collectors.toList());
    }
}
