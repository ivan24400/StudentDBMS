package ivn.typh.tchr;

import static com.mongodb.client.model.Filters.eq;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Iterator;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

import ivn.typh.main.BasicUI;
import ivn.typh.main.Engine;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

public class TchrUI implements Runnable {

	private Stage stage;
	private Scene scene;
	private BorderPane pane;
	private static ObservableList<String> studList;
	private Button update;
	private Button report;
	// Personal

	private ImageView dpImgView;
	private TextField tsname;
	private TextField tsid;
	private TextField tsrno;
	private ComboBox<String> tsdprt;
	private TextField tsclass;
	private TextField tsbatch;
	private TextField tsmail;
	private TextField tsaddr;
	private TextField tsphone;
	private TextField tpphone;

	// Academic

	private TableView<Marks> tsem1;
	private TableView<Marks> tsem2;
	private Button addEntry;
	private RadioButton rbsem1;
	private RadioButton rbsem2;
	private LineChart<String, Number> studProgress;

	// Attendance

	private TableView<Attendance> atsem1;
	private TableView<Attendance> atsem2;
	private Button addat;
	private RadioButton atrbsem1;
	private RadioButton atrbsem2;
	private BarChart<String, Number> atBarChart;
	private CategoryAxis atXaxis;
	private NumberAxis atYaxis;

	// Projects

	private ListView<String> prList;

	// Assignments

	private ListView<Assignment> asList;
	private Button addAssignment;
	private Button removeAssignment;

	private Label prof;
	private Label pname;
	private Label dprt;
	private Label pdprt;
	private Label cls;
	private Label pcls;
	private Label tstuds;
	private Label nstuds;

	
	public TchrUI(Stage s,BorderPane p, Scene scen) {
		scene = scen;
		stage = s;
		studList = FXCollections.observableArrayList();
		pane=p;
		Engine.gfs = GridFSBuckets.create(Engine.db, "projects");
	}

	@SuppressWarnings("unchecked")
	public void startUI() {

		GridPane tgpane = new GridPane();
		ScrollPane sctgpane = new ScrollPane();

		VBox left = new VBox();
		VBox right = new VBox();
		HBox top = new HBox();
		HBox topL = new HBox();
		GridPane center = new GridPane();

		prof = new Label("Professor");
		pname = new Label();
		dprt = new Label("Department");
		pdprt = new Label();
		cls = new Label("Class");
		pcls = new Label();
		tstuds = new Label("Total Students");
		nstuds = new Label();
		Label srch = new Label("Search");
		Search text = new Search();
		Label reports = new Label("Reports");
		Label student = new Label("Student");
		ObservableList<String> lreps = FXCollections.observableArrayList();
		ListView<String> reps = new ListView<>(lreps);
		ComboBox<String> slist = new ComboBox<>(studList);
		String[] cat = new String[] { "Personal", "Academic", "Attendance", "Projects", "Assignments" };
		TitledPane[] tp = new TitledPane[cat.length];
		Accordion accord = new Accordion();

		ToggleButton editable = new ToggleButton("Edit");
		 update = new Button("Update");
		 report = new Button("Report");

		HBox aboveAcc = new HBox();
		aboveAcc.getChildren().addAll(student, slist, editable, update, report);
		aboveAcc.setSpacing(30);
		aboveAcc.setAlignment(Pos.CENTER);

		ColumnConstraints cc0 = new ColumnConstraints();
		ColumnConstraints cc1 = new ColumnConstraints();
		ColumnConstraints cc2 = new ColumnConstraints();

		cc0.setPercentWidth(15);
		cc1.setPercentWidth(65);
		cc2.setPercentWidth(20);
		

		update.setOnAction(arg->{
			uploadData(tsid.getText());
		});
		StringBuffer reportText = new StringBuffer("Enter your report ..");

		report.setOnAction(arg -> {
			Dialog<String> dialog = new Dialog<>();
			ButtonType reportb = new ButtonType("Report", ButtonData.OK_DONE);
			dialog.setTitle("Report - Typh™");
			dialog.setHeaderText("Enter Report details");
			TextArea ta = new TextArea();
			ta.setPromptText(tsname.getText());
			ta.setText(reportText.toString());
			dialog.getDialogPane().setContent(ta);
			dialog.initOwner(stage);
			dialog.getDialogPane().getButtonTypes().add(reportb);
			dialog.setResultConverter(value -> {
				try {
					if (value.getButtonData().equals(ButtonData.OK_DONE)) {

						return ta.getText();
					}
				} catch (NullPointerException e) {
					e.getMessage();
				}
				return "";

			});

			dialog.showAndWait().ifPresent(result -> {
				reportText.append(result);
			});
			;
		});

		editable.selectedProperty().addListener((arg, o, n) -> {
			edAllFields(n);
		});

		int scrollCount = cat.length;
		ScrollPane[] scroll = new ScrollPane[scrollCount];

		slist.getSelectionModel().selectedItemProperty().addListener((arg,o,n)->{
			loadStudentProfile(n.split(":")[1]);
		});
		
		loadData();

		//
		// Personal
		//

		scroll[cat.length - (scrollCount)] = new ScrollPane();
		GridPane personal = new GridPane();
		Label sname = new Label("Name");
		Label sid = new Label("ID");
		Label srno = new Label("Roll No");
		Label sdprt = new Label("Department");
		Label sclass = new Label("Class");
		Label sbatch = new Label("Batch");
		Label smail = new Label("Email");
		Label saddr = new Label("Address");
		Label sphone = new Label("Phone");
		Label pphone = new Label("Parent Phone");

		personal.setAlignment(Pos.CENTER);
		personal.setPadding(new Insets(50));
		personal.setHgap(20);
		personal.setVgap(20);

		dpImgView = new ImageView(new Image(getClass().getResourceAsStream("raw/pic.jpg")));
		tsname = new TextField();
		tsid = new TextField();
		tsrno = new TextField();
		tsdprt = new ComboBox<>();
		tsclass = new TextField();
		tsbatch = new TextField();
		tsmail = new TextField();
		tsaddr = new TextField();
		tsphone = new TextField();
		tpphone = new TextField();

		tsname.setPromptText("Name");
		tsid.setPromptText("ID");
		tsrno.setPromptText("Roll No");
		tsdprt.setPromptText("Department");
		tsclass.setPromptText("Class");
		tsbatch.setPromptText("Batch");
		tsmail.setPromptText("Email");
		tsaddr.setPromptText("Address");
		tsphone.setPromptText("Phone");
		tpphone.setPromptText("Parent Phone");

		dpImgView.setEffect(new DropShadow());
		dpImgView.setFitHeight(128);
		dpImgView.setFitWidth(128);
		dpImgView.setOnDragOver((arg0) -> {
			Dragboard db = arg0.getDragboard();
			if (db.hasFiles()) {
				arg0.acceptTransferModes(TransferMode.COPY);
			} else {
				arg0.consume();
			}
		});

		dpImgView.setOnDragDropped((arg0) -> {
			Dragboard db = arg0.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				db.getFiles().forEach(file -> {
					dpImgView.setImage(new Image(file.toURI().toString()));
					System.out.println(file.getAbsolutePath());
				});
				success = true;
			}
			arg0.setDropCompleted(success);
			arg0.consume();
		});

