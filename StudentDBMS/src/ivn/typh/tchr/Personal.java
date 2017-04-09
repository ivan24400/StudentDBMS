package ivn.typh.tchr;

import static com.mongodb.client.model.Filters.eq;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import ivn.typh.main.Engine;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class Personal {
	
	public static GridPane personal;
	public static ImageView dpImgView;
	public static TextField tsname;
	public static TextField tsid;
	public static ListView<ReportData> reportPane;
	public static ChoiceBox<String> tsrno;
	public static ChoiceBox<String> tsdprt;
	public static ChoiceBox<String> tsclass;
	public static ChoiceBox<String> tsbatch;
	public static ChoiceBox<String> tsyear;
	public static TextField tsmail;
	public static TextField tsaddr;
	public static TextField tsphone;
	public static TextField tpphone;
	public static ObservableList<String> reportList;
	public static Label reports;



	public static void setup(){
		
		Components.scroll[Components.paneList.length - (Components.paneCount)] = new ScrollPane();
		personal = new GridPane();
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
		reports = new Label("Reports");

		dpImgView = new ImageView(new Image(TchrUI.class.getResourceAsStream("/ivn/typh/main/raw/pic.jpg")));
		tsname = new TextField();
		tsid = new TextField();
		tsrno = new ChoiceBox<>();
		tsdprt = new ChoiceBox<>();
		tsclass = new ChoiceBox<>();
		tsbatch = new ChoiceBox<>();
		tsmail = new TextField();
		tsaddr = new TextField();
		tsphone = new TextField();
		tpphone = new TextField();
		reportPane = new ListView<>();
		reportList = FXCollections.observableArrayList();


		tsname.setPromptText("Name");
		tsid.setPromptText("ID");
		tsmail.setPromptText("Email");
		tsaddr.setPromptText("Address");
		tsphone.setPromptText("Phone");
		tpphone.setPromptText("Parent Phone");

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
				tool.show(tsname, p.getX() + tsname.getCaretPosition(),
						p.getY() + tsname.getHeight());
				tsname.setText(n.replaceAll("[\\d]", ""));
			}
		});

		 tsaddr.setOnMouseEntered(value->{
		 tool.hide();
		 if(!tsaddr.getText().isEmpty() && !tsaddr.isDisabled()){
		 tool.setText(tsaddr.getText());
		 Point2D p = tsaddr.localToScene(0.0, 0.0);
		 tool.show(tsaddr, p.getX() +
		 tsaddr.getWidth()/2,
		 p.getY() + tsaddr.getHeight()+4);
		 }
		 });

		tsaddr.setOnMouseExited(value -> {
			tool.hide();
		});

		tsphone.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				System.out.println(obs.getValue() + "\t" + o + "\t" + n);
				Point2D p = tsphone.localToScene(0.0, 0.0);
				tool.setText("Enter numbers only");
				tool.show(tsphone, p.getX() + tsphone.getCaretPosition(),
						p.getY() + tsphone.getHeight());
				tsphone.setText(n.replaceAll("[^\\d]", ""));
			}
		});

		tpphone.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				Point2D p = tpphone.localToScene(0.0, 0.0);
				tool.setText("Enter numbers only");

				tool.show(tpphone, p.getX() + tpphone.getCaretPosition(),
						p.getY() + tpphone.getHeight());
				tpphone.setText(n.replaceAll("[^\\d]", ""));
			}
		});

		ContextMenu tsidcm = new ContextMenu();
		MenuItem tsida = new MenuItem("Generate ID");
		tsida.setOnAction(arg -> {

			tsid.setText(TchrUI.getSId());
		});
		ScrollPane spReport = new ScrollPane();
		ContextMenu repcm = new ContextMenu();
		MenuItem del = new MenuItem("Delete this report");
		del.setOnAction(arg -> {
			reportList.remove(reportPane.getSelectionModel().getSelectedIndex());
		});
		repcm.getItems().add(del);

		reportPane.setContextMenu(repcm);
		reportPane.getSelectionModel().selectLast();

		StringConverter<ReportData> rconvertor = new StringConverter<ReportData>() {
			@Override
			public ReportData fromString(String arg0) {
				return null;
			}

			@Override
			public String toString(ReportData arg) {
				return "[Semester " + arg.getSem() + "]\t" + arg.getReport();
			}

		};
		reportPane.setCellFactory(CheckBoxListCell.forListView(ReportData::seenProperty, rconvertor));
		
		Components.report.setOnAction(arg -> {
			Dialog<String> dialog = new Dialog<>();
			ButtonType reportb = new ButtonType("Report", ButtonData.OK_DONE);
			dialog.setTitle("Report - Typh™");
			dialog.setHeaderText("Enter Report details");
			VBox vb = new VBox();
			vb.setId("reportDialog");
			TextArea ta = new TextArea();
			ComboBox<String> sem = new ComboBox<>();
			sem.getItems().addAll("Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6",
					"Semester 7", "Semester 8");
			sem.getSelectionModel().selectFirst();
			ta.setPromptText(tsname.getText());
			ta.setPromptText("Enter your report details ...");

			vb.getChildren().addAll(sem, ta);
			dialog.getDialogPane().setContent(vb);
			dialog.initOwner(Components.stage);
			dialog.getDialogPane().getButtonTypes().add(reportb);
			dialog.setResultConverter(value -> {
				try {
					if (value.getButtonData().equals(ButtonData.OK_DONE)) {

						reportList.add(ta.getText());
					}
				} catch (NullPointerException e) {
					e.getMessage();
				}
				return "";

			});

			dialog.show();
		});

		tsidcm.getItems().add(tsida);
		tsid.setContextMenu(tsidcm);
		GridPane.setMargin(dpImgView, new Insets(40));
		spReport.setContent(reportPane);
		GridPane.setFillWidth(reportPane, true);
		reportPane.setPrefWidth(600);
		reportPane.setPrefHeight(150);
		spReport.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spReport.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		personal.setId("personalP");
		dpImgView.setId("dpImgView");
		
		Platform.runLater(()->{
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
			personal.add(reports, 0, 6);
			personal.add(spReport, 0, 7, 5, 1);
			personal.setAlignment(Pos.CENTER);

		});
	
		
		Components.scroll[Components.paneList.length - (Components.paneCount)].setHbarPolicy(ScrollBarPolicy.NEVER);
		Components.scroll[Components.paneList.length - (Components.paneCount--)].setContent(personal);
	}
	
	static void loadReport(String year) {

		Personal.reportPane.getItems().clear();
		String data = Engine.db.getCollection("Students").find(eq("sid", Personal.tsid.getText())).first().toJson();
		JSONArray rep = new JSONObject(data).getJSONArray("reports");
		Iterator<?> it = rep.iterator();
		while (it.hasNext()) {
			JSONObject j = (JSONObject) it.next();
			boolean b = j.getBoolean("seen");
			int sem = j.getInt("sem");
			String r = j.getString("report");
			if (TchrUI.sMatchesY(sem, year) == 1){
				Platform.runLater(()->{
					Personal.reportPane.getItems().add(new ReportData(b, sem, r));
				});
			}
		}
	}

}
