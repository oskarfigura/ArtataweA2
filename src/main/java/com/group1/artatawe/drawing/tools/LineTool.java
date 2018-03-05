package com.group1.artatawe.drawing.tools;

import com.group1.artatawe.drawing.DrawingController;

import javafx.scene.paint.Color;

/**
 * Represents a tool for drawing a line
 */
public class LineTool extends Tool {
	
	private boolean inDraw = false;
	
	private double startx;
	private double starty;
	
	public LineTool() {
		super("Line Tool", 
				"Draw a line", 
				"/images/drawing icons/Line Tool.png");
	}
	
	public void onMouseDrag(DrawingController controller, double x, double y, double toolsize, Color color) {
		if(this.inDraw) {
			controller.undo(1); //Undo the previous preview draw
		} else {
			this.inDraw = true;
			this.startx = x;
			this.starty = y;
		}
		
		double tmpStartX = this.startx;
		double tmpStartY = this.starty;
		
		controller.draw(graphics -> {
			graphics.setFill(color);
			graphics.setStroke(color);
			
			graphics.strokeLine(tmpStartX, tmpStartY, x, y);
		});
	};
	
	public void onMouseRelease(DrawingController controller, double x, double y, double toolsize, Color color) {
		this.inDraw = false;
	};
	
}
