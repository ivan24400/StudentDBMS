package ivn.typh.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import java.awt.Toolkit;
import java.time.LocalDateTime;
import java.util.Optional;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.MongoCursor;

import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import static com.mongodb.client.model.Filters.*;

public class AdminUI implements Runnable {

	private Stage stage;

	private GridPane userGrid;
	static GridPane studGrid;
	private GridPane dprtGrid;

	private static Button addAcc;
	private static Button addDepartment;
	private static Button addStudent;

	static ListView<String> onlineUser;
	static ToolBar mb;

	private Button menu;
	private Label rts;
	private Label rtu;
	private Label rll;
	private Search srch;
	static BorderPane pane;

	public AdminUI(Stage s, BorderPane p, ToolBar tb) {
		mb = tb;
		stage = s;
		pane = p;
		menu = new Button("Menu");
		rts = new Label();
		rtu = new Label();
		rll = new Label();
	}

	public void startUI() {
		StackPane spMain = new StackPane();
		GridPane gpane = new GridPane();
		Pane dummy = new Pane();
		ScrollPane sgpane = new ScrollPane();

		VBox topL = new VBox();
		VBox left = new VBox();
		VBox right = new VBox();
		HBox center = new HBox();
		HBox top = new HBox();

		Thread pulse = new Thread(new HeartBeat());
		pulse.start();

		gpane.setId("TheGrid");
		rts.setId("logInfo");
		rtu.setId("logInfo");
		rll.setId("logInfo");

		ColumnConstraints cc0 = new ColumnConstraints();
		ColumnConstraints cc1 = new ColumnConstraints();
		RowConstraints rc0 = new RowConstraints();
		RowConstraints rc1 = new RowConstraints();

		Label admin = new Label("Administrator");
		Label ts = new Label("Total Students:");
		Label tu = new Label("Total Users:");
		Label ll = new Label("Last Login:");

		Label search = new Label("Search");
		Label au = new Label("Online Users");
		Button logout = new Button("Log Out");
		srch = new Search();
		
		logout.setId("logout");
		search.setId("search");
		srch.setId("searchBox");

		SideBar side = new SideBar(dummy, menu);
		side.setMenuWidth(300);

		onlineUser = new ListView<>();
		onlineUser.getItems().add("No User is online !");
		ContextMenu oucm = new ContextMenu();
		MenuItem sText = new MenuItem("Send a message");
		oucm.getItems().add(sText);
		sText.setOnAction(event -> {
			sendData();
		});

		onlineUser.setContextMenu(oucm);

		cc0.setPercentWidth(70);
		cc1.setPercentWidth(30);
		rc0.setPercentHeight(15);
		rc1.setPercentHeight(85);

		TabPane tabPane = new TabPane();
		tabPane.setSide(Side.LEFT);
		tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

		Tab user = new Tab("Users");
		Tab stud = new Tab("Students");
		Tab dprt = new Tab("Departments");

		user.setId("tabU");
		stud.setId("tabS");
		dprt.setId("tabD");

		user.setClosable(false);
		stud.setClosable(false);
		dprt.setClosable(false);

		tabPane.setEffect(new DropShadow());

		logout.setOnAction(arg -> {
			logoutApplication();
		});

		userGrid = new GridPane();
		studGrid = new GridPane();
		dprtGrid = new GridPane();

		userGrid.setId("uGrid");
		studGrid.setId("sGrid");
		dprtGrid.setId("dGrid");

		ScrollPane scrollStud = new ScrollPane();
		ScrollPane scrollUser = new ScrollPane();
		ScrollPane scrollDprt = new ScrollPane();

		scrollStud.setContent(studGrid);
		scrollStud.setHbarPolicy(ScrollBarPolicy.NEVER);

		scrollUser.setContent(userGrid);
		scrollUser.setHbarPolicy(ScrollBarPolicy.NEVER);

		scrollDprt.setContent(dprtGrid);
		scrollDprt.setHbarPolicy(ScrollBarPolicy.NEVER);

		addAcc = new Button("+");
		addDepartment = new Button("+");
		addStudent = new Button("+");

		Tooltip tabtipd = new Tooltip("Add a Department");
		tabtipd.setFont(new Font(12));

		Tooltip tabtipu = new Tooltip("Add a User account");
		tabtipu.setFont(new Font(12));

		Tooltip tabtips = new Tooltip("Add a Student");
		tabtips.setFont(new Font(12));

		addDepartment.setTooltip(tabtipd);
		addAcc.setTooltip(tabtipu);
		addStudent.setTooltip(tabtips);

		loadProfiles();

		addAcc.setOnAction((arg0) -> {
			if (Departments.dprtList != null) {
				UserAccounts dialog = new UserAccounts(stage, userGrid, addAcc);
				dialog.begin();

			} else
				Notification.message(stage, AlertType.ERROR, "Error - Empty List - Typh™",
						"First add at least one department before adding an account !");

		});

		addDepartment.setOnAction((arg0) -> {
			Departments dialog = new Departments(stage, dprtGrid, addDepartment);
			dialog.begin();

		});

		addStudent.setOnAction((arg0) -> {
			if (Departments.dprtList != null) {
				Students dialog = new Students(stage, studGrid, addStudent);
				dialog.begin();

			} else {
				Notification.message(stage, AlertType.ERROR, "Error - Empty List - Typh™",
						"First add at least one department before adding a profile !");
			}
		});

		userGrid.add(addAcc, UserAccounts.x, UserAccounts.y);
		studGrid.add(addStudent, Students.x, Students.y);
		dprtGrid.add(addDepartment, Departments.x, Departments.y);

		user.setContent(scrollUser);
		stud.setContent(scrollStud);
		dprt.setContent(scrollDprt);

		tabPane.getTabs().addAll(user, stud, dprt);

		center.setId("center");
		topL.setId("topL");
		left.setId("left");
		right.setId("right");
		top.setId("top");

		HBox.setHgrow(tabPane, Priority.ALWAYS);
		VBox.setVgrow(onlineUser, Priority.ALWAYS);
		StackPane.setAlignment(side, Pos.CENTER_LEFT);

		center.getChildren().add(tabPane);

		topL.getChildren().add(admin);
		left.getChildren().addAll(topL, ts, rts, tu, rtu, ll, rll);
		right.getChildren().addAll(au, onlineUser);
		top.getChildren().addAll(search, srch);

		side.addNodes(topL, left, mb.getItems().get(2), mb.getItems().get(3), mb.getItems().get(5));
		side.setPrefWidth(300);

		mb.getItems().remove(7);
		mb.getItems().add(7, logout);
		mb.getItems().remove(0, 5);
		mb.getItems().add(0, menu);

		gpane.getColumnConstraints().addAll(cc0, cc1);
		gpane.getRowConstraints().addAll(rc0, rc1);
		gpane.add(top, 0, 0);
		gpane.add(center, 0, 1);
		gpane.add(right, 1, 0, 1, 2);
		gpane.setMaxSize(Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
				Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		gpane.setMinSize(Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
				Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		sgpane.setContent(gpane);

		gpane.applyCss();
		gpane.layout();

		sgpane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sgpane.setVbarPolicy(ScrollBarPolicy.NEVER);
		spMain.getChildren().addAll(sgpane, dummy, side);

		stage.getScene().getStylesheets().remove(0);
		stage.getScene().getStylesheets().add(getClass().getResource("raw/style.css").toExternalForm());
		pane.setCenter(spMain);

		pane.applyCss();
		pane.layout();
		pane.requestLayout();
		tabPane.setTabMinWidth(tabPane.getHeight() / 3 - 17);
		tabPane.setTabMaxWidth(tabPane.getHeight() / 3 - 17);

	}

	private void sendData() {
		String item = onlineUser.getSelectionModel().getSelectedItem();
		if (item == null || item.equals("No User is online !")) {
			Platform.runLater(() -> {
				Notification.message(stage, AlertType.ERROR, "Invalid User - Typh™", "First select a valid user.");
			});
			return;
		}
		Dialog<String> dialog = new Dialog<>();
		dialog.setHeaderText("Enter message for " + item);
		dialog.setTitle("Messenger - Typh™");
		dialog.getDialogPane().setPadding(new Insets(50));
		dialog.initOwner(stage);
		TextField text = new TextField();
		text.setPromptText("Enter message ...");
		ButtonType send = new ButtonType("Send", ButtonData.OK_DONE);
		dialog.getDialogPane().setContent(text);
		dialog.getDialogPane().getButtonTypes().addAll(send, ButtonType.CANCEL);
		dialog.setResultConverter(value -> {
			if (value.getButtonData().equals(ButtonData.OK_DONE))
				return text.getText().trim();
			return null;
		});
		Node snode = dialog.getDialogPane().lookupButton(send);
		text.textProperty().addListener((observable, oldv, newv) -> {
			snode.setDisable(newv.trim().isEmpty());
		});

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(msg -> HeartBeat.message = msg);

	}

	private void loadProfiles() {

		Document tmpdoc = Engine.db.getCollection("Users").find(eq("user", "admin")).first();
		rll.setText(tmpdoc.getString("lastLogin"));

		rtu.setText(Long.toString(Engine.db.getCollection("Users").count() - 1));
		Engine.db.getCollection("Users").updateOne(eq("user", "admin"),
				new Document("$set", new Document("lastLogin",
						LocalDateTime.now().getDayOfMonth() + "-" + LocalDateTime.now().getMonthValue() + "-"
								+ LocalDateTime.now().getYear() + "\n" + LocalDateTime.now().getHour() + "h:"
								+ LocalDateTime.now().getMinute() + "m:" + LocalDateTime.now().getSecond() + "s")));
		MongoCursor<Document> cursor;

		// Department

		cursor = Engine.db.getCollection("Departments").find().iterator();
		Departments.dprtList = FXCollections.observableHashMap();
		try {
			while (cursor.hasNext()) {

				JSONObject json = new JSONObject(cursor.next().toJson());
				String name = json.getString("department");
				String head = json.getString("hod");
				String crooms = Integer.toString(json.getInt("classrooms"));
				String labs = Integer.toString(json.getInt("laboratory"));
				String srooms = Integer.toString(json.getInt("staffrooms"));
				boolean lib = json.getBoolean("library");
				String id = json.getString("dprtID");
				Departments.dprtList.put(id, name);

				Button tmp = new Button(name);
				tmp.setOnAction(new Departments(stage, dprtGrid, name, head, crooms, labs, srooms, id, lib));
				if (Departments.x < 6) {
					Departments.x++;
					dprtGrid.add(tmp, Departments.x - 1, Departments.y);
					GridPane.setColumnIndex(addDepartment, Departments.x);
					GridPane.setRowIndex(addDepartment, Departments.y);

				} else {
					Departments.x = 1;
					Departments.y++;
					dprtGrid.add(tmp, Departments.x - 1, Departments.y);
					GridPane.setColumnIndex(addDepartment, Departments.x);
					GridPane.setRowIndex(addDepartment, Departments.y);

				}
			}
			// Students
			cursor = Engine.db.getCollection("Students").find().iterator();
			Students.studentList = FXCollections.observableArrayList();

			while (cursor.hasNext()) {

				JSONObject json = new JSONObject(cursor.next().toJson());

				String tsname = json.getString("name");
				String tsid = json.getString("sid");
				String tsrno = json.getString("rno");
				String tsclass = json.getString("class");
				String tsbatch = json.getString("batch");
				String tsmail = json.getString("email");
				String tsaddr = json.getString("address");
				String tsphone = Integer.toString(json.getInt("studentPhone"));
				String tpphone = Integer.toString(json.getInt("parentPhone"));
				String tsdprt = json.getString("department");
				String img = json.getString("img");
				String y = json.getString("year");
				Students.studentList.add(tsname);

				Button tmp = new Button(tsname);
				tmp.setOnAction(new Students(stage, studGrid, tsname, tsid, tsrno, tsclass, tsbatch, tsmail, tsaddr,
						tsphone, tpphone, tsdprt, img, y));
				if (Students.x < 6) {
					Students.x++;
					studGrid.add(tmp, Students.x - 1, Students.y);
					GridPane.setColumnIndex(addStudent, Students.x);
					GridPane.setRowIndex(addStudent, Students.y);

				} else {
					Students.x = 1;
					Students.y++;
					studGrid.add(tmp, Students.x - 1, Students.y);
					GridPane.setColumnIndex(addStudent, Students.x);
					GridPane.setRowIndex(addStudent, Students.y);

				}
			}

			rts.setText(Integer.toString(Students.studentList.size()));
			// UserAccounts
			cursor = Engine.db.getCollection("Users").find().iterator();
			UserAccounts.userList = FXCollections.observableArrayList();

			while (cursor.hasNext()) {

				JSONObject json = new JSONObject(cursor.next().toJson());
				String username = json.getString("user");
				if (!username.equals("admin")) {
					String password = null, email = null, dprt = null, full = null, cli = null, yin = null, ll = null;
					boolean stat = false;
					try {
						password = json.getString("passwd");
						email = json.getString("email");
						dprt = json.getString("department");
						full = json.getString("fullname");
						cli = json.getString("classIncharge");
						yin = json.getString("yearIncharge");
						ll = json.getString("lastLogin");
						stat = json.getBoolean("status");
					} catch (JSONException e) {
					}
					UserAccounts.userList.add(json.getString("user"));
					Button tmp = new Button(username);
					tmp.setOnAction(new UserAccounts(stage, userGrid, username, full, password, email, dprt, cli, yin,
							ll, stat));
					if (UserAccounts.x < 6) {
						UserAccounts.x++;
						userGrid.add(tmp, UserAccounts.x - 1, UserAccounts.y);
						GridPane.setColumnIndex(addAcc, UserAccounts.x);
						GridPane.setRowIndex(addAcc, UserAccounts.y);

					} else {
						UserAccounts.x = 1;
						UserAccounts.y++;
						userGrid.add(tmp, UserAccounts.x - 1, UserAccounts.y);
						GridPane.setColumnIndex(addAcc, UserAccounts.x);
						GridPane.setRowIndex(addAcc, UserAccounts.y);

					}
				}
			}
		} catch (JSONException e) {
		}
		srch.setItems(Students.studentList);
	}

	private void logoutApplication() {
		Alert ex = new Alert(AlertType.CONFIRMATION);
		ex.setHeaderText("LogOut Typh™ ? ");
		ex.setTitle("Exit - Typh™");
		ex.initOwner(stage);
		ex.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

		Optional<ButtonType> result = ex.showAndWait();
		result.ifPresent(arg -> {
			if (arg.equals(ButtonType.OK)) {
				if (!(Engine.mongo == null))
					Engine.mongo.close();
				HeartBeat.heartAttack = true;
				Platform.exit();
			}
		});
	}

	@Override
	public void run() {
		Platform.runLater(() -> {
			startUI();
		});
	}
}
