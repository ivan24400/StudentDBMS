package ivn.typh.main;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class BasicUI extends Application implements Runnable {

	public static String user;
	public static String password;
	public static BorderPane pane ;
	public static String ipAddr;
	public static Button login;

	public Scene basic;
	private Stage stage;
	private Button exit;
	private Button tictac;
	private Button about;
	private Button howto;
	private Button cnct;
	private ToggleButton fulls;


	public void startUI() throws InterruptedException, ExecutionException {
		pane = new BorderPane();
		ToolBar tool = new ToolBar();

		Pane dummy = new Pane();
		HBox.setHgrow(dummy, Priority.ALWAYS);
		exit = new Button("Exit");
		tictac = new Button("TicTacToe");
		about = new Button("About");
		howto = new Button("How To Use ?");
		//login = new Button("Log In");
		cnct = new Button("Connect");
		fulls = new ToggleButton("X");
		exit.setOnAction(event -> {
			exitApplication();
			});
		tictac.setOnAction(event -> loadGame());
		fulls.setOnAction(value -> {
			stage.setFullScreen(fulls.isSelected());
		});
		fulls.setSelected(true);
		cnct.setOnAction(arg0 -> {
			Dialog<String> dialog = new Dialog<>();
			dialog.setTitle("Connection - Typh™");
			dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
			dialog.initOwner(stage);
			dialog.setHeaderText("Enter Server IP address");
			VBox vb = new VBox();
			HBox hb = new HBox();
			vb.setPadding(new Insets(20));
			vb.setSpacing(10);
			TextField tf1 = new TextField("127");
			TextField tf2 = new TextField("0");
			TextField tf3 = new TextField("0");
			TextField tf4 = new TextField("1");
			Tooltip tt = new Tooltip();

			tf1.textProperty().addListener((obs, o, n) -> {
				if (!n.matches("\\d*")) {
					tt.setText("Enter numbers only");
					Point2D p = tf1.localToScene(0.0, 0.0);
					tt.show(tf1, p.getX() + tf1.getCaretPosition() + tf1.getScene().getWindow().getX(),
							p.getY() + tf1.getHeight() * 2 + tf1.getScene().getWindow().getY());
					tf1.setText(n.replaceAll("[\\D]", ""));
				}
			});

			tf2.textProperty().addListener((obs, o, n) -> {
				if (!n.matches("\\d*")) {
					tt.setText("Enter numbers only");
					Point2D p = tf2.localToScene(0.0, 0.0);
					tt.show(tf2, p.getX() + tf2.getCaretPosition() + tf2.getScene().getWindow().getX(),
							p.getY() + tf2.getHeight() * 2 + tf2.getScene().getWindow().getY());
					tf2.setText(n.replaceAll("[\\D]", ""));
				}
			});

			tf3.textProperty().addListener((obs, o, n) -> {
				if (!n.matches("\\d*")) {
					tt.setText("Enter numbers only");
					Point2D p = tf3.localToScene(0.0, 0.0);
					tt.show(tf3, p.getX() + tf3.getCaretPosition() + tf3.getScene().getWindow().getX(),
							p.getY() + tf3.getHeight() * 2 + tf3.getScene().getWindow().getY());
					tf3.setText(n.replaceAll("[\\D]", ""));
				}
			});

			tf4.textProperty().addListener((obs, o, n) -> {
				if (!n.matches("\\d*")) {
					tt.setText("Enter numbers only");
					Point2D p = tf4.localToScene(0.0, 0.0);
					tt.show(tf4, p.getX() + tf4.getCaretPosition() + tf4.getScene().getWindow().getX(),
							p.getY() + tf4.getHeight() * 2 + tf4.getScene().getWindow().getY());
					tf4.setText(n.replaceAll("[\\D]", ""));
				}
			});

			tf1.addEventFilter(KeyEvent.KEY_TYPED, arg -> {
				TextField tx = (TextField) arg.getSource();
				if (tx.getText().length() >= 3) {
					arg.consume();
					tf2.requestFocus();
				}
			});
			tf2.addEventFilter(KeyEvent.KEY_TYPED, arg -> {
				TextField tx = (TextField) arg.getSource();
				if (tx.getText().length() >= 3) {
					arg.consume();
					tf3.requestFocus();
				}
			});
			tf3.addEventFilter(KeyEvent.KEY_TYPED, arg -> {
				TextField tx = (TextField) arg.getSource();
				if (tx.getText().length() >= 3) {
					arg.consume();
					tf4.requestFocus();
				}
			});
			tf4.addEventFilter(KeyEvent.KEY_TYPED, arg -> {
				TextField tx = (TextField) arg.getSource();
				if (tx.getText().length() >= 3) {
					arg.consume();
				}
			});

			tf1.setOnMouseMoved(value -> {
				tt.hide();
			});

			tf2.setOnMouseMoved(value -> {
				tt.hide();
			});
			tf3.setOnMouseMoved(value -> {
				tt.hide();
			});
			tf4.setOnMouseMoved(value -> {
				tt.hide();
			});

			tf1.setPrefWidth(50);
			tf2.setPrefWidth(50);
			tf3.setPrefWidth(50);
			tf4.setPrefWidth(50);

			hb.getChildren().addAll(tf1, new Label(" . "), tf2, new Label(" . "), tf3, new Label(" . "), tf4);
			vb.getChildren().add(hb);

			dialog.getDialogPane().setContent(vb);

			dialog.setResultConverter(result -> {
				if ((tf1.getText() + tf2.getText() + tf3.getText() + tf4.getText()).trim().isEmpty())
					return null;
				else
					return tf1.getText() + "." + tf2.getText() + "." + tf3.getText() + "." + tf4.getText();
	
			});

			Optional<String> result = dialog.showAndWait();
			Loading load = new Loading(stage);

			Task<Boolean> cm = checkMachine(stage);
			load.startTask(cm);

			result.ifPresent(ip -> {
				ipAddr = ip;
				pane.setDisable(true);
				(new Thread(cm)).start();
				
				pane.setDisable(false);

			});

			cm.setOnSucceeded(value -> {
				load.hideProgress();
				if (cm.getValue())
					Notification.message(stage, AlertType.INFORMATION, "Connection  - Typh™", "Connected to Server");
				else
					Notification.message(stage, AlertType.ERROR, "Connection - Typh™", "Server not found!");
			});


		});

		StackPane sp = new StackPane();
		
		Circle login = new Circle();
		login.setFill(Color.AQUA);
		login.setRadius(59);
		login.setOnMouseEntered(event->{
			 DropShadow dropShadow = new DropShadow();

		    dropShadow.setOffsetX(5);

		    dropShadow.setOffsetY(5);

		    dropShadow.setRadius(5);

		    dropShadow.setBlurType(BlurType.GAUSSIAN);

		    dropShadow.setColor(Color.color(0, 0, 0, 0.45));
			login.setEffect(dropShadow);
		});
		
		login.setOnMouseExited(event->{
			login.setEffect(null);
		});		
		

		login.setOnMouseClicked(arg -> {
			if (ipAddr != null) {
				ExecutorService execsrv = Executors.newSingleThreadExecutor();
				execsrv.execute(new LogIn(stage, pane, basic, tool));
				execsrv.shutdown();
			} else {
				Notification.message(stage, AlertType.ERROR, "Connection - Typh™", "System is Offline");
			}
		});

	
		login.setId("login");
		sp.getChildren().addAll(login,new Label("Login"));
		tool.getItems().addAll(tictac, cnct, new Separator(), howto, about, new Separator(),
				fulls,dummy,exit);
		pane.setTop(tool);
		pane.setCenter(sp);

		basic = new Scene(pane, 1360, 768);

		basic.getStylesheets().add(getClass().getResource("raw/style.css").toExternalForm());
		
	}

	private void exitApplication() {
		Alert ex = new Alert(AlertType.CONFIRMATION);
		ex.setHeaderText("Exit Typh™ ? ");
		ex.setTitle("Exit - Typh™");
		ex.initOwner(stage);
		ex.getButtonTypes().setAll(ButtonType.OK,ButtonType.CANCEL);

		Optional<ButtonType> result = ex.showAndWait();
		result.ifPresent(arg->{
			if(arg.equals(ButtonType.OK)){
			if(!(Engine.mongo== null))
					Engine.mongo.close();
				Platform.exit();
			}
		});
	}

	public Task<Boolean> checkMachine(Stage stage) {
		Task<Boolean> task = new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				InetAddress addr;
				Boolean result = false;
				try {
					if(ipAddr == null){
						return false;
					}
					addr = InetAddress.getByName(ipAddr);
					if(!addr.isReachable(4000))
						return false;
					MongoClientOptions.Builder options = MongoClientOptions.builder().sslEnabled(true).sslInvalidHostNameAllowed(true);
					MongoClientURI connectionString =  new MongoClientURI("mongodb://typh:typhpass@"+ipAddr+":24000/?authSource=Students",options);
					Engine.mongo = new MongoClient(connectionString);
					result = true;

				} catch (Exception e){
					Engine.mongo.close();
				}

				return result;
			}

		};
		return task;
	}

	public void loadGame() {
		try {
			Runtime.getRuntime().exec("java -jar " + System.getProperty("user.dir") + File.separator + "game.jar");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Typh™ - Student Database");
		stage.setFullScreenExitHint("");
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		try {
			this.stage=stage;
			startUI();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		stage.setTitle("Typh™ - Student Database");
		stage.setScene(basic);
		stage.setFullScreen(true);
		stage.setAlwaysOnTop(true);
		stage.show();
	}

	@Override
	public void run() {
		launch();
	}

}
