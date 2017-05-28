package ivn.typh.tchr;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/*
 * This class creates a pane with a visible border along with title placed at
 * top center.
 */
public class BorderTitledPane{
	
	public static StackPane addTitle(String title,Node node){
		StackPane sp = new StackPane();
		Label label = new Label(title);
		StackPane.setAlignment(label, Pos.TOP_CENTER);
		StackPane content = new StackPane();
		content.getChildren().add(node);
		
		label.getStyleClass().add("border-titled-pane-title");
		content.getStyleClass().add("border-titled-pane-content");
		sp.getStyleClass().add("border-titled-pane");
		sp.getChildren().addAll(label,content);
		
		return sp;
	}

}
