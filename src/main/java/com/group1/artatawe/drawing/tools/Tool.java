package com.group1.artatawe.drawing.tools;

import com.group1.artatawe.drawing.DrawingController;

import javafx.scene.paint.Color;

/**
 * Parent Tool class
 */
public abstract class Tool {
	
	private final String name;
	private final String description;
	private final String icon;

	/**
	 * Creates a new tool
	 * 
	 * @param name        - The name of the tool
	 * @param description - Basic description of tool
	 * @param icon        - Path to the icon of the tool
	 */
	public Tool(String name, String description, String icon) {
		this.name = name;
		this.description = description;
		this.icon = icon;
	}
	
	/**
	 * Called when a mouse is clicked
	 * 
	 * @param controller - The controller
	 * @param x        - The x position of the mouse
	 * @param y        - The y position of the mouse
	 * @param toolsize - The current size of the tool
	 * @param color    - The currently selected color
	 */
	public void onMouseClick(DrawingController controller, double x, double y, double toolsize, Color color) {};
	
	/**
	 * Called when the mouse is dragged (Left click + move)
	 * 
	 * @param controller - The controller
	 * @param x        - The x position of the mouse
	 * @param y        - The y position of the mouse
	 * @param toolsize - The current size of the tool
	 * @param color    - The currently selected color
	 */
	public void onMouseDrag(DrawingController controller, double x, double y, double toolsize, Color color) {};
	
	/**
	 * Called when the mouse is released
	 * 
	 * @param controller - The controller
	 * @param x        - The x position of the mouse
	 * @param y        - The y position of the mouse
	 * @param toolsize - The current size of the tool
	 * @param color    - The currently selected color
	 */
	public void onMouseRelease(DrawingController controller, double x, double y, double toolsize, Color color) {};
	
	/**
	 * Get the name of the tool
	 * @return Tool's name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the tool description
	 * @return Tool's description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Get the path to the Icon
	 * @return Path to Icon
	 */
	public String getIcon() {
		return this.icon;
	}
}