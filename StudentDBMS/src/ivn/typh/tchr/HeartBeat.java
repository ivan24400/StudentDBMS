package ivn.typh.tchr;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ivn.typh.admin.Components;
import ivn.typh.main.BasicUI;
import ivn.typh.main.Notification;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;

public class HeartBeat implements Runnable {

	private Socket socket;
	static boolean heartAttack;
	private static ScheduledExecutorService service;

	@Override
	public void run() {
		try {
			heartAttack = false;
			socket = new Socket(BasicUI.ipAddr,61000);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			out.writeObject(BasicUI.user);
			out.flush();
			service = Executors.newSingleThreadScheduledExecutor();

			Runnable beat = new Runnable(){

				@Override
				public void run() {
					if(!heartAttack){
					try {
						System.out.println("pulse: user");

						String text = (String) in.readObject();
						if(!(text.equals("__BEAT__"))){
							Platform.runLater(()->{
								Notification.message(Components.stage, AlertType.INFORMATION, "Message from admin - Typh�", formatMessage(text));
							});
						}
					} catch (IOException | ClassNotFoundException e) {
						serverFailed();
					}
					}else{
						serverFailed();
					}
				}

				
				private String formatMessage(String text) {
					StringBuffer message = new StringBuffer();
					for(int i=1;i<=text.length();i++){
						message.append(text.charAt(i-1));
						if(i%20 == 0)
							message.append("\n");
					}
					return message.toString();
				}
				
			};
			service.scheduleAtFixedRate(beat, 0, 5, TimeUnit.SECONDS);
		} catch (IOException e) {
			serverFailed();
		}
	}
	
	private void serverFailed() {
		Platform.runLater(()->{
			Notification.message(Components.stage, AlertType.ERROR,"Connection - Typh�","Server Failed");	
			service.shutdown();
			try {
				socket.close();
			} catch (IOException a) {
			}
			Platform.exit();
		});
		
	}


}
