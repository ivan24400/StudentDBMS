package ivn.typh.main;

public enum Resources {

	MENU_ICON("/ivn/typh/main/icons/menu.png"), 
	STYLE_SHEET("raw/style.css"),
	DEFAULT_PIC("/ivn/typh/main/raw/pic.jpg"),
	LOADING("/ivn/typh/main/icons/gifs/loading_dots.gif");

	public String path;

	Resources(String p) {
		this.path = p;
	}

}
