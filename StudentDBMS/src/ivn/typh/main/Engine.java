package ivn.typh.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class Engine {
	public static MongoClient mongo;
	public static MongoDatabase db;

	public static void main(String[] args) {
//		try {
//			Socket socket = new Socket("127.0.0.1",24000);
//			PrintWriter out = new PrintWriter(socket.getOutputStream());
//			out.print("Hey this client");
//			out.close();
//			socket.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		Thread tliui = new Thread(new BasicUI());
		tliui.start();
	
	}

}
