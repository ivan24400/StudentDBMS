package ivn.typh.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ivn.typh.main.BasicUI;

public class HeartBeat implements Runnable {

	@Override
	public void run() {
		try {
			Socket socket = new Socket(BasicUI.ipAddr,61000);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println("heartAttack");
			
			Runnable users = new Runnable(){
				@Override
				public void run(){
					try {
						@SuppressWarnings("unchecked")
						List<String> u = (List<String>) in.readObject();
						AdminUI.onlineUser.clear();
						System.out.println(u.toString());
						AdminUI.onlineUser.addAll(u);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(users, 0, 5, TimeUnit.SECONDS);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
