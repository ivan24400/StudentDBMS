package ivn.typh.admin;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/*
 * This class is used to send messages to other clients.
 */
public class Messenger {
	
	/*
	 * Messenger to send a text to a given user.
	 * @param user Name of Client to send text.
	 */
	
	static void sendMessage(String user) {
	
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Messenger - Typh™");
		dialog.initOwner(Components.stage);
		
		VBox mpane = new VBox();
		HBox hpane = new HBox();
		hpane.setSpacing(20);
		mpane.setId("message_pane");
		
		Label characterLimit = new Label("255");
		TextArea text = new TextArea();
		Pane dummy = new Pane();
		
		text.setPromptText("Enter message ...");
		text.setPrefRowCount(8);
		text.setPrefColumnCount(30);
		text.setWrapText(true);
		text.textProperty().addListener((obs,o,n)->{
			characterLimit.setText(Integer.toString(255 - text.getText().length()));
		});

		HBox.setHgrow(dummy, Priority.ALWAYS);
		hpane.getChildren().addAll(dummy,characterLimit,new Label("  characters"));
		mpane.getChildren().addAll(new Label("Enter a message for [ "+user+" ]"),text,hpane);
		
		ButtonType send = new ButtonType("Send", ButtonData.OK_DONE);
		
		dialog.getDialogPane().setContent(mpane);
		dialog.getDialogPane().getButtonTypes().addAll(send, ButtonType.CANCEL);
		
		dialog.setResultConverter(value -> {
			if (value.getButtonData().equals(ButtonData.OK_DONE))
				return text.getText().trim();
			return null;
		});
		
		Node snode = dialog.getDialogPane().lookupButton(send);
		text.textProperty().addListener((observable, oldv, newv) -> {
			snode.setDisable(newv.trim().isEmpty() || (newv.length()>255));
			if(newv.length()>255)
				characterLimit.setTextFill(Color.RED);
			else
				characterLimit.setTextFill(Color.BLACK);

		});

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(msg -> HeartBeat.message = msg);

	}

}
