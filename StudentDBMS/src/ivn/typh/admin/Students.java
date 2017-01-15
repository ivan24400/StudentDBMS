package ivn.typh.admin;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import ivn.typh.main.Engine;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Students extends Dialog<String> implements EventHandler<ActionEvent> {

	static ObservableList<String> studentList;
	static int x, y;

	private String dpImg;
	private GridPane home;
	private Button addS;
	private Stage parent;
	private TextField tsname;
	private TextField tsid;
	private TextField tsrno;
	private TextField tsclass;
	private TextField tsbatch;
	private TextField tsmail;
	private TextField tsaddr;
	private TextField tsphone;
	private TextField tpphone;
	private ImageView dpImgView;
	private ToggleButton edit;
	private ComboBox<String> tsdprt;

	
	public Students(Stage s,GridPane gp,String n,String i,String srno,String clas,String batch,String mail,String addr,String sp,String pp,String dprt,String img){
		this(s);
		home = gp;
		tsname = new TextField();
		tsid = new TextField();
		tsrno = new TextField();
		tsdprt = new ComboBox<>();
		tsclass = new TextField();
		tsbatch = new TextField();
		tsmail = new TextField();
		tsaddr = new TextField();
		tsphone = new TextField();
		tpphone = new TextField();
		tsdprt = new ComboBox<>();
		
		tsname.setText(n);
		tsid.setText(i);
		tsrno.setText(srno);
		tsbatch.setText(batch);
		tsmail.setText(mail);
		tsaddr.setText(addr);
		tsphone.setText(sp);
		tpphone.setText(pp);
		tsdprt.setValue(dprt);
		dpImg=img;
		
	}

	public Students(Stage arg){
		parent = arg;
		initOwner(parent);

	}
	
	public Students(Stage arg, GridPane gp, Button b) {
		this(arg);
		home = gp;
		addS = b;
		tsname = new TextField();
		tsid = new TextField();
		tsrno = new TextField();
		tsdprt = new ComboBox<>();
		tsclass = new TextField();
		tsbatch = new TextField();
		tsmail = new TextField();
		tsaddr = new TextField();
		tsphone = new TextField();
		tpphone = new TextField();
		tsdprt = new ComboBox<>();

	}

	public void createUI(boolean first) {
		setTitle("Student - Typh™");
		setHeaderText("Fill in required fields to add a Student");

		GridPane dPane = new GridPane();
		
		dPane.setAlignment(Pos.CENTER);
		dPane.setPadding(new Insets(30));
		dPane.setHgap(15);
		dPane.setVgap(20);
	
		Label sname = new Label("Name");
		Label sid = new Label("ID");
		Label srno = new Label("Roll No");
		Label sdprt = new Label("Department");
		Label sclass = new Label("Class");
		Label sbatch = new Label("Batch");
		Label smail = new Label("Email");
		Label saddr = new Label("Address");
		Label sphone = new Label("Phone");
		Label pphone = new Label("Parent Phone");
		if(dpImg.isEmpty())
			dpImgView = new ImageView(new Image(getClass().getResourceAsStream("raw/pic.jpg")));
		else{
		try {
				byte[] imgd = Base64.getDecoder().decode(dpImg);
				BufferedImage bf = ImageIO.read(new ByteArrayInputStream(imgd));
				dpImgView = new ImageView(SwingFXUtils.toFXImage(bf, null));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		tsname.setPromptText("Name");
		tsid.setPromptText("ID");
		tsrno.setPromptText("Roll No");
		tsdprt.setPromptText("Department");
		tsclass.setPromptText("Class");
		tsbatch.setPromptText("Batch");
		tsmail.setPromptText("Email");
		tsaddr.setPromptText("Address");
		tsphone.setPromptText("Phone");
		tpphone.setPromptText("Parent Phone");

		dpImgView.setEffect(new DropShadow());


		Tooltip tool = new Tooltip();
		tsname.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\D*")) {
				Bounds screen = tsname.localToScreen(tsname.getBoundsInLocal());
				tool.setText("Enter text only");
				tool.show(tsname, screen.getMinX() + tsname.getCaretPosition(),
						screen.getMinY() + tsname.getScene().getY());
				Platform.runLater(() -> {
					tsname.setText(o);
					tsname.positionCaret(tsname.getText().length());
				});
			}
		});

		tsphone.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				Bounds screen = tsphone.localToScreen(tsphone.getBoundsInLocal());
				tool.setText("Enter numbers only");
				tool.show(tsphone, screen.getMinX() + tsphone.getCaretPosition(),
						screen.getMinY() + tsphone.getScene().getY());
				Platform.runLater(() -> {
					tsphone.setText(o);
					tsphone.positionCaret(tsphone.getText().length());
				});
			}
		});

		tpphone.textProperty().addListener((obs, o, n) -> {
			if (!n.matches("\\d*")) {
				Bounds screen = tpphone.localToScreen(tpphone.getBoundsInLocal());
				tool.setText("Enter numbers only");
				tool.show(tpphone, screen.getMinX() + tpphone.getCaretPosition(),
						screen.getMinY() + tpphone.getScene().getY());
				Platform.runLater(() -> {
					tpphone.setText(o);
					tpphone.positionCaret(tpphone.getText().length());
				});
			}
		});

		tsname.setOnMouseMoved(arg->tool.hide());
		tsphone.setOnMouseMoved(arg->tool.hide());
		tpphone.setOnMouseMoved(arg->tool.hide());

		
		studentList = FXCollections.observableArrayList("None");
		tsdprt.getItems().addAll(Departments.dprtList);
		tsname.requestFocus();
		

		dpImgView.setFitHeight(128);
		dpImgView.setFitWidth(128);
		dpImgView.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent arg0) {
				Dragboard db = arg0.getDragboard();
				if (db.hasFiles()) {
					arg0.acceptTransferModes(TransferMode.COPY);
				} else {
					arg0.consume();
				}
			}

		});

		dpImgView.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent arg0) {
				Dragboard db = arg0.getDragboard();
				boolean success = false;
				if (db.hasFiles()) {
					db.getFiles().forEach(file -> {
						dpImgView.setImage(new Image(file.toURI().toString()));
						System.out.println(file.getAbsolutePath());
					});
					success = true;
				}
				arg0.setDropCompleted(success);
				arg0.consume();
			}

		});
		dPane.add(sname, 0, 0);
		dPane.add(tsname, 1, 0);
		dPane.add(sid, 0, 1);
		dPane.add(tsid, 1, 1);
		dPane.add(srno, 0, 2);
		dPane.add(tsrno, 1, 2);
		dPane.add(sdprt, 0, 3);
		dPane.add(tsdprt, 1, 3);
		dPane.add(sclass, 2, 1);
		dPane.add(tsclass, 3, 1);
		dPane.add(sbatch, 2, 0);
		dPane.add(tsbatch, 3, 0);
		dPane.add(smail, 0, 4);
		dPane.add(tsmail, 1, 4);
		dPane.add(saddr, 2, 2);
		dPane.add(tsaddr, 3, 2);
		dPane.add(sphone, 2, 3);
		dPane.add(tsphone, 3, 3);
		dPane.add(pphone, 2, 4);
		dPane.add(tpphone, 3, 4);
		dPane.add(dpImgView, 4, 0, 1, 5);
		
		getDialogPane().setContent(dPane);
		if(!first){
			addEdit(dPane);
			ButtonType save = new ButtonType("Save", ButtonData.OK_DONE);
			getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

		setResultConverter(arg -> {
			if (arg.equals(save)){
				addButton();
			}
			return null;
		});
		show();
		}else{
			setResultConverter(arg -> {
				getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

				if (arg.equals(ButtonType.OK))
					addButton();
				
				hide();
				return null;
			});

			show();
		}
	}


	private void addEdit(GridPane pane) {
		HBox seBox = new HBox();
		seBox.setPadding(new Insets(20));
		seBox.setSpacing(20);
		seBox.setAlignment(Pos.CENTER);


		 edit = new ToggleButton("Edit");
		 edit.selectedProperty().addListener((obs, o, n) -> {
				disableAll(obs.getValue());
			});
		 seBox.getChildren().add(edit);
			pane.add(seBox, 0, 5, 5, 1);

	}

	private void disableAll(Boolean flag) {

		tsname.setDisable(flag);
		tsid.setDisable(flag);
		tsrno.setDisable(flag);
		tsdprt.setDisable(flag);
		tsclass.setDisable(flag);
		tsbatch.setDisable(flag);
		tsmail.setDisable(flag);
		tsaddr.setDisable(flag);
		tsphone.setDisable(flag);
		tpphone.setDisable(flag);
	}

	private void addButton() {
		
		Document doc = new Document("name",tsname.getText()).append("sid",tsid.getText()).append("srno",tsrno.getText()).append("sdprt",tsdprt.getSelectionModel().getSelectedItem()).
				append("sclass",tsclass.getText()).append("smail",tsmail.getText()).append("saddr",tsaddr.getText()).append("sphone",tsphone.getText()).append("pphone",tpphone.getText());
		MongoCollection<Document> coll = Engine.db.getCollection("Students");
		coll.insertOne(doc);
		Button tmp = new Button(getStudent());
		tmp.setOnAction(new Students(parent));
		if (x < 6) {
			x++;
			home.add(tmp, x - 1, y);
			GridPane.setColumnIndex(addS, x);
			GridPane.setRowIndex(addS, y);

		} else {
			x = 1;
			y++;
			home.add(tmp, x - 1, y);
			GridPane.setColumnIndex(addS, x);
			GridPane.setRowIndex(addS, y);

		}
	}

	private String getStudent() {
		return tsname.getText();
	}

	
	public void begin() {
		createUI(true);
	}
	@Override
	public void handle(ActionEvent arg) {
		createUI(false);
	}


}
