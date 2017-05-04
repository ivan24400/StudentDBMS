package ivn.typh.main;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
		alert.initOwner(BasicUI.stage);
		alert.initStyle(StageStyle.TRANSPARENT);

		loading = new Label("Loading . . .");
		loading.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(Resources.LOADING.path))));
		
		shade.setVisible(false);
		shade.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,0.2),CornerRadii.EMPTY,Insets.EMPTY)));
		
		getChildren().clear();
		getChildren().addAll(pane,shade);
	}
	
	public CenterPane(Node pane, Node sidebar){
		this(pane);
		getChildren().add(sidebar);
	}
	
	public void changeRootPane(Node pane,Node sidebar){
		Platform.runLater(()->{
			getChildren().remove(0);
			getChildren().add(0, pane);
			getChildren().add(sidebar);
				
		});
	}
	
	public void showMessage(String message){

				loading.setText("\t"+message);

				dialogPane = new HBox();
				dialogPane.setId("loadingPane");
				dialogPane.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,0),CornerRadii.EMPTY,Insets.EMPTY)));
				dialogPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-padding: 10 50 0 0;");
				dialogPane.getChildren().add(loading);
		 		alert.getDialogPane().setContent(dialogPane);

				Platform.runLater(()->{
					shade.setVisible(true);
					alert.show();	
				});				
			
		
	}

	public void showMessage(){
		showMessage("Loading . . . ");
	}
	
	public void hideMessage(){
	
				Platform.runLater(()->{
					System.out.println("CenterPane: hideMessage");
					shade.setVisible(false);
					CenterPane.alert.setResult(ButtonType.CLOSE);
					CenterPane.alert.close();
				});				
			
	}
	
	public void setShadeVisible(boolean flag){
		Platform.runLater(()->{
			shade.setVisible(flag);
		});
	}
}
