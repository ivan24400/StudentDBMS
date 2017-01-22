package ivn.typh.tchr;

public class Assignment {

	private String year;
	private String title;
	
	public Assignment(String y,String t){
		year=y;
		title=t;
	}

	public String getYear() {
		return year;
	}

	public String getTitle() {
		return title;
	}
	
	public String getAssignment(){
		return "["+getYear()+"]\t"+getTitle();
	}
}
