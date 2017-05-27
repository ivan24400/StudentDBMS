package ivn.typh.main;

public enum PortList {
	USER(61000),
	
	ADMIN(61001),
	
	NETWORKTEST(61002),
	
	CHECKUSER(61003);
	
	int port;
	
	PortList(int p){
		this.port=p;
	}
}
