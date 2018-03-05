package com.group1.artatawe.utils;

import java.util.LinkedList;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * Handle insert nodes into a Grid
 */
public class GridUtil {

	/**
	 * Insert nodes into a grid
	 * 
	 * @param box   - The GridPane
	 * @param nodes - The nodes to insert
	 */
	public static void insertList(GridPane box, LinkedList<Node> nodes) {
		//Reset the Grid
		box.getChildren().clear();
		box.getRowConstraints().clear();
		box.addRow(1);

		//Insert the nodes into the Grid
		int row = 1;
		int column = 0;
		int maxColumn = box.getColumnConstraints().size();

		for(Node n : nodes) {
			if(column == maxColumn) { //Go onto the next line
				column = 0;
				row++;
				box.addRow(row);
			}
			box.add(n, column, row);
			column++;
		}	
	}
}
