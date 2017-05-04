package ivn.typh.main;

import java.awt.Toolkit;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;

public class BasicUI extends Application implements Runnable {

	public static String user;
	public static String password;
	public static BorderPane homePane;
	public static CenterPane centerOfHomePane;
	
	public static Label institute;
	public static String ipAddr;
	public static Stage stage;
	public static double screenWidth;
	public static double screenHeight;
	public static Label loading;

	public Circle login;

	public Scene basic;
	private Button exit;
	private Button about;
	private Button help;
	private Button connect;
	private ToggleButton fulls;
	private StackPane loginPane;


	public void startUI() throws InterruptedException, ExecutionException {

				homePane = new BorderPane();
				loginPane = new StackPane();

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
				connect.setOnAction(new Connect());

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
						execsrv.execute(new LogIn(stage, homePane, basic, tool));
						execsrv.shutdown();
					} else {
						Notification.message(stage, AlertType.ERROR, "Connection - Typh™", "System is Offline");
					}
				});

				login.setId("login");
				Label lLabel = new Label("Login");
				lLabel.setOnMouseClicked(login.getOnMouseClicked());
				
				about.setOnAction(new About());
				
				help.setOnAction(new Help());
				
				loginPane.getChildren().addAll(login, lLabel);
				dummy.getChildren().add(institute);
				institute.setFont(Font.font(16));
				dummy.setAlignment(Pos.CENTER);
				
				tool.getItems().addAll(connect, new Separator(), help, about, new Separator(), fulls, dummy, exit);
				homePane.setTop(tool);
				
				basic = new Scene(homePane, 1360, 768);


				basic.getStylesheets().add(getClass().getResource("raw/style.css").toExternalForm());
				stage.setScene(basic);
				
				centerOfHomePane = new CenterPane(loginPane);
				homePane.setCenter(centerOfHomePane);
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
