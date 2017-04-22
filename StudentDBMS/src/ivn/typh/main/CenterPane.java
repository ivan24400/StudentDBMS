package ivn.typh.main;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CenterPane extends StackPane{

	private static Alert alert;
	private static HBox dialogPane;
	private static Label loading;
	
	public static Pane shade;
	
	public CenterPane(Node pane){
		super();
		shade =  new Pane();
		alert = new Alert(AlertType.NONE);
		
		loading = new Label("Loading ...");
	//	loading.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(Resources.LOADING.path))));
        loading.setStyle("-fx-background-image: url(\"/ivn/typh/main/icons/gifs/loading_triple.gif\");");


		shade.setVisible(false);
		shade.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,0.2),CornerRadii.EMPTY,Insets.EMPTY)));
		
		getChildren().clear();
		getChildren().addAll(pane,shade);

	}
	
	public CenterPane(Stage s, Node pane, Node sidebar){
		this(pane);
		getChildren().add(sidebar);
	}
	
	public static void showMessage(String message){
		loading.setText(message);

		alert.initOwner(BasicUI.stage);
		alert.initStyle(StageStyle.TRANSPARENT);
		dialogPane = new HBox();
		dialogPane.setId("loadingPane");
		dialogPane.getChildren().add(loading);
		alert.getDialogPane().setContent(dialogPane);
		
		
		Platform.runLater(()->{
			CenterPane.shade.setVisible(true);
			CenterPane.alert.show();	
		});
	}

	
	public static void hideMessage(){
		Platform.runLater(()->{
			CenterPane.shade.setVisible(false);
			CenterPane.alert.setResult(ButtonType.OK);
		});
	}
}
