package ivn.typh.lm;

public class Core {

	public static void main(String[] arg){
		Thread tui = new Thread(new LmUI());
		tui.start();
	}
}
