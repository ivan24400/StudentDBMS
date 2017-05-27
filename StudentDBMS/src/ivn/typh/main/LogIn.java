package ivn.typh.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.client.MongoCollection;
import ivn.typh.admin.AdminUI;
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
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LogIn implements Runnable {

	private Stage stage;
	private GridPane gpane;
	private BorderPane pane;
	private Scene scene;
	private ToolBar mb;

	public LogIn(Stage arg, BorderPane p, Scene s, ToolBar menu) {
		mb = menu;
		scene = s;
		stage = arg;
		pane = p;
	}

	public void startUI() {

		CenterPane.shade.setVisible(true);

		gpane = new GridPane();
		Label user = new Label("User :");
		Label pass = new Label("Password :");
		TextField userText = new TextField();
		PasswordField passText = new PasswordField();

		userText.setId("user");
		passText.setId("password");
		
		userText.setPromptText("UserName");
		passText.setPromptText("Password");
		Platform.runLater(()->{
			userText.requestFocus();
		});
		
		Dialog<LoginData> dialog = new Dialog<>();
		dialog.setTitle("Typh™ Login");
		dialog.setHeaderText("Enter your login information");

		gpane.setPadding(new Insets(50));
		gpane.setVgap(20);
		gpane.setHgap(20);
		gpane.add(user, 0, 0);
		gpane.add(userText, 1, 0);
		gpane.add(pass, 0, 1);
		gpane.add(passText, 1, 1);

		
		
		dialog.getDialogPane().setContent(gpane);

		ButtonType login = new ButtonType("Log In", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(login);

		Node logined = dialog.getDialogPane().lookupButton(login);
		logined.getStyleClass().add("dialogOKButton");

		
		passText.textProperty().addListener((observable, oldv, newv) -> {
			logined.setDisable(newv.trim().isEmpty() || userText.getText().trim().isEmpty());

		});

		dialog.setResultConverter((button) -> {
			CenterPane.shade.setVisible(false);

			if (button == login) {
				return new LoginData(userText.getText(), passText.getText());
			}
			return null;
		});

		dialog.initOwner(stage);

		Optional<LoginData> result = dialog.showAndWait();
		Task<Boolean> loginTask = checkCredTask();

		result.ifPresent(arg -> {
			try {
				if (!(arg == null)) {
					BasicUI.user = arg.getUser();
					BasicUI.password = arg.getPassword();
					BasicUI.centerOfHomePane.showMessage("Logging in . . . ");
					(new Thread(loginTask)).start();
				}
			} catch (Exception e) {
				System.out.println("No db running");
				e.printStackTrace();
			}
		});

		loginTask.setOnSucceeded(value -> {

			Platform.runLater(() -> {

				if (!loginTask.getValue()) {
					BasicUI.centerOfHomePane.hideMessage();
					Notification.message(stage, AlertType.ERROR, "Invalid credentials - Typh™",
							"Either username or password is incorrect !");
				} else {
					Task<Void> loadUI = loadUI();
					(new Thread(loadUI)).start();
					
					loadUI.setOnSucceeded(arg->{
						BasicUI.centerOfHomePane.hideMessage();

					});

				}
			});
		});

	}

	private Task<Boolean> checkCredTask() {
		Task<Boolean> loginTask = new Task<Boolean>() {
			@Override
			public Boolean call() {
				Boolean result = false;
				int flag = verifyCredential();
				if (flag == 1) {
					result = true;

				} else if (flag == 2) {
					result = false;
				} else if (flag == 4) {
					Platform.runLater(() -> {
						Notification.message(BasicUI.stage, AlertType.ERROR, "User - Typh™", "User Already Logged In");
					});
					result = false;
				}
				return result;
			}
		};
		return loginTask;
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
		if (Engine.db == null)
			return 3;

		String pass, dbPass = null, dbUser = null;
		pass = encryptedPassword(BasicUI.password);

		MongoCollection<Document> coll = Engine.db.getCollection("Users");

		if (!(coll.count(new Document("user", BasicUI.user)) > 0))
			return 2;

		if (isUserLoggedIn())
			return 4;

		Document doc = coll.find(eq("user", BasicUI.user)).first();
		dbPass = doc.getString("passwd");
		dbUser = doc.getString("user");
		if (pass.equals(dbPass) && BasicUI.user.equals(dbUser))
			return 1;
		else
			return 2;

	}

	private boolean isUserLoggedIn() {
		Socket client;
		boolean flag = false;
		try {
			client = new Socket(BasicUI.ipAddr, PortList.CHECKUSER.port);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream());

			out.println(BasicUI.user);
			out.flush();
			String line = in.readLine();
			if (line.equals("__EXIST__"))
				flag = true;
			else
				flag = false;
			out.close();
			in.close();
			client.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;

	}

	private Task<Void> loadUI() {

		Task<Void> loadUI = null;
		if (BasicUI.user.equals(new String("admin"))) {
			loadUI = new AdminUI(stage, pane, mb);
		} else {
			String freeze = Engine.db.getCollection("Users").find(eq("user", BasicUI.user)).first().toJson();
			JSONObject json = new JSONObject(freeze);
			if (!json.getBoolean("status"))
				loadUI = new TchrUI(stage, pane, scene, mb);
			else
				Notification.message(stage, AlertType.ERROR, "User Accounts - Typh™",
						"Your account has been locked !\nContact system administrators");
		}

		return loadUI;
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
