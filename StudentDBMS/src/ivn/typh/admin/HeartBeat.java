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
import javafx.application.Platform;

public class HeartBeat implements Runnable {

	private Socket socket;
	static boolean heartAttack;
	static String message;

	@Override
	public void run() {
		heartAttack = false;
		message = "__NO__TEXT__";
		try {
			socket = new Socket(BasicUI.ipAddr, 61001);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

			Runnable users = new Runnable() {
				@Override
				public void run() {
					if (!heartAttack) {
						try {
							@SuppressWarnings("unchecked")
							List<String> u = (List<String>) in.readObject();
							Platform.runLater(() -> {
								AdminUI.onlineUser.getItems().clear();
								u.forEach(item -> {
									AdminUI.onlineUser.getItems().add((String) item);

								});
								if (AdminUI.onlineUser.getItems().isEmpty())
									AdminUI.onlineUser.getItems().add("No User is online !");
							});
							out.reset();
							out.writeObject(message);
							out.flush();

						} catch (ClassNotFoundException | IOException e) {
							e.printStackTrace();
						}
						message = "__NO__TEXT__";
					} else {
						service.shutdown();
						try {
							socket.close();
						} catch (IOException e) {
						}
					}
				}
			};

			service.scheduleAtFixedRate(users, 0, 5, TimeUnit.SECONDS);
		} catch (IOException e) {
			System.out.println("Server not found");
		}
	}

}
