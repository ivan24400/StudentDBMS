package ivn.typh.main;

/*
 * This enum provides path for resources available within package
 */
public enum Resources {

	MENU_ICON("/ivn/typh/main/icons/menu.png"), 
	STYLE_SHEET("raw/style.css"),
	DEFAULT_PIC("/ivn/typh/main/raw/pic.jpg"),
	LOADING("/ivn/typh/main/icons/loading_static.jpg");

	public String path;

	Resources(String p) {
		this.path = p;
	}

}
