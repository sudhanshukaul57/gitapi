package gitapi.api;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import gitapi.dao.MongoDAO;

public class Network {
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final Logger logger = LogManager.getLogger(Network.class);
	private static HttpRequest request;
	private static HttpResponse response;

	public void getRequest(String reqUrl) throws Exception {
				
		String location = "London";
		String searchTerm = "Nishan Perera";
		String q = searchTerm+" in:fullname";
		if(!location.isEmpty())
		{
			q="location:"+location+" "+q;
		}
		URIBuilder builder = new URIBuilder();
		builder.setScheme("https");
		builder.setHost("api.github.com");
		builder.setPath("/search/users");
		builder.addParameter("q", q);
		builder.addParameter("type", "Users");
		// builder.addParameter("access_token", "*TOKEN HERE*");
		
		MongoCursor<Document> searchResults = Network.findInSearchTable(searchTerm, location);
		int count = 0;
//		while(searchResults.hasNext())
//		{
//			count++;
//			Document result = searchResults.next();
//			String userId = (String)result.get("userId");
//			System.out.println(result.get("userId"));
//			MongoCursor<Document> repoResults = Network.findInUserSummaryTable(userId);
//			while(repoResults.hasNext())
//			{
//				Document repoResult = repoResults.next();
//				String user = (String)repoResult.get("userId");
//				Long commits = (Long)repoResult.get("commits");
//				String repoName = (String)repoResult.get("repoName");
//				System.out.println(user+" "+commits+" "+repoName);
//			}
//			
//		}
		
		if(count==0)
		{
			System.out.println("Fetching from API");
			String u = builder.build().toURL().toString();
			System.out.println(u);
			GenericUrl url = new GenericUrl(u);
	
			HttpRequest request = HTTP_TRANSPORT.createRequestFactory().buildGetRequest(url);
			try {
			HttpResponse response = request.execute();
			
			if(!Network.isRateLimitRemaining(response)) throw new Exception("fdfdf");
			
			// System.out.println(response.getStatusCode());
			InputStream is = response.getContent();
			JSONParser jsonParse = new JSONParser();
			JSONObject obj = (JSONObject) jsonParse.parse(new InputStreamReader(is, "UTF-8"));
			System.out.println(obj);
			System.out.println("*********");
			JSONArray userList = (JSONArray) obj.get("items");
			System.out.println(userList.size());
			List<Document> searchResults2 = new ArrayList<Document>();
			for (int i = 0; i < userList.size(); i++) {
				JSONObject user = (JSONObject) userList.get(i);
				String reposUrl = (String) user.get("repos_url");	
				String login = (String) user.get("login");
				JSONArray reposResult = Network.fetchResult(reposUrl);
				Network.fetchCommits(reposResult, login);
				searchResults2.add(Network.createSearchResult(login,searchTerm,location));
	
			}
			Network.insertInSearchTable(searchResults2);
			response.disconnect();
			
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
			
		}

		
		

	}
	
	
	public static boolean isRateLimitRemaining(HttpResponse response)
	{
		HttpHeaders headerNames = response.getHeaders();
		List rateLimit  = ((ArrayList) headerNames.get("x-ratelimit-remaining"));
		System.out.println("Rate-Limit :"+rateLimit.get(0));
		
		if(!((String)(rateLimit.get(0))).equals(0)) 
			return true;
		
		return false;
	}
	
	public static void insertInSearchTable(List<Document> searchResults) throws Exception
	{
		MongoClient conn = MongoDAO.getConnection();
		MongoDatabase db = conn.getDatabase("ratelimiter");
		MongoCollection<Document> collection = db.getCollection("searchTable");
		if(!searchResults.isEmpty())
		collection.insertMany(searchResults);
	}
	
	public static MongoCursor<Document> findInSearchTable(String searchTerm,String location) throws Exception
	{
		MongoClient conn = MongoDAO.getConnection();
		MongoDatabase db = conn.getDatabase("ratelimiter");
		MongoCollection<Document> collection = db.getCollection("searchTable");
		
//		Block<Document> printBlock = new Block<Document>() {
//		     @Override
//		     public void apply(final Document document) {
//		         System.out.println(document.toJson());
//		     }
//		};
		MongoCursor<Document> results = collection.find(Filters.and(Filters.eq("searchTerm",searchTerm),Filters.eq("location",location))).iterator();
		
//		System.out.println(results);
//		for(Document result: results)
//		{
//			System.out.println(result);
//		}
		
		return results;
	}
	
	public static Document createSearchResult(String userId,String searchTerm,String location)
	{
		Document result = new Document();
		result.put("userId", userId);
		result.put("searchTerm", searchTerm);
		result.put("location", location);
		return result;
	}

	public static JSONArray fetchResult(String u) throws Exception {
		// u+="?access_token="+"*TOKEN HERE*";
		JSONArray obj = null;
		System.out.println(u);
		try {
			GenericUrl url = new GenericUrl(u);

			request = HTTP_TRANSPORT.createRequestFactory().buildGetRequest(url);

			response = request.execute();
			if(!Network.isRateLimitRemaining(response)) throw new Exception("fdfdf");
			// System.out.println(response.getStatusCode());
			InputStream is = response.getContent();
			JSONParser jsonParse = new JSONParser();
			obj = (JSONArray) jsonParse.parse(new InputStreamReader(is, "UTF-8"));
		} catch (Exception e) {
			logger.error("Exception in parsing.", e);

		} finally {
			response.disconnect();
		}
		return obj;
	}

	public static void fetchCommits(JSONArray repos, String userId) throws Exception {
		
		for (int i = 0; i < repos.size(); i++) {
			try
			{
			JSONObject repo = (JSONObject) repos.get(i);
			String repoName = (String)repo.get("name");
			System.out.println(repo.get("name"));
			String contributersUrl = (String) repo.get("contributors_url");
			JSONArray contributers = fetchResult(contributersUrl);
			Network.fetchCommitsByRepo(contributers, userId,repoName);
			}
			catch(Exception e)
			{
				System.out.println(e);
			}

		}
	}

	public static void fetchCommitsByRepo(JSONArray contributers, String userId, String repoName) throws Exception {
		List<Document> searchResults = new ArrayList<Document>();
		for (int i = 0; i < contributers.size(); i++) {
			try
			{
			JSONObject contributer = (JSONObject) contributers.get(i);
			String loginId = (String) contributer.get("login");
			Long contributions = (Long) contributer.get("contributions");
			if (userId.equals(loginId)) {
				System.out.println(loginId + "  " + contributions);
				searchResults.add(Network.createUserSummaryResult(loginId,repoName,contributions));				
				break;
			}
			}
			catch(Exception e)
			{
				System.out.println(e);
			}

		}
		
		Network.insertInUserSummaryTable(searchResults);
	}
	
	public static void insertInUserSummaryTable(List<Document> searchResults) throws Exception
	{
		MongoClient conn = MongoDAO.getConnection();
		MongoDatabase db = conn.getDatabase("ratelimiter");
		MongoCollection<Document> collection = db.getCollection("userSummary");
		if(!searchResults.isEmpty())
		collection.insertMany(searchResults);
	}
	
	public static Document createUserSummaryResult(String userId,String repoName,Long commits)
	{
		Document result = new Document();
		result.put("userId", userId);
		result.put("repoName", repoName);
		result.put("commits", commits);
		return result;
	}
	
	public static MongoCursor<Document> findInUserSummaryTable(String userId) throws Exception
	{
		MongoClient conn = MongoDAO.getConnection();
		MongoDatabase db = conn.getDatabase("ratelimiter");
		MongoCollection<Document> collection = db.getCollection("userSummary");
		
		
		MongoCursor<Document> results = collection.find(Filters.eq("userId",userId)).iterator();
				
		return results;
	}

}
