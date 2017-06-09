package ivn.typh.tchr;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.Block;
import com.mongodb.client.gridfs.model.GridFSFile;

import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;

/*
 * This method creates the UI for the project pane.
 */
public class Project {

	public static Group recycle;
	public static GridPane projects;
	public static ListView<String> prList;
	public static Map<String, String> prPath;
	public static SVGPath bin;
	public static SVGPath bin_lid;
	public static SVGPath bin_handle;

	static void setup() {
		Components.scroll[Components.paneList.length - (Components.paneCount)] = new ScrollPane();
		projects = new GridPane();
		prPath = new HashMap<>();
		Button upload = new Button("Upload");
		recycle = new Group();
		bin = new SVGPath();
		bin_lid = new SVGPath();
		bin_handle = new SVGPath();

		String box = "M 0 50 H 300 V 220 H 0 z";
		String lid = "M 0 20 H 300 V 40 H 0 Z";
		String lid_handle = "M 120 20 L 130 0 L 160 0 L 170 20";

		bin.setContent(box);
		bin_lid.setContent(lid);
		bin_handle.setContent(lid_handle);

		bin.setId("bin");
		bin_lid.setId("bin_lid");
		bin_handle.setId("bin_handle");

		prList = new ListView<>();
		prList.setPrefWidth(600);
		prList.setTooltip(new Tooltip("Drag and Drop Files Over Here"));

		upload.setMaxWidth(Double.MAX_VALUE);
		upload.setOnAction(event -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Upload a Project - Typh™");
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Compressed files only", "*.zip",
					"*.rar", "*.tar", "*.7z", "*.xz", "*.gz");
			fc.getExtensionFilters().add(filter);
			File uploadFile = fc.showOpenDialog(Components.scene.getWindow());
			prPath.put(uploadFile.getName(), uploadFile.getAbsolutePath());
			prList.getItems().add(uploadFile.getName());
		});

		prList.setCellFactory((arg0) -> {

			return (new ProjectData(Components.scene));

		});

		Tooltip.install(recycle, new Tooltip("Drag Projects here to delete them"));
		ParallelTransition pt = new ParallelTransition();
		TranslateTransition bint = new TranslateTransition();
		TranslateTransition binldt = new TranslateTransition();
		bint.setNode(bin_lid);
		binldt.setNode(bin_handle);

		bin.setOnDragEntered(event -> {
			Platform.runLater(() -> {
				pt.getChildren().clear();
				bint.setByY(-10.0);
				binldt.setByY(-10.0);
				pt.getChildren().addAll(bint, binldt);
				pt.play();
			});

		});

		bin.setOnDragExited(event -> {
			Platform.runLater(() -> {
				pt.getChildren().clear();
				bint.setByY(10.0);
				binldt.setByY(10.0);
				pt.getChildren().addAll(bint, binldt);
				pt.play();
			});
		});

		bin.setOnDragOver(value -> {
			if (value.getGestureSource() != null) {
				value.acceptTransferModes(TransferMode.MOVE);
			}

		});
		bin.setOnDragDropped(value -> {
			Dragboard db = value.getDragboard();
			boolean success = false;
			if (value.getDragboard().hasString()) {
				int index = prList.getItems().indexOf(db.getString());
				prList.getItems().remove(index);
				Notification.message(Components.stage, AlertType.INFORMATION, "Project - Typh™",
						"Project " + db.getString() + " deleted !!!");
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
					prPath.put(file.getName(), file.getAbsolutePath());
					prList.getItems().add(file.getName());
				}
			}
			arg0.setDropCompleted(success);
			arg0.consume();
		});

		projects.setId("projectsP");
		prList.setCache(true);
		prList.setCacheShape(true);
		prList.setCacheHint(CacheHint.SPEED);
		bin.setCache(true);
		bin.setCacheHint(CacheHint.SPEED);
		bin_lid.setCache(true);
		bin_lid.setCacheHint(CacheHint.SPEED);
		bin_handle.setCache(true);
		bin_handle.setCacheHint(CacheHint.SPEED);

		recycle.getChildren().addAll(bin_handle, bin_lid, bin);

		Platform.runLater(() -> {
			projects.add(upload, 0, 0);
			projects.add(prList, 1, 0, 1, 2);
			projects.add(recycle, 0, 1);

		});
		Components.scroll[Components.paneList.length - (Components.paneCount--)].setContent(projects);

	}

	/*
	 * This method creates project data.
	 * 
	 * @param yr The year value.
	 */

	static void loadProjectData(String yr) {
		prList.getItems().clear();
		Engine.gfs.find().forEach(new Block<GridFSFile>() {
			public void apply(final GridFSFile file) {
				String name = file.getFilename().split(":")[1];
				String gfsid = file.getFilename().split(":")[0];
				int year = Integer.parseInt(gfsid.substring(2, 4));
				if (year == TchrUI.sMatchesY(0, yr)) {
					Platform.runLater(() -> {
						prList.getItems().add(name);

					});
				}
			}
		});
	}

}
