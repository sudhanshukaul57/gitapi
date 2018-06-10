package gitapi.dao;

import java.util.Arrays;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;

public class MongoDAO {

	public static MongoClient connection = null;
	public MongoDAO() {
		
	}

	public static MongoClient getConnection() throws Exception {
		if(connection!=null)	return connection;
		//MongoCredential credential = MongoCredential.createPlainCredential("sudhanshu", "userSummary", "abc123".toCharArray());
		MongoClient mongo = new MongoClient(
				new MongoClientURI("mongodb://lol:lol123@ds249530.mlab.com:49530/ratelimiter"));
		//mongo.getConnector().
		System.out.println("Connection Established");
		return mongo;
	}
}
