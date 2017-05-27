package ivn.typh.main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

public class About implements EventHandler<ActionEvent>{
	
	private double xOffset,yOffset;

	@Override
	public void handle(ActionEvent event) {
		
		if (CenterPane.menu != null)
			CenterPane.menu.fire();
		CenterPane.shade.setVisible(true);

		Alert abt = new Alert(AlertType.NONE);
		
		VBox contents = new VBox();
		HBox titleBar = new HBox();
		VBox window = new VBox();
		
		Button close = new Button("x");
		Pane space = new Pane();
		Label titleText = new Label("Typh™");
		//titleText.setGraphic(value);
		Label description = new Label("Typh™ Students Database Management System.\nVersion 1.0\nCopyright © 2017\nAuthor :- Ivan Pillay");
		
		xOffset = yOffset = 0;

		titleBar.setOnMousePressed(mouse->{
            xOffset = mouse.getSceneX();
            yOffset = mouse.getSceneY();
		});
	
		titleBar.setOnMouseDragged(mouse->{
			abt.setX(mouse.getScreenX() - xOffset);
			abt.setY(mouse.getScreenY() - yOffset);
		});
	
		titleBar.setId("about_pane_title_bar");
		contents.setId("about_pane_contents");
		window.setId("about_pane_window");
		description.setId("about_pane_description");
		titleText.setId("about_pane_title");
		close.setId("about_pane_close");
		
		HBox.setHgrow(space,Priority.ALWAYS);
		
		close.setOnAction(value->{
			Platform.runLater(()->{
				abt.setResult(ButtonType.CLOSE);
				CenterPane.shade.setVisible(false);
			});
		});
		abt.getDialogPane().getScene().setFill(Color.TRANSPARENT);
		titleBar.getChildren().addAll(space,close);
		
		contents.getChildren().addAll(titleText,description);
		window.getChildren().addAll(titleBar,contents);
		abt.initOwner(BasicUI.stage);
		abt.initStyle(StageStyle.UNDECORATED);
		abt.getButtonTypes().clear();
		abt.getDialogPane().setContent(window);
		abt.show();
		
	}

}
