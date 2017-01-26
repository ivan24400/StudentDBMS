package ivn.typh.main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import ivn.typh.admin.AdminUI;
import ivn.typh.admin.Notification;
import ivn.typh.tchr.TchrUI;

import static com.mongodb.client.model.Filters.*;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LogIn implements Runnable {

	private Stage stage;
	private GridPane gpane;
	private BorderPane pane;
	private Scene scene;
	
	public LogIn(Stage arg,BorderPane p,Scene s) {
		scene=s;
		stage = arg;
		pane=p;
	}

	public void startUI() {

		gpane = new GridPane();
		Label user = new Label("User");
		Label pass = new Label("Password");
		TextField userText = new TextField();
		PasswordField passText = new PasswordField();

		Dialog<LoginData> dialog = new Dialog<>();
		dialog.setTitle("Typh™ Login");
		dialog.setHeaderText("Enter your login information");

		gpane.setPadding(new Insets(30));
		gpane.setHgap(20);
		gpane.setVgap(20);
		gpane.add(user, 0, 0);
		gpane.add(userText, 1, 0);
		gpane.add(pass, 0, 1);
		gpane.add(passText, 1, 1);

		dialog.getDialogPane().setContent(gpane);

		ButtonType login = new ButtonType("Log In", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(login, ButtonType.CANCEL);

		Node logined = dialog.getDialogPane().lookupButton(login);
		logined.setDisable(true);

		userText.textProperty().addListener((observable, oldv, newv) -> {
				logined.setDisable(newv.trim().isEmpty());
			
		});

		Platform.runLater(() -> userText.requestFocus());
		
		Task<Void> loginTask = new Task<Void>() {
			@Override
			public Void call() {
				int flag = verifyCredential();
					if (flag == 1) {
						Platform.runLater(() -> {
							updateProgress(5, 10);
							loadUI();
							updateProgress(9,10);
						});
					} else if (flag == 2) {
						Platform.runLater(()->Notification.message(stage, AlertType.ERROR, "Invalid credentials - Typh™",
							"Either username or password is incorrect !!!"));
					} else {
						Platform.runLater(()->Notification.message(stage, AlertType.ERROR, "Database - Typh™",
								"Unable to connect Database"));
					}
				return null;
			}
		};
		dialog.setResultConverter((button) -> {
			if (button == login) {
				return new LoginData(userText.getText(),passText.getText());
			}	
			return null;
		});

		dialog.initOwner(stage);
		Optional<LoginData> result = dialog.showAndWait();
		Loading loadBar = new Loading(stage);

		result.ifPresent(arg ->{
			try {
				Engine.mongo = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
				BasicUI.user=arg.getUser();
				BasicUI.password=arg.getPassword();
				loadBar.setTask(loginTask);
				(new Thread(loginTask)).start();
			
			} catch (Exception e) {
				System.out.println("No db running");
			}
		});
		loginTask.setOnSucceeded(value -> {
			Platform.runLater(() -> {
				loadBar.hideProgress();
			});
		});
	}


	private String encryptedPassword(String text) {

		StringBuffer hash = new StringBuffer();
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] digest = sha.digest(text.getBytes());
			for (byte b : digest) {
				hash.append(Integer.toString(b & 0xff + 0x100, 16).substring(1));
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash.toString();
	}

	private int verifyCredential() {
		String pass, dbPass = null, dbUser = null;
		pass = encryptedPassword(BasicUI.password);
		Engine.db = Engine.mongo.getDatabase("Students");
		if (Engine.db == null)
			return 3;
		MongoCollection<Document> coll = Engine.db.getCollection("Users");
		Document doc = coll.find(eq("user", BasicUI.user)).first();
		dbPass = doc.getString("passwd");
		dbUser = doc.getString("user");
		if (pass.equals(dbPass) && BasicUI.user.equals(dbUser))
			return 1;
		else
			return 2;
	}
	private void loadUI() {
		if (BasicUI.user.equals(new String("admin"))) {
			Thread at = new Thread(new AdminUI(stage,pane));
			at.start();
		} else {
			Thread tt = new Thread(new TchrUI(stage,pane,scene));
			tt.start();
		}

	}


	@Override
	public void run() {
		Platform.runLater(() -> {
			try {
				startUI();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
