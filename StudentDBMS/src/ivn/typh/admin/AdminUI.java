package ivn.typh.admin;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import java.time.LocalDateTime;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.MongoCursor;

import ivn.typh.main.BasicUI;
import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import static com.mongodb.client.model.Filters.*;

public class AdminUI implements Runnable {

	private Stage stage;
	static ObservableList<String> onlineUser;
	
	private GridPane userGrid;
	static GridPane studGrid;
	private GridPane dprtGrid;
	private static Button addAcc;
	private static Button addDepartment;
	private static Button addStudent;
	private ToolBar mb;
	private Label rts;
	private Label rtu;
	private Label rll;
	private Search srch ;
	private BorderPane pane;

	public AdminUI(Stage s, BorderPane p, ToolBar tb) {
		mb = tb;
		stage = s;
		pane = p;
		rts = new Label();
		rtu = new Label();
		rll = new Label();
	}

	public void startUI() {
		GridPane gpane = new GridPane();
		ScrollPane sgpane = new ScrollPane();

		Thread pulse = new Thread(new HeartBeat());
		pulse.start();
		
		ColumnConstraints cc0 = new ColumnConstraints();
		ColumnConstraints cc1 = new ColumnConstraints();
		ColumnConstraints cc2 = new ColumnConstraints();
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

		onlineUser = FXCollections.observableArrayList();
		ListView<String> actusr = new ListView<>(onlineUser);
		VBox topL = new VBox();
		VBox left = new VBox();
		VBox right = new VBox();
		HBox center = new HBox();
		HBox top = new HBox();

		cc0.setPercentWidth(20);
		cc1.setPercentWidth(60);
		cc2.setPercentWidth(20);
		cc1.setHalignment(HPos.RIGHT);
		cc2.setHalignment(HPos.LEFT);
		rc0.setPercentHeight(30);
		rc1.setPercentHeight(70);

		TabPane tabPane = new TabPane();
		tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
		Tab user = new Tab("User Accounts");
		Tab stud = new Tab("Student Profiles");
		Tab dprt = new Tab("Departments");

		user.setId("tabU");
		stud.setId("tabS");
		dprt.setId("tabD");
		
		user.setClosable(false);
		stud.setClosable(false);
		dprt.setClosable(false);

		tabPane.setEffect(new DropShadow());
		
		logout.setOnAction(arg -> {
			Engine.mongo.close();
			mb.getItems().remove(8);
			stage.getScene().getStylesheets().remove(0);
			stage.getScene().getStylesheets().add(getClass().getResource("/ivn/typh/main/raw/style.css").toExternalForm());
			pane.setCenter(BasicUI.login);
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
		tabPane.setMinWidth(700);


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
		
		mb.getItems().add(8,logout);

		user.setContent(scrollUser);
		stud.setContent(scrollStud);
		dprt.setContent(scrollDprt);
		
		tabPane.getTabs().addAll(user, stud, dprt);

		center.setId("center");
		topL.setId("topL");
		left.setId("left");
		right.setId("right");
		top.setId("top");
		
		center.getChildren().addAll(tabPane);
		topL.getChildren().add(admin);
    	left.getChildren().addAll(ts, rts, tu, rtu, ll, rll);
		right.getChildren().addAll(au, actusr);
		top.getChildren().addAll(search, srch);

		
		gpane.getColumnConstraints().addAll(cc0, cc1, cc2);
		gpane.getRowConstraints().addAll(rc0, rc1);
		gpane.add(topL, 0, 0);
		gpane.add(left, 0, 1, 1, 2);
		gpane.add(center, 1, 1);
		gpane.add(right, 2, 0, 1, 2);
		gpane.add(top, 1, 0);
		gpane.setMaxHeight(768);
		gpane.setMaxWidth(1360);

		sgpane.setContent(gpane);
		sgpane.setHbarPolicy(ScrollBarPolicy.NEVER);
		sgpane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		stage.getScene().getStylesheets().remove(0);
		stage.getScene().getStylesheets().add(getClass().getResource("raw/style.css").toExternalForm());
		pane.setCenter(sgpane);
	}

	private void loadProfiles() {
		
		Document tmpdoc = Engine.db.getCollection("Users").find(eq("user", "admin")).first();
		rll.setText(tmpdoc.getString("lastLogin"));

		rtu.setText(Long.toString(Engine.db.getCollection("Users").count() - 1));
		Engine.db.getCollection("Users").updateOne(eq("user", "admin"),
				new Document("$set",
						new Document("lastLogin",
								LocalDateTime.now().getDayOfMonth() + "-" + LocalDateTime.now().getMonthValue() + "-"
										+ LocalDateTime.now().getYear() + "\t" + LocalDateTime.now().getHour() + "h:"
										+ LocalDateTime.now().getMinute() + "m:" + LocalDateTime.now().getSecond()+"s")));
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
				tmp.setOnAction(new Departments(stage, dprtGrid, name, head, crooms, labs, srooms, id,lib));
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
						tsphone, tpphone, tsdprt, img,y));
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
					String password = null, email = null, dprt = null, full = null, cli = null, yin = null,ll=null;
					boolean stat = false;
					try {
						password = json.getString("passwd");
						email = json.getString("email");
						dprt = json.getString("department");
						full = json.getString("fullname");
						cli = json.getString("classIncharge");
						yin = json.getString("yearIncharge");
						ll= json.getString("lastLogin");
						stat = json.getBoolean("status");
					} catch (JSONException e) {
					}
					UserAccounts.userList.add(json.getString("user"));
					Button tmp = new Button(username);
					tmp.setOnAction(new UserAccounts(stage, userGrid, username, full, password, email, dprt, cli, yin,ll,stat));
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

	@Override
	public void run() {
		Platform.runLater(() -> {
			startUI();
		});
	}
}
