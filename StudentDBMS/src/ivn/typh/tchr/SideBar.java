package ivn.typh.tchr;


import ivn.typh.main.CenterPane;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


public class SideBar extends VBox {

	private Button menu;
	private double width;
	static Label rts, rtu, rll;

	public SideBar() {
		menu = Components.menu;
		CenterPane.shade.setVisible(false);
		setVisible(false);
		
		setId("sideBar");


		menu.setOnAction(arg -> {

			final Animation show = new Transition() {
				{
					setCycleDuration(Duration.millis(240));
				}

				@Override
				protected void interpolate(double fraction) {
					final double newWidth = width * fraction;
					setTranslateX(newWidth - width);
				}

			};

			show.setOnFinished(value->{
			});

			final Animation hide = new Transition() {
				{
					setCycleDuration(Duration.millis(240));
				}

				@Override
				protected void interpolate(double frac) {
					final double newWidth = width*(1.0-frac);
					setTranslateX(newWidth-width);
				}
			};
			
			hide.setOnFinished(value->{
				setVisible(false);

			});
			
			if(show.statusProperty().get() == Animation.Status.STOPPED && hide.statusProperty().get() == Animation.Status.STOPPED){
				if(isVisible()){
					CenterPane.shade.setVisible(false);
					hide.play();
				}
				else{
					CenterPane.shade.setVisible(true);
					setVisible(true);
					show.play();
				}
			}
		});

	}

	public void setMenuWidth(double w){
		setMinWidth(w);
		setMaxWidth(w);
		width = w;
	}
	public void addNodes(Node... nodes) {
		getChildren().addAll(nodes);
		getChildren().forEach(node->VBox.setVgrow(node, Priority.ALWAYS));;
		
	}
	
	public ObservableList<Node> getChildren(){
		return getChildren();
	}
}
