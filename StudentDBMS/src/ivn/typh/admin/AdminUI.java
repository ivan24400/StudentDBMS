package ivn.typh.admin;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;

import java.time.LocalDateTime;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.MongoCursor;
import ivn.typh.main.Engine;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
	private GridPane userGrid;
	private GridPane studGrid;
	private GridPane dprtGrid;
	private static Button addAcc;
	private static Button addDepartment;
	private static Button addStudent;

	private Label rts; 
	private Label rtu ;
	private Label rll ;
	
	
	public AdminUI(Stage s) {
		stage = s;
		rts = new Label();
		rtu = new Label();
		rll = new Label();
	}

	public void startUI() {
		GridPane gpane = new GridPane();
		ScrollPane sgpane = new ScrollPane();

		ColumnConstraints cc0 = new ColumnConstraints();
		ColumnConstraints cc1 = new ColumnConstraints();
		ColumnConstraints cc2 = new ColumnConstraints();
		RowConstraints rc0 = new RowConstraints();
		RowConstraints rc1 = new RowConstraints();

		Label admin = new Label("Administrator");
		Label ts = new Label("Total Students");
		Label tu = new Label("Total Users");
		Label ll = new Label("Last Login");
		Label search = new Label("Search");
		TextField srch = new TextField();
		Label au = new Label("Active Users");

		ObservableList<String> onlineUser = FXCollections.observableArrayList();
		ListView<String> actusr = new ListView<>(onlineUser);
		VBox pane11 = new VBox();
		VBox pane12 = new VBox();
		VBox pane3 = new VBox();
		HBox pane22 = new HBox();
		HBox pane21 = new HBox();

		cc0.setPercentWidth(20);
		cc1.setPercentWidth(60);
		cc2.setPercentWidth(20);
		cc1.setHalignment(HPos.RIGHT);
		cc2.setHalignment(HPos.LEFT);
		rc0.setPercentHeight(30);
		rc1.setPercentHeight(70);

		TabPane tabPane = new TabPane();
		Tab user = new Tab("User Accounts");
		Tab stud = new Tab("Student Profiles");
		Tab dprt = new Tab("Departments");

		user.setClosable(false);
		stud.setClosable(false);
		dprt.setClosable(false);

		userGrid = new GridPane();
		studGrid = new GridPane();
		dprtGrid = new GridPane();
		ScrollPane scrollStud = new ScrollPane();
		scrollStud.setContent(studGrid);
		scrollStud.setHbarPolicy(ScrollBarPolicy.NEVER);

		tabPane.setStyle(
				"-fx-border-style: solid inside;-fx-border-width: 2;-fx-border-insets: 5;-fx-background-color: #30234f");
		tabPane.setMinWidth(700);

		userGrid.setPadding(new Insets(40));
		userGrid.setHgap(30);
		userGrid.setVgap(30);

		studGrid.setPadding(new Insets(40));
		studGrid.setHgap(30);
		studGrid.setVgap(30);

		dprtGrid.setPadding(new Insets(40));
		dprtGrid.setHgap(30);
		dprtGrid.setVgap(30);

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

		user.setContent(userGrid);
		stud.setContent(scrollStud);
		dprt.setContent(dprtGrid);
		tabPane.getTabs().addAll(user, stud, dprt);

		pane22.getChildren().addAll(tabPane);
		pane22.setPadding(new Insets(30));
		pane22.setSpacing(20);
		pane11.getChildren().add(admin);
		pane12.getChildren().addAll(ts, rts, tu, rtu, ll, rll);
		pane3.getChildren().addAll(au, actusr);
		pane21.getChildren().addAll(search, srch);
		pane21.setPadding(new Insets(30));
		pane21.setSpacing(20);

		pane11.setPadding(new Insets(30));
		pane12.setPadding(new Insets(30));
		pane3.setPadding(new Insets(30));
		pane12.setSpacing(30);
		pane3.setSpacing(40);

		gpane.getColumnConstraints().addAll(cc0, cc1, cc2);
		gpane.getRowConstraints().addAll(rc0, rc1);
		gpane.add(pane11, 0, 0);
		gpane.add(pane12, 0, 1, 1, 2);
		gpane.add(pane22, 1, 1);
		gpane.add(pane3, 2, 0, 1, 2);
		gpane.add(pane21, 1, 0);
		gpane.setMaxHeight(768);
		gpane.setMaxWidth(1360);

		sgpane.setContent(gpane);
		Scene scene = new Scene(sgpane);
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.show();

	}

	private void loadProfiles() {
		Document tmpdoc = Engine.db.getCollection("Users").find(eq("user","admin")).first();
		rll.setText(tmpdoc.getString("lastLogin"));
		
		rtu.setText(Long.toString(Engine.db.getCollection("Users").count()-1));
		Engine.db.getCollection("Users").updateOne(eq("user","admin"), new Document("$set",new Document("lastLogin",LocalDateTime.now().getDayOfMonth()+"-"+LocalDateTime.now().getMonthValue()+"-"+LocalDateTime.now().getYear()+"\t"+LocalDateTime.now().getHour()+":"+LocalDateTime.now().getMinute()+":"+LocalDateTime.now().getSecond())));
		MongoCursor<Document> cursor;
		// Department
		cursor = Engine.db.getCollection("Departments").find().iterator();
		Departments.dprtList = FXCollections.observableArrayList();

		while (cursor.hasNext()) {

			JSONObject json = new JSONObject(cursor.next().toJson());
			String name = json.getString("department");
			String head = json.getString("hod");
			String rooms = Integer.toString(json.getInt("classrooms"));
			String labs = Integer.toString(json.getInt("laboratory"));

			Departments.dprtList.add(name);

			Button tmp = new Button(name);
			tmp.setOnAction(new Departments(stage, dprtGrid, name, head, rooms, labs));
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
		 Students.studentList  = FXCollections.observableArrayList();

		while (cursor.hasNext()) {

			JSONObject json = new JSONObject(cursor.next().toJson());
		
			String tsname= json.getString("name");   
			String tsid= Integer.toString(json.getInt("sid"));     
			String tsrno= Integer.toString(json.getInt("rno"));    
			String tsclass= json.getString("class");  
			String tsbatch= json.getString("batch");  
			String tsmail= json.getString("email");   
			String tsaddr= json.getString("address");   
			String tsphone= Integer.toString(json.getInt("studentPhone"));  
			String tpphone= Integer.toString(json.getInt("parentPhone"));  
			String tsdprt= json.getString("department");
			String img = json.getString("img");
			
			Students.studentList.add(tsdprt);

			Button tmp = new Button(tsname);
			tmp.setOnAction(new Students(stage, studGrid, tsname, tsid, tsrno, tsclass,tsbatch,tsmail,tsaddr,tsphone,tpphone,tsdprt,img));
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
				String password = null, email = null, dprt = null,full= null;
				            
				try{
				password = json.getString("passwd");
				email = json.getString("email");
				dprt = json.getString("department");
				full = json.getString("fullname");
				}catch(JSONException e){}
				UserAccounts.userList.add(json.getString("user"));
				Button tmp = new Button(username);
				tmp.setOnAction(new UserAccounts(stage, userGrid, username,full, password, email,dprt));
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

	}

	@Override
	public void run() {
		Platform.runLater(() -> {
			startUI();
		});
	}
}
