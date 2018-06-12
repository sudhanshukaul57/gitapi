package gitapi.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.mongodb.client.MongoCursor;

import gitapi.GitMain;
import gitapi.dao.ApiDao;
import gitapi.dao.SearchTableDao;
import gitapi.dao.UserSummaryDao;
import gitapi.utils.Network;

public class RateLimiter {
	
	private static final Logger logger = LogManager.getLogger(RateLimiter.class);
	
	public static void computeResults(String fullName, String location) {
		boolean isPresentInCache = RateLimiter.computeResultsFromDB(fullName, location);
		if (!isPresentInCache)
			RateLimiter.computeResultsFromAPI(fullName, location);
	}

	public static JSONObject sendSearchRequestToServer(String fullName, String location) throws URISyntaxException, IOException {
		GenericUrl url = Network.createSearchRequest(fullName, location);
		JSONObject responseObj = (JSONObject)Network.fetchResult(url.toString());
		return responseObj;
	}

	public static boolean computeResultsFromDB(String fullName, String location) {
		boolean isSearchTermInDB = false;
		MongoCursor<Document> searchResults = SearchTableDao.findInSearchTable(fullName, location);

		while (searchResults.hasNext()) {
			isSearchTermInDB = true;
			Document result = searchResults.next();
			String userId = result.getString("userId");
			System.out.println(result.getString("userId"));
			MongoCursor<Document> repoResults = UserSummaryDao.findInUserSummaryTable(userId);
			RateLimiter.iterateThroughRepos(repoResults);
		}

		return isSearchTermInDB;
	}
	
	public static void iterateThroughRepos(MongoCursor<Document> repoResults)
	{
		while (repoResults.hasNext()) {
			Document repoResult = repoResults.next();
			String user = repoResult.getString("userId");
			Long commits = repoResult.getLong("commits");
			String repoName = repoResult.getString("repoName");
			System.out.println(user + " " + commits + " " + repoName);
		}
	}

	public static void computeResultsFromAPI(String fullName, String location) {
		System.out.println("Fetching from API");

		try {
			JSONObject obj = RateLimiter.sendSearchRequestToServer(fullName, location);
			JSONArray userList = (JSONArray) obj.get("items");
			List<Document> searchResults = new ArrayList<Document>();
			for (int i = 0; i < userList.size(); i++) {
				JSONObject user = (JSONObject) userList.get(i);
				String reposUrl = (String) user.get("repos_url");
				String login = (String) user.get("login");
				JSONArray reposResult = (JSONArray) Network.fetchResult(reposUrl);
				ApiDao.fetchCommits(reposResult, login);
				searchResults.add(SearchTableDao.createSearchResult(login, fullName, location));
				
			}
			SearchTableDao.insertInSearchTable(searchResults);
		} catch (IOException| URISyntaxException e) {
			logger.error(e);
		}
	}

}
