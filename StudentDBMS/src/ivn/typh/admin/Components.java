package ivn.typh.admin;

import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Components {
	
	public static Stage stage;
	public static GridPane gpane;
	public static ScrollPane sgpane;
	public static GridPane userGrid;
	public static GridPane dprtGrid;
	public static ListView<String> onlineUser;
	public static ToolBar mb;
	public static BorderPane pane;
	
	public static Label admin;
	public static Label totalStudents; 
	public static Label totalUsers; 
	public static Label lastLogin;
	
	public static Button menu;
	public static Label rTotalStudents;
	public static Label rTotalUsers;
	public static Label rLastLogin;
	public static SideBar side;
	
	public static Search srch;

	public static GridPane studGrid;
	public static Button addAcc;
	public static Button addDepartment;
	public static Button addStudent;

	public static VBox topL;
	public static VBox left;
	public static VBox right; 
	public static HBox center;
	public static HBox top; 
	
	public static Tab user; 
	public static Tab stud; 
	public static Tab dprt; 

	public static void setIdAll(){

		Components.gpane.setId("TheGrid");
		
		Components.rTotalStudents.setId("logInfo");
		Components.rTotalUsers.setId("logInfo");
		Components.rLastLogin.setId("logInfo");
		Components.srch.setId("searchBox");
		Components.user.setId("tabU");
		Components.stud.setId("tabS");
		Components.dprt.setId("tabD");
		

		Components.userGrid.setId("uGrid");
		Components.studGrid.setId("sGrid");
		Components.dprtGrid.setId("dGrid");
		
		Components.center.setId("center");
		Components.topL.setId("topL");
		Components.left.setId("left");
		Components.right.setId("right");
		Components.top.setId("top");

		
	}
	
	public static void setCacheAll(){
		side.setCache(true);                
		side.setCacheShape(true);           
		side.setCacheHint(CacheHint.SPEED); 
	}
}

