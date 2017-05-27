package ivn.typh.admin;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Departments extends Dialog<String> implements EventHandler<ActionEvent> {

	public static ObservableMap<String, String> dprtList;
	static int x, y;
	
	private int counter;

	private boolean isFirst;
	private int saveAdded;
	private Stage stage;
	private GridPane home;
	private Button addDprt;
	private Button del;
	private TextField dname;
	private TextField dhead;
	private ChoiceBox<String> tlabs;
	private ChoiceBox<String> tcrooms;
	private ChoiceBox<String> tsrooms;
	private ChoiceBox<String> departmentId;
	private String slab;
	private String scroom;
	private String ssroom, did;
	private CheckBox library;
	private ToggleButton edit;

	public Departments(Stage arg, GridPane pane, String name, String head, String labs, String crooms, String srooms,
			String id, boolean lib) {
		this(arg);
		home = pane;
		library.setSelected(lib);
		dname.setText(name);
		dhead.setText(head);
		slab = labs;
		scroom = crooms;
		ssroom = srooms;
		did = id;
		dprtList.put(id,name);
	}

	public Departments(Stage arg) {
		stage = arg;
		initOwner(stage);
		dname = new TextField();
		dhead = new TextField();
		tlabs = new ChoiceBox<>();
		tcrooms = new ChoiceBox<>();
		tsrooms = new ChoiceBox<>();
		departmentId = new ChoiceBox<>();
		library = new CheckBox();
	}

	public Departments(Stage arg, GridPane pane, Button b) {
		this(arg);
		home = pane;
		addDprt = b;
	}

	public void createUI() {
		setTitle("Department - Typh™");

		GridPane dPane = new GridPane();
		Label lib = new Label("Library");
		HBox lbox = new HBox(lib);

		lib.setGraphic(library);
		lib.setContentDisplay(ContentDisplay.RIGHT);
		lbox.setAlignment(Pos.BASELINE_CENTER);

		dPane.setPadding(new Insets(40));
		dPane.setHgap(20);
		dPane.setVgap(20);

		dname.setPromptText("Department Name");
		dhead.setPromptText("Department Head");

		Tooltip tool = new Tooltip();

		dhead.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\D*")) {
				tool.setText("Enter text only !");
				Bounds screen = dhead.localToScreen(dhead.getBoundsInLocal());
				tool.show(dhead, screen.getMinX() + dhead.getCaretPosition(),
						screen.getMinY() + dhead.getScene().getY());
				Platform.runLater(() -> {
					dhead.setText(o);
					dhead.positionCaret(dhead.getText().length());
				});
			}
		});

		dhead.setOnMouseMoved(value -> tool.hide());
		tlabs.setOnMouseMoved(value -> tool.hide());
		tcrooms.setOnMouseMoved(value -> tool.hide());
		tsrooms.setOnMouseMoved(value -> tool.hide());
		addEdit(dPane);

		dPane.add(new Label("Department Name"), 0, 0);
		dPane.add(dname, 1, 0);
		dPane.add(new Label("Department ID"), 0, 1);
		dPane.add(departmentId, 1, 1);
		dPane.add(new Label("Head of the Deapartment"), 0, 2);
		dPane.add(dhead, 1, 2);
		dPane.add(new Label("Total number of labs"), 0, 3);
		dPane.add(tlabs, 1, 3);
		dPane.add(new Label("Total number of class rooms"), 0, 4);
		dPane.add(tcrooms, 1, 4);
		dPane.add(new Label("Total number of staff rooms"), 0, 5);
		dPane.add(tsrooms, 1, 5);
		dPane.add(lbox, 0, 6, 2, 1);

		lib.setAlignment(Pos.BASELINE_CENTER);
		getDialogPane().setContent(dPane);

		if (!isFirst) {
			if (saveAdded == 0) {
				ButtonType save = new ButtonType("Save", ButtonData.OK_DONE);
				getDialogPane().getButtonTypes().clear();
				getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
				initRoom();
			}
			saveAdded++;
			setHeaderText(dname.getText().substring(0, 1).toUpperCase() + dname.getText().substring(1) + " Department");
			tlabs.getSelectionModel().select(slab);
			tcrooms.getSelectionModel().select(scroom);
			tsrooms.getSelectionModel().select(ssroom);
			departmentId.getSelectionModel().select(did);
			disableAll(true);
		} else if (isFirst) {
			setHeaderText("Fill in required fields to add a Department");
			initRoom();
			tlabs.getSelectionModel().selectFirst();
			tsrooms.getSelectionModel().selectFirst();
			tcrooms.getSelectionModel().selectFirst();
			departmentId.getSelectionModel().selectFirst();
			getDialogPane().getButtonTypes().clear();
			getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		}

		setResultConverter((arg) -> {
			if ((arg.equals(ButtonType.OK) || arg.getButtonData().equals(ButtonData.OK_DONE)) && (!areFieldsEmpty())) {
				dprtList.put(departmentId.getValue(), dname.getText());
				addDepartment();
			} else if ((arg.equals(ButtonType.OK) || arg.getButtonData().equals(ButtonData.OK_DONE))
					&& (areFieldsEmpty()))
				Notification.message(stage, AlertType.ERROR, "Error - Department - Typh™",
						"All Fields are Mandatory.");
			return null;

		});

		Platform.runLater(()->{
			show();
		});
	}

	private void initRoom() {
		for ( counter = 1; counter <= 99; counter++) {
			tlabs.getItems().add(String.format("%02d", counter));
			tsrooms.getItems().add(String.format("%02d", counter));
			tcrooms.getItems().add(String.format("%02d", counter));
			if(!dprtList.keySet().contains(String.format("%02d", counter)))
				departmentId.getItems().add(String.format("%02d", counter));
			}
	}

	private boolean areFieldsEmpty() {
		if (dname.getText().trim().isEmpty() || dhead.getText().trim().isEmpty())
			return true;
		else
			return false;
	}

	public void addDepartment() {

		Button tmp = new Button(dname.getText());
		Document document = new Document("hod", dhead.getText())
				.append("classrooms", tcrooms.getSelectionModel().getSelectedItem())
				.append("laboratory", tlabs.getSelectionModel().getSelectedItem())
				.append("library", library.isSelected());
		MongoCollection<Document> collection = Engine.db.getCollection("Departments");
		if (isFirst) {
			document.append("department", dname.getText());
			collection.insertOne(document);
			tmp.setOnAction(this);
			if (x < 6) {
				x++;
				home.add(tmp, x - 1, y);
				GridPane.setColumnIndex(addDprt, x);
				GridPane.setRowIndex(addDprt, y);

			} else {
				x = 1;
				y++;
				home.add(tmp, x - 1, y);
				GridPane.setColumnIndex(addDprt, x);
				GridPane.setRowIndex(addDprt, y);

			}
		} else {
			Bson filter = new Document("department", dname.getText());
			Bson query = new Document("$set", document);
			collection.updateOne(filter, query);
		}

	}

	public void disableAll(boolean flag) {
		dname.setEditable(!flag);

		dhead.setEditable(!flag);

		tlabs.setDisable(flag);

		tcrooms.setDisable(flag);

		tsrooms.setDisable(flag);

		departmentId.setDisable(flag);
		library.setDisable(flag);
		if(!isFirst)
			del.setDisable(flag);
	}

	public void addEdit(GridPane pane) {
		if (!isFirst) {
			HBox seBox = new HBox();
			seBox.setPadding(new Insets(50));
			seBox.setSpacing(20);
			seBox.setAlignment(Pos.CENTER);
			del = new Button("Delete");
			edit = new ToggleButton("Edit");
			edit.selectedProperty().addListener((arg, o, n) -> {
				disableAll(!n);
			});
			del.setOnAction(val -> {
				Alert dalert = new Alert(AlertType.CONFIRMATION);
				dalert.setTitle("Delete a Department - Typh™");
				dalert.setHeaderText("Are you sure to delete Department of : " + dname.getText() + "?");
				dalert.initOwner(this.getDialogPane().getScene().getWindow());
				dalert.setResultConverter(value -> {
					if (value.equals(ButtonType.OK)) {
						Bson query = new Document("department", dname.getText());
						Engine.db.getCollection("Departments").deleteOne(query);
						Stage s_t = stage;
						BorderPane bp_t = Components.pane;
						ToolBar b_t=Components.menuBar;
						Platform.runLater(()->{
							//stage.close();
							(new Thread(new AdminUI(s_t,bp_t,b_t))).start();
						});
						
					}
					return null;
				});
			Platform.runLater(()->{
				dalert.show();
			});	

			});
			seBox.getChildren().addAll(edit, del);

			pane.add(seBox, 0, 7, 2, 1);
		}
	}

	@Override
	public void handle(ActionEvent arg) {
		isFirst = false;
		createUI();
	}

	public void begin() {
		isFirst = true;
		createUI();
	}
}
