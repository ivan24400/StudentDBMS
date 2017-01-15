package ivn.typh.lm;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LmUI extends Application implements Runnable {

	@Override
	public void start(Stage stage) throws Exception {
		
		VBox vbox = new VBox();
		ListView<String> chatList = new ListView<>();
		ListView<String> contacts = new ListView<>();
		ObservableList<String> contactsData = FXCollections.observableArrayList();
		ObservableList<String> chatData = FXCollections.observableArrayList();
		TextArea text = new TextArea();
		TextField to = new TextField();
		Button send = new Button("Send");
		TitledPane tp1 = new TitledPane("Chats",chatList);
		TitledPane tp2 = new TitledPane("Online ",contacts);
		Accordion ac = new Accordion();
		
		
		chatList.setItems(chatData);
		contacts.setItems(contactsData);
		text.setPromptText("Enter a message");
		to.setPromptText("Enter Recepient ID");
		ac.getPanes().addAll(tp1,tp2);
		ac.setExpandedPane(tp1);
		ac.setMaxWidth(240);
		text.setMaxSize(240, 24);
		to.setMaxWidth(240);
		send.setMaxWidth(240);
		vbox.setSpacing(20);
		vbox.setPadding(new Insets(30));
		vbox.getChildren().addAll(ac,to,text,send);

		
		Scene scene = new Scene(vbox,320,360);
		scene.setFill(Color.AQUA);
		stage.setScene(scene);
		stage.setTitle("TyphLM™");
		stage.show();
		
	}

	@Override
	public void run() {
			launch();
	}

}
