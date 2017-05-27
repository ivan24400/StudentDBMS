package ivn.typh.main;

import com.mongodb.client.gridfs.GridFSBucket;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/*
 * This is the main class that only creates a single thread.
 * 
 * Author Ivan Pillay
 * Version 1.0
 */

public class Engine {
	
	public static MongoClient mongo;
	public static MongoDatabase db;
	public static GridFSBucket gfs;


	public static void main(String[] args) {

		Thread tliui = new Thread(new BasicUI());
		tliui.start();
	}

}
