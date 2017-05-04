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

import ivn.typh.main.BasicUI;
import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import ivn.typh.admin.Components;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import static com.mongodb.client.model.Filters.*;

public class AdminUI extends Task<Void>{

	public AdminUI(Stage s, BorderPane p, ToolBar tb) {
		Components.mb = tb;
		Components.stage = s;
		Components.pane = p;
		Components.menu = new Button("Menu");
		Components.rTotalStudents = new Label("0");
		Components.rTotalUsers = new Label("0");
		Components.rLastLogin = new Label("No last Login");
	}

	public void startUI() {
		Components.gpane = new GridPane();
		Components.sgpane = new ScrollPane();

		Components.topL = new VBox();
		Components.left = new VBox();
		Components.right = new VBox();
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


		Components.admin = new Label("Administrator");
		Components.totalStudents = new Label("Total Students:");
		Components.totalUsers = new Label("Total Users:");
		Components.lastLogin = new Label("Last Login:");

		Label search = new Label("Search");
		Label au = new Label("Online Users");
		Button logout = new Button("Log Out");
		Components.srch = new Search();

		logout.setId("logout");
		search.setId("search");

		Components.onlineUser = new ListView<>();
		Components.onlineUser.getItems().add("No User is online !");
		
		
		ContextMenu oucm = new ContextMenu();
		MenuItem sText = new MenuItem("Send a message");
		oucm.getItems().add(sText);
		sText.setOnAction(event -> {
			String item = Components.onlineUser.getSelectionModel().getSelectedItem();
			if (item == null || item.equals("No User is online !")) {
				Platform.runLater(() -> {
					Notification.message(Components.stage, AlertType.ERROR, "Invalid User - Typh™",
							"First select a valid user.");
				});
			}else
			sendMessage(item);
		});

		Components.onlineUser.setContextMenu(oucm);
		
		// Start pulse
		
		Thread pulse = new Thread(new HeartBeat());
		pulse.start();
				
		
		Components.menu.setGraphic(new ImageView(new Image("/ivn/typh/main/icons/menu.png")));

		TabPane tabPane = new TabPane();
		tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

		Components.user = new Tab("Users");
		Components.stud = new Tab("Students");
		Components.dprt = new Tab("Departments");


		Components.user.setClosable(false);
		Components.stud.setClosable(false);
		Components.dprt.setClosable(false);

		tabPane.setEffect(new DropShadow());

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
				Notification.message(Components.stage, AlertType.ERROR, "Error - Empty List - Typh™",
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
				Notification.message(Components.stage, AlertType.ERROR, "Error - Empty List - Typh™",
						"First add at least one department before adding a profile !");
			}
		});

		Components.userGrid.add(Components.addAcc, UserAccounts.x, UserAccounts.y);
		Components.studGrid.add(Components.addStudent, Students.x, Students.y);
		Components.dprtGrid.add(Components.addDepartment, Departments.x, Departments.y);

		Components.user.setContent(scrollUser);
		Components.stud.setContent(scrollStud);
		Components.dprt.setContent(scrollDprt);

		tabPane.getTabs().addAll(Components.user, Components.stud, Components.dprt);

		Components.center.getChildren().add(tabPane);

		Components.topL.getChildren().add(Components.admin);
		Components.left.getChildren().addAll(Components.topL, Components.totalStudents, Components.rTotalStudents,
		Components.totalUsers, Components.rTotalUsers, Components.lastLogin, Components.rLastLogin);
		Components.right.getChildren().addAll(au, Components.onlineUser);
		Components.top.getChildren().addAll(search, Components.srch);

		Components.gpane.getColumnConstraints().addAll(cc0, cc1);
		Components.gpane.getRowConstraints().addAll(rc0, rc1);
		Components.gpane.add(Components.top, 0, 0);
		Components.gpane.add(Components.center, 0, 1);
		Components.gpane.add(Components.right, 1, 0, 1, 2);
		Components.gpane.setMaxSize(BasicUI.screenWidth, BasicUI.screenHeight);
		Components.gpane.setMinSize(BasicUI.screenWidth, BasicUI.screenHeight);

		Components.sgpane.setContent(Components.gpane);
		Components.sgpane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		Components.sgpane.setVbarPolicy(ScrollBarPolicy.NEVER);
		


		Platform.runLater(()->{
			Components.side = new SideBar();
			
			Components.setIdAll();
			Components.setCacheAll();

			Components.mb.getItems().remove(7);
			Components.mb.getItems().add(logout);
			Components.mb.getItems().remove(0, 4);
			Components.mb.getItems().add(0, Components.menu);
			Components.mb.getItems().get(1).setId("fullscreen");

			
			BasicUI.centerOfHomePane.changeRootPane(Components.sgpane, Components.side);

			StackPane.setAlignment(Components.side, Pos.CENTER_LEFT);
			HBox.setHgrow(tabPane, Priority.ALWAYS);
			VBox.setVgrow(Components.onlineUser, Priority.ALWAYS);
			
			Components.stage.getScene().getStylesheets().remove(0);
			Components.stage.getScene().getStylesheets().add(getClass().getResource("raw/style.css").toExternalForm());
	
			loadProfiles();
			Components.pane.applyCss();
			Components.pane.layout();
			Components.pane.requestLayout();
			
			tabPane.setTabMinWidth(tabPane.getWidth()/tabPane.getTabs().size() -20);
			tabPane.setTabMaxWidth(tabPane.getWidth()/tabPane.getTabs().size() -20);
		});

	}

	private void sendMessage(String user) {
	
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Messenger - Typh™");
		dialog.initOwner(Components.stage);
		
		VBox mpane = new VBox();
		HBox hpane = new HBox();
		hpane.setSpacing(20);
		mpane.setId("message_pane");
		
		Label characterLimit = new Label("255");
		TextArea text = new TextArea();
		Pane dummy = new Pane();
		text.setPromptText("Enter message ...");
		text.setPrefRowCount(8);
		text.setPrefColumnCount(30);
		text.setWrapText(true);
		text.textProperty().addListener((obs,o,n)->{
			characterLimit.setText(Integer.toString(255 - text.getText().length()));
		});

		HBox.setHgrow(dummy, Priority.ALWAYS);
		hpane.getChildren().addAll(dummy,characterLimit,new Label("  characters"));
		mpane.getChildren().addAll(new Label("Enter a message for [ "+user+" ]"),text,hpane);
		ButtonType send = new ButtonType("Send", ButtonData.OK_DONE);
		dialog.getDialogPane().setContent(mpane);
		dialog.getDialogPane().getButtonTypes().addAll(send, ButtonType.CANCEL);
		dialog.setResultConverter(value -> {
			if (value.getButtonData().equals(ButtonData.OK_DONE))
				return text.getText().trim();
			return null;
		});
		Node snode = dialog.getDialogPane().lookupButton(send);
		text.textProperty().addListener((observable, oldv, newv) -> {
			snode.setDisable(newv.trim().isEmpty() || (newv.length()>255));
			if(newv.length()>255)
				characterLimit.setTextFill(Color.RED);
			else
				characterLimit.setTextFill(Color.BLACK);

		});

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(msg -> HeartBeat.message = msg);

	}

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
				String tsclass = json.getString("class");
				String tsbatch = json.getString("batch");
				String tsmail = json.getString("email");
				String tsaddr = json.getString("address");
				String tsphone = Integer.toString(json.getInt("studentPhone"));
				String tpphone = Integer.toString(json.getInt("parentPhone"));
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
						e.printStackTrace();
					}
					UserAccounts.userList.add(json.getString("user"));
					Button tmp = new Button(username);
					tmp.setOnAction(new UserAccounts(Components.stage, Components.userGrid, username, full, password,
							email, dprt, cli, yin, ll, stat));
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
		Components.srch.setItems(Students.studentList);
	}

	private String getDefaultImage() {
		BufferedImage bf = SwingFXUtils.fromFXImage(new Image(getClass().getResourceAsStream("/ivn/typh/main/raw/pic.jpg")), null);
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		try {
			ImageIO.write(bf, "jpg", array);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return  Base64.getEncoder().encodeToString(array.toByteArray());
	}

	private void logoutApplication() {
		Alert ex = new Alert(AlertType.CONFIRMATION);
		ex.setHeaderText("LogOut Typh™ ? ");
		ex.setTitle("Exit - Typh™");
		ex.initOwner(Components.stage);
		ex.getDialogPane().getButtonTypes().clear();
		ex.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

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
	protected Void call() throws Exception {
		startUI();
		return null;
	}
}
