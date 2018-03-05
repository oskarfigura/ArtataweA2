package com.group1.artatawe.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Handle Image I/O to disk
 */
public class ImageUtil {

	/**
	 * Save an image to a file
	 * 
	 * @see http://www.java2s.com/Tutorials/Java/JavaFX_How_to/Image/Save_an_Image_to_a_file.htm
	 * @param file  - The file to save the image to
	 * @param image - The image to save
	 * @throws IOException 
	 */
	public static void writeImageToFile(File file, Image image) throws IOException {
		BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
		ImageIO.write(bImage, "png", file);
	}

	/**
	 * Load a Image from a file
	 * 
	 * @param file - The file to load
	 * @return The loaded file
	 * @throws FileNotFoundException 
	 */
	public static Image loadImage(File file) throws FileNotFoundException {
		FileInputStream stream = new FileInputStream(file);
		return new Image(stream);
	}
}