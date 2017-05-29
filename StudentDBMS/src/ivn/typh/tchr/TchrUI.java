package ivn.typh.tchr;

import static com.mongodb.client.model.Filters.eq;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import ivn.typh.tchr.Components;
import ivn.typh.main.BasicUI;
import ivn.typh.main.CenterPane;
import ivn.typh.main.Engine;
import ivn.typh.main.Resources;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*
 * This class creates the user interface for teacher account.
 */
public class TchrUI extends Task<Void> {

	private static ObservableList<String> studList;
	private static ObservableMap<String, String> dprtList;

	public TchrUI(Stage s, BorderPane p, Scene scen, ToolBar tb) {

		studList = FXCollections.observableArrayList();
		dprtList = FXCollections.observableHashMap();
		Engine.gfs = GridFSBuckets.create(Engine.db, "projects");

		Components.mb = tb;
		Components.pane = p;
		Components.stage = s;
		Components.scene = scen;

	}

	public void startUI() {

		Components.tgpane = new GridPane();
		Components.sctgpane = new ScrollPane();
		CenterPane.menu = new Button("Menu");
		CenterPane.menu.setGraphic(new ImageView(new Image(Resources.MENU_ICON.path)));

		Components.center = new GridPane();
		Components.accDescPane = new VBox();
		Components.top = new HBox();
		Components.accUserPane = new HBox();
		Components.aboveAcc = new HBox();

		Components.logout = new Button("Log Out");
		Components.editable = new ToggleButton("Edit");

		Components.paneList = new String[] { "Personal", "Academic", "Attendance", "Projects", "Assignments" };
		Components.tp = new TitledPane[Components.paneList.length];

		Components.pname = new Label();
		Components.pname.setFont(new Font(30));
		Components.dprt = new Label("Department:");
		Components.pdprt = new Label();
		Components.cls = new Label("Class:");
		Components.pcls = new Label();
		Components.tstuds = new Label("Total Students:");
		Components.nstuds = new Label();

		Components.slist = new ComboBox<>();
		Components.srch = new Label("Search");
		Components.searchBox = new Search();
		Components.student = new Label("Student");
		Components.accord = new Accordion();
		Components.update = new Button("Update");
		Components.report = new Button("Report");

		Components.yrlst = new ComboBox<>();
		Components.yrlst.getItems().addAll(FXCollections.observableArrayList("FE", "SE", "TE", "BE"));
		Components.yrlst.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
			Personal.loadReport(n);
			Academic.loadAcademicData(n);
			Attendance.loadAttendanceData(n);
			Project.loadProjectData(n);
			Assignment.loadAssignmentData(n);
		});

		Components.logout.setOnAction(arg -> {
			logoutApplication();
		});

		Components.update.setOnAction(arg -> {
			uploadData(Personal.tsid.getText());
		});

		Components.editable.selectedProperty().addListener((arg, o, n) -> {
			disableAll(!n);
		});

		Components.slist.getSelectionModel().selectedItemProperty().addListener((arg, o, n) -> {
			loadStudentProfile(n.split(":")[1]);
		});

		// Export Data

		Components.export = new Button("Export");
		Components.export.setOnAction(arg -> {
			Export.export();
		});

		Components.aboveAcc.getChildren().addAll(Components.student, Components.slist, new Label("Select Year"),
				Components.yrlst, Components.editable, Components.update, Components.report, Components.export);

		Components.paneCount = Components.paneList.length;
		Components.scroll = new ScrollPane[Components.paneCount];

		// Setup All the components

		Personal.setup();

		Academic.setup();

		Attendance.setup();

		Project.setup();

		Assignment.setup();

		loadData();

		// Start the heart beat

		Thread pulse = new Thread(new HeartBeat());
		pulse.start();

		//
		// Adding all panes to the accordion
		//

		for (int i = 0; i < Components.paneList.length; i++) {
			Components.scroll[i].setId("homeScrollPane");
			Components.tp[i] = new TitledPane(Components.paneList[i], Components.scroll[i]);
		}

		Components.slist.setPrefWidth(150);

		Components.tgpane.setMaxSize(BasicUI.screenWidth, BasicUI.screenHeight);
		Components.tgpane.setMinSize(BasicUI.screenWidth, BasicUI.screenHeight);
		Components.sctgpane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		Components.sctgpane.setVbarPolicy(ScrollBarPolicy.NEVER);

		Components.accord.getPanes().addAll(Components.tp);
		Components.accord.setExpandedPane(Components.tp[0]);

		Components.top.getChildren().addAll(Components.srch, Components.searchBox);
		Components.accUserPane.getChildren().add(Components.pname);

		Components.accDescPane.getChildren().addAll(Components.dprt, Components.pdprt, Components.cls, Components.pcls,
				Components.tstuds, Components.nstuds);

		Components.tgpane.add(Components.top, 0, 0);
		Components.tgpane.add(Components.aboveAcc, 0, 1);
		Components.tgpane.add(Components.accord, 0, 2);

		Components.sctgpane.setContent(Components.tgpane);

		Platform.runLater(() -> {
			Components.side = new SideBar();

			Components.mb.getItems().remove(7);
			Components.mb.getItems().add(7, Components.logout);
			Components.mb.getItems().remove(0, 4);
			Components.mb.getItems().add(0, CenterPane.menu);
			Components.mb.getItems().get(2).setId("fullscreen");

			Components.setIdAll();
			Components.setCacheAll();

			BasicUI.centerOfHomePane.changeRootPane(Components.sctgpane, Components.side);

			GridPane.setHgrow(Components.accord, Priority.ALWAYS);
			GridPane.setValignment(Components.accDescPane, VPos.CENTER);
			StackPane.setAlignment(Components.side, Pos.CENTER_LEFT);

			Components.stage.getScene().getStylesheets().remove(0);
			Components.stage.getScene().getStylesheets()
					.add(getClass().getResource(Resources.STYLE_SHEET.path).toExternalForm());

			Components.pane.applyCss();
			Components.pane.layout();
			disableAll(true);
			Components.slist.getSelectionModel().selectFirst();
			BasicUI.centerOfHomePane.hideMessage();

		});
	}

	/*
	 * This method uploads data to database.
	 * 
	 * @param sid The student ID.
	 */

	private void uploadData(String sid) {
		Bson filter = new Document("sid", sid);
		Document newValue = new Document("name", Personal.tsname.getText().trim())
				.append("rno", Personal.tsrno.getValue()).append("batch", Personal.tsbatch.getValue())
				.append("class", Personal.tsclass.getValue()).append("email", Personal.tsmail.getText())
				.append("address", Personal.tsaddr.getText()).append("studentPhone", Personal.tsphone.getText())
				.append("parentPhone", Personal.tpphone.getText())
				.append("department", Personal.tsdprt.getSelectionModel().getSelectedItem());

		int[] sem = null;
		String yr = null;
		int year = sMatchesY(0, Components.yrlst.getValue());
		switch (year) {
		case 1:
			sem = new int[] { 1, 2 };
			yr = "fe";
			break;
		case 2:
			sem = new int[] { 3, 4 };
			yr = "se";

			break;
		case 3:
			sem = new int[] { 5, 6 };
			yr = "te";

			break;
		case 4:
			sem = new int[] { 7, 8 };
			yr = "be";

		}

		List<Document> acData = new LinkedList<>();
		for (int i = 0; i < (Academic.tsem1.getItems().size() + Academic.tsem2.getItems().size()); i++) {
			if (i <= (Academic.tsem1.getItems().size() - 1)) {
				Document tmp = new Document("name", Academic.tsem1.getItems().get(i).getSubject())
						.append("thScored", Academic.tsem1.getItems().get(i).getTheoryScored())
						.append("thTotal", Academic.tsem1.getItems().get(i).getTheoryTotal())
						.append("orScored", Academic.tsem1.getItems().get(i).getOralScored())
						.append("orTotal", Academic.tsem1.getItems().get(i).getOralTotal())
						.append("prScored", Academic.tsem1.getItems().get(i).getPracsScored())
						.append("prTotal", Academic.tsem1.getItems().get(i).getPracsTotal())
						.append("back", Academic.tsem1.getItems().get(i).getBacklog())
						.append("attended", Attendance.atsem1.getItems().get(i).getAttended())
						.append("attendedTotal", Attendance.atsem1.getItems().get(i).getTotal())
						.append("twScored", Academic.tsem1.getItems().get(i).getTermworkScored())
						.append("twTotal", Academic.tsem1.getItems().get(i).getTermworkTotal()).append("sem", sem[0]);
				acData.add(tmp);
			} else {
				Document tmp = new Document("name", Academic.tsem2.getItems().get(i - 6).getSubject())
						.append("thScored", Academic.tsem2.getItems().get(i - 6).getTheoryScored())
						.append("thTotal", Academic.tsem2.getItems().get(i - 6).getTheoryTotal())
						.append("orScored", Academic.tsem2.getItems().get(i - 6).getOralScored())
						.append("orTotal", Academic.tsem2.getItems().get(i - 6).getOralTotal())
						.append("prScored", Academic.tsem2.getItems().get(i - 6).getPracsScored())
						.append("prTotal", Academic.tsem2.getItems().get(i - 6).getPracsTotal())
						.append("back", Academic.tsem2.getItems().get(i - 6).getBacklog())
						.append("attended", Attendance.atsem2.getItems().get(i - 6).getAttended())
						.append("attendedTotal", Attendance.atsem2.getItems().get(i - 6).getTotal())
						.append("twScored", Academic.tsem2.getItems().get(i - 6).getTermworkScored())
						.append("twTotal", Academic.tsem2.getItems().get(i - 6).getTermworkTotal())
						.append("sem", sem[1]);
				acData.add(tmp);
			}

		}

		List<Document> asData = new LinkedList<>();
		for (int i = 0; i < Assignment.asList.getItems().size(); i++) {
			Document tmp = new Document("title", Assignment.asList.getItems().get(i).getTitle())
					.append("sem", Assignment.asList.getItems().get(i).getSem())
					.append("completed", Assignment.asList.getItems().get(i).getCompleted());
			asData.add(tmp);
		}

		List<Document> repData = new LinkedList<>();
		for (int i = 0; i < Personal.reportPane.getItems().size(); i++) {
			Document tmp = new Document("sem", Personal.reportPane.getItems().get(i).getSem())
					.append("seen", Personal.reportPane.getItems().get(i).getSeen())
					.append("report", Personal.reportPane.getItems().get(i).getReport());
			repData.add(tmp);
		}

		newValue.append(yr, acData).append(yr + "Assignments", asData).append("reports", repData);

		Bson query = new Document("$set", newValue);
		Engine.db.getCollection("Students").updateOne(filter, query);

		// To upload project file.

		GridFSBucket gfsBucket = GridFSBuckets.create(Engine.db, "projects");
		Project.prPath.forEach((key, val) -> {
			InputStream in = null;
			try {
				in = new FileInputStream(new File(val));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			gfsBucket.uploadFromStream(getSId() + ":" + key, in);
		});
	}

	/*
	 * This method loads the profile corresponding to the name selected in
	 * student's combo box.
	 * 
	 * @param student The student's name.
	 */
	@SuppressWarnings("unchecked")
	private void loadStudentProfile(String student) {

		String json = Engine.db.getCollection("Students").find(eq("name", student.substring(1))).first().toJson();
		JSONObject jsonData = new JSONObject(json);

		// Personal

		byte[] deci;
		BufferedImage bf = null;
		try {
			deci = Base64.getDecoder().decode(jsonData.getString("img"));

			bf = ImageIO.read(new ByteArrayInputStream(deci));
			Personal.dpImgView.setImage(SwingFXUtils.toFXImage(bf, null));

		} catch (IOException | JSONException e) {
			Personal.dpImgView.setImage(new Image(getClass().getResourceAsStream(Resources.DEFAULT_PIC.path)));

		}

		Personal.tsname.setText(jsonData.getString("name"));
		Personal.tsid.setText(jsonData.getString("sid"));
		Personal.tsrno.getSelectionModel().select(jsonData.getString("rno"));
		Personal.tsdprt.getSelectionModel().select(jsonData.getString("department"));
		Personal.tsclass.getSelectionModel().select(jsonData.getString("batch"));
		Personal.tsbatch.getSelectionModel().select(jsonData.getString("class"));
		Personal.tsmail.setText(jsonData.getString("email"));
		Personal.tsaddr.setText(jsonData.getString("address"));
		Personal.tsphone.setText(jsonData.getString("studentPhone"));
		Personal.tpphone.setText(jsonData.getString("parentPhone"));
		Components.yrlst.getItems().clear();

		Components.tscsem = jsonData.getString("current_semester");

		switch (Components.tscsem) {
		case "SEM 7":
		case "SEM 8":
			Components.yrlst.getItems().add("BE");
		case "SEM 5":
		case "SEM 6":
			Components.yrlst.getItems().add("TE");
		case "SEM 3":
		case "SEM 4":
			Components.yrlst.getItems().add("SE");
		case "SEM 1":
		case "SEM 2":
			Components.yrlst.getItems().add("FE");
		}

		Components.yrlst.getSelectionModel().selectFirst();

		// Academic

		Academic.studProgress.getData().clear();
		Academic.tsem1.setFixedCellSize(24);
		Academic.tsem1.prefHeightProperty()
				.bind(Bindings.size(Academic.tsem1.getItems()).multiply(Academic.tsem1.getFixedCellSize()).add(90));
		Academic.tsem2.setFixedCellSize(24);
		Academic.tsem2.prefHeightProperty()
				.bind(Bindings.size(Academic.tsem2.getItems()).multiply(Academic.tsem2.getFixedCellSize()).add(90));
		XYChart.Series<String, Number> data = new XYChart.Series<>();
		for (int j = 1; j <= 8; j++) {
			float p = getSemesterPercent(j);
			if (p != 0)
				data.getData().addAll(new XYChart.Data<>("Semester " + j, p));
		}

		Academic.studProgress.getData().add(data);

		// Attendance

		Attendance.atsem1.setFixedCellSize(24);
		Attendance.atsem2.setFixedCellSize(24);

		Attendance.atsem1.prefHeightProperty().bind(
				Bindings.size(Attendance.atsem1.getItems()).multiply(Attendance.atsem1.getFixedCellSize()).add(90));
		Attendance.atsem2.prefHeightProperty().bind(
				Bindings.size(Attendance.atsem2.getItems()).multiply(Attendance.atsem2.getFixedCellSize()).add(90));

		// Projects

		Engine.gfs = GridFSBuckets.create(Engine.db, "projects");

	}

	/*
	 * This method generates an ID for the student.
	 * 
	 * @return Student id.
	 */
	public static String getSId() {
		String id = dprtList.entrySet().stream()
				.filter(a -> a.getValue().equals(Personal.tsdprt.getSelectionModel().getSelectedItem()))
				.map(map -> map.getKey()).collect(Collectors.joining());
		return (id + String.format("%02d", sMatchesY(0, Components.yrlst.getValue())) + Personal.tsclass.getValue()
				+ Personal.tsrno.getValue());
	}

	/*
	 * This method calculates percentage for each subject.
	 * 
	 * @param sem Semester number.
	 * 
	 * @return percentage for that semester.
	 */

	private float getSemesterPercent(int sem) {
		String data = Engine.db.getCollection("Students").find(eq("sid", Personal.tsid.getText())).first().toJson();
		JSONObject json = new JSONObject(data);
		float percent = 0, scored, total;
		int theory = 0, oral = 0, practical = 0, termwork = 0, theoryt = 0, oralt = 0, practicalt = 0, termworkt = 0;
		if (sem == 1 || sem == 2) {
			JSONArray jsona = json.getJSONArray("fe");
			Iterator<?> it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject tmp = (JSONObject) it.next();
				if (tmp.getInt("sem") == sem) {
					theory = theory + tmp.getInt("thScored");
					theoryt = theoryt + tmp.getInt("thTotal");
					oral = oral + tmp.getInt("orScored");
					oralt = oralt + tmp.getInt("orTotal");
					practical = practical + tmp.getInt("prScored");
					practicalt = practicalt + tmp.getInt("prTotal");
					termwork = termwork + tmp.getInt("twScored");
					termworkt = termworkt + tmp.getInt("twTotal");

				}
			}
		} else if (sem == 3 || sem == 4) {
			JSONArray jsona = json.getJSONArray("se");
			Iterator<?> it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject tmp = (JSONObject) it.next();
				if (tmp.getInt("sem") == sem) {
					theory = theory + tmp.getInt("thScored");
					theoryt = theoryt + tmp.getInt("thTotal");
					oral = oral + tmp.getInt("orScored");
					oralt = oralt + tmp.getInt("orTotal");
					practical = practical + tmp.getInt("prScored");
					practicalt = practicalt + tmp.getInt("prTotal");
					termwork = termwork + tmp.getInt("twScored");
					termworkt = termworkt + tmp.getInt("twTotal");

				}
			}
		} else if (sem == 5 || sem == 6) {
			JSONArray jsona = json.getJSONArray("te");
			Iterator<?> it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject tmp = (JSONObject) it.next();
				if (tmp.getInt("sem") == sem) {
					theory = theory + tmp.getInt("thScored");
					theoryt = theoryt + tmp.getInt("thTotal");
					oral = oral + tmp.getInt("orScored");
					oralt = oralt + tmp.getInt("orTotal");
					practical = practical + tmp.getInt("prScored");
					practicalt = practicalt + tmp.getInt("prTotal");
					termwork = termwork + tmp.getInt("twScored");
					termworkt = termworkt + tmp.getInt("twTotal");

				}
			}
		} else if (sem == 7 || sem == 8) {
			JSONArray jsona = json.getJSONArray("be");
			Iterator<?> it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject tmp = (JSONObject) it.next();
				if (tmp.getInt("sem") == sem) {
					theory = theory + tmp.getInt("thScored");
					theoryt = theoryt + tmp.getInt("thTotal");
					oral = oral + tmp.getInt("orScored");
					oralt = oralt + tmp.getInt("orTotal");
					practical = practical + tmp.getInt("prScored");
					practicalt = practicalt + tmp.getInt("prTotal");
					termwork = termwork + tmp.getInt("twScored");
					termworkt = termworkt + tmp.getInt("twTotal");

				}
			}
		}
		scored = theory + oral + practical + termwork;
		total = theoryt + oralt + practicalt + termworkt;

		percent = (scored / total) * 100;

		return percent;
	}

	/*
	 * This method compares the sem value with that of year provided.
	 * 
	 * @param sem Semester Number.
	 * 
	 * @param year Year number.
	 * 
	 * @return integer value corresponding to year.
	 */
	public static int sMatchesY(int sem, String year) {
		int y = 0;
		if (year.equals("FE"))
			y = 1;
		else if (year.equals("SE"))
			y = 2;
		else if (year.equals("TE"))
			y = 3;
		else if (year.equals("BE"))
			y = 4;
		if (sem == 0)
			return y;
		if (((sem == 1 || sem == 2) && (y == 1)) || ((sem == 3 || sem == 4) && (y == 2))
				|| ((sem == 5 || sem == 6) && (y == 3)) || ((sem == 7 || sem == 8) && (y == 4)))
			return 1;
		else
			return 0;

	}

	/*
	 * This method loads the default values after creating the user interface.
	 */
	private void loadData() {

		Components.pname.setText(BasicUI.user);
		Components.pdprt.setText(
				(String) Engine.db.getCollection("Users").find(eq("user", BasicUI.user)).first().get("department"));
		Components.classIncharge = (String) Engine.db.getCollection("Users").find(eq("user", BasicUI.user)).first()
				.get("classIncharge");
		Components.pcls.setText(Components.classIncharge);

		MongoCursor<Document> cursor = Engine.db.getCollection("Students").find().iterator();
		while (cursor.hasNext()) {
			JSONObject json = new JSONObject(cursor.next().toJson());
			if ((Components.pdprt.getText().equals(json.getString("department")))
					&& (Components.pcls.getText().equals(Components.classIncharge)))
				studList.add("[" + json.getString("class") + "]" + ": " + json.getString("name"));
		}

		cursor = Engine.db.getCollection("Departments").find().iterator();
		while (cursor.hasNext()) {
			JSONObject json = new JSONObject(cursor.next().toJson());
			dprtList.put(json.getString("dprtID"), json.getString("department"));
		}

		// Update Login TimeStamp

		Engine.db.getCollection("Users").updateOne(eq("user", BasicUI.user), new Document("$set", new Document(
				"lastLogin",
				LocalDateTime.now().getDayOfMonth() + "-" + LocalDateTime.now().getMonthValue() + "-"
						+ LocalDateTime.now().getYear() + "\t" + String.format("%02d", LocalDateTime.now().getHour())
						+ "h:" + String.format("%02d", LocalDateTime.now().getMinute()) + "m:"
						+ String.format("%02d", LocalDateTime.now().getSecond()) + "s")));

		Personal.tsdprt.getItems().addAll(dprtList.values());
		for (int i = 1; i <= 200; i++) {
			Personal.tsrno.getItems().add(String.format("%03d", i));
		}
		for (int i = 1; i < 100; i++) {
			Personal.tsclass.getItems().add(String.format("%02d", i));
			Personal.tsbatch.getItems().add(String.format("%02d", i));
		}
		for (int i = 0; i < 26; i++) {
			Personal.tsbatch.getItems().add(Character.toString((char) ('A' + i)));
			Personal.tsbatch.getItems().add(Character.toString((char) ('a' + i)));
		}

		// Add students to teacher list

		List<String> name = new ArrayList<>();
		Components.counter = 0;

		studList.forEach(student -> {
			name.add(student.split(": ")[1]);
			String tmp = student.split("]")[0].substring(1);
			if (Components.classIncharge.equals(tmp)) {
				Components.slist.getItems().add(student);
				Components.counter++;
			}
			Components.nstuds.setText(Integer.toString(Components.counter));
		});

		Components.searchBox.setItems(name);
	}

	/*
	 * This method enables or disables nodes in the UI.
	 */

	private void disableAll(boolean flag) {
		Components.update.setDisable(flag);
		Components.report.setDisable(flag);
		Components.export.setDisable(flag);

		// Personal Pane

		Personal.dpImgView.setDisable(flag);
		Personal.tsname.setEditable(flag);
		Personal.tsid.setEditable(false);
		Personal.tsrno.setDisable(false);
		Personal.tsdprt.setDisable(false);
		Personal.tsclass.setDisable(false);
		Personal.tsbatch.setDisable(flag);
		Personal.tsmail.setEditable(!flag);
		Personal.tsaddr.setEditable(!flag);
		Personal.tsphone.setEditable(!flag);
		Personal.tpphone.setEditable(!flag);

		// Academic Pane

		Academic.tsem1.setEditable(!flag);
		Academic.tsem2.setEditable(!flag);
		Academic.addEntry.setDisable(flag);
		Academic.rbsem1.setDisable(flag);
		Academic.rbsem2.setDisable(flag);

		// Attendance Pane

		Attendance.atsem1.setEditable(!flag);
		Attendance.atsem2.setEditable(!flag);
		Attendance.addat.setDisable(flag);

		// Projects Pane

		Project.recycle.setDisable(flag);
		Project.prList.setDisable(flag);

		// Assignments Pane

		Assignment.asList.setEditable(!flag);
		Assignment.addAssignment.setDisable(flag);
		Assignment.removeAssignment.setDisable(flag);

	}

	/*
	 * This method is called when the user exits the application.
	 */
	private void logoutApplication() {
		Alert ex = new Alert(AlertType.CONFIRMATION);
		ex.setHeaderText("LogOut " + BasicUI.user + " - Typh™ ? ");
		ex.setTitle("Exit - Typh™");
		ex.initOwner(Components.stage);
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
	public Void call() throws Exception {
		startUI();
		BasicUI.stage.setTitle(BasicUI.stage.getTitle() + " - " + BasicUI.user);
		return null;
	}
}
