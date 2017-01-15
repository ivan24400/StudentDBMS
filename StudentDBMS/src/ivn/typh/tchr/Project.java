package ivn.typh.tchr;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class Project extends ListCell<String>{
	
	private HBox pane;
	private Pane dummy;
	private Button download;
	private Label label;
	
	public Project(){
		pane = new HBox();
		dummy = new Pane();
		label = new Label();
		download = new Button("Download");
		download.setOnAction(value->{
			System.out.print("Downloaded file");
		});
		HBox.setHgrow(dummy,Priority.ALWAYS);

		pane.getChildren().addAll(label,dummy,download);
		
		setOnDragDetected(arg->{
				Dragboard db = startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(getItem());
				db.setContent(content);
				db.setDragView(new Image("raw/pic.jpg"));
			arg.consume();
		});
		
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if(empty){
			setText(null);
			setGraphic(null);
		}else{
			label.setText((item!=null) ? item:null);
			setGraphic(pane);
		}
	}
	
	
	
}
