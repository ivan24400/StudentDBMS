package ivn.typh.tchr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ivn.typh.main.BasicUI;

public class HeartBeat implements Runnable {

	@Override
	public void run() {
		Socket socket;
		System.out.println("In heartbeat");
		try {
			socket = new Socket(BasicUI.ipAddr,61000);
			System.out.println("Connected to Server"+socket.getRemoteSocketAddress());
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(BasicUI.user);
			out.flush();
			Runnable beat = new Runnable(){

				@Override
				public void run() {
					System.out.println("Running");
					try {
						in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			};
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(beat, 0, 5, TimeUnit.SECONDS);
		} catch (IOException e) {
			System.out.println("Server not Found");
		}
	}

}
