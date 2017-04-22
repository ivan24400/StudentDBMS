package ivn.typh.main;

import java.util.Set;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.StageStyle;

public class Help implements EventHandler<ActionEvent>{

	private double xOffset,yOffset;

	@Override
	public void handle(ActionEvent event) {
		Alert helpPage = new Alert(AlertType.NONE);
		
		VBox window = new VBox();
		VBox contents = new VBox();
		HBox titleBar = new HBox();
		
		Button close = new Button("X");
		
		xOffset = yOffset = 0;
		WebView helpPane = new WebView();
		helpPane.getEngine().load(getClass().getResource("/ivn/typh/main/raw/help/help.html").toExternalForm());
		
		contents.setId("help_pane_contents");
		window.setId("help_pane_window");
		close.setId("help_pane_close");
		helpPane.setId("help_pane");
		
		helpPane.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
		      @Override 
		      public void onChanged(Change<? extends Node> change) {
		        Set<Node> deadSeaScrolls = helpPane.lookupAll(".scroll-bar");
		        for (Node scroll : deadSeaScrolls) {
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
		window.getChildren().addAll(titleBar,contents);
		
		helpPage.getButtonTypes().clear();
		helpPage.initOwner(BasicUI.stage);
		helpPage.initStyle(StageStyle.TRANSPARENT);
		helpPage.getDialogPane().setContent(window);
		CenterPane.shade.setVisible(true);

		helpPage.show();		
	}

}
