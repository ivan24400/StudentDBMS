package ivn.typh.tchr;

import java.util.Map;

import ivn.typh.admin.SideBar;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Components {
	
	public static Stage stage;
	public static Scene scene;
	

	public static GridPane tgpane;
	public static ScrollPane sctgpane;
	public static StackPane spMain;

	public static GridPane center;
	public static VBox left;
	public static HBox top;
	public static HBox topL;
	public static HBox aboveAcc;
	
	public static String classIncharge;
	public static BorderPane pane;
	public static ObservableList<String> repList;

	public static SideBar side;
	
	public static Button menu;
	public static ToolBar mb;
	public static TitledPane[] tp;
	public static Accordion accord;
	
	public static ComboBox<String> slist;
	public static ToggleButton editable;
	public static Search searchBox;
	public static Button update;
	public static Button report;
	public static Button export;
	
	public static Label srch;
	public static Label reports;
	public static Label student;
	public static Label pname;
	public static Label dprt;
	public static Label pdprt;
	public static Label cls;
	public static Label pcls;
	public static Label tstuds;
	public static Label nstuds;
	
	//	Personal 
	
	public static GridPane personal;
	public static ImageView dpImgView;
	public static TextField tsname;
	public static TextField tsid;
	public static ListView<Report> reps;
	public static ChoiceBox<String> tsrno;
	public static ChoiceBox<String> tsdprt;
	public static ChoiceBox<String> tsclass;
	public static ChoiceBox<String> tsbatch;
	public static ChoiceBox<String> tsyear;
	public static String tscsem;
	public static TextField tsmail;
	public static TextField tsaddr;
	public static TextField tsphone;
	public static TextField tpphone;
	
	//	Academics
	
	public static GridPane academic;
	public static TableView<Marks> tsem1;
	public static TableView<Marks> tsem2;
	public static Button addEntry;
	public static RadioButton rbsem1;
	public static RadioButton rbsem2;
	public static LineChart<String, Number> studProgress;

	//	Attendance
	
	public static GridPane attendance;
	
	//	Assignments
	
	public static TableView<Attendance> atsem1;
	public static TableView<Attendance> atsem2;
	public static Button addat;
	public static RadioButton atrbsem1;
	public static RadioButton atrbsem2;
	public static BarChart<String, Number> atBarChart;
	public static CategoryAxis atXaxis;
	public static NumberAxis atYaxis;
	
	//	Projects
	
	public static Group recycle;
	public static GridPane projects;
	public static ListView<String> prList;
	public static Map<String, String> prtmp;

	//	Assignments
	
	public static GridPane assignment;
	public static ListView<Assignment> asList;
	public static Button addAssignment;
	public static Button removeAssignment;


	public static void setIdAll(){
		
		tgpane.setId("home");
		center.setId("center");
		left.setId("leftP");
		top.setId("topP");
		topL.setId("toplP");
		aboveAcc.setId("acP");
		
		//	Side Menu
		
		pdprt.setId("logInfo"); 
		pcls.setId("logInfo");  
		nstuds.setId("logInfo");
		
		//	Personal
		
		personal.setId("personalP");
		dpImgView.setId("dpImgView");
		
		//	Academic
		
		academic.setId("academicP");
		
		// Attendance
		
		attendance.setId("attendanceP");
		
		//	Projects
		
		projects.setId("projectsP");
		           
		//	Assignments
		assignment.setId("assignmentP");

		
	}
}