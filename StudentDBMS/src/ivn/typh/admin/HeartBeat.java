package ivn.typh.admin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ivn.typh.main.BasicUI;
import ivn.typh.main.Notification;
import ivn.typh.main.PortList;
import ivn.typh.admin.Components;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;

/*
 * This class is responsible to provide the admin with 
 * list of all logged in users. 
 */
public class HeartBeat implements Runnable {

	private Socket socket;
	static boolean heartAttack;
	static String message;
	private static ScheduledExecutorService service;

	@Override
	public void run() {
		heartAttack = false;
		message = "__BEAT__";

		try {
			socket = new Socket(BasicUI.ipAddr, PortList.ADMIN.port);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

			service = Executors.newSingleThreadScheduledExecutor();

			Runnable users = new Runnable() {
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					if (!heartAttack) {
						try {
							System.out.println("Admin:client\t" + Boolean.toString(heartAttack));
							List<String> u;
							if ((u = (List<String>) in.readObject()) != null) {
								Platform.runLater(() -> {
									Components.onlineUser.getItems().clear();
									u.forEach(item -> {
										Components.onlineUser.getItems().add((String) item);

									});
									if (Components.onlineUser.getItems().isEmpty())
										Components.onlineUser.getItems().add("No User is online !");
								});
							} else {
								throw new IOException();
							}
							out.writeObject(message);
							out.reset();
							out.flush();

						} catch (ClassNotFoundException | IOException e) {
							serverFailed(true);
						}
						message = "__BEAT__";
					} else {
						serverFailed(false);
					}
				}

			};

			service.scheduleAtFixedRate(users, 0, 3, TimeUnit.SECONDS);
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
		Platform.runLater(() -> {
			service.shutdown();
			try {
				socket.close();
			} catch (IOException e) {
			}
			if(isServerFailed)
				Notification.message(Components.stage, AlertType.ERROR, "Connection - Typh™", "Server Failed");
			Platform.exit();
		});

	}
}
