package ivn.typh.admin;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Components {
	
	public static Stage stage;
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

}