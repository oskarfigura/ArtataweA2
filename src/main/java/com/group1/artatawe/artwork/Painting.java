package com.group1.artatawe.artwork;

import com.google.gson.JsonObject;
import com.group1.artatawe.listings.Listing;

import javafx.scene.image.Image;

/**
 * Represents the painting type of Artwork.
 */
public class Painting extends Artwork {

	private final double height;
	private final double width;
	
	/**
	 * Construct a new Painting
	 * 
	 * @param height - The height of the painting
	 * @param width  - The width of the painting
	 * @see {@link Artwork#Artwork Artwork}'s constructor for more parameter descriptions.
	 */
	public Painting(String title, String desc, Image img, String artist, int year, 
			double height, double width) {
		super(title, desc, img, artist, year);
		
		this.height = height;
		this.width = width;
	}

	/**
	 * Get the height of this painting
	 * @return The height of the painting
	 */
	public double getHeight() {
		return this.height;
	}

	/**
	 * Get the width of this painting
	 * @return The width of the painting
	 */
	public double getWidth() {
		return this.width;
	}
	
	@Override
	protected JsonObject getChildJsonObject(Listing listing) {
		JsonObject jo = new JsonObject();
		
		jo.addProperty("height", this.height);
		jo.addProperty("width", this.width);
		
		return jo;
	}
	
	/**
	 * Load a Painting from a JsonObject
	 * 
	 * @param title  - The title
	 * @param desc   - The description
	 * @param image  - The image
	 * @param artist - The artist
	 * @param year   - The year the art was produced
	 * @param jo     - The JsonObject to load from
	 * @return The created Painting
	 */
	protected static Painting loadFromJson(String title, String desc, Image img, String artist, int year, JsonObject jo) {
		double height = jo.get("height").getAsDouble();
		double width = jo.get("width").getAsDouble();
		
		Painting p = new Painting(title, desc, img, artist, year, height, width);
		return p;
	}
}