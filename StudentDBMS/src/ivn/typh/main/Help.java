package ivn.typh.main;

import java.util.Set;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.StageStyle;

/*
 * This class provides directions and requirements of this software in a window frame.
 */
public class Help implements EventHandler<ActionEvent>{

	private double xOffset,yOffset;

	@Override
	public void handle(ActionEvent event) {

		if (CenterPane.menu != null)
			CenterPane.menu.fire();
		CenterPane.shade.setVisible(true);
		
		Alert helpPage = new Alert(AlertType.NONE);
		
		VBox window = new VBox();
		VBox contents = new VBox();
		HBox titleBar = new HBox();
		ProgressBar progress = new ProgressBar();
		WebView helpPane = new WebView();
		Button close = new Button("X");
		
		xOffset = yOffset = 0;
		
		progress.progressProperty().bind(helpPane.getEngine().getLoadWorker().progressProperty());
		progress.prefWidthProperty().bind(window.widthProperty());
		progress.setPrefHeight(5);
		
		contents.setId("help_pane_contents");
		window.setId("help_pane_window");
		close.setId("help_pane_close");
		helpPane.setId("help_pane");
		
		helpPane.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
		      @Override 
		      public void onChanged(Change<? extends Node> change) {
		        Set<Node> deadScrolls = helpPane.lookupAll(".scroll-bar");
		        for (Node scroll : deadScrolls) {
		          scroll.setVisible(false);
		        }
		      }
		    });
		 
		close.setOnAction(value->{
			helpPage.setResult(ButtonType.CLOSE);
			CenterPane.shade.setVisible(false);

		});
		Pane space = new Pane();
		HBox.setHgrow(space,Priority.ALWAYS);

		helpPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		titleBar.setOnMousePressed(mouse->{
                xOffset = mouse.getSceneX();
                yOffset = mouse.getSceneY();
        });
		
		titleBar.setOnMouseDragged(mouse->{
			helpPage.setX(mouse.getScreenX() - xOffset);
			helpPage.setY(mouse.getScreenY() - yOffset);
		});
		
		titleBar.getChildren().addAll(space,close);
		contents.getChildren().add(helpPane);
		window.getChildren().addAll(titleBar,progress,contents);
		
		helpPage.getButtonTypes().clear();
		helpPage.initOwner(BasicUI.stage);
		helpPage.initStyle(StageStyle.TRANSPARENT);
		helpPage.getDialogPane().setContent(window);

		helpPage.show();		
		
		helpPane.getEngine().load(getClass().getResource("/ivn/typh/main/raw/help/help.html").toExternalForm());

	}

}
