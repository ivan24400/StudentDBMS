package ivn.typh.main;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Loading{

	private static Stage stage;
//	private static ProgressIndicator p;
	private static ProgressBar p;
	private static Alert alert;
	
	public Loading(Stage s){
		stage=s;
		p=new ProgressBar();
	}
	
	public static void createUI(){
		alert = new Alert(AlertType.NONE);
		alert.initOwner(stage);
		alert.initStyle(StageStyle.TRANSPARENT);
		//alert.initModality(Modality.WINDOW_MODAL);
		alert.getDialogPane().setContent(p);
		alert.show();
	}
	
	public void setTask(Task<Void> task){
		if(p != null){
			p.progressProperty().bind(task.progressProperty());
			createUI();

		}
	}

	public void showProgress(){
		createUI();
	}
	
	public void hideProgress(){
		alert.setResult(ButtonType.CLOSE);
	}
}
