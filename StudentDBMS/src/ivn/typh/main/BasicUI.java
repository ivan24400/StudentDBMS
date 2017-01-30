package ivn.typh.main;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class BasicUI extends Application implements Runnable {

	public static String user;
	public static String password;
	public static GridPane center;
	public Scene basic;

	public static String ipAddr;
	private Button exit;
	private Button tlm;
	private Button tictac;
	private Button about;
	private Button howto;
	private Button login;
	private Button cnct;
	private ToggleButton fulls;

	public static void createStage(Stage stage, Scene scene, String title) {
		Platform.runLater(() -> {
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setFullScreen(true);
			stage.setAlwaysOnTop(true);
			stage.show();
		});
	}

	public void startUI(Stage stage) throws InterruptedException, ExecutionException {
		BorderPane pane = new BorderPane();
		center = new GridPane();
		ToolBar tool = new ToolBar();

		Pane dummy = new Pane();
		HBox.setHgrow(dummy, Priority.ALWAYS);
		exit = new Button("Exit");
		tlm = new Button("TyphLM");
		tictac = new Button("TicTacToe");
		about = new Button("About");
		howto = new Button("How To Use ?");
		login = new Button("Log In");
		cnct = new Button("Connect");
		fulls = new ToggleButton("X");
		exit.setOnAction(event -> Platform.exit());
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
			VBox vb = new VBox();
			vb.setPadding(new Insets(20));
			vb.setSpacing(10);
			TextField tf = new TextField();
			Tooltip tt = new Tooltip();
			tf.setPromptText("Enter an IP address");
			tf.textProperty().addListener((obs, o, n) -> {
				if (n.matches("\\D")) {
					tt.setText("Enter numbers only");
					Point2D p = tf.localToScene(0.0, 0.0);
					tt.show(tf, p.getX() + tf.getCaretPosition(), p.getY() + tf.getHeight());
					tf.setText(n.replaceAll("[\\D]", ""));
				}
			});
			tf.setOnMouseMoved(value -> {
				tt.hide();
			});
			vb.getChildren().add(new Label("Enter Server IP address"));
			vb.getChildren().add(tf);

			dialog.getDialogPane().setContent(vb);
			dialog.setResultConverter(result -> {
				return tf.getText();
			});

			Optional<String> result = dialog.showAndWait();
			result.ifPresent(ip -> {
				ipAddr = ip;
			});

		});

		center.add(login, 0, 0);
		login.setOnAction(arg -> {
			if (ipAddr != null) {
				ExecutorService execsrv = Executors.newSingleThreadExecutor();
				execsrv.execute(new LogIn(stage, pane, basic, tool));
				execsrv.shutdown();
			} else {
				Notification.message(stage, AlertType.ERROR, "Connection Error - Typh™",
						"Server Address not found!");
			}
		});

		tool.getItems().addAll(exit, new Separator(), tictac, tlm, cnct, new Separator(), howto, about, new Separator(),
				fulls);

		pane.setTop(tool);
		pane.setCenter(center);

		basic = new Scene(pane, 1360, 768);
		createStage(stage, basic, "Typh™ - Student Database");
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
		try {
			startUI(stage);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		launch();
	}

}
