package com.group1.artatawe.drawing;

import javafx.scene.canvas.GraphicsContext;

/**
 * Functional interface to carry out a draw operation
 */
public interface DrawOperation {
	public void draw(GraphicsContext graphics);
}