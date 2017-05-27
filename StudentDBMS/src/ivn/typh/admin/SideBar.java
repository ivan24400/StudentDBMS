package ivn.typh.admin;

import org.bson.Document;
import org.bson.conversions.Bson;

import ivn.typh.main.BasicUI;
import ivn.typh.main.CenterPane;
import ivn.typh.main.Engine;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SideBar extends VBox {

	private Button menu;
	private final double width = 300;
	static Label rts, rtu, rll;

	public SideBar() {
		menu = CenterPane.menu;
		setMinWidth(width);
		setMaxWidth(width);
		setPrefWidth(width);

		Button about = ((Button) Components.menuBar.getItems().get(3));
		Button help = ((Button) Components.menuBar.getItems().get(2));
		
	
		
		Button instituteName = new Button("Change Institute Name");
		instituteName.setId("side-menu-button");
		instituteName.setOnAction(event -> {
			Dialog<?> dialog = new Dialog<Object>();
			VBox pane = new VBox();
			pane.setId("institute_dialog");
			Label label = new Label("Enter new name:-");
			TextField tf = new TextField();
			tf.setPromptText(">");
			pane.getChildren().addAll(label, tf);
			ButtonType apply = new ButtonType("Apply", ButtonData.APPLY);

			dialog.setTitle("Institute Name - Typh™");
			dialog.initOwner(Components.stage);
			dialog.getDialogPane().getButtonTypes().addAll(apply, ButtonType.CANCEL);
			dialog.getDialogPane().setContent(pane);

			Node apply_t = dialog.getDialogPane().lookupButton(apply);
			apply_t.setDisable(true);
			tf.textProperty().addListener((obs, o, n) -> {
				apply_t.setDisable((n.trim().isEmpty()));
			});

			dialog.setResultConverter(button -> {
				ButtonType tmp = button;
				if (tmp.getButtonData().equals(ButtonData.APPLY)) {
					BasicUI.institute.setText(tf.getText());
					Bson newv = new Document("instituteName", BasicUI.institute.getText());
					Bson query = new Document("user", "admin");
					Engine.db.getCollection("Users").updateOne(query, new Document("$set", newv));
				}
				return null;
			});
			dialog.show();
		});

		Pane space = new Pane();

		VBox.setVgrow(space, Priority.ALWAYS);

		Platform.runLater(() -> {
			setId("sideBar");
			about.setId("side-menu-button");
			help.setId("side-menu-button");
			getChildren().addAll(Components.accNamePane, Components.accDescPane, space,instituteName, help, about);
			getChildren().forEach(node -> VBox.setVgrow(node, Priority.ALWAYS));

		});
		

		menu.setOnAction(arg -> {

			final Animation show = new Transition() {
				{
					setCycleDuration(Duration.millis(240));
				}

				@Override
				protected void interpolate(double fraction) {
					final double newWidth = width * fraction;
					setTranslateX(newWidth - width);
				}

			};

			final Animation hide = new Transition() {
				{
					setCycleDuration(Duration.millis(240));
				}

				@Override
				protected void interpolate(double frac) {
					final double newWidth = width * (1.0 - frac);
					setTranslateX(newWidth - width);
				}
			};

			hide.setOnFinished(value -> {
				setVisible(false);
			});

			if (show.statusProperty().get() == Animation.Status.STOPPED
					&& hide.statusProperty().get() == Animation.Status.STOPPED) {
				if (isVisible()) {
					hide.play();
					CenterPane.shade.setVisible(false);
				} else {
					CenterPane.shade.setVisible(true);
					setVisible(true);
					show.play();
				}
			}
		});

		setVisible(false);
	}

}
