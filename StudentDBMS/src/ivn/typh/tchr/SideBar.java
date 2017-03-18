package ivn.typh.tchr;


import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SideBar extends VBox {

	private Button menu;
	private Pane home;
	private double width;
	static Label rts, rtu, rll;

	public SideBar(Pane gp,Button m) {
		home = gp;
		menu = m;
		home.setVisible(false);
		setVisible(false);
	}

	public void setMenuWidth(double w){
		setMinWidth(w);
		setMaxWidth(w);
		width = w;
	}
	public void addNodes(Node... nodes) {
		
		home.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.2),CornerRadii.EMPTY,Insets.EMPTY)));
		setId("sideBar");
		getChildren().addAll(nodes);
		getChildren().forEach(node->VBox.setVgrow(node, Priority.ALWAYS));;

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
					home.setVisible(false);
					hide.play();
				}
				else{
					home.setVisible(true);
					setVisible(true);
					show.play();
				}
			}
		});

	}
}
