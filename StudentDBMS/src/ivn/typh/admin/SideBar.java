package ivn.typh.admin;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SideBar extends VBox {

	private Button menu;
	static Label rts, rtu, rll;

	public SideBar(Button m) {
		menu = m;
	}

	public void addAll(Node... nodes) {

		setId("sideBar");
		Label user = (Label) nodes[0];
		
		addAll(nodes);

		menu.setOnAction(arg -> {

			final double width = getWidth();

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

			show.setOnFinished(value -> {

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
				if(isVisible())
					hide.play();
				else{
					setVisible(true);
					show.play();
				}
			}
		});

	}
}
