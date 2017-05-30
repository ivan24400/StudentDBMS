package ivn.typh.main;

/*
 * This enum provides path for resources available within package
 */
public enum Resources {

	MENU_ICON("/ivn/typh/main/icons/menu.png"), 
	STYLE_SHEET("raw/style.css"),
	DEFAULT_PIC("/ivn/typh/main/raw/pic.png"),
	LOADING("/ivn/typh/main/icons/loading_static.jpg"),
	APP_ICON("/ivn/typh/main/icons/appicon.png"),
	KEY_STORE_PATH(System.getProperty("user.dir")+"\\typh.ks"),
	KEY_STORE_PASSWD("keystore");
	
	public String VALUE;

	Resources(String p) {
		this.VALUE = p;
	}

}
