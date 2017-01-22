package ivn.typh.admin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.client.MongoCursor;

import ivn.typh.main.Engine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class UserAccounts extends Dialog<String> implements EventHandler<ActionEvent> {

	static ObservableList<String> userList;
	static int x, y;

	
	private Stage parent;
	private GridPane home;
	private Button addAcc;

	private TextField username;
	private TextField fullname;
	private ObservableList<String> classList;
	private PasswordField password;
	private TextField email;
	private ToggleButton freeze;
	private ToggleButton edit;
	private ComboBox<String> dprtMember;

	public UserAccounts(Stage s,GridPane pane,String u,String f,String p,String e,String dprt){
		this(s);
		home=pane;	
		
		fullname.setText(f);
		username.setText(u);
		password.setText(p);
		email.setText(e);
		dprtMember.setValue(dprt);
	}
	
	public UserAccounts(Stage arg) {
		parent = arg;
		initOwner(parent);
		username = new TextField();
		password = new PasswordField();
		email = new TextField();
		dprtMember = new ComboBox<>();
		fullname = new TextField();
		classList = FXCollections.observableArrayList();
	}

	public UserAccounts(Stage arg, GridPane gp, Button addB) {
		this(arg);
		home = gp;
		addAcc = addB;
		freeze = new ToggleButton("Freeze");

	}

	public void createUI(boolean first) {

		setTitle("User Account - Typh™");
		setHeaderText("Fill in required fields to add a user account");

		GridPane dPane = new GridPane();

		dPane.setPadding(new Insets(20));
		dPane.setHgap(20);
		dPane.setVgap(20);

		dprtMember.getItems().addAll(Departments.dprtList);

		MongoCursor<Document> cursor = Engine.db.getCollection("Students").find().iterator();
		
		while(cursor.hasNext()){
			JSONObject json = new JSONObject(cursor.next().toJson());
			boolean exist=false;
			for(String s: classList){
				if(s.equals(json.getString("department")))
					exist=true;
			}
			if(!exist)
				classList.add(json.getString("department"));
		}
		
		Label lusername = new Label("User Name");
		Label lfullname = new Label("Full Name");
		Label lpassword = new Label("Password");
		Label lemail = new Label("E-mail");
		Label ldprt = new Label("Department");

		fullname.setPromptText("Enter full name");
		username.setPromptText("Enter username");
		password.setPromptText("Enter password");
		email.setPromptText("Enter email");

		dPane.add(lusername, 0, 0);
		dPane.add(username, 1, 0);
		dPane.add(lfullname, 0, 1);
		dPane.add(fullname, 1, 1);
		dPane.add(lpassword, 0, 2);
		dPane.add(password, 1, 2);
		dPane.add(lemail, 0, 3);
		dPane.add(email, 1, 3);
		dPane.add(ldprt, 0, 4);
		dPane.add(dprtMember, 1, 4);
		
//		if (!first) {
//			addEdit(dPane);
//			ButtonType save = new ButtonType("Save", ButtonData.OK_DONE);
//			getDialogPane().setContent(dPane);
//			getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
//			setResultConverter((button) -> {
//				if (button.equals(save) && (!isAvailable()) && (!areFieldsEmpty())) {
//						reloadData();
//				} else if (button.equals(save) && (isAvailable()) && (!areFieldsEmpty())) {
//					Notification.message(parent, AlertType.ERROR, "Error - User Accounts - Typh™",
//							"Username already taken !");
//				} else if ((button.equals(save) && (areFieldsEmpty())))
//					Notification.message(parent, AlertType.ERROR, "Error - User Accounts - Typh™",
//							"All Fields are mandatory !");
//				return null;
//			});
//			show();
//		}else{
			getDialogPane().setContent(dPane);
			getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			setResultConverter((button) -> {
				if (button.equals(ButtonType.OK) && (!isAvailable()) && (!areFieldsEmpty())) {
					userList.add(username.getText());
					addButton();
				} else if (button.equals(ButtonType.OK) && (isAvailable()) && (!areFieldsEmpty())) {
					Notification.message(parent, AlertType.ERROR, "Error - User Accounts - Typh™",
							"Username already taken !");
				} else if ((button.equals(ButtonType.OK) && (areFieldsEmpty())))
					Notification.message(parent, AlertType.ERROR, "Error - User Accounts - Typh™",
							"All Fields are mandatory !");
				return null;
			});
			show();
//		}
	}



	private void addEdit(GridPane dPane) {
		HBox seBox = new HBox();
		seBox.setPadding(new Insets(20));
		seBox.setSpacing(20);
		seBox.setAlignment(Pos.CENTER);

		edit = new ToggleButton("Edit");
		edit.selectedProperty().addListener((arg, o, n) -> {
			disableAll(arg.getValue());
		});
		seBox.getChildren().addAll(edit);
		dPane.add(seBox, 0, 4, 2, 1);

	}

	private boolean isAvailable() {
		if (userList.contains(username.getText())) {
			System.out.println("yes");
			return true;
		} else {
			System.out.println("no");
			return false;
		}
	}

	public GridPane getHomeGrid() {
		return home;
	}

	private void disableAll(Boolean flag) {
		username.setDisable(flag);
		password.setDisable(flag);
		email.setDisable(flag);
		freeze.setDisable(flag);
	}

	public void addButton() {
		Button tmp = new Button(username.getText());
		Document doc = new Document("user",username.getText()).append("password",encryptedPassword(password.getText())).append("fullname",fullname.getText()).append("email",email.getText()).append("department",dprtMember.getValue());
		Engine.db.getCollection("Users").insertOne(doc);
		tmp.setOnAction(new UserAccounts(parent));
		if (x < 6) {
			x++;
			getHomeGrid().add(tmp, x - 1, y);
			GridPane.setColumnIndex(addAcc, x);
			GridPane.setRowIndex(addAcc, y);

		} else {
			x = 1;
			y++;
			getHomeGrid().add(tmp, x - 1, y);
			GridPane.setColumnIndex(addAcc, x);
			GridPane.setRowIndex(addAcc, y);

		}
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
	private boolean areFieldsEmpty() {
		if (username.getText().trim().isEmpty() || password.getText().trim().isEmpty()
				|| email.getText().trim().isEmpty())
			return true;
		else
			return false;
	}

	public void begin() {
		createUI(true);
	}

	private void reloadData() {
		Notification.message(parent, "User Accounts updated to server");

	}
	@Override
	public void handle(ActionEvent arg) {
		createUI(false);

	}

}
