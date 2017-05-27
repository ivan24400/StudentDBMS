package ivn.typh.main;

/*
 * This class consists of port values 
 * used by the client application
 */
public enum PortList {
	USER(61000),
	
	ADMIN(61001),
	
	NETWORKTEST(61002),
	
	CHECKUSER(61003);
	
	public int port;
	
	PortList(int p){
		this.port=p;
	}
}
