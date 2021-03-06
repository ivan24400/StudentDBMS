package ivn.typh.admin;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bson.Document;
import org.bson.conversions.Bson;
import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/*
 * This class provides management of all the recorded users 
 * in database.
 */
public class UserAccounts extends Dialog<String> implements EventHandler<ActionEvent> {

	static ObservableList<String> userList;
	static int x, y;

	private boolean isFirst;
	private boolean freez;
	private boolean saveAdded;
	private Stage stage;
	private GridPane home;
	private Button addAcc;
	private Button del;
	private String lastLogin;
	private TextField username;
	private TextField fullname;
	private PasswordField password;
	private TextField email;
	private ToggleButton freeze;
	private ToggleButton edit;
	private ChoiceBox<String> dprtMember;
	private ChoiceBox<String> yearIncharge;
	private String dm;
	private String yi;


	public UserAccounts(Stage s, GridPane pane, String u, String f, String p, String e, String dprt, String yin,String ll,boolean fr) {
		this(s);
		home = pane;
		lastLogin=ll;
		fullname.setText(f);
		username.setText(u);
		password.setText(p);
		email.setText(e);
		dm = dprt;
		yi = yin;
		freez=fr;
	}
	
	public UserAccounts(Stage arg) {
		stage = arg;
		lastLogin = "Not logged in yet !";
		initOwner(stage);
		username = new TextField();
		password = new PasswordField();
		email = new TextField();
		dprtMember = new ChoiceBox<>();
		yearIncharge = new ChoiceBox<>();
		fullname = new TextField();
		saveAdded = false;
	}

	public UserAccounts(Stage arg, GridPane gp, Button addB) {
		this(arg);
		home = gp;
		addAcc = addB;

	}

	public void createUI() {

		setTitle("User Account - Typh�");

		GridPane dPane = new GridPane();

		dPane.setPadding(new Insets(40));
		dPane.setHgap(20);
		dPane.setVgap(20);
		addEdit(dPane);
		fullname.setPromptText("Enter full name");
		username.setPromptText("Enter username");
		password.setPromptText("Enter password");
		email.setPromptText("Enter email");
		
		Tooltip tool = new Tooltip();

		email.focusedProperty().addListener((obs,o,n)->{
			if(!email.getText().contains("@") && !email.getText().trim().isEmpty()){
				email.requestFocus();
				email.setStyle("-fx-text-fill: red");
			}else
				email.setStyle("-fx-text-fill: black");
		});
		
		email.setOnMouseMoved(arg-> tool.hide());
		dprtMember.setItems(FXCollections.observableArrayList(Departments.dprtList.values()));
		yearIncharge.setItems(FXCollections.observableArrayList("FE", "SE", "TE", "BE"));

		if (!isFirst) {
			if (!saveAdded) {
				dprtMember.getSelectionModel().selectFirst();
				yearIncharge.getSelectionModel().selectFirst();
				ButtonType save = new ButtonType("Save", ButtonData.OK_DONE);
				getDialogPane().getButtonTypes().clear();
				getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
			}
			saveAdded = true;
			setHeaderText("User:-\t" + username.getText());
			dprtMember.getSelectionModel().select(dm);
			yearIncharge.getSelectionModel().select(yi);
			freeze.setSelected(freez);
			disableAll(true);
			setResultConverter((button) -> {
				if ((button.equals(ButtonType.OK) || button.getButtonData().equals(ButtonData.OK_DONE))
						&& (!areFieldsEmpty())) {
					addButton();
				}else if ((button.equals(ButtonType.OK) || button.getButtonData().equals(ButtonData.OK_DONE))
						&& (areFieldsEmpty()))
					Notification.message(stage, AlertType.ERROR, "Error - User Accounts - Typh�",
							"All Fields are mandatory !");
				return null;
			});
			
		} else if (isFirst) {
			setHeaderText("Fill in required fields to add a user account");
			dprtMember.getSelectionModel().selectFirst();
			yearIncharge.getSelectionModel().selectFirst();
			getDialogPane().getButtonTypes().clear();
			getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			setResultConverter((button) -> {
				if ((button.equals(ButtonType.OK) || button.getButtonData().equals(ButtonData.OK_DONE)) && (!isAvailable())
						&& (!areFieldsEmpty())) {
					userList.add(username.getText());
					addButton();
				} else if ((button.equals(ButtonType.OK) || button.getButtonData().equals(ButtonData.OK_DONE))
						&& (isAvailable()) && (!areFieldsEmpty())) {
					Notification.message(stage, AlertType.ERROR, "Error - User Accounts - Typh�",
							"Username already taken !");
				} else if ((button.equals(ButtonType.OK) || button.getButtonData().equals(ButtonData.OK_DONE))
						&& (areFieldsEmpty()))
					Notification.message(stage, AlertType.ERROR, "Error - User Accounts - Typh�",
							"All Fields are mandatory !");
				return null;
			});
			
		}

		
		dPane.add(new Label("User Name"), 0, 0);
		dPane.add(username, 1, 0);
		dPane.add(new Label("Full Name"), 0, 1);
		dPane.add(fullname, 1, 1);
		dPane.add(new Label("Password"), 0, 2);
		dPane.add(password, 1, 2);
		dPane.add(new Label("E-mail"), 0, 3);
		dPane.add(email, 1, 3);
		dPane.add(new Label("Department"), 0, 4);
		dPane.add(dprtMember, 1, 4);
		dPane.add(new Label("Class Incharge"), 0, 5);
		dPane.add(yearIncharge, 1, 5);
		getDialogPane().setContent(dPane);

		Platform.runLater(()->{
			show();
		});
	}
	

