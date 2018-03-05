package com.group1.artatawe.drawing.tools;

import com.group1.artatawe.drawing.DrawingController;

import javafx.scene.paint.Color;

/**
 * Represents a tool for drawing a square 
 */
public class SquareTool extends Tool {
	
	public SquareTool() {
		super("Square Tool", 
				"Draw a square", 
				"/images/drawing icons/Square Tool.png");
	}

	
	@Override
	public void onMouseClick(DrawingController controller, double x, double y, double toolsize, Color color) {
		controller.draw(graphics -> {
			graphics.setFill(color);
			graphics.setStroke(color);
			graphics.fillRect(x-(toolsize/2), y-(toolsize/2), toolsize, toolsize);
		});
	}
}
