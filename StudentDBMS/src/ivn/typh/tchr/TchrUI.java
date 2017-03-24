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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
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

import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

import ivn.typh.admin.SideBar;
import ivn.typh.main.BasicUI;
import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.control.MenuItem;
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
import javafx.scene.control.ToolBar;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

public class TchrUI implements Runnable {

	private static ObservableList<String> studList;
	private static ObservableMap<String, String> dprtList;
	private static ComboBox<String> yrlst;

	public TchrUI(Stage s, BorderPane p, Scene scen, ToolBar tb) {
		
		studList = FXCollections.observableArrayList();
		dprtList = FXCollections.observableHashMap();
		Engine.gfs = GridFSBuckets.create(Engine.db, "projects");
		
		Components.mb = tb;
		Components.pane = p;
		Components.stage = s;
		Components.scene = scen;
		Components.menu = new Button("Menu");
		Components.repList = FXCollections.observableArrayList();

	}

	@SuppressWarnings("unchecked")
	public void startUI() {

		GridPane tgpane = new GridPane();
		ScrollPane sctgpane = new ScrollPane();
		StackPane spMain = new StackPane();

		GridPane center = new GridPane();
		VBox left = new VBox();
		HBox top = new HBox();
		HBox topL = new HBox();
		HBox aboveAcc = new HBox();
		
		tgpane.setId("home");
		center.setId("center");
		left.setId("leftP");
		top.setId("topP");
		topL.setId("toplP");
		aboveAcc.setId("acP");

		String[] cat = new String[] { "Personal", "Academic", "Attendance", "Projects", "Assignments" };
		Button logout = new Button("Log Out");
		ToggleButton editable = new ToggleButton("Edit");

	
		Pane dummy = new Pane();

		SideBar side = new SideBar(dummy,Components.menu);
		side.setMenuWidth(300);
		side.getStyleClass().add(".sideBarButton");
		Components.menu.setGraphic(new ImageView(new Image("/ivn/typh/main/icons/menu.png")));

		Components.pname = new Label();
		Components.dprt = new Label("Department:");
		Components.pdprt = new Label();
		Components.cls = new Label("Class:");
		Components.pcls = new Label();
		Components.tstuds = new Label("Total Students:");
		Components.nstuds = new Label();
		
		Components.pdprt.setId("logInfo");
		Components.pcls.setId("logInfo");
		Components.nstuds.setId("logInfo");
		
		Components.reps = new ListView<>();
		Components.slist = new ComboBox<>();
		Components.srch = new Label("Search");
		Components.searchBox = new Search();
		Components.reports = new Label("Reports");
		Components.student = new Label("Student");
		Components.tp = new TitledPane[cat.length];
		Components.accord = new Accordion();
		Components.update = new Button("Update");
		Components.report = new Button("Report");
		Components.tsyear = new ChoiceBox<>();

		yrlst = new ComboBox<>();
		yrlst.getItems().addAll(FXCollections.observableArrayList("FE", "SE", "TE", "BE"));
		yrlst.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
			loadReport(n);
			loadAcademicData(n);
			loadAttendanceData(n);
			loadProjectData(n);
			loadAssignmentData(n);
		});
		
		Components.tsyear.getItems().addAll(yrlst.getItems());

		//		Start the heartbeat
		
		Thread pulse = new Thread(new HeartBeat());
		pulse.start();
		
		//		Logout Action
		
		logout.setOnAction(arg -> {
			logoutApplication();
		});

		Components.update.setOnAction(arg -> {
			uploadData(Components.tsid.getText());
		});

		
		editable.selectedProperty().addListener((arg, o, n) -> {
			disableAll(!n);
		});

		
		int scrollCount = cat.length;
		ScrollPane[] scroll = new ScrollPane[scrollCount];

		Components.slist.getSelectionModel().selectedItemProperty().addListener((arg, o, n) -> {
			loadStudentProfile(n.split(":")[1]);
		});

		// 		Search Box 
		
		Components.srch.setId("search");
		Components.searchBox.setId("searchBox");
		
		//		Export Data
		
		Components.export = new Button("Export");
		Components.export.setOnAction(arg->{
			Export.export();
		});
		
		aboveAcc.getChildren().addAll(Components.student, Components.slist, new Label("Select Year"), yrlst, editable, Components.update, Components.report, Components.export);

		
		//
		// Personal
		//

		scroll[cat.length - (scrollCount)] = new ScrollPane();
		GridPane personal = new GridPane();
		personal.setId("personalP");
		
		Label sname = new Label("Name:");
		Label sid = new Label("ID:");
		Label srno = new Label("Roll No:");
		Label sdprt = new Label("Department:");
		Label sclass = new Label("Class:");
		Label sbatch = new Label("Batch:");
		Label smail = new Label("Email:");
		Label saddr = new Label("Address:");
		Label sphone = new Label("Phone:");
		Label pphone = new Label("Parent Phone:");


		Components.dpImgView = new ImageView(new Image(getClass().getResourceAsStream("/ivn/typh/main/raw/pic.jpg")));
		Components.dpImgView.setId("dpImgView");
		Components.tsname = new TextField();
		Components.tsid = new TextField();
		Components.tsrno = new ChoiceBox<>();
		Components.tsdprt = new ChoiceBox<>();
		Components.tsclass = new ChoiceBox<>();
		Components.tsbatch = new ChoiceBox<>();
		Components.tsmail = new TextField();
		Components.tsaddr = new TextField();
		Components.tsphone = new TextField();
		Components.tpphone = new TextField();

		Components.tsname.setPromptText("Name");
		Components.tsid.setPromptText("ID");
		Components.tsmail.setPromptText("Email");
		Components.tsaddr.setPromptText("Address");
		Components.tsphone.setPromptText("Phone");
		Components.tpphone.setPromptText("Parent Phone");


		Components.dpImgView.setFitHeight(128);
		Components.dpImgView.setFitWidth(128);
		Components.dpImgView.setOnDragOver((arg0) -> {
			Dragboard db = arg0.getDragboard();
			if (db.hasFiles()) {
				arg0.acceptTransferModes(TransferMode.COPY);
			} else {
				arg0.consume();
			}
		});

		Components.dpImgView.setOnDragDropped((arg0) -> {
			Dragboard db = arg0.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				db.getFiles().forEach(file -> {
					Components.dpImgView.setImage(new Image(file.toURI().toString()));
					System.out.println(file.getAbsolutePath());
				});
				success = true;
			}
			arg0.setDropCompleted(success);
			arg0.consume();
		});

		Tooltip tool = new Tooltip();
		tool.setAutoHide(true);

		Components.tsname.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\D*")) {
				tool.setText("Enter alphabets only");
				Point2D p = Components.tsname.localToScene(0.0, 0.0);
				tool.show(Components.tsname, p.getX() + Components.tsname.getCaretPosition(), p.getY() + Components.tsname.getHeight());
				Components.tsname.setText(n.replaceAll("[\\d]", ""));
			}
		});

		Components.tsphone.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				System.out.println(obs.getValue() + "\t" + o + "\t" + n);
				Point2D p = Components.tsphone.localToScene(0.0, 0.0);
				tool.setText("Enter numbers only");
				tool.show(Components.tsphone, p.getX() + Components.tsphone.getCaretPosition(), p.getY() + Components.tsphone.getHeight());
				Components.tsphone.setText(n.replaceAll("[^\\d]", ""));
			}
		});

		Components.tpphone.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				Point2D p = Components.tpphone.localToScene(0.0, 0.0);
				tool.setText("Enter numbers only");

				tool.show(Components.tpphone, p.getX() + Components.tpphone.getCaretPosition(), p.getY() + Components.tpphone.getHeight());
				Components.tpphone.setText(n.replaceAll("[^\\d]", ""));
			}
		});

		ContextMenu tsidcm = new ContextMenu();
		MenuItem tsida = new MenuItem("Generate ID");
		tsida.setOnAction(arg -> {

					Components.tsid.setText(getSId());
		});
		ScrollPane spReport = new ScrollPane();
		ContextMenu repcm = new ContextMenu();
		MenuItem del = new MenuItem("Delete this report");
		del.setOnAction(arg -> {
			Components.repList.remove(Components.reps.getSelectionModel().getSelectedIndex());
		});
		repcm.getItems().add(del);

		Components.reps.setContextMenu(repcm);

		Components.reps.getSelectionModel().selectLast();

		StringConverter<Report> rconvertor = new StringConverter<Report>() {
			@Override
			public Report fromString(String arg0) {
				return null;
			}

			@Override
			public String toString(Report arg) {
				return "[Semester " + arg.getSem() + "]\t" + arg.getReport();
			}

		};
		Components.reps.setCellFactory(CheckBoxListCell.forListView(Report::seenProperty, rconvertor));
		Components.report.setOnAction(arg -> {
			Dialog<String> dialog = new Dialog<>();
			ButtonType reportb = new ButtonType("Report", ButtonData.OK_DONE);
			dialog.setTitle("Report - Typh™");
			dialog.setHeaderText("Enter Report details");
			VBox vb = new VBox();
			vb.setId("reportDialog");
			TextArea ta = new TextArea();
			ComboBox<String> sem = new ComboBox<>();
			sem.getItems().addAll("Semester 1","Semester 2", "Semester 3","Semester 4","Semester 5","Semester 6","Semester 7","Semester 8");
			sem.getSelectionModel().selectFirst();
			ta.setPromptText(Components.tsname.getText());
			ta.setPromptText("Enter your report details ...");
			
			vb.getChildren().addAll(sem,ta);
			dialog.getDialogPane().setContent(vb);
			dialog.initOwner(Components.stage);
			dialog.getDialogPane().getButtonTypes().add(reportb);
			dialog.setResultConverter(value -> {
				try {
					if (value.getButtonData().equals(ButtonData.OK_DONE)) {

						Components.repList.add(ta.getText());
					}
				} catch (NullPointerException e) {
					e.getMessage();
				}
				return "";

			});

			dialog.show();
		});
		
		tsidcm.getItems().add(tsida);
		Components.tsid.setContextMenu(tsidcm);
		GridPane.setMargin(Components.dpImgView, new Insets(40));
		spReport.setContent(Components.reps);
		GridPane.setFillWidth(Components.reps, true);
		Components.reps.setPrefWidth(600);
		Components.reps.setPrefHeight(150);
		spReport.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spReport.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		personal.add(sname, 0, 1);
		personal.add(Components.tsname, 1, 1);
		personal.add(sid, 0, 2);
		personal.add(Components.tsid, 1, 2);
		personal.add(srno, 0, 3);
		personal.add(Components.tsrno, 1, 3);
		personal.add(sdprt, 0, 4);
		personal.add(Components.tsdprt, 1, 4);
		personal.add(sclass, 2, 2);
		personal.add(Components.tsclass, 3, 2);
		personal.add(sbatch, 2, 1);
		personal.add(Components.tsbatch, 3, 1);
		personal.add(smail, 0, 5);
		personal.add(Components.tsmail, 1, 5);
		personal.add(saddr, 2, 3);
		personal.add(Components.tsaddr, 3, 3);
		personal.add(sphone, 2, 4);
		personal.add(Components.tsphone, 3, 4);
		personal.add(pphone, 2, 5);
		personal.add(Components.tpphone, 3, 5);
		personal.add(Components.dpImgView, 4, 1, 1, 5);
		personal.add(Components.reports, 0, 6);
		personal.add(spReport, 0, 7, 5, 1);
		personal.setAlignment(Pos.CENTER);

		scroll[cat.length - (scrollCount)].setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll[cat.length - (scrollCount--)].setContent(personal);

		loadData();


		//
		// Academic
		//

		scroll[cat.length - (scrollCount)] = new ScrollPane();
		GridPane academic = new GridPane();
		academic.setId("academicP");
			ObservableList<Marks> subjects1 = FXCollections.observableArrayList();
		ObservableList<Marks> subjects2 = FXCollections.observableArrayList();
		ColumnConstraints accc0 = new ColumnConstraints();
		accc0.setHalignment(HPos.RIGHT);
		
		academic.getColumnConstraints().add(accc0);

		// Semester 1

		Components.tsem1 = new TableView<>();
		
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

		Components.tsem1.getColumns().addAll(sub, th, oral, prac, tw, back);
		Components.tsem1.setTooltip(new Tooltip("Semester 1"));
		
		Components.tsem1.setItems(subjects1);
		GridPane.setFillWidth(Components.tsem1, true);

		// Semester 2

		Components.tsem2 = new TableView<>();
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
		TableColumn<Marks, Boolean> back1 = new TableColumn<>("BackLog");

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
		Components.tsem2.getColumns().addAll(sub1, th1, oral1, prac1, tw1, back1);
		Components.tsem2.setTooltip(new Tooltip("Semester 2"));
		Components.tsem2.setItems(subjects2);
		GridPane.setFillWidth(Components.tsem2, true);
		
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

		Components.addEntry = new Button("Add Subject");
		Components.rbsem1 = new RadioButton("Semester 1");
		Components.rbsem2 = new RadioButton("Semester 2");
		ToggleGroup tg = new ToggleGroup();
		tg.getToggles().addAll(Components.rbsem1, Components.rbsem2);
		tg.selectToggle(Components.rbsem1);

		Components.addEntry.setOnAction((arg0) -> {
			if (Components.rbsem1.isSelected()) {
				subjects1.add(new Marks("", 0, 0, 0, 0, 0, 0, 0, 0, false));
			} else if (Components.rbsem2.isSelected()) {
				subjects2.add(new Marks("", 0, 0, 0, 0, 0, 0, 0, 0, false));
			}
		});

		Components.addEntry.setMaxWidth(1000);
		GridPane.setFillWidth(Components.addEntry, true);

		Components.tsem1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		Components.tsem2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Student Progress

		CategoryAxis xaxis = new CategoryAxis();
		xaxis.setCategories(FXCollections.observableArrayList("Semester 1", "Semester 2", "Semester 3", "Semester 4",
				"Semester 5", "Semester 6", "Semester 7", "Semester 8"));
		xaxis.setLabel("Semester");

		NumberAxis yaxis = new NumberAxis(0.0, 100.0, 10.0);
		yaxis.setLabel("Percentage");

		Components.studProgress = new LineChart<>(xaxis, yaxis);
		Components.studProgress.setTitle("Student Progress");
		Components.studProgress.setTitleSide(Side.TOP);
		Components.studProgress.setLegendVisible(false);

		academic.add(Components.tsem1, 0, 1, 5, 1);
		academic.add(Components.tsem2, 0, 2, 5, 1);
		academic.add(Components.addEntry, 2, 0);
		academic.add(Components.rbsem1, 3, 0);
		academic.add(Components.rbsem2, 4, 0);
		academic.add(Components.studProgress, 0, 7, 5, 1);
		
		scroll[cat.length - (scrollCount)].setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll[cat.length - (scrollCount--)].setContent(academic);
		
		//
		// Attendance
		//

		GridPane attendance = new GridPane();
		attendance.setId("attendanceP");
		scroll[cat.length - (scrollCount)] = new ScrollPane();

		Components.addat = new Button("Add Record");
		ObservableList<Attendance> atsem1Data = FXCollections.observableArrayList();


		Components.atrbsem1 = new RadioButton("Semester 1");
		Components.atrbsem1.setUserData(1);
		Components.atrbsem2 = new RadioButton("Semester 2");
		Components.atrbsem2.setUserData(2);
		ToggleGroup artg = new ToggleGroup();
		artg.getToggles().addAll(Components.atrbsem1, Components.atrbsem2);

		artg.selectedToggleProperty().addListener((obs, o, n) -> {
			loadAttendanceChart(yrlst.getSelectionModel().getSelectedItem(),
					Integer.parseInt(n.getUserData().toString()));
		});

		// Semester 1 table

		Components.atsem1 = new TableView<Attendance>();
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

		Components.atsem1.getColumns().addAll(atsub, atAttended, atTotal);
		Components.atsem1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		Components.atsem1.setItems(atsem1Data);

		// Semester 2 table

		Components.atsem2 = new TableView<Attendance>();
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

		Components.atsem2.getColumns().addAll(atsub1, atAttended1, atTotal1);
		Components.atsem2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		Components.atsem2.setItems(atsem2Data);

		// Attendance Graph

		Components.atXaxis = new CategoryAxis();
		Components.atYaxis = new NumberAxis(0.0, 100.0, 10.0);

		Components.atYaxis.setLabel("Percentage");
		Components.atXaxis.setLabel("Subjects");
		Components.atBarChart = new BarChart<>(Components.atXaxis, Components.atYaxis);
		Components.atBarChart.setTitle("Semester Attendance Report");
		Components.atBarChart.setLegendVisible(false);

		Components.addat.setOnAction(arg -> {
			if (Components.atrbsem1.isSelected()) {
				atsem1Data.add(new Attendance("subject", 0, 0));
			} else {
				atsem2Data.add(new Attendance("subject", 0, 0));

			}
		});

		GridPane.setHalignment(Components.addat,HPos.RIGHT);
		attendance.add(Components.atsem1, 0, 1, 3, 1);
		attendance.add(Components.atsem2, 4, 1, 3, 1);
		attendance.add(Components.addat, 2, 2);
		attendance.add(Components.atrbsem1, 3, 2);
		attendance.add(Components.atrbsem2, 4, 2);
		attendance.add(Components.atBarChart, 0, 4, 8, 1);

		scroll[cat.length - (scrollCount)].setContent(attendance);
		scroll[cat.length - (scrollCount--)].setHbarPolicy(ScrollBarPolicy.NEVER);

		//
		// Projects
		//

		GridPane projects = new GridPane();
		projects.setId("projectsP");
		scroll[cat.length - (scrollCount)] = new ScrollPane();
		Components.prtmp = new HashMap<>();
		Button upload = new Button("Upload");

		Rectangle bin = new Rectangle(150,150);
		ImagePattern binIcon = new ImagePattern(new Image("/ivn/typh/tchr/icons/recycle_empty.png"));
		bin.setFill(binIcon);

		bin.setEffect(new DropShadow());
		Components.prList = new ListView<>();
		Components.prList.setPrefWidth(600);
		Components.prList.setTooltip(new Tooltip("Drag and Drop Files Over Here"));

		upload.setOnAction(event -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Upload a Project - Typh™");
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Compressed files only", "*.zip",
					"*.rar", "*.tar", "*.7z", "*.xz", "*.gz");
			fc.getExtensionFilters().add(filter);
			File uploadFile = fc.showOpenDialog(Components.scene.getWindow());
			Components.prtmp.put(uploadFile.getName(), uploadFile.getAbsolutePath());
			Components.prList.getItems().add(uploadFile.getName());
		});

		Components.prList.setCellFactory((arg0) -> {

			return (new Project(Components.scene));

		});
		
		Tooltip tip = new Tooltip();

		bin.setOnMouseEntered(value -> {
			tip.hide();

			Point2D p = bin.localToScene(0.0, 0.0);
			tip.setText("Drag projects to delete");
			tip.show(bin, p.getX(), p.getY() + bin.getWidth());
		});

		bin.setOnMouseExited(value -> {
			tip.hide();
		});

		bin.setOnDragOver(value -> {
			if (value.getGestureSource() != null){
				value.acceptTransferModes(TransferMode.MOVE);
			}
		});
		bin.setOnDragDropped(value -> {
			Dragboard db = value.getDragboard();
			boolean success = false;
			if (value.getDragboard().hasString()) {
				int index = Components.prList.getItems().indexOf(db.getString());
				Components.prList.getItems().remove(index);
				Notification.message(Components.stage, AlertType.INFORMATION, "Project - Typh™",
						"Project " + db.getString() + " deleted !!!");
				success = true;
			}

			value.setDropCompleted(success);
			value.consume();
		});

		Components.prList.setOnDragOver((arg0) -> {
			Dragboard db = arg0.getDragboard();
			if (db.hasFiles()) {
				arg0.acceptTransferModes(TransferMode.COPY);
			} else {
				arg0.consume();
			}
		});

		Components.prList.setOnDragDropped((arg0) -> {
			Dragboard db = arg0.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				success = true;
				for (File file : db.getFiles()) {
					Components.prtmp.put(file.getName(), file.getAbsolutePath());
					Components.prList.getItems().add(file.getName());
				}
			}
			arg0.setDropCompleted(success);
			arg0.consume();
		});

		projects.add(upload, 1, 0);
		projects.add(Components.prList, 0, 0, 1, 2);
		projects.add(bin, 1, 1);

		scroll[cat.length - (scrollCount--)].setContent(projects);

		// Assignments

		scroll[cat.length - (scrollCount)] = new ScrollPane();
		GridPane assignment = new GridPane();
		assignment.setId("assignmentP");
		
		ScrollPane spAssignment = new ScrollPane();
		
		Components.addAssignment = new Button("Add an Assignment");
		Components.removeAssignment = new Button("Remove selected item");
		Components.asList = new ListView<>();

		Components.asList.setPrefWidth(600);
		GridPane.setFillWidth(Components.asList, true);
		spAssignment.setContent(Components.asList);
		spAssignment.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spAssignment.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		StringConverter<Assignment> converter = new StringConverter<Assignment>() {

			@Override
			public Assignment fromString(String arg0) {
				return null;
			}

			public String toString(Assignment assignment) {
				return "[Semester " + assignment.getSem() + "]\t" + assignment.getTitle();
			}

		};
		Components.asList.setCellFactory(CheckBoxListCell.forListView(Assignment::completedProperty, converter));
		Components.addAssignment.setOnAction(arg0 -> {
			Dialog<Assignment> dialog = new Dialog<>();
			TextField asTitle = new TextField();
			ComboBox<String> asYear = new ComboBox<>();
			ButtonType add = new ButtonType("Add", ButtonData.OK_DONE);
			HBox pane = new HBox();
			pane.setPadding(new Insets(30));
			pane.setSpacing(20);
			asYear.getItems().addAll("Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6",
					"Semester 7", "Semester 8");
			asYear.getSelectionModel().selectFirst();
			pane.getChildren().addAll(asYear, asTitle);
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
					return new Assignment(asYear.getSelectionModel().getSelectedIndex() + 1, asTitle.getText(), false);
				} else if (value.getButtonData().equals(ButtonData.OK_DONE) && asTitle.getText().isEmpty()) {

				}
				return null;
			});

			asTitle.setPrefWidth(500);

			dialog.initOwner(Components.stage);
			Optional<Assignment> result = dialog.showAndWait();
			result.ifPresent(arg -> Components.asList.getItems().add(arg));
		});

		Components.removeAssignment.setTooltip(new Tooltip("Deletes last assignment by default"));
		Components.removeAssignment.setOnAction(value -> {
			Components.asList.getSelectionModel().select(Components.asList.getItems().size() - 1);
			int index = Components.asList.getSelectionModel().getSelectedIndex();
			Components.asList.getItems().remove(index);

		});

		assignment.add(Components.addAssignment, 2, 0);
		assignment.add(Components.removeAssignment, 3, 0);
		assignment.add(spAssignment, 0, 1, 4, 1);

		scroll[cat.length - (scrollCount)].setContent(assignment);
		scroll[cat.length - (scrollCount--)].setHbarPolicy(ScrollBarPolicy.NEVER);

		//
		// Adding all titled panes to the accordion
		//

		
		for (int i = 0; i < cat.length; i++) {
			scroll[i].setId("homeScrollPane");
			Components.tp[i] = new TitledPane(cat[i], scroll[i]);
		}
		
		
		Components.accord.getPanes().addAll(Components.tp);
		Components.accord.setExpandedPane(Components.tp[0]);

		Components.slist.setPrefWidth(150);
		
		GridPane.setHgrow(Components.accord, Priority.ALWAYS);

		top.getChildren().addAll(Components.srch, Components.searchBox);
			
		topL.getChildren().add(Components.pname);

		left.getChildren().addAll(Components.dprt, Components.pdprt, Components.cls, Components.pcls, Components.tstuds, Components.nstuds);
		side.addNodes(topL,left,Components.mb.getItems().get(2),Components.mb.getItems().get(3),Components.mb.getItems().get(5));
		
		Components.mb.getItems().remove(7);
		Components.mb.getItems().add(7, logout);
		Components.mb.getItems().remove(0, 4);
		Components.mb.getItems().add(0,Components.menu);
		Components.mb.getItems().get(2).setId("fullscreen");
		

		GridPane.setValignment(left, VPos.CENTER);
		StackPane.setAlignment(side, Pos.CENTER_LEFT);

		tgpane.add(top, 0, 0);
		tgpane.add(aboveAcc, 0, 1);
		tgpane.add(Components.accord, 0, 2);
		
		tgpane.setMaxSize(Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
				Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		tgpane.setMinSize(Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
				Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		sctgpane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sctgpane.setVbarPolicy(ScrollBarPolicy.NEVER);
		sctgpane.setContent(tgpane);
		Components.stage.getScene().getStylesheets().remove(0);
		Components.stage.getScene().getStylesheets().add(getClass().getResource("raw/style.css").toExternalForm());
		
		spMain.getChildren().addAll(sctgpane,dummy,side);
		Components.pane.setCenter(spMain);

		disableAll(true);
		Components.slist.getSelectionModel().selectFirst();
		yrlst.getSelectionModel().selectFirst();
	
	}


	private void uploadData(String sid) {
		Bson filter = new Document("sid", sid);
		Document newValue = new Document("name", Components.tsname.getText().trim()).append("rno", Components.tsrno.getValue())
				.append("batch", Components.tsbatch.getValue()).append("class", Components.tsclass.getValue())
				.append("email", Components.tsmail.getText()).append("address", Components.tsaddr.getText())
				.append("studentPhone", Components.tsphone.getText()).append("parentPhone", Components.tpphone.getText())
				.append("department", Components.tsdprt.getSelectionModel().getSelectedItem());

		int[] sem = null;
		int year = sMatchesY(0, Components.tsyear.getValue());
		String yr = null;
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
		for (int i = 0; i < (Components.tsem1.getItems().size() + Components.tsem2.getItems().size()); i++) {
			if (i <= (Components.tsem1.getItems().size() - 1)) {
				Document tmp = new Document("name", Components.tsem1.getItems().get(i).getSubject())
						.append("thScored", Components.tsem1.getItems().get(i).getTheoryScored())
						.append("thTotal", Components.tsem1.getItems().get(i).getTheoryTotal())
						.append("orScored", Components.tsem1.getItems().get(i).getOralScored())
						.append("orTotal", Components.tsem1.getItems().get(i).getOralTotal())
						.append("prScored", Components.tsem1.getItems().get(i).getPracsScored())
						.append("prTotal", Components.tsem1.getItems().get(i).getPracsTotal())
						.append("back", Components.tsem1.getItems().get(i).getBacklog())
						.append("attended", Components.atsem1.getItems().get(i).getAttended())
						.append("attendedTotal", Components.atsem1.getItems().get(i).getTotal())
						.append("twScored", Components.tsem1.getItems().get(i).getTermworkScored())
						.append("twTotal", Components.tsem1.getItems().get(i).getTermworkTotal()).append("sem", sem[0]);
				acData.add(tmp);
			} else {
				Document tmp = new Document("name", Components.tsem2.getItems().get(i - 6).getSubject())
						.append("thScored", Components.tsem2.getItems().get(i - 6).getTheoryScored())
						.append("thTotal", Components.tsem2.getItems().get(i - 6).getTheoryTotal())
						.append("orScored", Components.tsem2.getItems().get(i - 6).getOralScored())
						.append("orTotal", Components.tsem2.getItems().get(i - 6).getOralTotal())
						.append("prScored", Components.tsem2.getItems().get(i - 6).getPracsScored())
						.append("prTotal", Components.tsem2.getItems().get(i - 6).getPracsTotal())
						.append("back", Components.tsem2.getItems().get(i - 6).getBacklog())
						.append("attended", Components.atsem2.getItems().get(i - 6).getAttended())
						.append("attendedTotal", Components.atsem2.getItems().get(i - 6).getTotal())
						.append("twScored", Components.tsem2.getItems().get(i - 6).getTermworkScored())
						.append("twTotal", Components.tsem2.getItems().get(i - 6).getTermworkTotal()).append("sem", sem[1]);
				acData.add(tmp);
			}

		}

		List<Document> asData = new LinkedList<>();
		for(int i =0 ;i<Components.asList.getItems().size();i++){
			Document tmp = new Document("title",Components.asList.getItems().get(i).getTitle()).append("sem", Components.asList.getItems().get(i).getSem()).append("completed",Components.asList.getItems().get(i).getCompleted());
			asData.add(tmp);
		}
		
		
		List<Document> repData = new LinkedList<>();
		for(int i=0; i<Components.reps.getItems().size();i++){
			Document tmp = new Document("sem",Components.reps.getItems().get(i).getSem()).append("seen",Components.reps.getItems().get(i).getSeen()).append("report", Components.reps.getItems().get(i).getReport());
			repData.add(tmp);
		}
		
		newValue.append(yr, acData).append(yr+"Assignments", asData).append("reports", repData);

		Bson query = new Document("$set", newValue);
		Engine.db.getCollection("Students").updateOne(filter, query);

		GridFSBucket gfsBucket = GridFSBuckets.create(Engine.db, "projects");
		Components.prtmp.forEach((key, val) -> {
			InputStream in = null;
			try {
				in = new FileInputStream(new File(val));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			gfsBucket.uploadFromStream(getSId() + ":" + key, in);
		});
	}

	@SuppressWarnings("unchecked")
	private void loadStudentProfile(String student) {

		String json = Engine.db.getCollection("Students").find(eq("name", student.substring(1))).first().toJson();
		JSONObject jsonData = new JSONObject(json);

		// Personal

		byte[] deci = Base64.getDecoder().decode(jsonData.getString("img"));
		BufferedImage bf = null;
		try {
			bf = ImageIO.read(new ByteArrayInputStream(deci));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Components.dpImgView.setImage(SwingFXUtils.toFXImage(bf, null));
		Components.tsname.setText(jsonData.getString("name"));
		Components.tsid.setText(jsonData.getString("sid"));
		Components.tsrno.getSelectionModel().select(jsonData.getString("rno"));
		Components.tsdprt.getSelectionModel().select(jsonData.getString("department"));
		Components.tsclass.getSelectionModel().select(jsonData.getString("batch"));
		Components.tsbatch.getSelectionModel().select(jsonData.getString("class"));
		Components.tsmail.setText(jsonData.getString("email"));
		Components.tsaddr.setText(jsonData.getString("address"));
		Components.tsphone.setText(jsonData.getString("studentPhone"));
		Components.tpphone.setText(jsonData.getString("parentPhone"));
		
		Components.tscyear = jsonData.getString("year");
		
		Components.tsyear.getItems().clear();
		switch(Components.tscyear){
		case "BE":
			Components.tsyear.getItems().add("BE");
		case "TE":
			Components.tsyear.getItems().add("TE");
		case "SE":
			Components.tsyear.getItems().add("SE");
		case "FE":
			Components.tsyear.getItems().add("FE");
		}
		
		Components.tsyear.getSelectionModel().select(Components.tscyear);
		
		
		
		// Academic

		Components.studProgress.getData().clear();
		Components.tsem1.setFixedCellSize(24);
		Components.tsem1.prefHeightProperty().bind(Bindings.size(Components.tsem1.getItems()).multiply(Components.tsem1.getFixedCellSize()).add(90));
		Components.tsem2.setFixedCellSize(24);
		Components.tsem2.prefHeightProperty().bind(Bindings.size(Components.tsem2.getItems()).multiply(Components.tsem2.getFixedCellSize()).add(90));
		XYChart.Series<String, Number> data = new XYChart.Series<>();
		for (int j = 1; j <= 8; j++) {
			float p = getSemesterPercent(j);
			if (p != 0)
				data.getData().addAll(new XYChart.Data<>("Semester " + j, p));
		}
		Components.studProgress.getData().add(data);

		// Projects

		Engine.gfs = GridFSBuckets.create(Engine.db, "projects");

	}

	private String getSId() {
		String id = dprtList.entrySet().stream().filter(a -> a.getValue().equals(Components.tsdprt.getSelectionModel().getSelectedItem())).map(map->map.getKey()).collect(Collectors.joining());
		return (id + String.format("%02d", sMatchesY(0, yrlst.getValue())) + Components.tsclass.getValue() + Components.tsrno.getValue());
	}

	private void loadReport(String n) {

		Components.reps.getItems().clear();
		String data = Engine.db.getCollection("Students").find(eq("sid", Components.tsid.getText())).first().toJson();
		JSONArray rep = new JSONObject(data).getJSONArray("reports");
		Iterator<?> it = rep.iterator();
		while (it.hasNext()) {
			JSONObject j = (JSONObject) it.next();
			boolean b = j.getBoolean("seen");
			int sem = j.getInt("sem");
			String r = j.getString("report");
			if (sMatchesY(sem, n) == 1)
				Components.reps.getItems().add(new Report(b, sem, r));
		}
	}

	private void loadAttendanceChart(String year, int semester) {
		if (year.equals("SE"))
			semester = semester + 2;
		else if (year.equals("TE"))
			semester = semester + 4;
		else if (year.equals("BE"))
			semester = semester + 6;

		String data = Engine.db.getCollection("Students").find(eq("sid", Components.tsid.getText())).first().toJson();
		JSONArray jsona = new JSONObject(data).getJSONArray(year.toLowerCase());
		Iterator<?> it = jsona.iterator();

		Components.atBarChart.getData().clear();
		XYChart.Series<String, Number> cdata = new XYChart.Series<String, Number>();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			if (json.getInt("sem") == semester) {
				String name = json.getString("name");
				float at = json.getInt("attended");
				float att = json.getInt("attendedTotal");
				at = (at / att) * 100;
				Components.atXaxis.getCategories().add(name);
				cdata.getData().add(new XYChart.Data<>(name, at));
			}
		}
		Components.atBarChart.getData().add(cdata);
	}

	private float getSemesterPercent(int i) {
		String data = Engine.db.getCollection("Students").find(eq("sid", Components.tsid.getText())).first().toJson();
		JSONObject json = new JSONObject(data);
		float percent = 0, scored, total;
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

	private void loadAssignmentData(String n) {
		JSONArray jsona = null;
		try {
			String data = Engine.db.getCollection("Students").find(eq("sid", Components.tsid.getText())).first().toJson();
			jsona = new JSONObject(data).getJSONArray(n.toLowerCase() + "Assignments");

			Components.asList.getItems().clear();
			Iterator<?> it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject json = (JSONObject) it.next();
				String title = json.getString("title");
				int semester = json.getInt("sem");
				boolean flag = json.getBoolean("completed");
				Components.asList.getItems().add(new Assignment(semester, title, flag));
			}
		} catch (JSONException | NullPointerException e) {
		}
	}

	private void loadAttendanceData(String n) {
		JSONArray jsona = null;
		try {
			String data = Engine.db.getCollection("Students").find(eq("sid", Components.tsid.getText())).first().toJson();
			jsona = new JSONObject(data).getJSONArray(n.toLowerCase());
		} catch (JSONException e) {
		}
		Iterator<?> it = jsona.iterator();
		Components.atsem1.getItems().clear();
		Components.atsem2.getItems().clear();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			String name = json.getString("name");
			int at = json.getInt("attended");
			int att = json.getInt("attendedTotal");
			int sem = json.getInt("sem");
			if (sem % 2 == 1) {
				Components.atsem1.setTooltip(new Tooltip("Semester: " + Integer.toString(sem)));
				Components.atrbsem1.setText("Semester: " + Integer.toString(sem));
				Components.atsem1.getItems().add(new Attendance(name, at, att));
			} else {
				Components.atsem2.setTooltip(new Tooltip("Semester: " + Integer.toString(sem)));
				Components.atsem2.getItems().add(new Attendance(name, at, att));
				Components.atrbsem2.setText("Semester: " + Integer.toString(sem));

			}
		}
	}

	private void loadAcademicData(String year) {
		JSONArray jsona = null;
		try {
			String data = Engine.db.getCollection("Students").find(eq("sid", Components.tsid.getText())).first().toJson();
			jsona = new JSONObject(data).getJSONArray(year.toLowerCase());
		} catch (JSONException e) {
		}
		Iterator<?> it = jsona.iterator();
		Components.tsem1.getItems().clear();
		Components.tsem2.getItems().clear();
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

			if (sem % 2 == 1) {
				Components.tsem1.setTooltip(new Tooltip("Semester: " + Integer.toString(sem)));
				Components.tsem1.getItems().add(new Marks(name, ths, tht, ors, ort, prs, prt, tws, twt, back));
			} else {
				Components.tsem2.setTooltip(new Tooltip("Semester: " + Integer.toString(sem)));
				Components.tsem2.getItems().add(new Marks(name, ths, tht, ors, ort, prs, prt, tws, twt, back));
			}
		}

	}

	private void loadProjectData(String yr) {
		Components.prList.getItems().clear();
		Engine.gfs.find().forEach(new Block<GridFSFile>() {
			public void apply(final GridFSFile file) {
				String name = file.getFilename().split(":")[1];
				String gfsid = file.getFilename().split(":")[0];
				int year = Integer.parseInt(gfsid.substring(2, 4));
				if (year == sMatchesY(0, yr))
					Components.prList.getItems().add(name);
			}
		});
	}

	public int sMatchesY(int sem, String year) {
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
			studList.add("[" + json.getString("class") + "]" + ": " + json.getString("name"));
		}

		cursor = Engine.db.getCollection("Departments").find().iterator();
		while (cursor.hasNext()) {
			JSONObject json = new JSONObject(cursor.next().toJson());
			dprtList.put(json.getString("dprtID"), json.getString("department"));
		}

		
		Engine.db.getCollection("Users").updateOne(eq("user", BasicUI.user), new Document("$set", new Document(
				"lastLogin",
				LocalDateTime.now().getDayOfMonth() + "-" + LocalDateTime.now().getMonthValue() + "-"
						+ LocalDateTime.now().getYear() + "\t" + String.format("%02d", LocalDateTime.now().getHour())
						+ "h:" + String.format("%02d", LocalDateTime.now().getMinute()) + "m:"
						+ String.format("%02d", LocalDateTime.now().getSecond()) + "s")));

		Components.tsdprt.getItems().addAll(dprtList.values());
		for (int i = 1; i <= 200; i++) {
			Components.tsrno.getItems().add(String.format("%03d", i));
		}
		for (int i = 1; i < 100; i++) {
			Components.tsclass.getItems().add(String.format("%02d", i));
			Components.tsbatch.getItems().add(String.format("%02d", i));
		}
		for (int i = 0; i < 26; i++) {
			Components.tsbatch.getItems().add(Character.toString((char) ('A' + i)));
			Components.tsbatch.getItems().add(Character.toString((char) ('a' + i)));
		}
		
		List<String> name = new ArrayList<>();

		studList.forEach(student -> {
			name.add(student.split(": ")[1]);
		
			int i=0;
			String tmp = student.split("]")[0].substring(1);
			if (Components.classIncharge.equals(tmp)){
				Components.slist.getItems().add(student);
				i++;
			}
			Components.nstuds.setText(Integer.toString(i));
		});
		
		Components.searchBox.setItems(name);
	}

	
	private void disableAll(boolean flag) {
		Components.update.setDisable(flag);
		Components.report.setDisable(flag);
		Components.export.setDisable(flag);

		// Personal Pane

		Components.dpImgView.setDisable(flag);
		Components.tsname.setEditable(!flag);
		Components.tsid.setEditable(!flag);
		Components.tsrno.setDisable(flag);
		Components.tsdprt.setDisable(flag);
		Components.tsclass.setDisable(flag);
		Components.tsbatch.setDisable(flag);
		Components.tsmail.setEditable(!flag);
		Components.tsaddr.setEditable(!flag);
		Components.tsphone.setEditable(!flag);
		Components.tpphone.setEditable(!flag);

		// Academic Pane

		Components.tsem1.setEditable(!flag);
		Components.tsem2.setEditable(!flag);
		Components.addEntry.setDisable(flag);
		Components.rbsem1.setDisable(flag);
		Components.rbsem2.setDisable(flag);
		
		// Attendance Pane

		Components.atsem1.setEditable(!flag);
		Components.atsem2.setEditable(!flag);
		Components.addat.setDisable(flag);

		// Projects Pane
		
		Components.prList.setDisable(flag);

		// Assignments Pane

		Components.asList.setEditable(!flag);
		Components.addAssignment.setDisable(flag);
		Components.removeAssignment.setDisable(flag);

	}
	private void logoutApplication() {
		Alert ex = new Alert(AlertType.CONFIRMATION);
		ex.setHeaderText("LogOut "+BasicUI.user+" - Typh™ ? ");
		ex.setTitle("Exit - Typh™");
		ex.initOwner(Components.stage);
		ex.getButtonTypes().setAll(ButtonType.OK,ButtonType.CANCEL);

		Optional<ButtonType> result = ex.showAndWait();
		result.ifPresent(arg->{
			if(arg.equals(ButtonType.OK)){
			if(!(Engine.mongo== null))
					Engine.mongo.close();
			HeartBeat.heartAttack=true;
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
