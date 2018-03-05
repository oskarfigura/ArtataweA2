package com.group1.artatawe.drawing;

import java.util.LinkedList;

import com.group1.artatawe.Main;
import com.group1.artatawe.drawing.tools.BrushTool;
import com.group1.artatawe.drawing.tools.CircleTool;
import com.group1.artatawe.drawing.tools.LineTool;
import com.group1.artatawe.drawing.tools.SquareTool;
import com.group1.artatawe.drawing.tools.Tool;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for "Drawing.fxml"
 */
public class DrawingController {

	private static final int ICON_SIZE = 70;

	@FXML GridPane icons;
	@FXML Canvas canvas;

	@FXML ColorPicker colorpicker;
	@FXML ImageView undo;
	@FXML ImageView redo;
	@FXML Slider toolsize;

	@FXML Button done;
	@FXML Button cancel;

	private final LinkedList<DrawOperation> drawingOperations = new LinkedList<>();
	private final LinkedList<DrawOperation> undoHistory = new LinkedList<>();
	
	public static Image LAST_IMAGE = null;
	public static boolean WAS_SAVED = false;

	private Tool selected;

	public DrawingController() { }

	public void initialize() {
		WAS_SAVED = false;
		
		//Provide the mouse click event to the different tools
		this.canvas.setOnMouseClicked(e -> {
			if(this.selected != null) {
				this.selected.onMouseClick(this, e.getX(), e.getY(),
						this.toolsize.getValue(), this.colorpicker.getValue());
			}
		});

		//Provide the mouse drag event to the different tools
		this.canvas.setOnMouseDragged(e -> {
			if(this.selected != null) {
				this.selected.onMouseDrag(this, e.getX(), e.getY(), 
						this.toolsize.getValue(), this.colorpicker.getValue());
			}
		});

		//Provide the mouse release event to the different tools
		this.canvas.setOnMouseReleased(e -> {
			if(this.selected != null) {
				this.selected.onMouseRelease(this, e.getX(), e.getY(), 
						this.toolsize.getValue(), this.colorpicker.getValue());
			}
		});

		//Mouse scroll wheel to increase/decrease the tool size.
		this.canvas.setOnScroll((ScrollEvent event) -> {
			double delta = event.getDeltaY();

			//If delta is under 0, then the user is scrolling down
			if(delta < 0) {
				//Decrease the tool size
				this.toolsize.setValue(this.toolsize.getValue() - 5);
			} else {
				//Increase the tool size
				this.toolsize.setValue(this.toolsize.getValue() + 5);
			}
		});
		
		//Handle the done button
		this.done.setOnMouseClicked(e -> { this.handleDoneButton(); });
		
		//Handle the cancel button
		this.cancel.setOnMouseClicked(e -> { this.handleCancelButton(); });

		//Register the undo functionality.
		this.undo.setOnMouseClicked(e -> { this.hanleUndoButton(); });

		//Register the redo functionality.
		this.redo.setOnMouseClicked(e -> { this.handleRedoButton(); });

		//Add the the different tools to the system
		this.addTool(new CircleTool(), 0, 0);
		this.addTool(new BrushTool(), 0, 1);
		this.addTool(new SquareTool(), 1, 0);
		this.addTool(new LineTool(), 1, 1);
	}
	
	/**
	 * Handle opening the draw GUI
	 */
	public static void openDrawGUI() {
		DrawingController.WAS_SAVED = false;

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/Drawing.fxml"));

			BorderPane pane = (BorderPane) fxmlLoader.load();          

			Scene scene = new Scene(pane);

			Stage stage = new Stage();

			stage.setMaxHeight(550);
			stage.setMaxWidth(670);
			stage.setScene(scene);
			stage.setTitle(Main.WINDOW_TITLE);
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.showAndWait();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Do a draw operation on the canvas
	 * Will also clear the undo history so any undo previous to this cannot be undone
	 * 
	 * @param drawOperation - The draw operation to carry out
	 */
	public void draw(DrawOperation drawOperation) {
		this.undoHistory.clear();

		drawOperation.draw(this.canvas.getGraphicsContext2D());
		this.drawingOperations.add(drawOperation);
	}
	
	/**
	 * Undo a specified amount of DrawOperations
	 * @param amount - The amount of operations to undo
	 */
	public void undo(int amount) {
		for(int i = 0; i < amount; i++) {
			if(! this.drawingOperations.isEmpty()) {
				this.drawingOperations.removeLast();
			}
		}
		this.redraw();
	}
	
	/**
	 * Handle the done button
	 */
	private void handleDoneButton() {
		WAS_SAVED = true;
		WritableImage writableImage = new WritableImage((int) this.canvas.getWidth(), (int) this.canvas.getHeight());
		LAST_IMAGE = this.canvas.snapshot(null, writableImage);
		
		Stage stage = (Stage) this.done.getScene().getWindow();
	    stage.close();
	}
	
	/**
	 * Handle the cancel button
	 */
	private void handleCancelButton() {
		WAS_SAVED = false;
		LAST_IMAGE = null;
		
		Stage stage = (Stage) this.done.getScene().getWindow();
	    stage.close();
	}

	/**
	 * Redraw the last thing that was undone
	 */
	private void handleRedoButton() {
		if(! this.undoHistory.isEmpty()) {
			DrawOperation lastUndo = this.undoHistory.removeLast();

			lastUndo.draw(this.canvas.getGraphicsContext2D());
			this.drawingOperations.add(lastUndo);
		}
	}

	/**
	 * Undo the last thing drawn, and add it to the undo history
	 */
	private void hanleUndoButton() {
		if(! this.drawingOperations.isEmpty()) {
			DrawOperation dw = this.drawingOperations.removeLast();
			this.undoHistory.add(dw);
		}
		this.redraw();
	}
	
	/**
	 * Completely re-draw the canvas, using all the DrawOperations
	 */
	private void redraw() {
		this.canvas.getGraphicsContext2D().clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());

		for (DrawOperation operation : this.drawingOperations) {
			operation.draw(this.canvas.getGraphicsContext2D());
		}
	}

	/**
	 * Add a tool to the system
	 * 
	 * @param tool   - The tool to add
	 * @param row    - The row of the tool
	 * @param column - The column in the grid
	 */
	private void addTool(Tool tool, int row, int column) {
		ImageView view = new ImageView(new Image(tool.getIcon()));

		view.setPickOnBounds(true);
		view.setPreserveRatio(true);
		view.setFitHeight(ICON_SIZE);
		view.setFitWidth(ICON_SIZE);

		Tooltip tip = new Tooltip(tool.getName() + ": " + tool.getDescription());
		Tooltip.install(view, tip);

		this.icons.add(view, column, row);

		view.setOnMouseClicked(e -> this.selected = tool);
	}
}