		Tooltip tool = new Tooltip();
		tool.setAutoHide(true);

		tsname.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\D*")) {
				tool.setText("Enter alphabets only");
				Point2D p = tsname.localToScene(0.0, 0.0);
				tool.show(tsname, p.getX() + tsname.getCaretPosition(), p.getY() + tsname.getHeight());
				tsname.setText(n.replaceAll("[\\d]", ""));
			}
		});

		tsphone.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				System.out.println(obs.getValue() + "\t" + o + "\t" + n);
				Point2D p = tsphone.localToScene(0.0, 0.0);
				tool.setText("Enter numbers only");
				tool.show(tsphone, p.getX() + tsphone.getCaretPosition(), p.getY() + tsphone.getHeight());
				tsphone.setText(n.replaceAll("[^\\d]", ""));
			}
		});

		tpphone.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				Point2D p = tpphone.localToScene(0.0, 0.0);
				tool.setText("Enter numbers only");

				tool.show(tpphone, p.getX() + tpphone.getCaretPosition(), p.getY() + tpphone.getHeight());
				tpphone.setText(n.replaceAll("[^\\d]", ""));
			}
		});
		GridPane.setMargin(dpImgView, new Insets(40));
		personal.add(sname, 0, 1);
		personal.add(tsname, 1, 1);
		personal.add(sid, 0, 2);
		personal.add(tsid, 1, 2);
		personal.add(srno, 0, 3);
		personal.add(tsrno, 1, 3);
		personal.add(sdprt, 0, 4);
		personal.add(tsdprt, 1, 4);
		personal.add(sclass, 2, 2);
		personal.add(tsclass, 3, 2);
		personal.add(sbatch, 2, 1);
		personal.add(tsbatch, 3, 1);
		personal.add(smail, 0, 5);
		personal.add(tsmail, 1, 5);
		personal.add(saddr, 2, 3);
		personal.add(tsaddr, 3, 3);
		personal.add(sphone, 2, 4);
		personal.add(tsphone, 3, 4);
		personal.add(pphone, 2, 5);
		personal.add(tpphone, 3, 5);
		personal.add(dpImgView, 4, 1, 1, 5);
		personal.setAlignment(Pos.CENTER);

		scroll[cat.length - (scrollCount)].setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll[cat.length - (scrollCount)].setVbarPolicy(ScrollBarPolicy.NEVER);
		scroll[cat.length - (scrollCount--)].setContent(personal);

		//
		// Academic
		//

		scroll[cat.length - (scrollCount)] = new ScrollPane();
		GridPane academic = new GridPane();

		Label course = new Label("Select Year");
		ScrollPane sp1 = new ScrollPane();
		ScrollPane sp2 = new ScrollPane();
		ObservableList<Marks> subjects1 = FXCollections.observableArrayList();
		ObservableList<Marks> subjects2 = FXCollections.observableArrayList();
		ObservableList<String> yrs = FXCollections.observableArrayList();

		ComboBox<String> yrlst = new ComboBox<>(yrs);

		yrs.addAll("FE", "SE", "TE", "BE");

		ColumnConstraints accc0 = new ColumnConstraints();
		accc0.setHalignment(HPos.RIGHT);
		academic.setPadding(new Insets(30));
		academic.setHgap(20);
		academic.setVgap(20);
		academic.getColumnConstraints().add(accc0);
		
		yrlst.getSelectionModel().selectedItemProperty().addListener((obs,o,n)->{
			loadAcademicData(n);
		});

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

		sub.setCellValueFactory(new PropertyValueFactory<Marks,String>("subject"));
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
		
		sub.setOnEditCommit(arg->{
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
		back.setCellValueFactory(cvf -> cvf.getValue().getBacklog());
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
		sub1.setCellValueFactory(new PropertyValueFactory<Marks,String>("subject"));

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
	
		sub1.setOnEditCommit(arg->{
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
		back1.setCellValueFactory(cvf -> cvf.getValue().getBacklog());

		addEntry = new Button("Add Subject");
		rbsem1 = new RadioButton("Semester 1");
		rbsem2 = new RadioButton("Semester 2");
		ToggleGroup tg = new ToggleGroup();
		tg.getToggles().addAll(rbsem1, rbsem2);
		tg.selectToggle(rbsem1);

		addEntry.setOnAction((arg0) -> {
			if (rbsem1.isSelected()) {
				subjects1.add(new Marks("", 0, 0, 0, 0, 0, 0, 0, 0, false));
			} else if (rbsem2.isSelected()) {
				subjects2.add(new Marks("", 0, 0, 0, 0, 0, 0, 0, 0, false));
			}
		});

		addEntry.setMaxWidth(1000);
		GridPane.setFillWidth(addEntry, true);

		tsem1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tsem2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		sp1.setContent(tsem1);
		sp2.setContent(tsem2);

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

		academic.add(sp1, 0, 1, 5, 1);
		academic.add(sp2, 0, 2, 5, 1);
		academic.add(addEntry, 2, 0);
		academic.add(rbsem1, 3, 0);
		academic.add(rbsem2, 4, 0);
		academic.add(course, 0, 0);
		academic.add(yrlst, 1, 0);
		academic.add(studProgress, 0, 7, 5, 1);

		scroll[cat.length - (scrollCount--)].setContent(academic);

		//
		// Attendance
		//

		GridPane attendance = new GridPane();
		scroll[cat.length - (scrollCount)] = new ScrollPane();

		addat = new Button("Add Record");
		ObservableList<Attendance> atsem1Data = FXCollections.observableArrayList();
		attendance.setPadding(new Insets(30));
		attendance.setHgap(20);
		attendance.setVgap(20);
		Label atyear = new Label("Select Year");
		ComboBox<String> atyr = new ComboBox<>();
		atyr.getItems().addAll("FE", "SE", "TE", "BE");

		atrbsem1 = new RadioButton("Semester 1");
		atrbsem1.setUserData(1);
		atrbsem2 = new RadioButton("Semester 2");
		atrbsem2.setUserData(2);
		ToggleGroup artg = new ToggleGroup();
		artg.getToggles().addAll(atrbsem1, atrbsem2);
		artg.selectToggle(atrbsem1);
		
		artg.selectedToggleProperty().addListener((obs,o,n)->{
			loadAttendanceChart(atyr.getSelectionModel().getSelectedItem(),Integer.parseInt(n.getUserData().toString()));
		});

		atyr.getSelectionModel().selectedItemProperty().addListener((obs,o,n)->{
			loadAttendanceData(n);
		});
		
		// Semester 1 table

		atsem1 = new TableView<Attendance>();
		TableColumn<Attendance, String> atsub = new TableColumn<>("Subjects");
		TableColumn<Attendance, Integer> atAttended = new TableColumn<>("Attended");
		TableColumn<Attendance, Integer> atTotal = new TableColumn<>("Total");

		atsub.setCellValueFactory(new PropertyValueFactory<Attendance, String>("subject"));
		atAttended.setCellValueFactory(new PropertyValueFactory<Attendance, Integer>("attended"));
		atTotal.setCellValueFactory(new PropertyValueFactory<Attendance, Integer>("total"));

		atsub.setCellFactory(TextFieldTableCell.forTableColumn());
		atsub.setOnEditCommit(t -> {
			((Attendance) t.getTableView().getItems().get(t.getTablePosition().getRow())).setSubject(t.getNewValue());
		});

		atAttended.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		atAttended.setOnEditCommit(t -> {
			((Attendance) t.getTableView().getItems().get(t.getTablePosition().getRow())).setAttended(t.getNewValue());
		});

		atTotal.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		atTotal.setOnEditCommit(t -> {
			((Attendance) t.getTableView().getItems().get(t.getTablePosition().getRow())).setTotal(t.getNewValue());
		});

		atsem1.getColumns().addAll(atsub, atAttended, atTotal);
		atsem1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		atsem1.setItems(atsem1Data);

		// Semester 2 table

		atsem2 = new TableView<Attendance>();
		TableColumn<Attendance, String> atsub1 = new TableColumn<>("Subjects");
		TableColumn<Attendance, Integer> atAttended1 = new TableColumn<>("Attended");
		TableColumn<Attendance, Integer> atTotal1 = new TableColumn<>("Total");

		ObservableList<Attendance> atsem2Data = FXCollections.observableArrayList();

		atsub1.setCellValueFactory(new PropertyValueFactory<Attendance, String>("subject"));
		atAttended1.setCellValueFactory(new PropertyValueFactory<Attendance, Integer>("attended"));
		atTotal1.setCellValueFactory(new PropertyValueFactory<Attendance, Integer>("total"));

		atsub1.setCellFactory(TextFieldTableCell.forTableColumn());
		atsub1.setOnEditCommit(t -> {
			((Attendance) t.getTableView().getItems().get(t.getTablePosition().getRow())).setSubject(t.getNewValue());
		});

		atAttended1.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		atAttended1.setOnEditCommit(t -> {
			((Attendance) t.getTableView().getItems().get(t.getTablePosition().getRow())).setAttended(t.getNewValue());
		});

		atTotal1.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		atTotal1.setOnEditCommit(t -> {
			((Attendance) t.getTableView().getItems().get(t.getTablePosition().getRow())).setTotal(t.getNewValue());
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

		addat.setOnAction(arg -> {
			if (atrbsem1.isSelected()) {
				atsem1Data.add(new Attendance("subject", 0, 0));
			} else {
				atsem2Data.add(new Attendance("subject", 0, 0));

			}
		});

		attendance.add(atyear, 0, 0);
		attendance.add(atyr, 1, 0);
		attendance.add(atsem1, 0, 1, 3, 1);
		attendance.add(atsem2, 4, 1, 3, 1);
		attendance.add(addat, 2, 2);
		attendance.add(atrbsem1, 3, 2);
		attendance.add(atrbsem2, 4, 2);
		attendance.add(atBarChart, 0, 4, 8, 1);

		scroll[cat.length - (scrollCount)].setContent(attendance);
		scroll[cat.length - (scrollCount--)].setHbarPolicy(ScrollBarPolicy.NEVER);

		//
		// Projects
		//

		GridPane projects = new GridPane();
		scroll[cat.length - (scrollCount)] = new ScrollPane();
		Label prYr = new Label("Select Year");
		ComboBox<String> pryrlst = new ComboBox<>();
		Button upload = new Button("Upload");
		ObservableList<String> prData = FXCollections.observableArrayList("item1.rar","item2.zip","item3.7z");
		Circle bin = new Circle(20);

		
		upload.setOnAction(event->{
			FileChooser fc = new FileChooser();
			FileChooser.ExtensionFilter filter= new FileChooser.ExtensionFilter("Compressed files only","*.zip","*.rar","*.tar","*.7z","*.xz","*.gz");
			fc.getExtensionFilters().add(filter);
			File uploadFile = fc.showOpenDialog(scene.getWindow());
			prData.add(uploadFile.getName());
		});
		bin.setFill(Color.AQUA);
		pryrlst.getItems().addAll("FE", "SE", "TE", "BE");
		prList = new ListView<>();
		prList.setPrefWidth(600);
		prList.setItems(prData);
		prList.setTooltip(new Tooltip("Drag and Drop Files Over Here"));

		prList.setCellFactory((arg0) -> {

			return (new Project(scene));

		});

		bin.setOnMouseEntered(value -> {
			Tooltip tip = new Tooltip();
			tip.setAutoHide(true);
			Point2D p = bin.localToScene(0.0, 0.0);
			tip.setText("Drag projects to delete");
			tip.show(bin, p.getX(), p.getY());
		});

		bin.setOnDragOver(value -> {
			if (value.getGestureSource() != null)
				value.acceptTransferModes(TransferMode.MOVE);
		});
		bin.setOnDragDropped(value -> {
			Dragboard db = value.getDragboard();

			boolean success = false;
			if (value.getDragboard().hasString()) {
				int index = prList.getItems().indexOf(db.getString());
				prList.getItems().remove(index);
				success = true;
			}

			value.setDropCompleted(success);
			value.consume();
		});

		prList.setOnDragOver((arg0) -> {
			Dragboard db = arg0.getDragboard();
			if (db.hasFiles()) {
				arg0.acceptTransferModes(TransferMode.COPY);
			} else {
				arg0.consume();
			}
		});

		prList.setOnDragDropped((arg0) -> {
			Dragboard db = arg0.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				success = true;
				for (File file : db.getFiles()) {
					prData.add(file.getName());
					uploadProject(pryrlst.getSelectionModel().getSelectedItem(),file.getAbsolutePath(),file.getName());
				}
			}
			arg0.setDropCompleted(success);
			arg0.consume();
		});

		projects.setPadding(new Insets(30));
		projects.setHgap(20);
		projects.setVgap(20);
		projects.add(prYr, 0, 0);
		projects.add(upload, 2, 0);
		projects.add(pryrlst, 1, 0);
		projects.add(prList, 0, 1, 3, 1);
		projects.add(bin, 3, 1);

		scroll[cat.length - (scrollCount--)].setContent(projects);

		// Assignments

		scroll[cat.length - (scrollCount)] = new ScrollPane();
		GridPane assignment = new GridPane();

		assignment.setPadding(new Insets(30));
		assignment.setHgap(20);
		assignment.setVgap(20);

		Label asYr = new Label("Select Year");
		ComboBox<String> asyrlst = new ComboBox<>();

		asyrlst.getItems().addAll("FE", "SE", "TE", "BE");
		addAssignment = new Button("Add an Assignment");
		removeAssignment = new Button("Remove selected item");
		
		asList = new ListView<>();
		asList.setPrefWidth(600);
		GridPane.setFillWidth(asList, true);

		asyrlst.getSelectionModel().selectedItemProperty().addListener((obs,o,n)->{
			loadAssignmentData(n);
		});
		
		StringConverter<Assignment> converter = new StringConverter<Assignment>(){

			@Override
			public Assignment fromString(String arg0) {
				return null;
			}

			public String toString(Assignment assignment) {
				return "[Semester "+assignment.getSem()+"]\t"+assignment.getTitle();
			}


			
		};
		asList.setCellFactory(CheckBoxListCell.forListView(Assignment::completedProperty,converter));

		addAssignment.setOnAction(arg0 -> {
			Dialog<Assignment> dialog = new Dialog<>();
			TextField asTitle = new TextField();
			ComboBox<String> asYear = new ComboBox<>();
			ButtonType add = new ButtonType("Add", ButtonData.OK_DONE);
			HBox pane = new HBox();
			pane.setPadding(new Insets(30));

			asYear.getItems().addAll("Semester 1","Semester 2","Semester 3","Semester 4","Semester 5","Semester 6","Semester 7","Semester 8");
			pane.getChildren().addAll(asYear,asTitle);
			dialog.setTitle("Assignments - Typh™");
			dialog.setHeaderText("Enter assignment title");
			asTitle.setPromptText("Enter title");
			dialog.getDialogPane().setContent(pane);
			dialog.getDialogPane().getButtonTypes().addAll(add, ButtonType.CANCEL);

			Node addNode = dialog.getDialogPane().lookupButton(add);
			addNode.setDisable(true);

			asTitle.textProperty().addListener((obs, o, n) -> {
				addNode.setDisable(n.trim().isEmpty());
			});

			dialog.setResultConverter(value -> {
				if (value.getButtonData().equals(ButtonData.OK_DONE) && !asTitle.getText().isEmpty()) {
					return new Assignment(asYear.getSelectionModel().getSelectedIndex()+1,asTitle.getText(),false);
				} else if (value.getButtonData().equals(ButtonData.OK_DONE) && asTitle.getText().isEmpty()) {

				}
				return null;
			});

			asTitle.setPrefWidth(500);

			dialog.initOwner(stage);
			Optional<Assignment> result = dialog.showAndWait();
			result.ifPresent(arg -> asList.getItems().add(arg));
		});

		removeAssignment.setTooltip(new Tooltip("Deletes last assignment by default"));
		removeAssignment.setOnAction(value -> {
			asList.getSelectionModel().select(asList.getItems().size() - 1);
			int index = asList.getSelectionModel().getSelectedIndex();
			asList.getItems().remove(index);
					
		});

		assignment.add(asYr, 0, 0);
		assignment.add(asyrlst, 1, 0);
		assignment.add(addAssignment, 2, 0);
		assignment.add(removeAssignment, 3, 0);
		assignment.add(asList, 0, 1, 4, 1);

		scroll[cat.length - (scrollCount)].setContent(assignment);
		scroll[cat.length - (scrollCount--)].setHbarPolicy(ScrollBarPolicy.NEVER);

		//
		// Adding all titled panes to the accordion
		//

		for (int i = 0; i < cat.length; i++) {
			tp[i] = new TitledPane(cat[i], scroll[i]);
		}

		accord.getPanes().addAll(tp);
		accord.setExpandedPane(tp[0]);

		slist.setPrefWidth(150);
		topL.setAlignment(Pos.CENTER);
		center.setHgap(20);
		center.setVgap(20);
		center.setPadding(new Insets(30));
		tgpane.setPadding(new Insets(30));
		tgpane.getColumnConstraints().addAll(cc0, cc1, cc2);

		right.setPadding(new Insets(30));
		topL.getChildren().add(prof);
		left.getChildren().addAll(pname, dprt, pdprt, cls, pcls, tstuds, nstuds);

		top.getChildren().addAll(srch, text);
		top.setAlignment(Pos.CENTER);
		top.setSpacing(20);

		center.add(aboveAcc, 0, 0);
		center.add(accord, 0, 1, 3, 1);

		right.getChildren().addAll(reports, reps);

		tgpane.add(topL, 0, 0, 1, 2);
		tgpane.add(left, 0, 2, 1, 1);
		tgpane.add(top, 1, 0);
		tgpane.add(center, 1, 1, 1, 2);
		tgpane.add(right, 2, 0, 1, 2);

		tgpane.setMaxSize(Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
				Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		sctgpane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sctgpane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sctgpane.setContent(tgpane);

		pane.setCenter(sctgpane);
	}

	private void uploadData(String sid) {
		Bson filter = new Document("sid",sid);
		Bson newValue = new Document("name",tsname.getText()).append("rno",tsrno.getText()).append("batch",tsbatch.getText()).append("class",tsclass.getText()).append("email",tsmail.getText()).append("address",tsaddr.getText())
		.append("studentPhone",tsphone.getText()).append("parentPhone",tpphone.getText()).append("department",tsdprt.getSelectionModel().getSelectedItem());
		Bson query = new Document("$set",newValue);
		Engine.db.getCollection("Students").updateOne(filter, query);
		
		GridFSBucket gfsBucket = GridFSBuckets.create(Engine.db,"projects");
		prList.getItems().forEach(action->{
			try {
				InputStream in = new FileInputStream(new File(action));
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void loadStudentProfile(String student) {
		
		String json = Engine.db.getCollection("Students").find(eq("name",student.substring(1))).first().toJson();
		JSONObject jsonData = new JSONObject (json);
		byte[] deci = Base64.getDecoder().decode(jsonData.getString("img"));	
		BufferedImage bf = null;
		try {
			bf = ImageIO.read(new ByteArrayInputStream(deci));
		} catch (IOException e) {
			e.printStackTrace();
		}
		dpImgView.setImage(SwingFXUtils.toFXImage(bf, null));
		tsname.setText(jsonData.getString("name"));           
		tsid.setText(jsonData.getString("sid"));            
		tsrno.setText(jsonData.getString("rno"));           
		tsdprt.getSelectionModel().select(jsonData.getString("department"));     
		tsclass.setText(jsonData.getString("batch" ));        
		tsbatch.setText(jsonData.getString("class"));          
		tsmail.setText(jsonData.getString("email"));          
		tsaddr.setText(jsonData.getString("address"));          
		tsphone.setText(jsonData.getString( "studentPhone"));  
		tpphone.setText(jsonData.getString("parentPhone"));  
		studProgress.getData().clear();
		XYChart.Series<String, Number> data = new XYChart.Series<>();
		for(int j=0;j<8;j++){
			float p = getSemesterPercent(j);
			if(p!=0)
				data.getData().addAll(new XYChart.Data<>("Semester "+(j+1),p));
		}
		studProgress.getData().add(data);		
		
		
		GridFSBucket gfs = GridFSBuckets.create(Engine.db, "projects");
		gfs.find().forEach(new Block<GridFSFile>(){
			public void apply(final GridFSFile file){
				prList.getItems().add(file.getFilename().split("-")[1]);
			}
		});
	}


	private void loadAttendanceChart(String year,int semester) {
		if(year.equals("SE"))
			semester=semester+2;
		else if(year.equals("TE"))
			semester=semester+4;
		else if(year.equals("BE"))
			semester=semester+6;
		
		String data = Engine.db.getCollection("Students").find(eq("sid",tsid.getText())).first().toJson();
		JSONArray jsona = new JSONObject(data).getJSONArray(year.toLowerCase());
		Iterator<?> it = jsona.iterator();
		
		atBarChart.getData().clear();
		XYChart.Series<String,Number> cdata = new XYChart.Series<String, Number>();
		while(it.hasNext()){
			JSONObject json = (JSONObject) it.next();
			if(json.getInt("sem")==semester){
				String name = json.getString("name");
				float at = json.getInt("attended");
				float att = json.getInt("attendedTotal");
				at=(at/att)*100;
				atXaxis.getCategories().add(name);
				cdata.getData().add(new XYChart.Data<>(name,at));
			}
		}
		atBarChart.getData().add(cdata);
	}

	private float getSemesterPercent(int i) {
		String data = Engine.db.getCollection("Students").find(eq("sid",tsid.getText())).first().toJson();
		JSONObject json = new JSONObject(data);
		float percent = 0,scored,total;
		int theory = 0,oral = 0,practical = 0,termwork = 0,theoryt = 0,oralt = 0,practicalt = 0,termworkt = 0;
		if(i==1 || i==2){
			JSONArray jsona = json.getJSONArray("fe");
			Iterator<?> it = jsona.iterator();
			while(it.hasNext()){
				JSONObject tmp = (JSONObject) it.next();
				if(tmp.getInt("sem")==i){
					theory=theory+tmp.getInt("thScored");
					theoryt=theoryt+tmp.getInt("thTotal");
					oral=oral+tmp.getInt("orScored");
					oralt=oralt+tmp.getInt("orTotal");
					practical = practical+tmp.getInt("prScored");
					practicalt = practicalt+tmp.getInt("prTotal");
					termwork = termwork +tmp.getInt("twScored");
					termworkt = termworkt +tmp.getInt("twTotal");
					
				}
			}
		}else if(i==3 || i==4){
			JSONArray jsona = json.getJSONArray("se");
			Iterator<?> it = jsona.iterator();
			while(it.hasNext()){
				JSONObject tmp = (JSONObject) it.next();
				if(tmp.getInt("sem")==i){
					theory=theory+tmp.getInt("thScored");
					theoryt=theoryt+tmp.getInt("thTotal");
					oral=oral+tmp.getInt("orScored");
					oralt=oralt+tmp.getInt("orTotal");
					practical = practical+tmp.getInt("prScored");
					practicalt = practicalt+tmp.getInt("prTotal");
					termwork = termwork +tmp.getInt("twScored");
					termworkt = termworkt +tmp.getInt("twTotal");
					
				}
			}
		}else if(i==5 || i==6){
			JSONArray jsona = json.getJSONArray("te");
			Iterator<?> it = jsona.iterator();
			while(it.hasNext()){
				JSONObject tmp = (JSONObject) it.next();
				if(tmp.getInt("sem")==i){
					theory=theory+tmp.getInt("thScored");
					theoryt=theoryt+tmp.getInt("thTotal");
					oral=oral+tmp.getInt("orScored");
					oralt=oralt+tmp.getInt("orTotal");
					practical = practical+tmp.getInt("prScored");
					practicalt = practicalt+tmp.getInt("prTotal");
					termwork = termwork +tmp.getInt("twScored");
					termworkt = termworkt +tmp.getInt("twTotal");
					
				}
			}
		}else if(i==7 || i==8){
			JSONArray jsona = json.getJSONArray("be");
			Iterator<?> it = jsona.iterator();
			while(it.hasNext()){
				JSONObject tmp = (JSONObject) it.next();
				if(tmp.getInt("sem")==i){
					theory=theory+tmp.getInt("thScored");
					theoryt=theoryt+tmp.getInt("thTotal");
					oral=oral+tmp.getInt("orScored");
					oralt=oralt+tmp.getInt("orTotal");
					practical = practical+tmp.getInt("prScored");
					practicalt = practicalt+tmp.getInt("prTotal");
					termwork = termwork +tmp.getInt("twScored");
					termworkt = termworkt +tmp.getInt("twTotal");
					
				}
			}
		}
		scored = theory+oral+practical+termwork;
		total = theoryt+oralt+practicalt+termworkt;

		percent=(scored/total)*100;

		return percent;
	}
	
	
	private void uploadProject(String year,String absolutePath,String name) {
		GridFS gfs = new GridFS((DB) Engine.db,"projects");
		GridFSInputFile gip = gfs.createFile(absolutePath);
		gip.setFilename(year+":"+name);
		gip.save();
	}

	private void loadAssignmentData(String n) {
		String data = Engine.db.getCollection("Students").find(eq("sid",tsid.getText())).first().toJson();
		JSONArray jsona = new JSONObject(data).getJSONArray(n.toLowerCase()+"Assignments");
		asList.getItems().clear();
		Iterator<?> it = jsona.iterator();
		while(it.hasNext()){
			JSONObject json = (JSONObject) it.next();
			String title = json.getString("title");
			int semester = json.getInt("sem");
			boolean flag = json.getBoolean("completed");
			asList.getItems().add(new Assignment(semester,title,flag));
		}
	}

	private void loadAttendanceData(String n) {
		String data = Engine.db.getCollection("Students").find(eq("sid",tsid.getText())).first().toJson();
		JSONArray jsona = new JSONObject(data).getJSONArray(n.toLowerCase());
		Iterator<?> it = jsona.iterator();
		atsem1.getItems().clear();
		atsem2.getItems().clear();		
		while(it.hasNext()){
			JSONObject json = (JSONObject) it.next();
			String name = json.getString("name");
			int at = json.getInt("attended");
			int att = json.getInt("attendedTotal");
			int sem = json.getInt("sem");
			if(sem % 2 ==1){
				atsem1.setTooltip(new Tooltip("Semester: "+Integer.toString(sem)));
				atsem1.getItems().add(new Attendance(name,at,att));
			}else{
				atsem2.setTooltip(new Tooltip("Semester: "+Integer.toString(sem)));
				atsem2.getItems().add(new Attendance(name,at,att));

			}
		}
	}

	private void loadAcademicData(String n) {
		String data = Engine.db.getCollection("Students").find(eq("sid",tsid.getText())).first().toJson();
		JSONArray jsona = new JSONObject(data).getJSONArray(n.toLowerCase());
		Iterator<?> it = jsona.iterator();
		tsem1.getItems().clear();
		tsem2.getItems().clear();
		while(it.hasNext()){
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

			if(sem % 2 == 1){
				tsem1.setTooltip(new Tooltip("Semester: "+Integer.toString(sem)));
				tsem1.getItems().add(new Marks(name,ths,tht, ors,ort ,prs ,prt,tws , twt, back));
			}else{
				tsem2.setTooltip(new Tooltip("Semester: "+Integer.toString(sem)));
				tsem2.getItems().add(new Marks(name,ths,tht, ors,ort ,prs ,prt,tws , twt, back));
			}
		}
		
	}

	
	private void loadData() {
		pname.setText(BasicUI.user);
		pdprt.setText((String) Engine.db.getCollection("Users").find(eq("user",BasicUI.user)).first().get("department"));
		
		MongoCursor<Document> cursor = Engine.db.getCollection("Students").find().iterator();
		while(cursor.hasNext()){
			JSONObject json = new JSONObject(cursor.next().toJson());
			studList.add("["+json.getString("class")+"]"+": "+json.getString("name"));
		}
		 
		Engine.db.getCollection("Users").updateOne(eq("user",BasicUI.user), new Document("$set",new Document("lastLogin",LocalDateTime.now().getDayOfMonth()+"-"+LocalDateTime.now().getMonthValue()+"-"+LocalDateTime.now().getYear()+"\t"+LocalDateTime.now().getHour()+":"+LocalDateTime.now().getMinute()+":"+LocalDateTime.now().getSecond())));

	}

	private void edAllFields(boolean flag) {

		update.setDisable(!flag);
		report.setDisable(!flag);
		// Personal Pane

		dpImgView.setDisable(!flag);
		tsname.setEditable(flag);
		tsname.setFocusTraversable(flag);
		tsid.setEditable(flag);
		tsid.setFocusTraversable(flag);
		tsrno.setEditable(flag);
		tsrno.setFocusTraversable(flag);
		tsdprt.setDisable(!flag);
		tsdprt.setFocusTraversable(flag);
		tsclass.setEditable(flag);
		tsclass.setFocusTraversable(flag);
		tsbatch.setEditable(flag);
		tsbatch.setFocusTraversable(flag);
		tsmail.setEditable(flag);
		tsmail.setFocusTraversable(flag);
		tsaddr.setEditable(flag);
		tsaddr.setFocusTraversable(flag);
		tsphone.setEditable(flag);
		tsphone.setFocusTraversable(flag);
		tpphone.setEditable(flag);
		tpphone.setFocusTraversable(flag);

		// Academic Pane

		tsem1.setEditable(flag);
		tsem2.setEditable(flag);
		addEntry.setDisable(!flag);

		// Attendance Pane

		atsem1.setEditable(flag);
		atsem2.setEditable(flag);
		addat.setDisable(!flag);

		// Projects Pane
		prList.setEditable(flag);

		// Assignments Pane

		asList.setEditable(flag);
		addAssignment.setDisable(!flag);
		removeAssignment.setDisable(!flag);

	}

	public static class Assignment {

		private final  IntegerProperty sem = new SimpleIntegerProperty();
		private final  StringProperty title = new SimpleStringProperty();
		private final  BooleanProperty completed = new SimpleBooleanProperty();
		
		public Assignment(int s,String t,boolean c){
			setSem(s);
			setTitle(t);
			setCompleted(c);
		}
		

		public  int getSem() {
			return sem.get();
		}

		public  String getTitle() {
			return title.get();
		}

		public  boolean getCompleted() {
			return completed.get();
		}

		public  StringProperty titleProperty(){
			return title;
		}
		
		public  BooleanProperty completedProperty(){
			return completed;
		}
		
		public  void setSem(int y) {
			sem.set(y);
		}

		public  void setTitle(String t) {
			title.set(t);
		}

		public  void setCompleted(boolean c) {
			completed.set(c);
		}




	}

	
	@Override
	public void run() {
		Platform.runLater(() -> {
			startUI();
		});
	}
}
