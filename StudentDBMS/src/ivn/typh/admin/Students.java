package ivn.typh.admin;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONException;
import org.json.JSONObject;
import static com.mongodb.client.model.Filters.*;

import ivn.typh.main.Engine;
import ivn.typh.main.Resources;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/*
 * This class provides a dialog box to manage all of the students
 */
public class Students extends Dialog<String> implements EventHandler<ActionEvent> {

	static ObservableList<String> studentList;
	static int x, y;

	private String sy, sb, dp, semester, srno;
	private int saveAdded;
	private boolean isFirst;
	private String dpImg;

	private GridPane home;
	private Button addS;
	private Button del;
	private Stage parent;
	private TextField tsname;
	private TextField tsid;
	private ChoiceBox<String> tsrno;
	private ChoiceBox<String> tsyear;
	private ChoiceBox<String> tsbatch;
	private ChoiceBox<String> tssem;
	private TextField tsmail;
	private TextField tsaddr;
	private TextField tsphone;
	private TextField tpphone;
	private ImageView dpImgView;
	private ToggleButton edit;
	private Button upload;
	private ChoiceBox<String> tsdprt;

	public Students(Stage s, GridPane gp, String n, String i, String rolln, String year, String batch, String mail,
			String addr, String sp, String pp, String dprt, String img, String csem) {
		this(s);
		home = gp;

		tsname.setText(n);
		tsid.setText(i);
		tsmail.setText(mail);
		tsaddr.setText(addr);
		tsphone.setText(sp);
		tpphone.setText(pp);
		tsdprt.setValue(dprt);
		dpImg = img;
		srno = rolln;
		sy = year;
		sb = batch;
		dp = dprt;
		semester = csem;
	}

	public Students(Stage arg) {
		parent = arg;
		initOwner(parent);
		tsname = new TextField();
		tsid = new TextField();
		tsrno = new ChoiceBox<>();
		tsdprt = new ChoiceBox<>();
		tsyear = new ChoiceBox<>();
		tsbatch = new ChoiceBox<>();
		tsmail = new TextField();
		tsaddr = new TextField();
		tsphone = new TextField();
		tpphone = new TextField();
		tssem = new ChoiceBox<>();
		upload = new Button("Upload");
		dpImg = new String();
	}

	public Students(Stage arg, GridPane gp, Button b) {
		this(arg);
		home = gp;
		addS = b;
	}

	public void createUI() {
		setTitle("Student - Typh™");

		GridPane dPane = new GridPane();

		dPane.setAlignment(Pos.CENTER);
		dPane.setPadding(new Insets(30));
		dPane.setHgap(20);
		dPane.setVgap(20);

		tssem.getItems().setAll(FXCollections.observableArrayList("SEM 1", "SEM 2", "SEM 3", "SEM 4", "SEM 5", "SEM 6",
				"SEM 7", "SEM 8"));
		tsname.setPromptText("Name");
		tsid.setPromptText("ID");
		tsmail.setPromptText("Email");
		tsaddr.setPromptText("Address");
		tsphone.setPromptText("Phone");
		tpphone.setPromptText("Parent Phone");

		ContextMenu cm = new ContextMenu();
		MenuItem autog = new MenuItem("Auto Generate");
		autog.setOnAction(arg -> {
			Departments.dprtList.forEach((key, val) -> {
				if (val == tsdprt.getValue()) {
					tsid.setText(key + tssem.getValue() + tsyear.getValue() + tsrno.getValue());

				}
			});
		});
		tsid.setContextMenu(cm);
		if (dpImg.isEmpty())
			dpImgView = new ImageView(new Image(getClass().getResourceAsStream(Resources.DEFAULT_PIC.VALUE)));
		else {
			try {
				byte[] imgd = Base64.getDecoder().decode(dpImg);
				BufferedImage bf = ImageIO.read(new ByteArrayInputStream(imgd));
				dpImgView = new ImageView(SwingFXUtils.toFXImage(bf, null));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		dpImgView.setEffect(new DropShadow());
		dpImgView.setCursor(Cursor.CLOSED_HAND);
		Tooltip.install(dpImgView, new Tooltip("You can Drag N Drop picture"));

		Tooltip tool = new Tooltip();
		tsname.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\D*")) {
				Bounds screen = tsname.localToScreen(tsname.getBoundsInLocal());
				tool.setText("Enter text only");
				tool.show(tsname, screen.getMinX() + tsname.getCaretPosition(),
						screen.getMinY() + tsname.getScene().getY());
				Platform.runLater(() -> {
					tsname.setText(o);
					tsname.positionCaret(tsname.getText().length());
				});
			}
		});

