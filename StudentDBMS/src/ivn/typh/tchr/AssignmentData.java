package ivn.typh.tchr;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
 * This class stores Data for Assignment.
 */
public final class AssignmentData {

	private final  IntegerProperty sem = new SimpleIntegerProperty();
	private final  StringProperty title = new SimpleStringProperty();
	private final  BooleanProperty completed = new SimpleBooleanProperty();
	
	public AssignmentData(int y,String t,boolean c){
		setSem(y);
		setTitle(t);
		setCompleted(c);
	}
	

	public  int getSem() {
		return sem.get();
	}

	public  String getTitle() {
		return title.get();
	}

	public  boolean getCompleted() {
		return completed.get();
	}

	public  StringProperty titleProperty(){
		return title;
	}
	
	public  BooleanProperty completedProperty(){
		return completed;
	}
	
	public  void setSem(int y) {
		sem.set(y);
	}

	public  void setTitle(String t) {
		title.set(t);
	}

	public  void setCompleted(boolean c) {
		completed.set(c);
	}




}
