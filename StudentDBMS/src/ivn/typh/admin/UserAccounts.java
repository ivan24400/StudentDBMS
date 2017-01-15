package ivn.typh.admin;

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
	private PasswordField password;
	private TextField email;
	private ToggleButton freeze;
	private ToggleButton edit;

	public UserAccounts(Stage s,GridPane pane,String u,String p,String e){
		this(s);
		home=pane;
		
		username = new TextField();
		password = new PasswordField();
		email = new TextField();
		
		username.setText(u);
		password.setText(p);
		email.setText(e);
	}
	
	public UserAccounts(Stage arg) {
		parent = arg;
		initOwner(parent);

	}

	public UserAccounts(Stage arg, GridPane gp, Button addB) {
		this(arg);
		home = gp;
		addAcc = addB;
		username = new TextField();
		password = new PasswordField();
		email = new TextField();
		freeze = new ToggleButton("Freeze");

	}

	public void createUI(boolean first) {

		setTitle("User Account - Typh™");
		setHeaderText("Fill in required fields to add a user account");

		GridPane dPane = new GridPane();

		dPane.setPadding(new Insets(20));
		dPane.setHgap(20);
		dPane.setVgap(20);

		ComboBox<String> dprtMember = new ComboBox<>();
		dprtMember.getItems().addAll(Departments.dprtList);

		Label lusername = new Label("User Name");
		Label lpassword = new Label("Password");
		Label lemail = new Label("E-mail");
		Label ldprt = new Label("Department");

		username.setPromptText("Enter username");
		password.setPromptText("Enter password");
		email.setPromptText("Enter email");

		dPane.add(lusername, 0, 0);
		dPane.add(username, 1, 0);
		dPane.add(lpassword, 0, 1);
		dPane.add(password, 1, 1);
		dPane.add(lemail, 0, 2);
		dPane.add(email, 1, 2);
		dPane.add(ldprt, 0, 3);
		dPane.add(dprtMember, 1, 3);
		
		if (!first) {
			addEdit(dPane);
			ButtonType save = new ButtonType("Save", ButtonData.OK_DONE);
			getDialogPane().setContent(dPane);
			getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
			setResultConverter((button) -> {
				if (button.equals(save) && (!isAvailable()) && (!areFieldsEmpty())) {
						reloadData();
				} else if (button.equals(save) && (isAvailable()) && (!areFieldsEmpty())) {
					Notification.message(parent, AlertType.ERROR, "Error - User Accounts - Typh™",
							"Username already taken !");
				} else if ((button.equals(save) && (areFieldsEmpty())))
					Notification.message(parent, AlertType.ERROR, "Error - User Accounts - Typh™",
							"All Fields are mandatory !");
				return null;
			});
			show();
		}else{
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
		}
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
