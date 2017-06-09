package ivn.typh.tchr;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ivn.typh.tchr.Components;
import ivn.typh.main.BasicUI;
import ivn.typh.main.Notification;
import ivn.typh.main.PortList;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;

/*
 * This class runs a service/daemon to check for any messages from admin,
 * and displays to the user if it is.
 * <p> 
 * It also notifies the server the existence of this application.
 */
public class HeartBeat implements Runnable {

	private Socket socket;
	static boolean heartAttack;
	private static ScheduledExecutorService service;

	@Override
	public void run() {
		try {
			heartAttack = false;
			socket = new Socket(BasicUI.ipAddr, PortList.USER.port);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			out.writeObject(BasicUI.user);
			out.flush();
			service = Executors.newSingleThreadScheduledExecutor();

			Runnable beat = new Runnable() {

				@Override
				public void run() {
					if (!heartAttack) {
						try {
							String text = (String) in.readObject();
							if (!(text.equals("__BEAT__"))) {
								Platform.runLater(() -> {
									Notification.message(Components.stage, AlertType.INFORMATION,
											"Message from admin - Typh™", formatMessage(text));
								});
							} else {

							}
						} catch (IOException | ClassNotFoundException e) {
							serverFailed(true);
						}
					} else {
						serverFailed(false);
					}
				}

				/*
				 * This method formats the message recieved from admin. It adds
				 * a newline character after every 40 characters present in the
				 * message.
				 * 
				 * @param text message
				 * 
				 * @return formatted string
				 */
				private String formatMessage(String text) {
					StringBuffer message = new StringBuffer();
					for (int i = 1; i <= text.length(); i++) {
						message.append(text.charAt(i - 1));
						if (i % 40 == 0)
							message.append(" --\n-- ");
					}
					return message.toString();
				}

			};
			service.scheduleAtFixedRate(beat, 0, 3, TimeUnit.SECONDS);
		} catch (IOException e) {
			serverFailed(true);
		}
	}

	/*
	 * This method is called if any server fault occurs
	 * 
	 * @param logout status of server.
	 */
	private void serverFailed(boolean isServerFailed) {
		service.shutdown();
		try {
			socket.close();
		} catch (IOException a) {
		}

		Platform.runLater(() -> {
			if(isServerFailed)
			Notification.message(Components.stage, AlertType.ERROR, "Connection - Typh™", "Server Failed");
			Platform.exit();
		});
	}
}
