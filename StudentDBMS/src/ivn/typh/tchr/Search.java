package ivn.typh.tchr;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ComboBox;

import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.converter.IntegerStringConverter;

import static com.mongodb.client.model.Filters.eq;

import java.awt.Toolkit;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ivn.typh.main.Engine;



public class Search extends TextField {
	private final SortedSet<String> list;
	private ContextMenu resultList;
	private TableView<Marks> tsem1,tsem2;
	private String result;	
	private Label year;
	private Label sem1;
	private Label sem2;
	private ComboBox<String> yrlst;
	private float percent = 0, scored, total;
	private JSONObject data;
	
	
	public Search() {
		super();
		yrlst = new ComboBox<>();
		year = new Label("Select Year");
		sem1 = new Label("Semester 1");
		sem2 = new Label("Semester 2");
		list = new TreeSet<>();
		resultList = new ContextMenu();
		textProperty().addListener((obs,o,n)-> {
				if (getText().length() == 0) {
					resultList.hide();
				} else {
					LinkedList<String> searchResult = new LinkedList<>();
					searchResult.addAll(list.subSet(getText(), getText() + Character.MAX_VALUE));
					if (list.size() > 0) {
						populatePopup(searchResult);
						if (!resultList.isShowing()) {
							resultList.show(Search.this, Side.BOTTOM, 0, 0);
						}
					} else {
						resultList.hide();
					}
				}
		});

		focusedProperty().addListener((obs,o,n)-> {
				resultList.hide();
		});
		
		
		yrlst.getSelectionModel().selectedItemProperty().addListener((obs,o,n)->{
			loadAcademicData();
		});
	}

	public SortedSet<String> getEntries() {
		return list;
	}

	public void setItems(List<String> items){
		list.addAll(items);
	}
	private void populatePopup(List<String> searchResult) {
		List<CustomMenuItem> menuItems = new LinkedList<>();
		int maxEntries = 10;
		int count = Math.min(searchResult.size(), maxEntries);
		for (int i = 0; i < count; i++) {
			result = searchResult.get(i);
			Label entryLabel = new Label(result);
			entryLabel.setPrefWidth(this.getWidth());
			CustomMenuItem item = new CustomMenuItem(entryLabel, true);
			item.setOnAction(action->{
					resultList.hide();
					setText(result);
					displayReport(result);
				});
			menuItems.add(item);
		}
		resultList.getItems().clear();
		resultList.getItems().addAll(menuItems);

	}

