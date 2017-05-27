package ivn.typh.main;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/*
 * This class is used to give notifications to the user
 */
public class Notification {

	
	/*
	 * This method pop ups a alert box.
	 * @param stage Main stage object.
	 * @param at Type of alert icon to use.
	 * @param title The title to display in title bar.
	 * @param msg The contents of the message to display.
	 */
	public static void message(Stage stage,AlertType at,String title,String msg){
		Alert alert = new Alert(at);
		alert.setTitle(title);
		alert.setHeaderText(msg);
		alert.initOwner(stage);
		alert.showAndWait();
	}
	
	/*
	 * This method displays a alert box
	 * @param stage Main stage object.
	 * @param message the contents of message.
	 */
	public static void message(Stage stage,String message){
		Notification.message(stage,AlertType.INFORMATION, "Notification - Typh™",message);
	}
}
