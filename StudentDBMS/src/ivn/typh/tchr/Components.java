package ivn.typh.tchr;

import java.util.Arrays;
import ivn.typh.tchr.SideBar;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
 * This class consists of all the variables common to the package.
 */
public class Components {

	public static Stage stage;
	public static Scene scene;

	public static GridPane tgpane;
	public static ScrollPane sctgpane;
	public static ScrollPane[] scroll;

	public static GridPane center;
	public static VBox accDescPane;
	public static HBox top;
	public static HBox accUserPane;
	public static HBox aboveAcc;

	public static String classIncharge;
	public static BorderPane pane;
	public static String[] paneList;
	public static int paneCount;

	public static TitledPane[] tp;
	public static Accordion accord;

	public static Button logout;
	public static SideBar side;
	public static Button menu;
	public static ToolBar mb;

	public static ComboBox<String> slist;
	public static ToggleButton editable;
	public static Search searchBox;
	public static Button update;
	public static Button report;
	public static Button export;

	public static Label srch;
	public static Label student;
	public static Label pname;
	public static Label dprt;
	public static Label pdprt;
	public static Label cls;
	public static Label pcls;
	public static Label tstuds;
	public static Label nstuds;

	public static ComboBox<String> yrlst;
	public static int counter;

	public static String tscsem;

	public static void setIdAll() {

		logout.setId("logout");
		
		tgpane.setId("home");
		center.setId("center");
		accDescPane.setId("leftP");
		top.setId("topP");
		accUserPane.setId("toplP");
		aboveAcc.setId("acP");

		srch.setId("search");
		searchBox.setId("searchBox");

		// Side Menu

		pdprt.setId("logInfo");
		pcls.setId("logInfo");
		nstuds.setId("logInfo");

	}

	public static void setCacheAll() {

		tgpane.setCache(true);
		tgpane.setCacheShape(true);
		tgpane.setCacheHint(CacheHint.SPEED);

		sctgpane.setCache(true);
		sctgpane.setCacheShape(true);
		sctgpane.setCacheHint(CacheHint.SPEED);

		pane.setCache(true);
		pane.setCacheShape(true);
		pane.setCacheHint(CacheHint.SPEED);

		Arrays.stream(tp).forEach(item -> {
			item.setCache(true);
			item.setCacheShape(true);
			item.setCacheHint(CacheHint.SPEED);
		});

		accord.setCache(true);
		accord.setCacheShape(true);
		accord.setCacheHint(CacheHint.SPEED);

		side.setCache(true);
		side.setCacheShape(true);
		side.setCacheHint(CacheHint.SPEED);

	}
}