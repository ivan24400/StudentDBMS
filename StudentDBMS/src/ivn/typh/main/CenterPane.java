package ivn.typh.main;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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

/*
 * This is the center of the border pane which is the scene.
 * It stacks :
 * [i]   The nodes which users interact with.
 * [ii]  A grey colored pane.
 * [iii] A menu bar.
 * <p>
 * It can also be used to popup messages during progress of operations.
 */
public class CenterPane extends StackPane{

	public static Button menu;
	
	private static Alert alert;
	private static HBox dialogPane;
	private static Label loading;
	private ReadOnlyDoubleProperty property;
	
	public static Pane shade;
	
	public CenterPane(Node pane){
		super();
		shade =  new Pane();
		alert = new Alert(AlertType.NONE);
		alert.initOwner(BasicUI.stage);
		alert.initStyle(StageStyle.TRANSPARENT);

		loading = new Label("Loading . . .");
		loading.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(Resources.LOADING.VALUE))));
		
		shade.setVisible(false);
		shade.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,0.2),CornerRadii.EMPTY,Insets.EMPTY)));
		
		getChildren().clear();
		getChildren().addAll(pane,shade);
	}
	
	public CenterPane(Node pane, Node sidebar){
		this(pane);
		getChildren().add(sidebar);
	}
	
	/*
	 * This method changes the frame which consists of all the nodes that interact with the user.
	 * @param pane The new pane which is to be replaced by the older one.
	 * @param sidebar The side bar
	 */
	public void changeRootPane(Node pane,Node sidebar){
		Platform.runLater(()->{
			getChildren().remove(0);
			getChildren().add(0, pane);
			getChildren().add(sidebar);
				
		});
	}
	
	/*
	 * This method is used to popup messages on screen. 
	 * Particularly used to display progress of long tasks.
	 * @param message The message to display on the label
	 * @param isText To indicate is it a text or progress message.
	 */
	public void showMessage(String message,boolean isText){
	
		dialogPane = new HBox();
		loading.setText("\t"+message+"\t\t\t");

		if(isText){
			dialogPane.getChildren().add(loading);
		}else{
			ProgressIndicator progress = new ProgressIndicator();
			progress.progressProperty().bind(property);
			dialogPane.getChildren().addAll(progress,loading);
		}
		
 		alert.getDialogPane().setContent(dialogPane);

		Platform.runLater(()->{
			shade.setVisible(true);
			alert.show();	
		});				
	
	}
	
	/*
	 * This method shows a message
	 * @param message The contents of the message to be displayed.
	 */
	
	public void showMessage(String message){
		showMessage(message,true);
	
	}
	
	/*
	 * This method is used to show a default message.
	 */

	public void showMessage(){
		showMessage("Loading . . . ");
	}
	
	/*
	 * This method is used to show progress box
	 * @param message The contents of the message to display.
	 * @param prpty a progress property of the task.
	 */
	public void showProgress(String message,ReadOnlyDoubleProperty prpty){
		property = prpty;
		showMessage(message,false);
	}
	
	/*
	 * This method hides the progress box.
	 */
	public void hideMessage(){
	
				Platform.runLater(()->{
					shade.setVisible(false);
					CenterPane.alert.setResult(ButtonType.CLOSE);
					CenterPane.alert.close();
				});				
			
	}
	
	/*
	 * This method changes the visibility status of the grey colored scene.
	 * @param flag A boolean value representing to enable or disable the scene.
	 */
	public void setShadeVisible(boolean flag){
		Platform.runLater(()->{
			shade.setVisible(flag);
		});
	}
}
