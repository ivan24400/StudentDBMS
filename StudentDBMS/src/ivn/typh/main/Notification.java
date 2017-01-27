package ivn.typh.main;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Notification {

	
	public static void message(Stage stage,AlertType at,String title,String msg){
		Alert alert = new Alert(at);
		alert.setTitle(title);
		alert.setHeaderText(msg);
		alert.initOwner(stage);
		alert.showAndWait();
	}
	
	public static void message(Stage stage,String message){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Notification - Typh™");
		alert.setHeaderText(message);
		alert.initOwner(stage);
		alert.showAndWait();
	}
}
