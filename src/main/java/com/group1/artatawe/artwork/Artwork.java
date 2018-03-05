package com.group1.artatawe.artwork;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.utils.ImageUtil;

import javafx.scene.image.Image;

/**
 * Parent class to all Artworks
 */
public abstract class Artwork {
	
	public static final String ARTWORK_FOLDER = "artworks";

	private final String title;
	private final String description;
	private final Image image;
	private final String artist;
	private final int year;
	
	static {
		//Create the Artwork folder (if it doesn't exist).
		new File(ARTWORK_FOLDER).mkdirs();
	}
	
	/**
	 * Construct a new Artwork
	 * 
	 * @param title  - The title of this artwork
	 * @param desc   - The description of the artwork
	 * @param img    - The image of this artwork
	 * @param artist - The artist
	 * @param year   - Year the art was made
	 */
	public Artwork(String title, String desc, Image img, String artist, int year) {
		this.title = title;
		this.description = desc;
		this.image = img;
		this.artist = artist;
		this.year = year;
	}
	
	/**
	 * For child classes to add their attributes too the JsonObject
	 *  
	 * @param listing - The listing being saved
	 * @return The Child's JsonObject
	 */
	protected abstract JsonObject getChildJsonObject(Listing listing);

	/**
	 * Turn a Artwork into a JsonObject
	 * @return The created JsonObject
	 */
	public JsonObject toJsonObject(Listing listing) {
		JsonObject jo = new JsonObject();
		
		jo.addProperty("type", this.getClass().getSimpleName());
		
		//Add everything from the child json
		JsonObject childJson = this.getChildJsonObject(listing);
		
		for(Entry<String, JsonElement> entry : childJson.entrySet()) {
			jo.add(entry.getKey(), entry.getValue());
		}
		
		//Add all the parents properties
		jo.addProperty("title", this.title);
		jo.addProperty("description", this.description);
		jo.addProperty("artist", this.artist);
		jo.addProperty("year", this.year);
		
		//Save the artwork image and add the path to the json
		String imgFileName = listing.getID() + "-1.png";
		
		File imageFile = new File(ARTWORK_FOLDER, imgFileName);
		
		try {
			ImageUtil.writeImageToFile(imageFile, this.image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		jo.addProperty("image", imgFileName);
		
		return jo;
	}

	/**
	 * Get the title of this artwork
	 * @return The title
	 */
	public final String getTitle() {
		return this.title;
	}

	/**
	 * Get the description of this artwork
	 * @return The description
	 */
	public final String getDescription() {
		return this.description;
	}

	/**
	 * Get the image (link) of this artwork
	 * @return The image link
	 */
	public final Image getImage() {
		return this.image;
	}

	/**
	 * Get the artist of this artwork
	 * @return The name of the artist
	 */
	public final String getArtist() {
		return this.artist;
	}

	/**
	 * Get the year this artwork was made
	 * @return The year the art was made
	 */
	public final int getYear() {
		return this.year;
	}
	
	
	/**
	 * Load the Artwork back from a JsonObject. Will load back using the
	 * correct child class.
	 * 
	 * @param jo - The JsonObject to load back from
	 * @return the Artwork to be loaded
	 * @throws Exception - If the The type of the Artwork isn't known
	 */
	public static Artwork loadFromJson(JsonObject jo) throws Exception {
		String type = jo.get("type").getAsString();
		
		String title = jo.get("title").getAsString();
		String desc = jo.get("description").getAsString();
		Image image = ImageUtil.loadImage(new File(ARTWORK_FOLDER, jo.get("image").getAsString()));
		String artist = jo.get("artist").getAsString();
		int year = jo.get("year").getAsInt();
		
		switch(type.toLowerCase()) {
			case "sculpture": return Sculpture.loadFromJson(title, desc, image, artist, year, jo);
			case "painting" : return Painting.loadFromJson(title, desc, image, artist, year, jo);
			default : throw new Exception("The type " + type + " is unknown and connot be loaded.");
		}
	}
}
