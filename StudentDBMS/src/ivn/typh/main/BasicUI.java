package ivn.typh.main;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class BasicUI extends Application implements Runnable {

	public static String user;
	public static String password;
	public Scene basic;
	private Button exit;
	private Button tlm;
	private Button tictac;
	private Button about;
	private Button howto;
	private Button login;
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

		ToolBar tool = new ToolBar();

		Pane dummy = new Pane();
		HBox.setHgrow(dummy, Priority.ALWAYS);
		exit = new Button("Exit");
		tlm = new Button("TyphLM");
		tictac = new Button("TicTacToe");
		about = new Button("About");
		howto = new Button("How To Use ?");
		login = new Button("Log In");
		fulls = new ToggleButton("X");
		exit.setOnAction(event -> Platform.exit());
		tictac.setOnAction(event -> loadGame());
		fulls.setOnAction(value -> {
			stage.setFullScreen(fulls.isSelected());
		});
		fulls.setSelected(true);
		tool.getItems().addAll( exit,new Separator(),tictac, tlm,new Separator(),howto,  about,new Separator(),fulls);

		pane.setTop(tool);
		pane.setCenter(login);
		login.setOnAction(arg -> {
			ExecutorService execsrv = Executors.newSingleThreadExecutor();
			execsrv.execute(new LogIn(stage, pane, basic, tool));
			execsrv.shutdown();

		});
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
