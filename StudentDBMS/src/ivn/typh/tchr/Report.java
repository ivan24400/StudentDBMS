package ivn.typh.tchr;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Report {

	private BooleanProperty seen = new SimpleBooleanProperty();
	private StringProperty report = new SimpleStringProperty();
	private IntegerProperty sem = new SimpleIntegerProperty();
	
	public Report(boolean s,int sm,String r){
		setSeen(s);
		setSem(sm);
		setReport(r);
	}

	public IntegerProperty semProperty(){
		return sem;
	}
	
	public BooleanProperty seenProperty(){
		return seen;
	}
	
	public StringProperty reportProperty(){
		return report;
	}
	
	public boolean getSeen() {
		return seen.get();
	}

	public String getReport() {
		return report.get();
	}

	public int getSem() {
		return sem.get();
	}

	public void setSem(int s) {
		this.sem.set(s);
	}
	
	public void setSeen(boolean s) {
		this.seen.set(s);
	}

	public void setReport(String r) {
		this.report.set(r);
	}
}
