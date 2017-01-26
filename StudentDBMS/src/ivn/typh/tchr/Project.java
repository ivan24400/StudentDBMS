package ivn.typh.tchr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.mongodb.Block;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

import ivn.typh.main.Engine;
import javafx.scene.Scene;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Project extends ListCell<String>{
	
	private HBox pane;
	private Pane dummy;
	private Button download;
	private Label label;
	private Window w;
	
	public Project(Scene s){
		w =s.getWindow();
		pane = new HBox();
		dummy = new Pane();
		label = new Label();
		download = new Button("Download");
		download.setOnAction(value->{
			System.out.print("Downloaded file");
		});
		HBox.setHgrow(dummy,Priority.ALWAYS);

		pane.getChildren().addAll(label,dummy,download);
		download.setOnAction(value->{
			DirectoryChooser dir = new DirectoryChooser();
			dir.setInitialDirectory(new File(System.getProperty("user.home")));
			File sel = dir.showDialog(w);
			if(sel !=null){
				try {
					OutputStream out = new FileOutputStream(sel+File.separator+label.getText());
					Engine.gfs.find().forEach(new Block<GridFSFile>(){
						public void apply(final GridFSFile file){
							if(label.getText().equals(file.getFilename())){
								Engine.gfs.downloadToStream(file.getId(),out);
							}
						}
					});
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		
//		setOnDragDetected(arg->{
//				Dragboard db = startDragAndDrop(TransferMode.MOVE);
//				ClipboardContent content = new ClipboardContent();
//				content.putString(getItem());
//				db.setContent(content);
//				db.setDragView(new Image("raw/pic.jpg"));
//			arg.consume();
//		});
		
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if(empty||item==null){
			setText(null);
			setGraphic(null);
		}else{
			label.setText((item!=null) ? getItemName():null);
			setGraphic(pane);
		}
	}

	private String getItemName() {
		String n = label.getText();
		int position = n.lastIndexOf(".");
		if(position==-1)
			return n;
		
		return n.substring(0,position);
	}
	
	
	
}
