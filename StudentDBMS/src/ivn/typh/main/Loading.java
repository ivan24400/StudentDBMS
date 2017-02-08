package ivn.typh.main;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Loading{

	private static Stage stage;
	private static ProgressIndicator p;
	private static Alert alert;
	
	public Loading(Stage s){
		stage=s;
		p=new ProgressIndicator();
		alert = new Alert(AlertType.NONE);

	}
	

	public void startTask(Task<Boolean> cm){
		if(p != null){
			p.setProgress(-1);
			p.progressProperty().unbind();
			p.progressProperty().bind(cm.progressProperty());
			alert.initOwner(stage);
			alert.getDialogPane().setStyle("-fx-background-color:rgba(0,0,0,0);");
			alert.getDialogPane().getScene().setFill(Color.TRANSPARENT);
			alert.initStyle(StageStyle.TRANSPARENT);
			alert.getDialogPane().setContent(p);
			alert.show();
		}
	}

	
	public void hideProgress(){
		alert.setResult(ButtonType.CLOSE);
	}
}
