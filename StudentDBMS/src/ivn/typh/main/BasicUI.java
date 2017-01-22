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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BasicUI extends Application implements Runnable{

	public static String user;
	public static  String password;
	public Scene basic;

	
	public static void createStage(Stage stage,Scene scene,String title){
		Platform.runLater(()->{
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setFullScreen(true);
			stage.setAlwaysOnTop(true);
			stage.show();
		});
	}
	
	public void startUI(Stage stage) throws InterruptedException, ExecutionException{
		BorderPane pane = new BorderPane();
		MenuBar mb  = new MenuBar();
		Menu file = new Menu("File");
		Menu tools = new Menu("Tools");
		Menu help = new Menu("Help");
		
		

		MenuItem exit = new MenuItem("Exit");
		MenuItem tlm = new MenuItem("TyphLM");
		MenuItem tictac = new MenuItem("TicTacToe");
		MenuItem about = new MenuItem("About");
		MenuItem howto = new MenuItem("How To Use ?");
		HBox mbox = new HBox();
		Button login = new Button("Log In");
		
		exit.setOnAction(event -> Platform.exit());
		tictac.setOnAction(event -> loadGame());
		
		file.getItems().addAll(new SeparatorMenuItem(),exit);
		tools.getItems().addAll(tictac,tlm);
		help.getItems().addAll(howto,new SeparatorMenuItem(),about);
		mb.getMenus().addAll(file,tools,help);
		pane.setCenter(login);
		login.setOnAction(arg ->
		{
			ExecutorService execsrv = Executors.newSingleThreadExecutor();
			execsrv.execute(new LogIn(stage));
			execsrv.shutdown();
			
		});
			
		
		pane.setTop(mb);	
		pane.setRight(mbox);
		
		
		basic = new Scene(pane,1360,768);
		createStage(stage,basic,"Typh™ - Student Database");
	}
	
	public void loadGame(){
		try {
			Runtime.getRuntime().exec("java -jar "+System.getProperty("user.dir")+File.separator+"game.jar");
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
