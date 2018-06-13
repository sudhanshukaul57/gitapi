package gitapi.dao;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class SearchTableDao {

	public static void insertInSearchTable(List<Document> searchResults){
		MongoCollection<Document> collection = MongoDAO.getCollection("searchTable");
		if (!searchResults.isEmpty())
			collection.insertMany(searchResults);
	}
/**
 * this database table contains user name, location and userID
 * */
	public static MongoCursor<Document> findInSearchTable(String searchTerm, String location) {
		MongoCollection<Document> collection = MongoDAO.getCollection("searchTable");
		MongoCursor<Document> results = collection
				.find(Filters.and(Filters.eq("searchTerm", searchTerm), Filters.eq("location", location))).iterator();
		return results;
	}

	/**
	 * inserts user data in the database
	 * */
	public static Document createSearchResult(String userId, String searchTerm, String location, String htmlUrl) {
		Document result = new Document();
		result.put("userId", userId);
		result.put("searchTerm", searchTerm);
		result.put("location", location);
		result.put("htmlUrl", htmlUrl);
		return result;
	}

}
