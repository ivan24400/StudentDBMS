package ivn.typh.tchr;

import java.util.Map;

import javafx.collections.ObservableList;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Components {
	
	public static Stage stage;
	public static Scene scene;
	
	public static String classIncharge;
	public static BorderPane pane;
	public static ObservableList<String> repList;
	public static Button update;
	public static Button report;
	public static ToolBar mb;
	public static ComboBox<String> slist;
	public static TitledPane[] tp;
	public static Accordion accord;
	public static Label srch;
	public static Search searchBox;
	public static Label reports;
	public static Label student;
	public static Button menu;
	public static Label pname;
	public static Label dprt;
	public static Label pdprt;
	public static Label cls;
	public static Label pcls;
	public static Label tstuds;
	public static Label nstuds;
	
	//	Personal 
	
	public static ImageView dpImgView;
	public static TextField tsname;
	public static TextField tsid;
	public static ListView<Report> reps;
	public static ChoiceBox<String> tsrno;
	public static ChoiceBox<String> tsdprt;
	public static ChoiceBox<String> tsclass;
	public static ChoiceBox<String> tsbatch;
	public static ChoiceBox<String> tsyear;
	public static TextField tsmail;
	public static TextField tsaddr;
	public static TextField tsphone;
	public static TextField tpphone;
	
	//	Academics
	
	public static TableView<Marks> tsem1;
	public static TableView<Marks> tsem2;
	public static Button addEntry;
	public static RadioButton rbsem1;
	public static RadioButton rbsem2;
	public static LineChart<String, Number> studProgress;

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
	
	public static ListView<String> prList;
	public static Map<String, String> prtmp;

	//	Assignments
	
	
	public static ListView<Assignment> asList;
	public static Button addAssignment;
	public static Button removeAssignment;


}