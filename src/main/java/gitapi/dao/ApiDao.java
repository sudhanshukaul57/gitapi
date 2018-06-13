package gitapi.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gitapi.model.DevInfo;
import gitapi.model.RepositoryDetails;
import gitapi.utils.Network;

public class ApiDao {

	@SuppressWarnings("unchecked")
	public static void fetchCommits(JSONArray repos, String userId, DevInfo devInfo) {
		for (int i = 0; i < repos.size(); i++) {
			try {
				JSONObject repo = (JSONObject) repos.get(i);
				String repoName = (String) repo.get("name");
				String contributersUrl = (String) repo.get("contributors_url");
				JSONArray contributers = (JSONArray) Network.fetchResult(contributersUrl,true);
				fetchCommitsByRepo(contributers, userId, repoName, devInfo);
			} catch (Exception e) {
				System.out.println(e);
			}

		}
	}

	/**
	 * fetches developer's repo data and commits on the basis of userId
	 * */
	public static void fetchCommitsByRepo(JSONArray contributers, String userId, String repoName, DevInfo devInfo) {
		List<Document> searchResults = new ArrayList<>();
		List<RepositoryDetails> repositories = new ArrayList<>();
		for (int i = 0; i < contributers.size(); i++) {

			JSONObject contributer = (JSONObject) contributers.get(i);
			String loginId = (String) contributer.get("login");
			Long contributions = (Long) contributer.get("contributions");
			if (userId.equals(loginId)) {
				System.out.println(loginId + "  " + contributions);
				repositories.add(new RepositoryDetails(repoName, contributions));
				searchResults.add(UserSummaryDao.createUserSummaryResult(loginId, repoName, contributions));
				break;
			}

		}
		devInfo.setRepositories(repositories);

		UserSummaryDao.insertInUserSummaryTable(searchResults);
	}

}
