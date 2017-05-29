package ivn.typh.tchr;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/*
 * This method stores data for academic class.
 */
public class AcademicData {

	private final SimpleStringProperty subject;
	private final SimpleIntegerProperty theoryTotal;
	private final SimpleIntegerProperty oralTotal;
	private final SimpleIntegerProperty pracsTotal;
	private final SimpleIntegerProperty termworkTotal;
	private SimpleIntegerProperty theoryScored;
	private SimpleIntegerProperty oralScored;
	private SimpleIntegerProperty pracsScored;
	private SimpleIntegerProperty termworkScored;
	private final SimpleBooleanProperty backlog;

	public AcademicData(String sub, Integer thscr, Integer thtot, Integer orscr, Integer ortot, Integer prscr,
			Integer prtot, Integer twscr, Integer twtot, boolean back) {
		this.subject = new SimpleStringProperty(sub);
		this.theoryTotal = new SimpleIntegerProperty(thtot);
		this.oralTotal = new SimpleIntegerProperty(ortot);
		this.pracsTotal = new SimpleIntegerProperty(prtot);
		this.termworkTotal = new SimpleIntegerProperty(twtot);
		this.backlog = new SimpleBooleanProperty(back);
		this.theoryScored = new SimpleIntegerProperty(thscr);
		this.oralScored = new SimpleIntegerProperty(orscr);
		this.pracsScored = new SimpleIntegerProperty(prscr);
		this.termworkScored = new SimpleIntegerProperty(twscr);

	}

	public String getSubject() {
		return subject.get();
	}

	public void setSubject(String subject) {
		this.subject.set(subject);
	}

	public Integer getTheoryTotal() {
		return theoryTotal.get();
	}

	public void setTheoryTotal(Integer theoryTotal) {
		this.theoryTotal.set(theoryTotal);
	}

	public Integer getOralTotal() {
		return oralTotal.get();
	}

	public void setOralTotal(Integer oralTotal) {
		this.oralTotal.set(oralTotal);
	}

	public Integer getPracsTotal() {
		return pracsTotal.get();
	}

	public void setPracsTotal(Integer pracsTotal) {
		this.pracsTotal.set(pracsTotal);
	}

	public Integer getTermworkTotal() {
		return termworkTotal.get();
	}

	public void setTermworkTotal(Integer termworkTotal) {
		this.termworkTotal.set(termworkTotal);
	}

	public boolean getBacklog() {
		return backlog.get();
	}

	public BooleanProperty backlogProperty() {
		return backlog;
	}

	public void setBacklog(boolean backlog) {
		this.backlog.set(backlog);
	}

	public Integer getTheoryScored() {
		return theoryScored.get();
	}

	public Integer getOralScored() {
		return oralScored.get();
	}

	public Integer getPracsScored() {
		return pracsScored.get();
	}

	public Integer getTermworkScored() {
		return termworkScored.get();
	}

	public void setTheoryScored(Integer theoryScored) {
		this.theoryScored.set(theoryScored);
	}

	public void setOralScored(Integer oralScored) {
		this.oralScored.set(oralScored);
	}

	public void setPracsScored(Integer pracsScored) {
		this.pracsScored.set(pracsScored);
	}

	public void setTermworkScored(Integer termworkScored) {
		this.termworkScored.set(termworkScored);
	}

}
