package com.group1.artatawe.artwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.utils.ImageUtil;

import javafx.scene.image.Image;

/**
 * Represents the sculpture type of artwork.
 */
public class Sculpture extends Artwork {
	
	private final double height;
	private final double width;
	private final double depth;
	private final double weight;
	private final String material;
	private final List<Image> extraImages;

	/**
	 * Construct a new Sculpture.
	 * 
	 * @param height   - The height of the sculpture
	 * @param width    - The width of the sculpture
	 * @param depth    - The depth of the sculpture.
	 * @param weight   - The weight of the sculpture.
	 * @param material - The material of the sculpture.
	 * @param extraImages - Extra images.
	 * @see {@link Artwork#Artwork Artwork}'s constructor for more parameter descriptions.
	 */
	public Sculpture(String title, String desc, Image img, String artist, int year,
			double height, double width, double depth, double weight, String material, List<Image> extraImages) {
		super(title, desc, img, artist, year);
		
		this.height = height;
		this.width = width;
		this.depth = depth;
		this.weight = weight;
		this.material = material;
		this.extraImages = extraImages;
	}
	
	/**
	 * Construct a new Sculpture, without extra images.
	 * 
	 * @param height   - The height of the sculpture
	 * @param width    - The width of the sculpture
	 * @param depth    - The depth of the sculpture.
	 * @param weight   - The weight of the sculpture.
	 * @param material - The material of the sculpture.
	 * @see {@link Artwork#Artwork Artwork}'s constructor for more parameter descriptions.
	 */
	public Sculpture(String title, String desc, Image img, String artist, int year,
			double height, double width, double depth, double weight, String material) {
		this(title, desc, img, artist, year, height, width, depth, weight, material, new LinkedList<Image>());
	}

	/**
	 * Get the height
	 * @return The height
	 */
	public double getHeight() {
		return this.height;
	}

	/**
	 * Get the width
	 * @return The width
	 */
	public double getWidth() {
		return this.width;
	}

	/**
	 * Get the depth
	 * @return The depth
	 */
	public double getDepth() {
		return this.depth;
	}

	/**
	 * Get the weight
	 * @return The weight
	 */
	public double getWeight() {
		return this.weight;
	}

	/**
	 * Get the material
	 * @return The material
	 */
	public String getMaterial() {
		return this.material;
	}

	/**
	 * Get any extra images.
	 * @return The Extra images. Array will be empty if there are no extra images.
	 */
	public List<Image> getExtraImages() {
		return this.extraImages;
	}
	
	@Override
	protected JsonObject getChildJsonObject(Listing listing) {
		JsonObject jo = new JsonObject();
		
		jo.addProperty("height", this.height);
		jo.addProperty("width", this.width);
		jo.addProperty("depth", this.depth);
		jo.addProperty("weight", this.weight);
		jo.addProperty("material", this.material);
		
		JsonArray jarray = new JsonArray();
		
		if(this.extraImages != null) {
			for(int i = 0; i < this.extraImages.size(); i++) {
				Image img = this.extraImages.get(i);
				
				//We i+2 as the default image in every artwork ends with xxxx-1
				String imgFileName = listing.getID() + "-" + (i+2) + ".png";
				File imageFile = new File(ARTWORK_FOLDER, imgFileName);
				
				try {
					ImageUtil.writeImageToFile(imageFile, img);
					jarray.add(imgFileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		jo.add("extraimages", jarray);
		
		return jo;
	}
	
	/**
	 * Load a Sculpture from a JsonObject
	 * @param title  - The title
	 * @param desc   - The description
	 * @param image  - The image
	 * @param artist - The artist
	 * @param year   - The year the art was produced
	 * @param jo     - The JsonObject to load from
	 * @return The created Sculpture
	 */
	protected static Sculpture loadFromJson(String title, String desc, Image img, String artist, int year, JsonObject jo) {
		double height = jo.get("height").getAsDouble();
		double width = jo.get("width").getAsDouble();
		double depth = jo.get("depth").getAsDouble();
		double weight = jo.get("weight").getAsDouble();
		String material = jo.get("material").getAsString();
		
		List<Image> imgs = new LinkedList<>();
		
		JsonArray extraImages = jo.get("extraimages").getAsJsonArray();
		
		extraImages.iterator().forEachRemaining(extraImg -> {
			try {
				imgs.add(ImageUtil.loadImage(new File(ARTWORK_FOLDER, extraImg.getAsString())));
			} catch (FileNotFoundException e) {
				System.out.println("Failed to load optional sculpture image: " + extraImg + ". Does it excist?");
			}
		});
		
		Sculpture s = new Sculpture(title, desc, img, artist, year, height, width, depth, weight, material, imgs);
		return s;
	}
}
