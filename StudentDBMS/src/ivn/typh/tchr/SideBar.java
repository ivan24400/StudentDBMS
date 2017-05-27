package ivn.typh.tchr;


import ivn.typh.main.CenterPane;
import ivn.typh.tchr.Components;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


public class SideBar extends VBox {

	private Button menu;
	private final double width=300;
	static Label rts, rtu, rll;

	public SideBar() {
		menu = CenterPane.menu;
		setMinWidth(width);
		setMaxWidth(width);
		setPrefWidth(width);
		
		Button about = ((Button) Components.mb.getItems().get(3));
		Button help = ((Button) Components.mb.getItems().get(2));
		Pane sideSpacer = new Pane();
		
		Platform.runLater(()->{
			setId("sideBar");
			about.setId("side-menu-button");
			help.setId("side-menu-button");
			getChildren().addAll(Components.accUserPane, Components.accDescPane, sideSpacer,help, about);
			getChildren().forEach(node -> VBox.setVgrow(node, Priority.ALWAYS));

		});

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
					hide.play();
					CenterPane.shade.setVisible(false);
				}
				else{
					CenterPane.shade.setVisible(true);
					setVisible(true);
					show.play();
				}
			}
		});
		getStyleClass().add(".sideBarButton");
		setVisible(false);
	}

}
