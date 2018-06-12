package gitapi.dao;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDAO {

	public static MongoClient connection = null;

	public MongoDAO() {

	}

	public static MongoClient getConnection() {
		if (connection != null)
			return connection;
		MongoClient mongo = new MongoClient(
				new MongoClientURI("mongodb://lol:lol123@ds249530.mlab.com:49530/ratelimiter"));
		return mongo;
	}

	public static MongoCollection<Document> getCollection(String collectionName) {
		MongoClient conn = MongoDAO.getConnection();
		MongoDatabase db = conn.getDatabase("ratelimiter");
		MongoCollection<Document> collection = db.getCollection(collectionName);
		return collection;
	}
}
