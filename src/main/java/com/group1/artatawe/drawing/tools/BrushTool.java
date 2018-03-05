package com.group1.artatawe.drawing.tools;

import java.util.LinkedList;

import com.group1.artatawe.drawing.DrawingController;

import javafx.scene.paint.Color;

/**
 * Simulates a brush by painting many circles on the canvas as the user
 * drags their mouse. Will group each motion in a single draw operation so
 * it can be undone.
 */
public class BrushTool extends Tool {
	
	private LinkedList<double[]> coordinates = new LinkedList<>(); 

	public BrushTool() {
		super("Brush Tool",
				"Simulates a paint brush",
				"/images/drawing icons/Brush Tool.png");
	}
	
	@Override
	public void onMouseDrag(DrawingController controller, double x, double y, double toolsize, Color color) {
		this.coordinates.add(new double[] {x, y});
		
		//This draw is temporary and will be removed when the mouse is released
		//This shows the user what they are drawing
		controller.draw(graphics -> {
			graphics.setFill(color);
			graphics.setStroke(color);
			
			graphics.fillOval(x - (toolsize / 2), y - (toolsize / 2), toolsize, toolsize);
		});
	}
	
	@Override
	public void onMouseRelease(DrawingController controller, double x, double y, double toolsize, Color color) {
		if(! this.coordinates.isEmpty()) {
			LinkedList<double[]> copyOfCoords = new LinkedList<>(this.coordinates);
			
			//Undo any temporary draws, before we add the real draw operation. 
			//This is so we can group a single brush stroke together (for undo).
			controller.undo(this.coordinates.size());
			
			controller.draw(graphics -> {
				graphics.setFill(color);
				graphics.setStroke(color);
				
				for(double[] coords : copyOfCoords) {
					double x1 = coords[0];
					double y1 = coords[1];
					
					graphics.fillOval(x1 - (toolsize / 2), y1 - (toolsize / 2), toolsize, toolsize);
				}
			});
			
			this.coordinates = new LinkedList<>();
		}
	}
}