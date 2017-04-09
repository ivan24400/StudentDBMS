package ivn.typh.tchr;

import static com.mongodb.client.model.Filters.eq;

import java.util.Iterator;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class Assignment {

	public static GridPane assignment;
	public static ListView<AssignmentData> asList;
	public static Button addAssignment;
	public static Button removeAssignment;


	static void setup(){
		Components.scroll[Components.paneList.length - (Components.paneCount)] = new ScrollPane();
		assignment = new GridPane();

		ScrollPane spAssignment = new ScrollPane();

		addAssignment = new Button("Add an Assignment");
		removeAssignment = new Button("Remove an Assignment");
		asList = new ListView<>();

		asList.setPrefWidth(600);

		GridPane.setFillWidth(asList, true);
		spAssignment.setContent(asList);
		spAssignment.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spAssignment.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		StringConverter<AssignmentData> converter = new StringConverter<AssignmentData>() {

			@Override
			public AssignmentData fromString(String arg0) {
				return null;
			}

			public String toString(AssignmentData assignment) {
				return "[Semester " + assignment.getSem() + "]\t" + assignment.getTitle();
			}

		};
		asList.setCellFactory(CheckBoxListCell.forListView(AssignmentData::completedProperty, converter));
		addAssignment.setOnAction(arg0 -> {
			Dialog<AssignmentData> dialog = new Dialog<>();
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
					return new AssignmentData(asYear.getSelectionModel().getSelectedIndex() + 1, asTitle.getText(), false);
				} else if (value.getButtonData().equals(ButtonData.OK_DONE) && asTitle.getText().isEmpty()) {

				}
				return null;
			});

			asTitle.setPrefWidth(500);

			dialog.initOwner(Components.stage);
			Optional<AssignmentData> result = dialog.showAndWait();
			result.ifPresent(arg -> asList.getItems().add(arg));
		});

		removeAssignment.setTooltip(new Tooltip("Deletes last assignment by default"));
		removeAssignment.setOnAction(value -> {
			if (asList.getSelectionModel().getSelectedIndex() != -1) {
				asList.getItems().remove(asList.getSelectionModel().getSelectedIndex());
			} else {
				Notification.message(Components.stage, AlertType.ERROR, "Assignments - Typh™",
						"First select an assignment");
			}
		});

		assignment.setId("assignmentP");
		asList.setCache(true);               
		asList.setCacheShape(true);          
		asList.setCacheHint(CacheHint.SPEED);
		
		Platform.runLater(()->{
			assignment.add(addAssignment, 2, 0);
			assignment.add(removeAssignment, 3, 0);
			assignment.add(spAssignment, 0, 1, 4, 1);

			
		});
		Components.scroll[Components.paneList.length - (Components.paneCount)].setContent(assignment);
		Components.scroll[Components.paneList.length - (Components.paneCount--)].setHbarPolicy(ScrollBarPolicy.NEVER);
	}
	
	 static void loadAssignmentData(String n) {
		JSONArray jsona = null;
		try {
			String data = Engine.db.getCollection("Students").find(eq("sid", Personal.tsid.getText())).first()
					.toJson();
			jsona = new JSONObject(data).getJSONArray(n.toLowerCase() + "Assignments");

			Iterator<?> it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject json = (JSONObject) it.next();
				String title = json.getString("title");
				int semester = json.getInt("sem");
				boolean flag = json.getBoolean("completed");
				Platform.runLater(()->{
					asList.getItems().clear();
					asList.getItems().add(new AssignmentData(semester, title, flag));

				});
			}
		} catch (JSONException | NullPointerException e) {
		}
	}
}
