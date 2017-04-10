package ivn.typh.main;

import static com.mongodb.client.model.Filters.eq;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.Optional;
import java.util.Set;
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
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
	private double xOffset,yOffset;

	public Circle login;
	

	public Scene basic;
	private Button exit;
	private Button about;
	private Button help;
	private Button connect;
	private ToggleButton fulls;

	public void startUI() throws InterruptedException, ExecutionException {

				pane = new BorderPane();
				ToolBar tool = new ToolBar();
								
				HBox dummy = new HBox();
				HBox.setHgrow(dummy, Priority.ALWAYS);
				exit = new Button("Exit");
				about = new Button("About");
				help = new Button("Help");
				connect = new Button("Connect");
				institute = new Label();
				fulls = new ToggleButton();

				exit.setId("logout");
				about.setId("about");
				help.setId("help");
				connect.setId("connect");
				fulls.setId("fullscreen");
				
				exit.setOnAction(event -> {
					exitApplication();
				});
				
				fulls.setOnAction(value -> {
					stage.setFullScreen(fulls.isSelected());
				});
				
				fulls.setSelected(true);
				connect.setOnAction(arg0 -> {
					Dialog<String> dialog = new Dialog<>();
					dialog.setTitle("Connection - Typh�");
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
							Notification.message(stage, AlertType.ERROR,"Network - Typh�", "Invalid network address");
						}

					});

					cm.setOnSucceeded(value -> {
						load.stopTask();
						if (cm.getValue()){
							Notification.message(stage, AlertType.INFORMATION, "Connection  - Typh�",
									"Connected to Server");
							try{
								Document doc = Engine.db.getCollection("Users").find(eq("user","admin")).first();
								institute.setText(doc.getString("instituteName"));
							}catch(NullPointerException e){
								institute.setText("");
							}
						}else
							Notification.message(stage, AlertType.ERROR, "Connection - Typh�", "Server not found!");
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
						Notification.message(stage, AlertType.ERROR, "Connection - Typh�", "System is Offline");
					}
				});

				login.setId("login");
				Label lLabel = new Label("Login");
				lLabel.setOnMouseClicked(login.getOnMouseClicked());
				
				about.setOnAction(event->{
					
					Alert abt = new Alert(AlertType.NONE);
					
					VBox contents = new VBox();
					HBox titleBar = new HBox();
					VBox window = new VBox();
					
					Button close = new Button("x");
					Pane space = new Pane();
					Label titleText = new Label("Typh�");
					//titleText.setGraphic(value);
					Label description = new Label("Typh� Students Database Management System.\nVersion 1.0\nCopyright � 2017\nAuthor :- Ivan Pillay");
					
					xOffset = yOffset = 0;

					titleBar.setOnMousePressed(mouse->{
		                xOffset = mouse.getSceneX();
		                yOffset = mouse.getSceneY();
					});
				
					titleBar.setOnMouseDragged(mouse->{
						abt.setX(mouse.getScreenX() - xOffset);
						abt.setY(mouse.getScreenY() - yOffset);
					});
				
					titleBar.setId("about_pane_title_bar");
					contents.setId("about_pane_contents");
					window.setId("about_pane_window");
					description.setId("about_pane_description");
					titleText.setId("about_pane_title");
					close.setId("about_pane_close");
					
					HBox.setHgrow(space,Priority.ALWAYS);
					close.setOnAction(value->{
						Platform.runLater(()->{
							abt.setResult(ButtonType.CLOSE);
						});
					});
					abt.getDialogPane().getScene().setFill(Color.TRANSPARENT);
					titleBar.getChildren().addAll(space,close);
					
					contents.getChildren().addAll(titleText,description);
					window.getChildren().addAll(titleBar,contents);
					abt.initOwner(stage);
					abt.initStyle(StageStyle.TRANSPARENT);
					abt.getButtonTypes().clear();
					abt.getDialogPane().setContent(window);
					abt.show();
				});
				
				help.setOnAction(event->{
					Alert helpPage = new Alert(AlertType.NONE);
					
					VBox window = new VBox();
					VBox contents = new VBox();
					HBox titleBar = new HBox();
					
					Button close = new Button("X");
					
					xOffset = yOffset = 0;
					WebView helpPane = new WebView();
					helpPane.getEngine().load(getClass().getResource("/ivn/typh/main/raw/help/help.html").toExternalForm());
					
					contents.setId("help_pane_contents");
					window.setId("help_pane_window");
					close.setId("help_pane_close");
					helpPane.setId("help_pane");
					
					helpPane.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
					      @Override 
					      public void onChanged(Change<? extends Node> change) {
					        Set<Node> deadSeaScrolls = helpPane.lookupAll(".scroll-bar");
					        for (Node scroll : deadSeaScrolls) {
					          scroll.setVisible(false);
					        }
					      }
					    });
					 
					close.setOnAction(value->{
						helpPage.setResult(ButtonType.CLOSE);
					});
					Pane space = new Pane();
					HBox.setHgrow(space,Priority.ALWAYS);
					
					titleBar.setOnMousePressed(mouse->{
			                xOffset = mouse.getSceneX();
			                yOffset = mouse.getSceneY();
			        });
					
					titleBar.setOnMouseDragged(mouse->{
						helpPage.setX(mouse.getScreenX() - xOffset);
						helpPage.setY(mouse.getScreenY() - yOffset);
					});
					
					titleBar.getChildren().addAll(space,close);
					contents.getChildren().add(helpPane);
					window.getChildren().addAll(titleBar,contents);
					
					helpPage.getButtonTypes().clear();
					helpPage.initOwner(stage);
					helpPage.initStyle(StageStyle.TRANSPARENT);
					helpPage.getDialogPane().setContent(window);
					helpPage.show();
				});
				
				sp.getChildren().addAll(login, lLabel);
				dummy.getChildren().add(institute);
				institute.setFont(Font.font(16));
				dummy.setAlignment(Pos.CENTER);
				tool.getItems().addAll(connect, new Separator(), help, about, new Separator(), fulls, dummy, exit);
				pane.setTop(tool);
				pane.setCenter(sp);
				basic = new Scene(pane, 1360, 768);

				basic.getStylesheets().add(getClass().getResource("raw/style.css").toExternalForm());
				stage.setScene(basic);
				stage.show();

	}

	private void exitApplication() {
		Alert ex = new Alert(AlertType.CONFIRMATION);
		ex.setHeaderText("Exit Typh� ? ");
		ex.setTitle("Exit - Typh�");
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
				Socket testSocket = null;
				try {
					if (ipAddr == null) {
						return false;
					}
					addr = InetAddress.getByName(ipAddr);
					if (!addr.isReachable(4000))
						return false;
					MongoClientOptions.Builder options = MongoClientOptions.builder().serverSelectionTimeout(8000).sslEnabled(true)
							.sslInvalidHostNameAllowed(true);
					MongoClientURI connectionString = new MongoClientURI(
							"mongodb://typh:typhpass@" + ipAddr + ":24000/?authSource=Students", options);
					Engine.mongo = new MongoClient(connectionString);
					Engine.db = Engine.mongo.getDatabase("Students");
					testSocket = new Socket(ipAddr,61002);
					result=true;

					Engine.mongo.getAddress();
					
				} catch (Exception e) {
					result=false;
					testSocket.close();
					Engine.mongo.close();
				}

				return result;
			}

		};
		return task;
	}

	@Override
	public void start(Stage st) throws Exception {
		stage = st;
		stage.setTitle("Typh� - Student Database");
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