		tsphone.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				Bounds screen = tsphone.localToScreen(tsphone.getBoundsInLocal());
				tool.setText("Enter numbers only");
				tool.show(tsphone, screen.getMinX() + tsphone.getCaretPosition(),
						screen.getMinY() + tsphone.getScene().getY());
				Platform.runLater(() -> {
					tsphone.setText(o);
					tsphone.positionCaret(tsphone.getText().length());
				});
			}
		});

		tpphone.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				Bounds screen = tpphone.localToScreen(tpphone.getBoundsInLocal());
				tool.setText("Enter numbers only");
				tool.show(tpphone, screen.getMinX() + tpphone.getCaretPosition(),
						screen.getMinY() + tpphone.getScene().getY());
				Platform.runLater(() -> {
					tpphone.setText(o);
					tpphone.positionCaret(tpphone.getText().length());
				});
			}
		});

		tsname.setOnMouseMoved(arg -> tool.hide());
		tsphone.setOnMouseMoved(arg -> tool.hide());
		tpphone.setOnMouseMoved(arg -> tool.hide());

		tsdprt.getItems().addAll(Departments.dprtList.values());
		tsname.requestFocus();

		dpImgView.setFitHeight(128);
		dpImgView.setFitWidth(128);

		Tooltip.install(dpImgView, new Tooltip("Drag N Drop here !"));
		dpImgView.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent arg0) {
				Dragboard db = arg0.getDragboard();
				if (db.hasFiles()) {
					arg0.acceptTransferModes(TransferMode.COPY);
				} else {
					arg0.consume();
				}
			}

		});

		dpImgView.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent arg0) {
				Dragboard db = arg0.getDragboard();
				boolean success = false;
				if (db.hasFiles()) {
					db.getFiles().forEach(file -> {
						dpImgView.setImage(new Image(file.toURI().toString()));
					});
					success = true;
				}
				arg0.setDropCompleted(success);
				arg0.consume();
			}

		});

		upload.setOnAction(event -> {
			FileChooser file = new FileChooser();
			file.setTitle("Upload a picture - Typh™");
			file.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
			File tmp = file.showOpenDialog(getOwner());
			if (tmp != null)
				dpImgView.setImage(new Image(tmp.getAbsolutePath()));
		});

		Label year = new Label("Current Semester");
		GridPane.setHalignment(year, HPos.RIGHT);

		dPane.add(new Label("Name"), 0, 0);
		dPane.add(tsname, 1, 0);
		dPane.add(new Label("ID"), 0, 1);
		dPane.add(tsid, 1, 1);
		dPane.add(new Label("Roll No"), 0, 2);
		dPane.add(tsrno, 1, 2);
		dPane.add(new Label("Department"), 0, 3);
		dPane.add(tsdprt, 1, 3);
		dPane.add(new Label("Class"), 2, 1);
		dPane.add(tsyear, 3, 1);
		dPane.add(new Label("Batch"), 2, 0);
		dPane.add(tsbatch, 3, 0);
		dPane.add(new Label("Email"), 0, 4);
		dPane.add(tsmail, 1, 4);
		dPane.add(new Label("Address"), 2, 2);
		dPane.add(tsaddr, 3, 2);
		dPane.add(new Label("Phone"), 2, 3);
		dPane.add(tsphone, 3, 3);
		dPane.add(new Label("Parent Phone"), 2, 4);
		dPane.add(tpphone, 3, 4);
		dPane.add(dpImgView, 4, 1, 1, 5);
		dPane.add(year, 1, 5);
		dPane.add(tssem, 2, 5);

		addEdit(dPane);

		upload.setPrefWidth(dpImgView.getFitWidth());
		GridPane.setFillWidth(upload, true);
		dPane.add(upload, 4, 1);

		getDialogPane().setContent(dPane);

		if (!isFirst) {
			if (saveAdded == 0) {
				ButtonType save = new ButtonType("Save", ButtonData.OK_DONE);
				getDialogPane().getButtonTypes().clear();
				getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
				initRooms();
			}
			saveAdded++;
			setHeaderText("Student:\t" + tsname.getText());
			tsyear.getSelectionModel().select(sy);
			tsbatch.getSelectionModel().select(sb);
			tsdprt.getSelectionModel().select(dp);
			tssem.getSelectionModel().select(semester);
			tsrno.getSelectionModel().select(srno);
			disableAll(true);

		} else {
			getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			setHeaderText("Fill in required fields to add a Student");
			initRooms();
			tsyear.getSelectionModel().selectFirst();
			tsbatch.getSelectionModel().selectFirst();
			tsdprt.getSelectionModel().selectFirst();
			tssem.getSelectionModel().selectFirst();
			tsrno.getSelectionModel().selectFirst();
		}

		setResultConverter(arg -> {
			if (arg.equals(ButtonType.OK) || arg.getButtonData().equals(ButtonData.OK_DONE)) {
				addButton();
			}
			return null;
		});

		Platform.runLater(() -> {
			show();
			tsrno.setPrefWidth(tsname.getWidth());
			tsdprt.setPrefWidth(tsname.getWidth());
			tsyear.setPrefWidth(tsname.getWidth());
			tsbatch.setPrefWidth(tsname.getWidth());
		});
	}

	/*
	 * This method is used to initialize all combo/choice boxes with a range of
	 * values.
	 */
	private void initRooms() {
		tsyear.getItems().addAll("FE", "SE", "TE", "BE");

		for (int i = 1; i < 200; i++) {
			if (i <= 100)
				tsbatch.getItems().add(String.format("%02d", i));
			tsrno.getItems().add(String.format("%03d", i));
		}
		for (int i = 0; i < 26; i++) {
			tsbatch.getItems().add(Character.toString((char) ('A' + i)));
			tsbatch.getItems().add(Character.toString((char) ('a' + i)));
		}

	}

	/*
	 * This method adds additional buttons onto the dialog. They are to edit and
	 * delete Profile.
	 * 
	 * @param pane The pane of Students dialog.
	 */

	private void addEdit(GridPane pane) {

		if (!isFirst) {
			HBox seBox = new HBox();
			seBox.setPadding(new Insets(50));
			seBox.setAlignment(Pos.CENTER);
			seBox.setSpacing(20);
			edit = new ToggleButton("Edit");
			del = new Button("Delete");
			edit.selectedProperty().addListener((obs, o, n) -> {
				disableAll(!n);
			});
			del.setOnAction(val -> {
				Alert dalert = new Alert(AlertType.CONFIRMATION);
				dalert.setTitle("Delete Student - Typh™");
				dalert.setHeaderText("Are you sure to delete Student Profile of : " + tsname.getText() + "?");
				dalert.initOwner(this.getDialogPane().getScene().getWindow());
				dalert.setResultConverter(value -> {
					if (value.equals(ButtonType.OK)) {
						Bson query = new Document("name", tsname.getText());
						Engine.db.getCollection("Students").deleteOne(query);
					}
					return null;
				});
				dalert.show();

			});
			seBox.getChildren().addAll(edit, del);

			pane.add(seBox, 0, 6, 5, 1);
		}
	}

	/*
	 * This method adds a new Student entry to the database.
	 */

	private void addButton() {

		BufferedImage bf = SwingFXUtils.fromFXImage(dpImgView.getImage(), null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] tmpb;
		String tmpString = null;
		try {
			ImageIO.write(bf, "png", out);
			tmpb = out.toByteArray();
			tmpString = Base64.getEncoder().encodeToString(tmpb);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Document doc = new Document("sid", tsid.getText()).append("rno", tsrno.getValue())
				.append("department", tsdprt.getValue()).append("batch", tsbatch.getValue())
				.append("year", tsyear.getValue()).append("email", tsmail.getText()).append("address", tsaddr.getText())
				.append("studentPhone", tsphone.getText()).append("parentPhone", tpphone.getText())
				.append("img", tmpString).append("current_semester", tssem.getValue())
				.append("current_year", getYear());
		if (isFirst) {
			doc.append("name", tsname.getText());
			Engine.db.getCollection("Students").insertOne(doc);
			Button tmp = new Button(tsname.getText());
			tmp.setOnAction(this);
			if (x < 6) {
				x++;
				home.add(tmp, x - 1, y);
				GridPane.setColumnIndex(addS, x);
				GridPane.setRowIndex(addS, y);

			} else {
				x = 1;
				y++;
				home.add(tmp, x - 1, y);
				GridPane.setColumnIndex(addS, x);
				GridPane.setRowIndex(addS, y);

			}
		} else {
			Bson filter = new Document("sid", tsid.getText());
			Bson query = new Document("$set", doc);
			Engine.db.getCollection("Students").updateOne(filter, query);
		}
		syncData();

	}

	private void syncData() {

		JSONObject json = new JSONObject(Engine.db.getCollection("Students").find(eq("name", tsname.getText())).first().toJson());

		tsname.setText(json.getString("name"));
		tsid.setText(json.getString("sid"));
		tsmail.setText(json.getString("email"));
		tsaddr.setText(json.getString("address"));
		tsphone.setText(json.getString("studentPhone"));
		tpphone.setText(json.getString("parentPhone"));
		tsdprt.setValue(json.getString("department"));
		tsrno.getSelectionModel().select(json.getString("rno"));
		tsyear.getSelectionModel().select(json.getString("year"));
		tsbatch.getSelectionModel().select(json.getString("batch"));
		tssem.getSelectionModel().select(json.getString("current_semester"));

		String img = null;
		try {
			img = json.getString("img");
			try {
				byte[] imgd = Base64.getDecoder().decode(img);
				BufferedImage bf = ImageIO.read(new ByteArrayInputStream(imgd));
				dpImgView = new ImageView(SwingFXUtils.toFXImage(bf, null));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			dpImgView.setImage(new Image(getClass().getResourceAsStream(Resources.DEFAULT_PIC.VALUE)));
		}

	}

	/*
	 * This method converts semester value to its corresponding academic year.
	 * 
	 * @return String representing any one of the four years.
	 */
	private String getYear() {
		String year = null;

		switch (tssem.getValue()) {
		case "SEM 1":
		case "SEM 2":
			year = "fe";
			break;
		case "SEM 3":
		case "SEM 4":
			year = "se";

			break;
		case "SEM 5":
		case "SEM 6":
			year = "te";

			break;
		case "SEM 7":
		case "SEM 8":
			year = "be";

			break;
		}
		return year;
	}

	/*
	 * This method enables or disables all of the nodes present inside Students
	 * 
	 * @param flag A boolean value representing yes or no
	 */
	private void disableAll(Boolean flag) {

		tsname.setEditable(!flag);
		tsid.setEditable(!flag);
		tsrno.setDisable(flag);
		tsdprt.setDisable(flag);
		tsyear.setDisable(flag);
		tsbatch.setDisable(flag);
		tsmail.setEditable(!flag);
		tsaddr.setEditable(!flag);
		tsphone.setEditable(!flag);
		tpphone.setEditable(!flag);
		tssem.setDisable(flag);
		dpImgView.setDisable(flag);
		upload.setDisable(flag);
		if (!isFirst)
			del.setDisable(flag);

	}

	public void begin() {
		isFirst = true;
		createUI();
	}

	@Override
	public void handle(ActionEvent arg) {
		isFirst = false;
		createUI();
	}

}
