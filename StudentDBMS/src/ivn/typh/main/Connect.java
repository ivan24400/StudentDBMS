package ivn.typh.main;

import static com.mongodb.client.model.Filters.eq;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/*
 * This class connects the client app to the server machine not
 * the database with user provided ip address.
 */
public class Connect implements EventHandler<ActionEvent>{

	@Override
	public void handle(ActionEvent event) {

		CenterPane.shade.setVisible(true);

		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Connection - Typh�");
		dialog.initOwner(BasicUI.stage);
		dialog.setHeaderText("Enter Server IP address");

		HBox hb = new HBox();

		TextField tf1 = new TextField("127");
		TextField tf2 = new TextField("0");
		TextField tf3 = new TextField("0");
		TextField tf4 = new TextField("1");
		Tooltip tt = new Tooltip();

		//	To make numeric input only
	
		tf1.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				tt.setText("Enter numbers only");
				Point2D p = tf1.localToScene(0.0, 0.0);
				tt.show(tf1, p.getX() + tf1.getCaretPosition() + tf1.getScene().getWindow().getX(),
						p.getY() + tf1.getHeight() * 2 + tf1.getScene().getWindow().getY());
				tf1.setText(n.replaceAll("[\\D]", ""));
			}
		});


		tf2.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				tt.setText("Enter numbers only");
				Point2D p = tf2.localToScene(0.0, 0.0);
				tt.show(tf2, p.getX() + tf2.getCaretPosition() + tf2.getScene().getWindow().getX(),
						p.getY() + tf2.getHeight() * 2 + tf2.getScene().getWindow().getY());
				tf2.setText(n.replaceAll("[\\D]", ""));
			}
		});

		tf3.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				tt.setText("Enter numbers only");
				Point2D p = tf3.localToScene(0.0, 0.0);
				tt.show(tf3, p.getX() + tf3.getCaretPosition() + tf3.getScene().getWindow().getX(),
						p.getY() + tf3.getHeight() * 2 + tf3.getScene().getWindow().getY());
				tf3.setText(n.replaceAll("[\\D]", ""));
			}
		});

		tf4.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				tt.setText("Enter numbers only");
				Point2D p = tf4.localToScene(0.0, 0.0);
				tt.show(tf4, p.getX() + tf4.getCaretPosition() + tf4.getScene().getWindow().getX(),
						p.getY() + tf4.getHeight() * 2 + tf4.getScene().getWindow().getY());
				tf4.setText(n.replaceAll("[\\D]", ""));
			}
		});

		
		// To allow digits of maximum length 3
		
		tf1.addEventFilter(KeyEvent.KEY_TYPED, arg -> {
			TextField tx = (TextField) arg.getSource();
			if (tx.getText().length() >= 3) {
				arg.consume();
				tf2.requestFocus();
			}
		});
		tf2.addEventFilter(KeyEvent.KEY_TYPED, arg -> {
			TextField tx = (TextField) arg.getSource();
			if (tx.getText().length() >= 3) {
				arg.consume();
				tf3.requestFocus();
			}
		});
		

		tf3.addEventFilter(KeyEvent.KEY_TYPED, arg -> {
			TextField tx = (TextField) arg.getSource();
			if (tx.getText().length() >= 3) {
				arg.consume();
				tf4.requestFocus();
			}
		});
		tf4.addEventFilter(KeyEvent.KEY_TYPED, arg -> {
			TextField tx = (TextField) arg.getSource();
			if (tx.getText().length() >= 3) {
				arg.consume();
			}
		});

		tf1.setOnMouseMoved(value -> {
			tt.hide();
		});

		tf2.setOnMouseMoved(value -> {
			tt.hide();
		});
		tf3.setOnMouseMoved(value -> {
			tt.hide();
		});
		tf4.setOnMouseMoved(value -> {
			tt.hide();
		});

		tf1.setPrefWidth(40);
		tf2.setPrefWidth(40);
		tf3.setPrefWidth(40);
		tf4.setPrefWidth(40);
		hb.setPadding(new Insets(40));
		hb.getChildren().addAll(tf1, new Label(" . "), tf2, new Label(" . "), tf3, new Label(" . "), tf4);
		dialog.getDialogPane().setContent(hb);
		
		ButtonType ok = new ButtonType("Connect",ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(ok);
		
		dialog.setResultConverter(result -> {
			CenterPane.shade.setVisible(false);

			if ((tf1.getText() + tf2.getText() + tf3.getText() + tf4.getText()).trim().isEmpty() || (result == null))
				return null;
			else
				return tf1.getText() + "." + tf2.getText() + "." + tf3.getText() + "." + tf4.getText();
		});

		Node connectOK = dialog.getDialogPane().lookupButton(ok);
		connectOK.getStyleClass().add("dialogOKButton");
		
		Optional<String> result = dialog.showAndWait();

		Task<Boolean> cm = checkMachine(BasicUI.stage);

		result.ifPresent(ip -> {
			if(ip!=null){
			BasicUI.ipAddr = ip;
			BasicUI.centerOfHomePane.showMessage("Connecting . . . ");
			(new Thread(cm)).start();
			}
		});

		cm.setOnSucceeded(value -> {
			Platform.runLater(()->{
				BasicUI.centerOfHomePane.hideMessage();

				if (cm.getValue()){
					Notification.message(BasicUI.stage, AlertType.INFORMATION, "Connection  - Typh�",
							"Connected to Server");
					try{
						Document doc = Engine.db.getCollection("Users").find(eq("user","admin")).first();
						BasicUI.institute.setText(doc.getString("instituteName"));
					}catch(NullPointerException e){
						BasicUI.institute.setText("");
					}
				}else
					Notification.message(BasicUI.stage, AlertType.ERROR, "Connection - Typh�", "Server not found!");
			});
			
		});

	cm.setOnFailed(value->{
		Platform.runLater(()->{
			BasicUI.centerOfHomePane.hideMessage();
			Notification.message(BasicUI.stage, AlertType.ERROR, "Connection - Typh�", "Server not found!");

		});
	});
		
	}
	
	
	/*
	 * This method verifies whether the system is
	 * reachable and if it is then make a connection to that
	 * system 
	 * @param stage Current stage
	 * @return Task wrapped process 
	 */

	public Task<Boolean> checkMachine(Stage stage) {
		Task<Boolean> task = new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				InetAddress addr;
				Boolean result = false;
				Socket testSocket = null;
				try {
					if (BasicUI.ipAddr == null) {
						return false;
					}
					addr = InetAddress.getByName(BasicUI.ipAddr);
					if (!addr.isReachable(4000))
						return false;
					MongoClientOptions.Builder options = MongoClientOptions.builder().serverSelectionTimeout(6000).sslEnabled(true)
							.sslInvalidHostNameAllowed(true);
					MongoClientURI connectionString = new MongoClientURI(
							"mongodb://typh:typhpass@" + BasicUI.ipAddr + ":24000/?authSource=Students", options);
					Engine.mongo = new MongoClient(connectionString);
					Engine.db = Engine.mongo.getDatabase("Students");
					testSocket = new Socket(BasicUI.ipAddr,PortList.NETWORKTEST.port);
					result=true;

					Engine.mongo.getAddress();
					
				} catch (Exception e) {
					result=false;
					testSocket.close();
					Engine.mongo.close();
				}

				return result;
			}

		};
		return task;
	}

}
