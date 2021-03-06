package ivn.typh.admin;

import javafx.geometry.Pos;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;

import ivn.typh.main.BasicUI;
import ivn.typh.main.CenterPane;
import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import ivn.typh.main.Resources;
import ivn.typh.admin.Components;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*
 *This class creates user interface for Admin user,
 *and it is called by LogIn class.
 *@see Task. 
 */


public class AdminUI extends Task<Void>{

	public AdminUI(Stage s, BorderPane p, ToolBar tb) {
		Components.menuBar = tb;
		Components.stage = s;
		Components.pane = p;
		
	}

	public void startUI() {
		Components.gpane = new GridPane();
		Components.sgpane = new ScrollPane();

		Components.accNamePane = new VBox();
		Components.accDescPane = new VBox();
		Components.onlineUserPane = new VBox();
		Components.center = new HBox();
		Components.top = new HBox();


		ColumnConstraints cc0 = new ColumnConstraints();
		ColumnConstraints cc1 = new ColumnConstraints();
		RowConstraints rc0 = new RowConstraints();
		RowConstraints rc1 = new RowConstraints();
		

		cc0.setPercentWidth(70);
		cc1.setPercentWidth(30);
		rc0.setPercentHeight(15);
		rc1.setPercentHeight(75);

		Button logout = new Button("Log Out");

		Components.admin = new Label("Admin");
		Components.admin.setFont(new Font(30));
		Components.totalStudents = new Label("Total Students:");
		Components.totalUsers = new Label("Total Users:");
		Components.lastLogin = new Label("Last Login:");
		Components.rTotalStudents = new Label("0");
		Components.rTotalUsers = new Label("0");
		Components.rLastLogin = new Label("No last Login");
		
		
		CenterPane.menu = new Button("Menu");
		CenterPane.menu.setGraphic(new ImageView(new Image("/ivn/typh/main/icons/menu.png")));

		Components.srch = new Label("Search");
		Components.searchBox = new Search();
		Components.srch.setCursor(Cursor.HAND);
		Components.srch.setTooltip(new Tooltip("Click to Display List of All Students"));
		Components.srch.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				Components.searchBox.populatePopup(Students.studentList,Integer.MAX_VALUE);
				Components.searchBox.displayPopUp();
			}
			
		});
		

		logout.setId("logout");
		logout.setCursor(Cursor.HAND);
		Components.srch.setId("search");

		Components.ou = new Label("Online Users");
		Components.ou.setEffect(new DropShadow());
		Components.onlineUser = new ListView<>();
		Components.onlineUser.getItems().add("No User is online !");
		Components.onlineUser.setEffect(new DropShadow());
		
		// Setup context menu for the messenger
		
		ContextMenu oucm = new ContextMenu();
		MenuItem sText = new MenuItem("Send a message");
		oucm.getItems().add(sText);
		sText.setOnAction(event -> {
			String item = Components.onlineUser.getSelectionModel().getSelectedItem();
			if (item == null || item.equals("No User is online !")) {
				Platform.runLater(() -> {
					Notification.message(Components.stage, AlertType.ERROR, "Invalid User - Typh�",
							"First select a valid user.");
				});
			}else
			Messenger.sendMessage(item);
		});

		Components.onlineUser.setContextMenu(oucm);
		
		// Start Background Pulse
		
		Thread pulse = new Thread(new HeartBeat());
		pulse.start();
				
		// Create TabPane

		Components.tabPane = new TabPane();
		Components.tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

		Components.user = new Tab("Users");
		Components.stud = new Tab("Students");
		Components.dprt = new Tab("Departments");


		Components.user.setClosable(false);
		Components.stud.setClosable(false);
		Components.dprt.setClosable(false);

		Components.tabPane.setEffect(new DropShadow());
		Components.tabPane.setTabMinWidth(200);

		logout.setOnAction(arg -> {
			logoutApplication();
		});

		Components.userGrid = new GridPane();
		Components.studGrid = new GridPane();
		Components.dprtGrid = new GridPane();


		ScrollPane scrollStud = new ScrollPane();
		ScrollPane scrollUser = new ScrollPane();
		ScrollPane scrollDprt = new ScrollPane();

		scrollStud.setContent(Components.studGrid);
		scrollUser.setContent(Components.userGrid);
		scrollDprt.setContent(Components.dprtGrid);
		
		scrollUser.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollStud.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollDprt.setHbarPolicy(ScrollBarPolicy.NEVER);

		Components.addAcc = new Button("+");
		Components.addDepartment = new Button("+");
		Components.addStudent = new Button("+");

		Tooltip tabtipd = new Tooltip("Add a Department");
		Tooltip tabtipu = new Tooltip("Add a User account");
		Tooltip tabtips = new Tooltip("Add a Student");
		
		tabtipd.setFont(new Font(12));
		tabtipu.setFont(new Font(12));
		tabtips.setFont(new Font(12));

		Components.addDepartment.setTooltip(tabtipd);
		Components.addAcc.setTooltip(tabtipu);
		Components.addStudent.setTooltip(tabtips);

		Components.addAcc.setOnAction((arg0) -> {
			if (Departments.dprtList != null) {
				UserAccounts dialog = new UserAccounts(Components.stage, Components.userGrid, Components.addAcc);
				dialog.begin();
			} else
				Notification.message(Components.stage, AlertType.ERROR, "Error - Empty List - Typh�",
						"First add at least one department before adding an account !");

		});

		Components.addDepartment.setOnAction((arg0) -> {
			Departments dialog = new Departments(Components.stage, Components.dprtGrid, Components.addDepartment);
			dialog.begin();

		});

		Components.addStudent.setOnAction((arg0) -> {
			if (Departments.dprtList != null) {
				Students dialog = new Students(Components.stage, Components.studGrid, Components.addStudent);
				dialog.begin();

			} else {
				Notification.message(Components.stage, AlertType.ERROR, "Error - Empty List - Typh�",
						"First add at least one department before adding a profile !");
			}
		});

		Components.userGrid.add(Components.addAcc, UserAccounts.x, UserAccounts.y);
		Components.studGrid.add(Components.addStudent, Students.x, Students.y);
		Components.dprtGrid.add(Components.addDepartment, Departments.x, Departments.y);

		Components.user.setContent(scrollUser);
		Components.stud.setContent(scrollStud);
		Components.dprt.setContent(scrollDprt);

		Components.tabPane.getTabs().addAll(Components.user, Components.stud, Components.dprt);
		
		
		//	Add all nodes to their panes.
		
		Components.center.getChildren().add(Components.tabPane);

		Components.accNamePane.getChildren().add(Components.admin);
		Components.accDescPane.getChildren().addAll(Components.accNamePane, Components.totalStudents, Components.rTotalStudents,
		Components.totalUsers, Components.rTotalUsers, Components.lastLogin, Components.rLastLogin);
		Components.onlineUserPane.getChildren().addAll(Components.ou, Components.onlineUser);
		Components.top.getChildren().addAll(Components.srch, Components.searchBox);

		Components.gpane.getColumnConstraints().addAll(cc0, cc1);
		Components.gpane.getRowConstraints().addAll(rc0, rc1);
		Components.gpane.add(Components.top, 0, 0);
		Components.gpane.add(Components.center, 0, 1);
		Components.gpane.add(Components.onlineUserPane, 1, 0, 1, 2);
		Components.gpane.setMaxSize(BasicUI.screenWidth, BasicUI.screenHeight);
		Components.gpane.setMinSize(BasicUI.screenWidth, BasicUI.screenHeight);

		Components.sgpane.setContent(Components.gpane);
		Components.sgpane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		Components.sgpane.setVbarPolicy(ScrollBarPolicy.NEVER);
		


		Platform.runLater(()->{
			Components.side = new SideBar();
			
			Components.setIdAll();
			Components.setCacheAll();

			Components.menuBar.getItems().remove(7);
			Components.menuBar.getItems().add(logout);
			Components.menuBar.getItems().remove(0, 4);
			Components.menuBar.getItems().add(0, CenterPane.menu);
			Components.menuBar.getItems().get(1).setId("fullscreen");
			
			BasicUI.centerOfHomePane.changeRootPane(Components.sgpane, Components.side);

			StackPane.setAlignment(Components.side, Pos.CENTER_LEFT);
			HBox.setHgrow(Components.tabPane, Priority.ALWAYS);
			VBox.setVgrow(Components.onlineUser, Priority.ALWAYS);
			
			Components.stage.getScene().getStylesheets().remove(0);
			Components.stage.getScene().getStylesheets().add(getClass().getResource("raw/style.css").toExternalForm());
	
			loadProfiles();
			
			Components.pane.applyCss();
			Components.pane.layout();
			Components.pane.requestLayout();
			
			BasicUI.centerOfHomePane.hideMessage();
		});


	}

	
	/*
	 * This method is invoked to load default values 
	 * and other details involved.
	 */
	
	private void loadProfiles() {
		
		Document tmpdoc = Engine.db.getCollection("Users").find(eq("user", "admin")).first();
		Components.rLastLogin.setText(tmpdoc.getString("lastLogin"));

		Components.rTotalUsers.setText(Long.toString(Engine.db.getCollection("Users").count() - 1));
		
		Engine.db.getCollection("Users").updateOne(eq("user", "admin"),
				new Document("$set", new Document("lastLogin",
						LocalDateTime.now().getDayOfMonth() + "-" + LocalDateTime.now().getMonthValue() + "-"
								+ LocalDateTime.now().getYear() + "\n" + LocalDateTime.now().getHour() + "h:"
								+ LocalDateTime.now().getMinute() + "m:" + LocalDateTime.now().getSecond() + "s")));
		MongoCursor<Document> cursor;

		//
		// Department
		//

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
				tmp.setOnAction(new Departments(Components.stage, Components.dprtGrid, name, head, crooms, labs, srooms,
						id, lib));
				if (Departments.x < 6) {
					Departments.x++;
					Components.dprtGrid.add(tmp, Departments.x - 1, Departments.y);
					GridPane.setColumnIndex(Components.addDepartment, Departments.x);
					GridPane.setRowIndex(Components.addDepartment, Departments.y);

				} else {
					Departments.x = 1;
					Departments.y++;
					Components.dprtGrid.add(tmp, Departments.x - 1, Departments.y);
					GridPane.setColumnIndex(Components.addDepartment, Departments.x);
					GridPane.setRowIndex(Components.addDepartment, Departments.y);

				}
			}

			//
			// Students
			//

			cursor = Engine.db.getCollection("Students").find().iterator();
			Students.studentList = FXCollections.observableArrayList();

			while (cursor.hasNext()) {

				JSONObject json = new JSONObject(cursor.next().toJson());

				String tsname = json.getString("name");
				String tsid = json.getString("sid");
				String tsrno = json.getString("rno");
				String tsclass = json.getString("year");
				String tsbatch = json.getString("batch");
				String tsmail = json.getString("email");
				String tsaddr = json.getString("address");
				String tsphone = json.getString("studentPhone");
				String tpphone = json.getString("parentPhone");
				String tsdprt = json.getString("department");
				String img = null;
				try{
					img = json.getString("img");
				}catch(JSONException e){
					img = getDefaultImage();
				}
				String csemester = json.getString("current_semester");
				Students.studentList.add(tsname);

				Button tmp = new Button(tsname);
				tmp.setOnAction(new Students(Components.stage, Components.studGrid, tsname, tsid, tsrno, tsclass,
						tsbatch, tsmail, tsaddr, tsphone, tpphone, tsdprt, img, csemester));
				if (Students.x < 6) {
					Students.x++;
					GridPane.setColumnIndex(Components.addStudent, Students.x);
					GridPane.setRowIndex(Components.addStudent, Students.y);
					Components.studGrid.add(tmp, Students.x - 1, Students.y);


				} else {
					Students.x = 1;
					Students.y++;
					GridPane.setColumnIndex(Components.addStudent, Students.x);
					GridPane.setRowIndex(Components.addStudent, Students.y);
					Components.studGrid.add(tmp, Students.x - 1, Students.y);


				}
			}

			Components.rTotalStudents.setText(Integer.toString(Students.studentList.size()));

			//
			// UserAccounts
			//

			MongoCursor<Document> cursorUser = Engine.db.getCollection("Users").find().iterator();
			UserAccounts.userList = FXCollections.observableArrayList();

			while (cursorUser.hasNext()) {

				JSONObject json = new JSONObject(cursorUser.next().toJson());
				String username = json.getString("user");
				if (!username.equals("admin")) {
					String password = null, email = null, dprt = null, full = null, yin = null, ll = null;
					boolean stat = false;
					try {
						password = json.getString("passwd");
						email = json.getString("email");
						dprt = json.getString("department");
						full = json.getString("fullname");
						yin = json.getString("yearIncharge");
						ll = json.getString("lastLogin");
						stat = json.getBoolean("freeze");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					UserAccounts.userList.add(json.getString("user"));
					Button tmp = new Button(username);
					tmp.setOnAction(new UserAccounts(Components.stage, Components.userGrid, username, full, password,
							email, dprt, yin, ll, stat));
					if (UserAccounts.x < 6) {
						UserAccounts.x++;
						GridPane.setColumnIndex(Components.addAcc, UserAccounts.x);
						GridPane.setRowIndex(Components.addAcc, UserAccounts.y);
						Components.userGrid.add(tmp, UserAccounts.x - 1, UserAccounts.y);
		

					} else {
						UserAccounts.x = 1;
						UserAccounts.y++;
						GridPane.setColumnIndex(Components.addAcc, UserAccounts.x);
						GridPane.setRowIndex(Components.addAcc, UserAccounts.y);
						Components.userGrid.add(tmp, UserAccounts.x - 1, UserAccounts.y);


					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Components.searchBox.setItems(Students.studentList);
	}

	/*
	 * This method is used to get a image stored  inside the
	 * package and use it in case when the image is not available
	 * from the database.
	 * @return Base64 representation of image.
	 */
	
	private String getDefaultImage() {
		BufferedImage bf = SwingFXUtils.fromFXImage(new Image(getClass().getResourceAsStream(Resources.DEFAULT_PIC.VALUE)), null);
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		try {
			ImageIO.write(bf, "jpg", array);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return  Base64.getEncoder().encodeToString(array.toByteArray());
	}

	/*
	 * This method is called when user exits the application.
	 */
	private void logoutApplication() {
		Alert ex = new Alert(AlertType.CONFIRMATION);
		ex.setHeaderText("LogOut Typh� ? ");
		ex.setTitle("Exit - Typh�");
		ex.initOwner(Components.stage);
		ex.getDialogPane().getButtonTypes().clear();
		ex.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		Optional<ButtonType> result = ex.showAndWait();
		result.ifPresent(arg -> {
			if (arg.equals(ButtonType.OK)) {
				HeartBeat.heartAttack = true;
				if (!(Engine.mongo == null))
					Engine.mongo.close();
			}
		});
	}


	@Override
	protected Void call() throws Exception {
		startUI();
		return null;
	}
}
