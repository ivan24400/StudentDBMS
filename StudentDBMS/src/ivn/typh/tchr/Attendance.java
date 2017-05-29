package ivn.typh.tchr;

import static com.mongodb.client.model.Filters.eq;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ivn.typh.main.Engine;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.CacheHint;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.converter.IntegerStringConverter;

/*
 * This class creates the user interface for attendance pane.
 */
public class Attendance {

	public static GridPane attendance;
	public static ObservableList<AttendanceData> atsem1Data;
	public static ObservableList<AttendanceData> atsem2Data;
	public static BarChart<String, Number> atBarChart;
	public static TableView<AttendanceData> atsem1;
	public static TableView<AttendanceData> atsem2;
	public static Button addat;
	public static RadioButton atrbsem1;
	public static RadioButton atrbsem2;
	public static CategoryAxis atXaxis;
	public static NumberAxis atYaxis;

	@SuppressWarnings("unchecked")
	static void setup() {
		attendance = new GridPane();
		Components.scroll[Components.paneList.length - (Components.paneCount)] = new ScrollPane();

		addat = new Button("Add Record");
		atsem1Data = FXCollections.observableArrayList();

		atrbsem1 = new RadioButton("Semester 1");
		atrbsem1.setUserData(1);
		atrbsem2 = new RadioButton("Semester 2");
		atrbsem2.setUserData(2);
		ToggleGroup artg = new ToggleGroup();
		artg.getToggles().addAll(atrbsem1, atrbsem2);

		artg.selectedToggleProperty().addListener((obs, o, n) -> {
			loadAttendanceChart(Components.yrlst.getSelectionModel().getSelectedItem(),
					Integer.parseInt(n.getUserData().toString()));
		});

		// Semester 1 table

		atsem1 = new TableView<AttendanceData>();
		TableColumn<AttendanceData, String> atsub = new TableColumn<>("Subjects");
		TableColumn<AttendanceData, Integer> atAttended = new TableColumn<>("Attended");
		TableColumn<AttendanceData, Integer> atTotal = new TableColumn<>("Total");

		atsub.setCellValueFactory(new PropertyValueFactory<AttendanceData, String>("subject"));
		atAttended.setCellValueFactory(new PropertyValueFactory<AttendanceData, Integer>("attended"));
		atTotal.setCellValueFactory(new PropertyValueFactory<AttendanceData, Integer>("total"));

		atsub.setCellFactory(TextFieldTableCell.forTableColumn());
		atsub.setOnEditCommit(t -> {
			((AttendanceData) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setSubject(t.getNewValue());
		});

		atAttended.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		atAttended.setOnEditCommit(t -> {
			((AttendanceData) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setAttended(t.getNewValue());
		});

		atTotal.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		atTotal.setOnEditCommit(t -> {
			((AttendanceData) t.getTableView().getItems().get(t.getTablePosition().getRow())).setTotal(t.getNewValue());
		});

		atsem1.getColumns().addAll(atsub, atAttended, atTotal);
		atsem1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		atsem1.setItems(atsem1Data);

		// Semester 2 table

		atsem2 = new TableView<AttendanceData>();
		TableColumn<AttendanceData, String> atsub1 = new TableColumn<>("Subjects");
		TableColumn<AttendanceData, Integer> atAttended1 = new TableColumn<>("Attended");
		TableColumn<AttendanceData, Integer> atTotal1 = new TableColumn<>("Total");

		atsem2Data = FXCollections.observableArrayList();

		atsub1.setCellValueFactory(new PropertyValueFactory<AttendanceData, String>("subject"));
		atAttended1.setCellValueFactory(new PropertyValueFactory<AttendanceData, Integer>("attended"));
		atTotal1.setCellValueFactory(new PropertyValueFactory<AttendanceData, Integer>("total"));

		atsub1.setCellFactory(TextFieldTableCell.forTableColumn());
		atsub1.setOnEditCommit(t -> {
			((AttendanceData) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setSubject(t.getNewValue());
		});

		atAttended1.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		atAttended1.setOnEditCommit(t -> {
			((AttendanceData) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setAttended(t.getNewValue());
		});

		atTotal1.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		atTotal1.setOnEditCommit(t -> {
			((AttendanceData) t.getTableView().getItems().get(t.getTablePosition().getRow())).setTotal(t.getNewValue());
		});

		atsem2.getColumns().addAll(atsub1, atAttended1, atTotal1);
		atsem2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		atsem2.setItems(atsem2Data);

		// Attendance Graph

		atXaxis = new CategoryAxis();
		atYaxis = new NumberAxis(0.0, 100.0, 10.0);

		atYaxis.setLabel("Percentage");
		atXaxis.setLabel("Subjects");
		atBarChart = new BarChart<>(atXaxis, atYaxis);
		atBarChart.setTitle("Semester Attendance Report");
		atBarChart.setLegendVisible(false);

		attendance.setId("attendanceP");
		atBarChart.setCache(true);
		atBarChart.setCacheShape(true);
		atBarChart.setCacheHint(CacheHint.SPEED);

		Platform.runLater(() -> {
			attendance.add(BorderTitledPane.addTitle("Semester 1", atsem1), 0, 1, 3, 1);
			attendance.add(BorderTitledPane.addTitle("Semester 2", atsem2), 4, 1, 3, 1);
			attendance.add(atrbsem1, 3, 5);
			attendance.add(atrbsem2, 4, 5);
			attendance.add(atBarChart, 0, 4, 8, 1);

		});
		Components.scroll[Components.paneList.length - (Components.paneCount)].setContent(attendance);
		Components.scroll[Components.paneList.length - (Components.paneCount--)].setHbarPolicy(ScrollBarPolicy.NEVER);

	}

	/*
	 * This method creates the attendance chart.
	 */
	public static void loadAttendanceChart(String year, int semester) {
		if (year.equals("SE"))
			semester = semester + 2;
		else if (year.equals("TE"))
			semester = semester + 4;
		else if (year.equals("BE"))
			semester = semester + 6;

		String data = Engine.db.getCollection("Students").find(eq("sid", Personal.tsid.getText())).first().toJson();
		JSONArray jsona = new JSONObject(data).getJSONArray(year.toLowerCase());
		Iterator<?> it = jsona.iterator();

		XYChart.Series<String, Number> cdata = new XYChart.Series<String, Number>();
		atBarChart.getData().clear();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			if (json.getInt("sem") == semester) {
				String name = json.getString("name");
				float at = json.getInt("attended");
				float att = json.getInt("attendedTotal");
				at = (at / att) * 100;
				atXaxis.getCategories().add(name);
				cdata.getData().add(new XYChart.Data<>(name, at));
			}
		}
		Platform.runLater(() -> {
			atBarChart.getData().add(cdata);

		});
	}

	/*
	 * This methods loads the attendance table with data
	 * 
	 * @param year The academic year.
	 */
	static void loadAttendanceData(String year) {
		JSONArray jsona = null;
		try {
			String data = Engine.db.getCollection("Students").find(eq("sid", Personal.tsid.getText())).first().toJson();
			jsona = new JSONObject(data).getJSONArray(year.toLowerCase());
		} catch (JSONException e) {
		}
		Iterator<?> it = jsona.iterator();
		atsem1.getItems().clear();
		atsem2.getItems().clear();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			String name = json.getString("name");
			int at = json.getInt("attended");
			int att = json.getInt("attendedTotal");
			int sem = json.getInt("sem");

			Platform.runLater(() -> {
				if (sem % 2 == 1) {
					atsem1.setTooltip(new Tooltip("Semester: " + Integer.toString(sem)));
					atrbsem1.setText("Semester: " + Integer.toString(sem));
					atsem1.getItems().add(new AttendanceData(name, at, att));
				} else {
					atsem2.setTooltip(new Tooltip("Semester: " + Integer.toString(sem)));
					atsem2.getItems().add(new AttendanceData(name, at, att));
					atrbsem2.setText("Semester: " + Integer.toString(sem));
				}
			});

		}
	}

}
