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
import javafx.geometry.HPos;
import javafx.geometry.Side;
import javafx.scene.CacheHint;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.converter.IntegerStringConverter;

public class Academic {

	public static GridPane academic;
	public static TableView<AcademicData> tsem1;
	public static TableView<AcademicData> tsem2;
	public static Button addEntry;
	public static RadioButton rbsem1;
	public static RadioButton rbsem2;
	public static LineChart<String, Number> studProgress;

	@SuppressWarnings("unchecked")
	static void setup() {
		Components.scroll[Components.paneList.length - (Components.paneCount)] = new ScrollPane();
		academic = new GridPane();
		ObservableList<AcademicData> subjects1 = FXCollections.observableArrayList();
		ObservableList<AcademicData> subjects2 = FXCollections.observableArrayList();
		ColumnConstraints accc0 = new ColumnConstraints();
		accc0.setHalignment(HPos.RIGHT);

		academic.getColumnConstraints().add(accc0);

		// Semester 1

		tsem1 = new TableView<>();

		TableColumn<AcademicData, String> sub = new TableColumn<>("Subject");
		TableColumn<AcademicData, Integer> th = new TableColumn<>("Theory");
		TableColumn<AcademicData, Integer> oral = new TableColumn<>("Oral");
		TableColumn<AcademicData, Integer> prac = new TableColumn<>("Practical");
		TableColumn<AcademicData, Integer> tw = new TableColumn<>("TermWork");
		TableColumn<AcademicData, Integer> scr0 = new TableColumn<>("Scored");
		TableColumn<AcademicData, Integer> total0 = new TableColumn<>("Total");
		TableColumn<AcademicData, Integer> scr1 = new TableColumn<>("Scored");
		TableColumn<AcademicData, Integer> total1 = new TableColumn<>("Total");
		TableColumn<AcademicData, Integer> scr2 = new TableColumn<>("Scored");
		TableColumn<AcademicData, Integer> total2 = new TableColumn<>("Total");
		TableColumn<AcademicData, Integer> scr3 = new TableColumn<>("Scored");
		TableColumn<AcademicData, Integer> total3 = new TableColumn<>("Total");
		TableColumn<AcademicData, Boolean> back = new TableColumn<>("BackLog");

		sub.setResizable(false);
		th.setResizable(false);
		oral.setResizable(false);
		prac.setResizable(false);
		tw.setResizable(false);
		scr0.setResizable(false);
		total0.setResizable(false);
		scr1.setResizable(false);
		total1.setResizable(false);
		scr2.setResizable(false);
		total2.setResizable(false);
		scr3.setResizable(false);
		total3.setResizable(false);
		back.setResizable(false);

		sub.setCellValueFactory(new PropertyValueFactory<AcademicData, String>("subject"));
		scr0.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("theoryScored"));
		scr1.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("oralScored"));
		scr2.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("pracsScored"));
		scr3.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("termworkScored"));

		total0.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("theoryTotal"));
		total1.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("oralTotal"));
		total2.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("pracsTotal"));
		total3.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("termworkTotal"));

		sub.setCellFactory(TextFieldTableCell.forTableColumn());
		scr0.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		scr1.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		scr2.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		scr3.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		total0.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		total1.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		total2.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		total3.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		sub.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setSubject(arg.getNewValue());
		});
		scr0.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr1.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr2.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr3.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total0.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total1.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total2.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total3.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		back.setCellFactory(CheckBoxTableCell.forTableColumn(back));
		back.setCellValueFactory(cvf -> cvf.getValue().backlogProperty());
		th.getColumns().addAll(scr0, total0);
		oral.getColumns().addAll(scr1, total1);
		prac.getColumns().addAll(scr2, total2);
		tw.getColumns().addAll(scr3, total3);

		tsem1.getColumns().addAll(sub, th, oral, prac, tw, back);
		tsem1.setTooltip(new Tooltip("Semester 1"));

		tsem1.setItems(subjects1);
		GridPane.setFillWidth(tsem1, true);

		// Semester 2

		tsem2 = new TableView<>();
		TableColumn<AcademicData, String> sub1 = new TableColumn<>("Subject");
		TableColumn<AcademicData, Integer> th1 = new TableColumn<>("Theory");
		TableColumn<AcademicData, Integer> oral1 = new TableColumn<>("Oral");
		TableColumn<AcademicData, Integer> prac1 = new TableColumn<>("Practical");
		TableColumn<AcademicData, Integer> tw1 = new TableColumn<>("TermWork");
		TableColumn<AcademicData, Integer> scr01 = new TableColumn<>("Scored");
		TableColumn<AcademicData, Integer> total01 = new TableColumn<>("Total");
		TableColumn<AcademicData, Integer> scr11 = new TableColumn<>("Scored");
		TableColumn<AcademicData, Integer> total11 = new TableColumn<>("Total");
		TableColumn<AcademicData, Integer> scr21 = new TableColumn<>("Scored");
		TableColumn<AcademicData, Integer> total21 = new TableColumn<>("Total");
		TableColumn<AcademicData, Integer> scr31 = new TableColumn<>("Scored");
		TableColumn<AcademicData, Integer> total31 = new TableColumn<AcademicData, Integer>("Total");
		TableColumn<AcademicData, Boolean> back1 = new TableColumn<>("BackLog");

		sub1.setResizable(false);
		th1.setResizable(false);
		oral1.setResizable(false);
		prac1.setResizable(false);
		tw1.setResizable(false);
		scr01.setResizable(false);
		total01.setResizable(false);
		scr11.setResizable(false);
		total11.setResizable(false);
		scr21.setResizable(false);
		total21.setResizable(false);
		scr31.setResizable(false);
		total31.setResizable(false);
		back1.setResizable(false);

		th1.getColumns().addAll(scr01, total01);
		oral1.getColumns().addAll(scr11, total11);
		prac1.getColumns().addAll(scr21, total21);
		tw1.getColumns().addAll(scr31, total31);
		tsem2.getColumns().addAll(sub1, th1, oral1, prac1, tw1, back1);
		tsem2.setTooltip(new Tooltip("Semester 2"));
		tsem2.setItems(subjects2);
		GridPane.setFillWidth(tsem2, true);

		sub1.setCellValueFactory(new PropertyValueFactory<AcademicData, String>("subject"));

		scr01.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("theoryScored"));
		scr11.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("oralScored"));
		scr21.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("pracsScored"));
		scr31.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("termworkScored"));

		total01.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("theoryTotal"));
		total11.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("oralTotal"));
		total21.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("pracsTotal"));
		total31.setCellValueFactory(new PropertyValueFactory<AcademicData, Integer>("termworkTotal"));

		scr01.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		scr11.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		scr21.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		scr31.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		total01.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		total11.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		total21.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		total31.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		sub1.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setSubject(arg.getNewValue());
		});
		scr01.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr11.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr21.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr31.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total01.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total11.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total21.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total31.setOnEditCommit(arg -> {
			((AcademicData) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		back1.setCellFactory(CheckBoxTableCell.forTableColumn(back1));
		back1.setCellValueFactory(cvf -> cvf.getValue().backlogProperty());

		addEntry = new Button("Add Subject");
		rbsem1 = new RadioButton("Semester 1");
		rbsem2 = new RadioButton("Semester 2");
		ToggleGroup tg = new ToggleGroup();
		tg.getToggles().addAll(rbsem1, rbsem2);
		tg.selectToggle(rbsem1);

		addEntry.setOnAction((arg0) -> {
			if (rbsem1.isSelected()) {
				subjects1.add(new AcademicData("", 0, 0, 0, 0, 0, 0, 0, 0, false));
				Attendance.atsem1Data.add(new AttendanceData("subject", 0, 0));

			} else if (rbsem2.isSelected()) {
				subjects2.add(new AcademicData("", 0, 0, 0, 0, 0, 0, 0, 0, false));
				Attendance.atsem2Data.add(new AttendanceData("subject", 0, 0));

			}
		});

		addEntry.setMaxWidth(1000);
		GridPane.setFillWidth(addEntry, true);

		tsem1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tsem2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Student Progress

		CategoryAxis xaxis = new CategoryAxis();
		xaxis.setCategories(FXCollections.observableArrayList("Semester 1", "Semester 2", "Semester 3", "Semester 4",
				"Semester 5", "Semester 6", "Semester 7", "Semester 8"));
		xaxis.setLabel("Semester");

		NumberAxis yaxis = new NumberAxis(0.0, 100.0, 10.0);
		yaxis.setLabel("Percentage");

		studProgress = new LineChart<>(xaxis, yaxis);
		studProgress.setTitle("Student Progress");
		studProgress.setTitleSide(Side.TOP);
		studProgress.setLegendVisible(false);

		academic.setId("academicP");
		studProgress.setCache(true);
		studProgress.setCacheShape(true);
		studProgress.setCacheHint(CacheHint.SPEED);

		Platform.runLater(() -> {
			academic.add(BorderTitledPane.addTitle("Semester 1", tsem1), 0, 1, 5, 1);
			academic.add(BorderTitledPane.addTitle("Semester 2", tsem2), 0, 2, 5, 1);
			academic.add(addEntry, 2, 0);
			academic.add(rbsem1, 3, 0);
			academic.add(rbsem2, 4, 0);
			academic.add(studProgress, 0, 7, 5, 1);
		

		});
		
		Components.scroll[Components.paneList.length - (Components.paneCount)].setHbarPolicy(ScrollBarPolicy.NEVER);
		Components.scroll[Components.paneList.length - (Components.paneCount--)].setContent(academic);
		
	}

	static void loadAcademicData(String year) {
		JSONArray jsona = null;
		try {
			String data = Engine.db.getCollection("Students").find(eq("sid", Personal.tsid.getText())).first().toJson();
			jsona = new JSONObject(data).getJSONArray(year.toLowerCase());
		} catch (JSONException e) {
		}
		Iterator<?> it = jsona.iterator();
		tsem1.getItems().clear();
		tsem2.getItems().clear();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			String name = json.getString("name");
			int ths = json.getInt("thScored");
			int tht = json.getInt("thTotal");
			int ors = json.getInt("orScored");
			int ort = json.getInt("orTotal");
			int prs = json.getInt("prScored");
			int prt = json.getInt("prTotal");
			int tws = json.getInt("twScored");
			int twt = json.getInt("twTotal");
			boolean back = json.getBoolean("back");
			int sem = json.getInt("sem");

			Platform.runLater(()->{
				if (sem % 2 == 1) {
					tsem1.setTooltip(new Tooltip("Semester: " + Integer.toString(sem)));
					tsem1.getItems().add(new AcademicData(name, ths, tht, ors, ort, prs, prt, tws, twt, back));
				} else {
					tsem2.setTooltip(new Tooltip("Semester: " + Integer.toString(sem)));
					tsem2.getItems().add(new AcademicData(name, ths, tht, ors, ort, prs, prt, tws, twt, back));
				}
			});
			
		}

	}

}
