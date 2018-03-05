package com.group1.artatawe.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Send globally formatted alerts
 */
public class AlertUtil {

	/**
	 * Send a pop-up alert to the user. Will hault the system until the alert is closed.
	 * @param type    - The type of the alert
	 * @param title   - The title of the pop-up
	 * @param message - The message to display
	 */
	public static void sendAlert(AlertType type, String title, String message) {
		Alert a = new Alert(type);
		a.setWidth(500);
		a.setWidth(100);
		a.setTitle(title);
		a.setContentText(message);
		a.showAndWait();
	}	
}