	/*
	 * This method adds additional buttons onto the dialog.
	 * They are to freeze, edit and delete account.
	 * @param dPane The pane of UserAccount dialog.
	 */
	private void addEdit(GridPane dPane) {
		if (!isFirst) {
			HBox seBox = new HBox();
			seBox.setPadding(new Insets(50));
			seBox.setSpacing(20);
			seBox.setAlignment(Pos.CENTER);
			
			freeze = new ToggleButton("Freeze");
			edit = new ToggleButton("Edit");
			del = new Button("Delete");
			
			edit.selectedProperty().addListener((arg, o, n) -> {
				disableAll(!n);
			});
			
			freeze.selectedProperty().addListener((arg, o, n) -> {
				Bson filter = new Document("user",username.getText());
				Bson query = new Document("$set",new Document("freeze",n));
				Engine.db.getCollection("Users").updateOne(filter, query);
				});
			
			del.setOnAction(val->{
				Alert dalert = new Alert(AlertType.CONFIRMATION);
				dalert.setTitle("Delete User Account - Typh�");
				dalert.setHeaderText("Are you sure to delete user: "+username.getText()+"?");
				dalert.initOwner(this.getDialogPane().getScene().getWindow());
				dalert.setResultConverter(value->{
					if(value.equals(ButtonType.OK)){
						Bson query = new Document("user",username.getText());
						Engine.db.getCollection("Users").deleteOne(query);
						Stage s_t = stage;
						BorderPane bp_t = Components.pane;
						ToolBar b_t=Components.menuBar;
						Platform.runLater(()->{
							(new Thread(new AdminUI(s_t,bp_t,b_t))).start();
							stage.close();
						});
						
					}
					return null;
				});
				dalert.show();

			});
			
			seBox.getChildren().addAll(edit,freeze,del);
			dPane.add(new Label("[ Last Login: "+lastLogin+"]"), 0, 6,2,1);
			dPane.add(seBox, 0, 7, 2, 1);
		}
	}
	
	/*
	 * This method checks whether do another user account with
	 * the same given name already exists.
	 * @return boolean representing does it exist or not.
	 */

	private boolean isAvailable() {
		if (userList.contains(username.getText())) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * This method enables or disables all of the nodes present in
	 * UserAccount.
	 */

	private void disableAll(boolean flag) {
		username.setEditable(!flag);
		password.setEditable(!flag);
		email.setEditable(!flag);
		yearIncharge.setDisable(flag);
		dprtMember.setDisable(flag);
		fullname.setEditable(!flag);
		if(!isFirst){
			freeze.setDisable(flag);
			del.setDisable(flag);
		}

	}
	
	/*
	 * This method is called to add a new user account entry to database
	 */

	public void addButton() {
		Button tmp = new Button(username.getText());
		Document doc = new Document("passwd", encryptedPassword(password.getText()))
				.append("fullname", fullname.getText()).append("email", email.getText())
				.append("department", dprtMember.getValue()).append("yearIncharge", yearIncharge.getValue()).append("freeze",freeze.isArmed());
		
		if (isFirst) {
			doc.append("user", username.getText());
			Engine.db.getCollection("Users").insertOne(doc);
			tmp.setOnAction(this);
			if (x < 6) {
				x++;
				home.add(tmp, x - 1, y);
				GridPane.setColumnIndex(addAcc, x);
				GridPane.setRowIndex(addAcc, y);

			} else {
				x = 1;
				y++;
				home.add(tmp, x - 1, y);
				GridPane.setColumnIndex(addAcc, x);
				GridPane.setRowIndex(addAcc, y);

			}
		} else {
			Bson filter = new Document("user", username.getText());
			Bson query = new Document("$set", doc);
			Engine.db.getCollection("Users").updateOne(filter, query);
		}

	}

	/*
	 * This method creates a hash from given text.
	 * @param text Input text to be SHA-256 hashed
	 * @return A String which is sha256 hashed
	 */
	private String encryptedPassword(String text) {
		StringBuffer hash = new StringBuffer();
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] digest = sha.digest(text.getBytes(StandardCharsets.UTF_8));
			 for (int i = 0; i < digest.length; i++) {
		            String hex = Integer.toHexString(0xff & digest[i]);
		            if(hex.length() == 1)
		            	hash.append('0');
		            hash.append(hex);
		        }

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash.toString();
	}
	
	/*
	 * This method checks whether the text fields are empty or not.
	 * @return boolean value representing empty or not.
	 */

	private boolean areFieldsEmpty() {
		if (username.getText().trim().isEmpty() || password.getText().trim().isEmpty()
				|| email.getText().trim().isEmpty())
			return true;
		else
			return false;
	}

	public void begin() {
		isFirst = true;
		createUI();
	}

	@Override
	public void handle(ActionEvent arg) {
		isFirst = false;
		createUI();

	}

}
