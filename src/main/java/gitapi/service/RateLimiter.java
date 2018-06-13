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

import gitapi.dao.ApiDao;
import gitapi.dao.SearchTableDao;
import gitapi.dao.UserSummaryDao;
import gitapi.model.DevInfo;
import gitapi.model.RepositoryDetails;
import gitapi.model.UsersList;
import gitapi.utils.Network;

public class RateLimiter {
	
	private static final Logger logger = LogManager.getLogger(RateLimiter.class);
	
	/**
	 * This method will first try to compute results by searching database if its not there then will compute the results from api.
	 * */
	public static void computeResults(UsersList userInfo) {
		List<DevInfo> developers = new ArrayList<>();
		boolean isPresentInCache = RateLimiter.computeResultsFromDB(userInfo, developers);
		if (!isPresentInCache)
			RateLimiter.computeResultsFromAPI(userInfo, developers);
		userInfo.setDevInfo(developers);
	}

	public static JSONObject sendSearchRequestToServer(String fullName, String location) throws URISyntaxException, IOException {
		// if results are not there in database then this server request will be made through this method
		GenericUrl url = Network.createSearchRequest(fullName, location);
		JSONObject responseObj = (JSONObject)Network.fetchResult(url.toString(),false);
		return responseObj;
	}

	public static boolean computeResultsFromDB(UsersList userInfo, List<DevInfo> developers) {
		boolean isSearchTermInDB = false;
		String fullName = userInfo.getFullName();
		String location = userInfo.getLocation();
		DevInfo devInfo;
		MongoCursor<Document> searchResults = SearchTableDao.findInSearchTable(fullName, location);

		while (searchResults.hasNext()) {
			isSearchTermInDB = true;
			Document result = searchResults.next();
			String userId = result.getString("userId");
			String htmlUrl = result.getString("htmlUrl");
			if(location.isEmpty()) {
				devInfo = new DevInfo(fullName);
			} else {
				devInfo = new DevInfo(fullName, location);
			}
			devInfo.setUserId(userId);
			devInfo.setProfileLink(htmlUrl);
			// will find repo details on the basis of userId
			MongoCursor<Document> repoResults = UserSummaryDao.findInUserSummaryTable(userId);
			RateLimiter.iterateThroughRepos(repoResults, devInfo);
			developers.add(devInfo);
		}

		return isSearchTermInDB;
	}
	/**
	 * Will find user repository details on the basis of its userId 
	 * */
	public static void iterateThroughRepos(MongoCursor<Document> repoResults, DevInfo devInfo) {
		List<RepositoryDetails> repositories = new ArrayList<>();
		while (repoResults.hasNext()) {
			Document repoResult = repoResults.next();
			String user = repoResult.getString("userId");
			Long commits = repoResult.getLong("commits");
			String repoName = repoResult.getString("repoName");
			repositories.add(new RepositoryDetails(repoName, commits));
		}
		devInfo.setRepositories(repositories);
	}

	public static void computeResultsFromAPI(UsersList userInfo, List<DevInfo> developers) {
		String fullName = userInfo.getFullName();
		String location = userInfo.getLocation();
		DevInfo devInfo;
		System.out.println("Fetching from API");

		try {
			JSONObject obj = RateLimiter.sendSearchRequestToServer(fullName, location);
			JSONArray userList = (JSONArray) obj.get("items");
			List<Document> searchResults = new ArrayList<Document>();
			for (int i = 0; i < userList.size(); i++) {
				JSONObject user = (JSONObject) userList.get(i);
				String reposUrl = (String) user.get("repos_url");
				String login = (String) user.get("login");
				String htmlUrl = (String) user.get("html_url");
				if(location.isEmpty()) {
					devInfo = new DevInfo(fullName);
				} else {
					devInfo = new DevInfo(fullName, location);
				}
				devInfo.setUserId(login);
				devInfo.setProfileLink(htmlUrl);
				JSONArray reposResult = (JSONArray) Network.fetchResult(reposUrl,true);
				ApiDao.fetchCommits(reposResult, login, devInfo);
				developers.add(devInfo);
				searchResults.add(SearchTableDao.createSearchResult(login, fullName, location,htmlUrl));
				
			}
			SearchTableDao.insertInSearchTable(searchResults);
		} catch (IOException| URISyntaxException e) {
			logger.error(e);
		}
		}

}
