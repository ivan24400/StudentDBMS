package ivn.typh.main;

/*
 * This class stores the user name and password provided in the login box.
 */
public class LoginData {
	private String user;
	private String password;
	
	public LoginData(String u,String p){
		this.user=u;
		this.password = p;
	}
	
	/*
	 * This method returns user name.
	 * @return username
	 */
	public String getUser() {
		return user;
	}
	
	/*
	 * This method returns the password.
	 * @return password
	 */
	public String getPassword() {
		return password;
	}
	
	
	
}
