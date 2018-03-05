package com.group1.artatawe.drawing.tools;

import com.group1.artatawe.drawing.DrawingController;

import javafx.scene.paint.Color;

/**
 * Represents a tool to draw a circle
 */
public class CircleTool extends Tool {

	public CircleTool() {
		super("Circle Tool", 
				"Draw a circle on to the canvas", 
				"/images/drawing icons/Circle Tool.png");
	}

	@Override
	public void onMouseClick(DrawingController controller, double x, double y, double toolsize, Color color) {
		controller.draw(graphics -> {
			graphics.setFill(color);
			graphics.setStroke(color);
			graphics.fillOval(x - (toolsize / 2), y - (toolsize / 2), toolsize, toolsize);
		});
	}
}