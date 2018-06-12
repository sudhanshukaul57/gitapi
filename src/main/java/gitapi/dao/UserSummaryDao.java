package gitapi.dao;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class UserSummaryDao {

	public static void insertInUserSummaryTable(List<Document> searchResults) {
		MongoCollection<Document> collection = MongoDAO.getCollection("userSummary");
		if (!searchResults.isEmpty())
			collection.insertMany(searchResults);
	}

	public static Document createUserSummaryResult(String userId, String repoName, Long commits) {
		Document result = new Document();
		result.put("userId", userId);
		result.put("repoName", repoName);
		result.put("commits", commits);
		return result;
	}

	public static MongoCursor<Document> findInUserSummaryTable(String userId) {
		MongoCollection<Document> collection = MongoDAO.getCollection("userSummary");
		MongoCursor<Document> results = collection.find(Filters.eq("userId", userId)).iterator();
		return results;
	}
}
