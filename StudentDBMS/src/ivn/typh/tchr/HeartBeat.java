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
import ivn.typh.main.Notification;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;

public class HeartBeat implements Runnable {

	private Socket socket;
	static boolean heartAttack;

	@Override
	public void run() {
		try {
			heartAttack = false;
			socket = new Socket(BasicUI.ipAddr,61000);
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(BasicUI.user);
			out.flush();
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

			Runnable beat = new Runnable(){

				@Override
				public void run() {
					if(!heartAttack){
					try {
						System.out.println("pulse: user");

						String text = in.readLine();
						if(!(text.equals("__BEAT__"))){
							Platform.runLater(()->{
								Notification.message(Components.stage, AlertType.INFORMATION, "Message from admin - Typh™", formatMessage(text));
							});
						}
					} catch (IOException e) {
						Notification.message(Components.stage, AlertType.ERROR,"Connection - Typh™","You are disconnected from the server");
						service.shutdown();
						try {
							socket.close();
						} catch (IOException k) {}
						Platform.exit();
					}
					}else{
						service.shutdown();
						try {
							socket.close();
						} catch (IOException e) {}
					}
				}

				private String formatMessage(String text) {
					StringBuffer message = new StringBuffer();
					for(int i=0;i<text.length();i++){
						message.append(message.charAt(i));
						if(i%20 == 0)
							message.append("\n");
					}
					return message.toString();
				}
				
			};
			service.scheduleAtFixedRate(beat, 0, 5, TimeUnit.SECONDS);
		} catch (IOException e) {
			System.out.println("heart: Server not found");
		}
	}

}
