package ivn.typh.admin;

import org.bson.Document;
import com.mongodb.client.MongoCollection;
import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Departments extends Dialog<String> implements EventHandler<ActionEvent> {

	static ObservableList<String> dprtList;
	static int x, y;

	private Stage parent;
	private GridPane home;
	private Button addDprt;
	private TextField dname;
	private TextField dhead;
	private TextField tlabs;
	private TextField tcrooms;
	private TextField tsrooms;
	private CheckBox library;
	private ToggleButton edit;

	public Departments(Stage arg, GridPane pane, String name, String head, String labs, String crooms) {
		this(arg);
		home = pane;
		

		dname.setText(name);
		dhead.setText(head);
		tlabs.setText(labs);
		tcrooms.setText(crooms);
	}

	public Departments(Stage arg) {
		parent = arg;
		initOwner(parent);
		dname = new TextField();
		dhead = new TextField();
		tlabs = new TextField();
		tcrooms = new TextField();
		tsrooms = new TextField();
		library = new CheckBox();
	}

	public Departments(Stage arg, GridPane pane, Button b) {
		this(arg);
		home = pane;
		addDprt = b;
	}

	public void createUI(boolean first) {

		setTitle("Department - Typh™");
		setHeaderText("Fill in required fields to add a Department");

		GridPane dPane = new GridPane();

		dPane.setPadding(new Insets(40));
		dPane.setHgap(20);
		dPane.setVgap(20);

		dname.setPromptText("Department Name");
		dhead.setPromptText("Department Head");
		tlabs.setPromptText("Number of Labs");
		tcrooms.setPromptText("Number of Class Rooms");
		tsrooms.setPromptText("Number of Staff Rooms");

		Label ldname = new Label("Department Name");
		Label ldhead = new Label("Head of the Deapartment");
		Label ltlabs = new Label("Total number of labs");
		Label ltcrooms = new Label("Total number of class rooms");
		Label ltsrooms = new Label("Total number of staff rooms");
		Label lib = new Label("Library");
		lib.setGraphic(library);
		lib.setContentDisplay(ContentDisplay.RIGHT);
		
		HBox lbox =new HBox(lib);
		lbox.setAlignment(Pos.BASELINE_CENTER);
		Tooltip tool = new Tooltip();

		if (!first)
			addEdit(dPane);

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

		tlabs.textProperty().addListener((obs, o, n) -> {

			if (!n.matches("\\d*")) {
				tlabs.setText(tlabs.getText().replaceAll("[^\\d]", ""));
				tool.setText("Enter numbers only !");
				Bounds p = tlabs.getBoundsInLocal();
				Bounds screen = tlabs.localToScreen(p);
				tool.show(tlabs, screen.getMinX() + tlabs.getCaretPosition(),
						screen.getMinY() + tlabs.getScene().getY());
			} else if (tlabs.getText().length() > 5) {
				tool.setText("Cannot add more rooms !");
				Bounds screen = tlabs.localToScreen(tlabs.getBoundsInLocal());
				tool.show(tlabs, screen.getMinX() + tlabs.getCaretPosition(),
						screen.getMinY() + tlabs.getScene().getY());
				Platform.runLater(() -> {
					tlabs.setText(tlabs.getText().substring(0, 5));
					tlabs.positionCaret(tlabs.getText().length());
				});
			}
		});

		tcrooms.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				tool.setText("Enter numbers only !");
				Bounds screen = tcrooms.localToScreen(tcrooms.getBoundsInLocal());
				tool.show(tcrooms, screen.getMinX() + tcrooms.getCaretPosition(),
						screen.getMinY() + tcrooms.getScene().getY());
				tcrooms.setText(tcrooms.getText().replaceAll("[^\\d]", ""));
			} else if (tcrooms.getText().length() > 5) {
				tool.setText("Cannot add more rooms !");
				Bounds screen = tcrooms.localToScreen(tcrooms.getBoundsInLocal());
				tool.show(tcrooms, screen.getMinX() + tcrooms.getCaretPosition(),
						screen.getMinY() + tcrooms.getScene().getY());
				Platform.runLater(() -> {
					tcrooms.setText(tcrooms.getText().substring(0, 5));
					tcrooms.positionCaret(tcrooms.getText().length());
				});
			}
		});
		
		tsrooms.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				tool.setText("Enter numbers only !");
				Bounds screen = tsrooms.localToScreen(tsrooms.getBoundsInLocal());
				tool.show(tsrooms, screen.getMinX() + tsrooms.getCaretPosition(),
						screen.getMinY() + tsrooms.getScene().getY());
				tsrooms.setText(tsrooms.getText().replaceAll("[^\\d]", ""));
			} else if (tsrooms.getText().length() > 5) {
				tool.setText("Cannot add more rooms !");
				Bounds screen = tsrooms.localToScreen(tsrooms.getBoundsInLocal());
				tool.show(tsrooms, screen.getMinX() + tsrooms.getCaretPosition(),
						screen.getMinY() + tsrooms.getScene().getY());
				Platform.runLater(() -> {
					tsrooms.setText(tsrooms.getText().substring(0, 5));
					tsrooms.positionCaret(tsrooms.getText().length());
				});
			}
		});
		

		dhead.setOnMouseMoved(value -> tool.hide());
		tlabs.setOnMouseMoved(value -> tool.hide());
		tcrooms.setOnMouseMoved(value -> tool.hide());
		tsrooms.setOnMouseMoved(value -> tool.hide());

		dPane.add(ldname, 0, 0);
		dPane.add(dname, 1, 0);
		dPane.add(ldhead, 0, 1);
		dPane.add(dhead, 1, 1);
		dPane.add(ltlabs, 0, 2);
		dPane.add(tlabs, 1, 2);
		dPane.add(ltcrooms, 0, 3);
		dPane.add(tcrooms, 1, 3);
		dPane.add(ltsrooms, 0, 4);
		dPane.add(tsrooms, 1, 4);
		dPane.add(lbox, 0, 5,2,1);
		
		lib.setAlignment(Pos.BASELINE_CENTER);
		ButtonType save = new ButtonType("Save", ButtonData.OK_DONE);
		getDialogPane().setContent(dPane);
		getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
		setResultConverter((arg) -> {
			if (arg.equals(save) && (!areFieldsEmpty())) {
				dprtList.add(dname.getText());
				addButton();
			} else if (arg.equals(save) && (areFieldsEmpty()))
				Notification.message(parent, AlertType.ERROR, "Error - Department - Typh™",
						"All Fields are Mandatory.");
			hide();
			return null;

		});
		showAndWait();
	}

	private boolean areFieldsEmpty() {
		if (dname.getText().trim().isEmpty() || dhead.getText().trim().isEmpty() || tlabs.getText().trim().isEmpty()
				|| tcrooms.getText().trim().isEmpty())
			return true;
		else
			return false;
	}

	public void addButton() {

		Button tmp = new Button(dname.getText());
		Document document = new Document("department", dname.getText()).append("hod", dhead.getText())
				.append("classrooms", tcrooms.getText()).append("laboratory", tlabs.getText());
		MongoCollection<Document> collection = Engine.db.getCollection("Departments");
		collection.insertOne(document);
		tmp.setOnAction(new Departments(parent));
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

	}

	public void edAllFields(boolean flag) {
		dname.setEditable(flag);
		dname.setFocusTraversable(flag);

		dhead.setEditable(flag);
		dhead.setFocusTraversable(flag);

		tlabs.setEditable(flag);
		tlabs.setFocusTraversable(flag);

		tcrooms.setEditable(flag);
		tcrooms.setFocusTraversable(flag);

	}

	public void addEdit(GridPane pane) {
		HBox seBox = new HBox();
		seBox.setPadding(new Insets(20));
		seBox.setSpacing(20);
		seBox.setAlignment(Pos.CENTER);

		edAllFields(true);
		edit = new ToggleButton("Edit");
		edit.selectedProperty().addListener((arg, o, n) -> {
			edAllFields(arg.getValue());
		});
		seBox.getChildren().add(edit);
		pane.add(seBox, 0, 6, 2, 1);

	}

	@Override
	public void handle(ActionEvent arg) {
		createUI(false);
	}

	public void begin() {
		createUI(true);
	}
}
