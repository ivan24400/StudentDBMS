package ivn.typh.tchr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.mongodb.Block;
import com.mongodb.client.gridfs.model.GridFSFile;

import ivn.typh.main.Engine;
import javafx.scene.Cursor;
import javafx.scene.Node;
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
	
		HBox.setHgrow(dummy,Priority.ALWAYS);
		pane.getChildren().addAll(label,dummy,download);
		download.setId("project_download");
		download.setOnAction(value->{
			DirectoryChooser dir = new DirectoryChooser();
			dir.setTitle("Select a download path - Typh™");
			dir.setInitialDirectory(new File(System.getProperty("user.home")));
			File dPath = dir.showDialog(w);
			if(dPath !=null){
				Engine.gfs.find().forEach(new Block<GridFSFile>(){
					public void apply(final GridFSFile file){
						
						if((label.getText()).equals(getItemName(file.getFilename().split(":")[1]))){
							OutputStream out = null;
							try {
								out = new FileOutputStream(dPath+File.separator+file.getFilename().split(":")[1]);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							Engine.gfs.downloadToStream(file.getId(),out);
						}
					}
				});
			}
		});
		
		setOnDragDetected(event -> {
            if (getItem() == null) {
                return;
            }

            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(getItem());
            dragboard.setDragView(new Image(getClass().getResourceAsStream("/ivn/typh/tchr/icons/project_drag.png")));
            dragboard.setContent(content);

            event.consume();
        });

		
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if(empty||item==null){
			setText(null);
			setGraphic(null);
		}else{
			label.setText((item!=null) ? getItemName(item):null);
			setGraphic(pane);
		}
	}
	
	private String getItemName(String n) {
		int position = n.lastIndexOf(".");
		if(position==-1)
			return n;
		
		return n.substring(0,position);
	}
	
	
}
