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

	public static MongoCursor<Document> findInSearchTable(String searchTerm, String location) {
		MongoCollection<Document> collection = MongoDAO.getCollection("searchTable");
		MongoCursor<Document> results = collection
				.find(Filters.and(Filters.eq("searchTerm", searchTerm), Filters.eq("location", location))).iterator();
		return results;
	}

	public static Document createSearchResult(String userId, String searchTerm, String location) {
		Document result = new Document();
		result.put("userId", userId);
		result.put("searchTerm", searchTerm);
		result.put("location", location);
		return result;
	}

}
