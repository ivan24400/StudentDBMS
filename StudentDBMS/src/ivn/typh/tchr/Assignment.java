package ivn.typh.tchr;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class Assignment {

	private final static StringProperty year = new SimpleStringProperty();
	private final static StringProperty title = new SimpleStringProperty();
	private final static BooleanProperty completed = new SimpleBooleanProperty();
	
	public Assignment(String y,String t,boolean c){
		setYear(y);
		setTitle(t);
		setCompleted(c);
	}
	

	public static String getYear() {
		return year.get();
	}

	public static String getTitle() {
		return title.get();
	}

	public static boolean getCompleted() {
		return completed.get();
	}

	public static StringProperty titleProperty(){
		return title;
	}
	
	public static BooleanProperty completedProperty(){
		return completed;
	}
	
	public static void setYear(String y) {
		year.set(y);
	}

	public static void setTitle(String t) {
		title.set(t);
	}

	public static void setCompleted(boolean c) {
		completed.set(c);
	}




}
