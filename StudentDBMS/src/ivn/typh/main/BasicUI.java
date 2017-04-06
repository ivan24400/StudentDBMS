package ivn.typh.main;

import static com.mongodb.client.model.Filters.eq;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
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
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class BasicUI extends Application implements Runnable {

	public static String user;
	public static String password;
	public static BorderPane pane;
	public static Label institute;
	public static String ipAddr;
	public static Stage stage;
	public static double screenWidth;
	public static double screenHeight;
	public Circle login;

	public Scene basic;
	private Button exit;
	private Button about;
	private Button help;
	private Button cnct;
	private ToggleButton fulls;

	public void startUI() throws InterruptedException, ExecutionException {

				pane = new BorderPane();
				ToolBar tool = new ToolBar();
								
				HBox dummy = new HBox();
				HBox.setHgrow(dummy, Priority.ALWAYS);
				exit = new Button("Exit");
				about = new Button("About");
				help = new Button("Help");
				cnct = new Button("Connect");
				institute = new Label();
				fulls = new ToggleButton();

				exit.setId("logout");
				about.setId("about");
				help.setId("help");
				cnct.setId("connect");
				fulls.setId("fullscreen");
				
				exit.setOnAction(event -> {
					exitApplication();
				});
				fulls.setOnAction(value -> {
					stage.setFullScreen(fulls.isSelected());
				});
				fulls.setSelected(true);
				cnct.setOnAction(arg0 -> {
					Dialog<String> dialog = new Dialog<>();
					dialog.setTitle("Connection - Typh™");
					dialog.initOwner(stage);
					dialog.setHeaderText("Enter Server IP address");

					HBox hb = new HBox();

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

					tf1.setPrefWidth(40);
					tf2.setPrefWidth(40);
					tf3.setPrefWidth(40);
					tf4.setPrefWidth(40);
					hb.setPadding(new Insets(40));
					hb.getChildren().addAll(tf1, new Label(" . "), tf2, new Label(" . "), tf3, new Label(" . "), tf4);
					dialog.getDialogPane().setContent(hb);
					dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
					dialog.setResultConverter(result -> {
						if ((tf1.getText() + tf2.getText() + tf3.getText() + tf4.getText()).trim().isEmpty()
								|| result.equals(ButtonType.CANCEL))
							return null;
						else
							return tf1.getText() + "." + tf2.getText() + "." + tf3.getText() + "." + tf4.getText();
					});


					Optional<String> result = dialog.showAndWait();
					Loading load = new Loading(stage);

					Task<Boolean> cm = checkMachine(stage);

					result.ifPresent(ip -> {
						if(ip!=null){
						ipAddr = ip;
						load.startTask(cm);
						pane.setDisable(true);
						(new Thread(cm)).start();
						pane.setDisable(false);
						}else{
							Notification.message(stage, AlertType.ERROR,"Network - Typh™", "Invalid network address");
						}

					});

					cm.setOnSucceeded(value -> {
						load.stopTask();
						if (cm.getValue()){
							Notification.message(stage, AlertType.INFORMATION, "Connection  - Typh™",
									"Connected to Server");
							try{
								Document doc = Engine.db.getCollection("Users").find(eq("user","admin")).first();
								institute.setText(doc.getString("instituteName"));
							}catch(NullPointerException e){
								institute.setText("");
							}
						}else
							Notification.message(stage, AlertType.ERROR, "Connection - Typh™", "Server not found!");
					});

				});


				StackPane sp = new StackPane();

				login = new Circle();
				login.setFill(Color.TEAL);
				login.setRadius(59);
				DropShadow dropShadow = new DropShadow();
				dropShadow.setOffsetX(5);
				dropShadow.setOffsetY(5);
				dropShadow.setRadius(5);
				dropShadow.setBlurType(BlurType.GAUSSIAN);
				dropShadow.setColor(Color.color(0, 0, 0, 0.5));

				FillTransition ft = new FillTransition(Duration.millis(500), login);

				login.setEffect(dropShadow);
				login.setOnMouseEntered(value -> {
					ft.setFromValue(Color.TEAL);
					ft.setToValue(Color.valueOf("#1affe8"));
					ft.play();
				});
				login.setOnMouseExited(value -> {
					ft.setToValue(Color.TEAL);
					ft.setFromValue(Color.valueOf("#1affe8"));
					ft.play();
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
				Label lLabel = new Label("Login");
				lLabel.setOnMouseClicked(login.getOnMouseClicked());
				sp.getChildren().addAll(login, lLabel);
				dummy.getChildren().add(institute);
				institute.setFont(Font.font(16));
				dummy.setAlignment(Pos.CENTER);
				tool.getItems().addAll(cnct, new Separator(), help, about, new Separator(), fulls, dummy, exit);
				pane.setTop(tool);
				pane.setCenter(sp);
				basic = new Scene(pane, 1360, 768);

				basic.getStylesheets().add(getClass().getResource("raw/style.css").toExternalForm());
				stage.setScene(basic);
				stage.show();

	}

	private void exitApplication() {
		Alert ex = new Alert(AlertType.CONFIRMATION);
		ex.setHeaderText("Exit Typh™ ? ");
		ex.setTitle("Exit - Typh™");
		ex.initOwner(stage);
		ex.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

		Optional<ButtonType> result = ex.showAndWait();
		result.ifPresent(arg -> {
			if (arg.equals(ButtonType.OK)) {
				if (!(Engine.mongo == null))
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
					if (ipAddr == null) {
						return false;
					}
					addr = InetAddress.getByName(ipAddr);
					if (!addr.isReachable(4000))
						return false;
					MongoClientOptions.Builder options = MongoClientOptions.builder().serverSelectionTimeout(7000).sslEnabled(true)
							.sslInvalidHostNameAllowed(true);
					MongoClientURI connectionString = new MongoClientURI(
							"mongodb://typh:typhpass@" + ipAddr + ":24000/?authSource=Students", options);
					Engine.mongo = new MongoClient(connectionString);
					Engine.db = Engine.mongo.getDatabase("Students");

					result=true;

					Engine.mongo.getAddress();
					
				} catch (Exception e) {
					result=false;
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
	public void start(Stage st) throws Exception {
		stage = st;
		stage.setTitle("Typh™ - Student Database");
		stage.setFullScreenExitHint("");
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stage.setFullScreen(true);
		stage.setAlwaysOnTop(true);
		startUI();
	}

	@Override
	public void run() {
		screenWidth=Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		screenHeight=Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		launch();
	}

}