	@SuppressWarnings("unchecked")
	private void displayReport(String result) {
		Dialog<?> report = new Dialog<>();
		report.initOwner(Components.stage);
		ScrollPane spAcad = new ScrollPane();
		GridPane academic = new GridPane();
		
		ScrollPane sp1 = new ScrollPane();
		ScrollPane sp2 = new ScrollPane();
		ObservableList<Marks> subjects1 = FXCollections.observableArrayList();
		ObservableList<Marks> subjects2 = FXCollections.observableArrayList();
		ColumnConstraints accc0 = new ColumnConstraints();
		accc0.setHalignment(HPos.RIGHT);

		academic.setId("searchResultAcademic");
		report.getDialogPane().setId("searchResultPane");
		
		academic.getColumnConstraints().add(accc0);

		yrlst.getItems().clear();

		data = new JSONObject(Engine.db.getCollection("Students").find(eq("name", result)).first().toJson());

		switch (data.getString("current_semester")) {
		case "SEM 7":
		case "SEM 8":
			yrlst.getItems().add("BE");
		case "SEM 5":
		case "SEM 6":
			yrlst.getItems().add("TE");
		case "SEM 3":
		case "SEM 4":
			yrlst.getItems().add("SE");
		case "SEM 1":
		case "SEM 2":
			yrlst.getItems().add("FE");
		}

		// Semester 1

		tsem1 = new TableView<>();
		TableColumn<Marks, String> sub = new TableColumn<>("Subject");
		TableColumn<Marks, Integer> th = new TableColumn<>("Theory");
		TableColumn<Marks, Integer> oral = new TableColumn<>("Oral");
		TableColumn<Marks, Integer> prac = new TableColumn<>("Practical");
		TableColumn<Marks, Integer> tw = new TableColumn<>("TermWork");
		TableColumn<Marks, Integer> scr0 = new TableColumn<>("Scored");
		TableColumn<Marks, Integer> total0 = new TableColumn<>("Total");
		TableColumn<Marks, Integer> scr1 = new TableColumn<>("Scored");
		TableColumn<Marks, Integer> total1 = new TableColumn<>("Total");
		TableColumn<Marks, Integer> scr2 = new TableColumn<>("Scored");
		TableColumn<Marks, Integer> total2 = new TableColumn<>("Total");
		TableColumn<Marks, Integer> scr3 = new TableColumn<>("Scored");
		TableColumn<Marks, Integer> total3 = new TableColumn<>("Total");
		TableColumn<Marks, Boolean> back = new TableColumn<>("BackLog");

		sub.setCellValueFactory(new PropertyValueFactory<Marks, String>("subject"));
		scr0.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("theoryScored"));
		scr1.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("oralScored"));
		scr2.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("pracsScored"));
		scr3.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("termworkScored"));

		total0.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("theoryTotal"));
		total1.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("oralTotal"));
		total2.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("pracsTotal"));
		total3.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("termworkTotal"));

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
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow())).setSubject(arg.getNewValue());
		});
		scr0.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr1.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr2.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr3.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total0.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total1.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total2.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total3.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
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
		TableColumn<Marks, String> sub1 = new TableColumn<>("Subject");
		TableColumn<Marks, Integer> th1 = new TableColumn<>("Theory");
		TableColumn<Marks, Integer> oral1 = new TableColumn<>("Oral");
		TableColumn<Marks, Integer> prac1 = new TableColumn<>("Practical");
		TableColumn<Marks, Integer> tw1 = new TableColumn<>("TermWork");
		TableColumn<Marks, Integer> scr01 = new TableColumn<>("Scored");
		TableColumn<Marks, Integer> total01 = new TableColumn<>("Total");
		TableColumn<Marks, Integer> scr11 = new TableColumn<>("Scored");
		TableColumn<Marks, Integer> total11 = new TableColumn<>("Total");
		TableColumn<Marks, Integer> scr21 = new TableColumn<>("Scored");
		TableColumn<Marks, Integer> total21 = new TableColumn<>("Total");
		TableColumn<Marks, Integer> scr31 = new TableColumn<>("Scored");
		TableColumn<Marks, Integer> total31 = new TableColumn<Marks, Integer>("Total");
		th1.getColumns().addAll(scr01, total01);
		oral1.getColumns().addAll(scr11, total11);
		prac1.getColumns().addAll(scr21, total21);
		tw1.getColumns().addAll(scr31, total31);
		TableColumn<Marks, Boolean> back1 = new TableColumn<>("BackLog");
		tsem2.getColumns().addAll(sub1, th1, oral1, prac1, tw1, back1);
		tsem2.setTooltip(new Tooltip("Semester 2"));
		tsem2.setItems(subjects2);
		GridPane.setFillWidth(tsem2, true);
		sub1.setCellValueFactory(new PropertyValueFactory<Marks, String>("subject"));

		scr01.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("theoryScored"));
		scr11.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("oralScored"));
		scr21.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("pracsScored"));
		scr31.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("termworkScored"));

		total01.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("theoryTotal"));
		total11.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("oralTotal"));
		total21.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("pracsTotal"));
		total31.setCellValueFactory(new PropertyValueFactory<Marks, Integer>("termworkTotal"));

		scr01.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		scr11.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		scr21.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		scr31.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		total01.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		total11.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		total21.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		total31.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		sub1.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow())).setSubject(arg.getNewValue());
		});
		scr01.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr11.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr21.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		scr31.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total01.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total11.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total21.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		total31.setOnEditCommit(arg -> {
			((Marks) arg.getTableView().getItems().get(arg.getTablePosition().getRow()))
					.setTheoryScored(arg.getNewValue());
		});

		back1.setCellFactory(CheckBoxTableCell.forTableColumn(back1));
		back1.setCellValueFactory(cvf -> cvf.getValue().backlogProperty());

		tsem1.setEditable(false);
		tsem2.setEditable(false);
		tsem1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tsem2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		sp1.setContent(tsem1);
		sp2.setContent(tsem2);
		tsem1.setFixedCellSize(24);
		tsem1.prefHeightProperty()
				.bind(Bindings.size(Components.tsem2.getItems()).multiply(Components.tsem2.getFixedCellSize()).add(90));
		tsem2.setFixedCellSize(24);
		tsem2.prefHeightProperty()
				.bind(Bindings.size(Components.tsem2.getItems()).multiply(Components.tsem2.getFixedCellSize()).add(90));

		// Student Progress

		CategoryAxis xaxis = new CategoryAxis();
		xaxis.setCategories(FXCollections.observableArrayList("Semester 1", "Semester 2", "Semester 3", "Semester 4",
				"Semester 5", "Semester 6", "Semester 7", "Semester 8"));
		xaxis.setLabel("Semester");

		NumberAxis yaxis = new NumberAxis(0.0, 100.0, 10.0);
		yaxis.setLabel("Percentage");

		LineChart<String,Number> studProgress = new LineChart<>(xaxis, yaxis);
		studProgress.setTitle("Student Progress");
		studProgress.setTitleSide(Side.TOP);
		studProgress.setLegendVisible(false);
		XYChart.Series<String, Number> data = new XYChart.Series<>();
		for (int j = 1; j <= 8; j++) {
			float p = getSemesterPercent(j);
			if (p != 0)
				data.getData().addAll(new XYChart.Data<>("Semester " + j, p));
		}
		studProgress.getData().add(data);
		

		academic.add(year,1,0);
		academic.add(yrlst, 2, 0);
		academic.add(sem1, 0, 1);
		academic.add(sp1, 0, 2, 5, 1);
		academic.add(sem2, 0, 3);
		academic.add(sp2, 0, 4, 5, 1);
		academic.add(studProgress, 0, 5, 5, 1);
		
		spAcad.setContent(academic);
		spAcad.setPrefHeight(480);
		spAcad.setVbarPolicy(ScrollBarPolicy.NEVER);
		yrlst.getSelectionModel().selectFirst();

		loadAcademicData();
		report.setTitle(result.substring(0,1).toUpperCase()+result.split(" ")[0].substring(1)+result.split(" ")[1].substring(0,1).toUpperCase()+result.split(" ")[1].substring(1)+" - Academic Data - Typh™");
		report.getDialogPane().setContent(spAcad);
		report.setY(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - 240);
		report.setX(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - 320);
		report.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

		report.show();
		

	}
	
	private void loadAcademicData() {
		JSONArray jsona = null;
		try {
			jsona = data.getJSONArray(yrlst.getSelectionModel().getSelectedItem().toLowerCase());
		} catch (JSONException e) {	}
		
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

			Tooltip tool = new Tooltip();
			tool.setFont(new Font("serif", 18));
			if (sem % 2 == 1) {
				tsem1.setTooltip(new Tooltip("Semester: " + Integer.toString(sem)));
				tool.setText("Percentage :- "+getSemesterPercent(sem));
				sem1.setTooltip(tool);
				sem1.setText("Semester: " + Integer.toString(sem)+"\t[ "+Float.toString(scored)+"/"+Float.toString(total)+" ]");
				tsem1.getItems().add(new Marks(name, ths, tht, ors, ort, prs, prt, tws, twt, back));
			} else {
				tsem2.setTooltip(new Tooltip("Semester: " + Integer.toString(sem)));
				tool.setText("Percentage :- "+getSemesterPercent(sem));
				sem2.setTooltip(tool);
				sem2.setText("Semester: " + Integer.toString(sem)+"\t[ "+Float.toString(scored)+"/"+Float.toString(total)+" ]");
				tsem2.getItems().add(new Marks(name, ths, tht, ors, ort, prs, prt, tws, twt, back));
			}
		}

		

	}
	
	private float getSemesterPercent(int i) {
		String data = Engine.db.getCollection("Students").find(eq("name", result)).first().toJson();
		JSONObject json = new JSONObject(data);
		int theory = 0, oral = 0, practical = 0, termwork = 0, theoryt = 0, oralt = 0, practicalt = 0, termworkt = 0;
		if (i == 1 || i == 2) {
			JSONArray jsona = json.getJSONArray("fe");
			Iterator<?> it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject tmp = (JSONObject) it.next();
				if (tmp.getInt("sem") == i) {
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
		} else if (i == 3 || i == 4) {
			JSONArray jsona = json.getJSONArray("se");
			Iterator<?> it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject tmp = (JSONObject) it.next();
				if (tmp.getInt("sem") == i) {
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
		} else if (i == 5 || i == 6) {
			JSONArray jsona = json.getJSONArray("te");
			Iterator<?> it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject tmp = (JSONObject) it.next();
				if (tmp.getInt("sem") == i) {
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
		} else if (i == 7 || i == 8) {
			JSONArray jsona = json.getJSONArray("be");
			Iterator<?> it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject tmp = (JSONObject) it.next();
				if (tmp.getInt("sem") == i) {
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
}