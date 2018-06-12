package gitapi.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gitapi.utils.Network;

public class ApiDao {

	public static void fetchCommits(JSONArray repos, String userId) {

		for (int i = 0; i < repos.size(); i++) {
			try {
				JSONObject repo = (JSONObject) repos.get(i);
				String repoName = (String) repo.get("name");
				System.out.println(repo.get("name"));
				String contributersUrl = (String) repo.get("contributors_url");
				JSONArray contributers = (JSONArray) Network.fetchResult(contributersUrl);
				fetchCommitsByRepo(contributers, userId, repoName);
			} catch (Exception e) {
				System.out.println(e);
			}

		}
	}

	public static void fetchCommitsByRepo(JSONArray contributers, String userId, String repoName) {
		List<Document> searchResults = new ArrayList<>();
		for (int i = 0; i < contributers.size(); i++) {

			JSONObject contributer = (JSONObject) contributers.get(i);
			String loginId = (String) contributer.get("login");
			Long contributions = (Long) contributer.get("contributions");
			if (userId.equals(loginId)) {
				System.out.println(loginId + "  " + contributions);
				searchResults.add(UserSummaryDao.createUserSummaryResult(loginId, repoName, contributions));
				break;
			}

		}

		UserSummaryDao.insertInUserSummaryTable(searchResults);
	}

}
