package ivn.typh.tchr;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Attendance {

	private SimpleStringProperty subject;
	private SimpleIntegerProperty attended;
	private SimpleIntegerProperty total;
	
	public Attendance(String subject,Integer attended,Integer total){
		this.subject = new SimpleStringProperty(subject);
		this.attended = new SimpleIntegerProperty(attended);
		this.total = new SimpleIntegerProperty(total);
	}

	public String getSubject() {
		return subject.get();
	}

	public void setSubject(String subject) {
		this.subject.set(subject);
	}

	public Integer getAttended() {
		return attended.get();
	}

	public void setAttended(int attended) {
		this.attended.set(attended);
	}

	public Integer getTotal() {
		return total.get();
	}

	public void setTotal(int total) {
		this.total.set(total);
	}
}
