package gitapi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import gitapi.GitMain;

public class Network {
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final Logger logger = LogManager.getLogger(Network.class);

	public static GenericUrl createSearchRequest(String fullName, String location)
			throws MalformedURLException, URISyntaxException {

		String query = fullName + " in:fullname";
		if (!location.isEmpty()) {
			query = "location:" + location + " " + query;
		}
		URIBuilder builder = new URIBuilder();
		builder.setScheme("https");
		builder.setHost("api.github.com");
		builder.setPath("/search/users");
		builder.addParameter("q", query);
		builder.addParameter("type", "Users");
		if(GitMain.getAuthToken() != null)
			builder.addParameter("access_token", GitMain.getAuthToken());

		String urlString = builder.build().toURL().toString();
		System.out.println(urlString);
		GenericUrl url = new GenericUrl(urlString);
		return url;

	}

	public static boolean isRateLimitRemaining(HttpResponse response) {
		HttpHeaders headerNames = response.getHeaders();
		List rateLimit = ((ArrayList) headerNames.get("x-ratelimit-remaining"));
		System.out.println("Rate-Limit :" + rateLimit.get(0));

		if (!((String) (rateLimit.get(0))).equals(0))
			return true;

		return false;
	}

	public static Object fetchResult(String u) throws IOException {
		if(GitMain.getAuthToken() != null)
			u+="?access_token="+GitMain.getAuthToken();
		Object obj = null;
		HttpResponse response = null;
		System.out.println(u);
		try {
			GenericUrl url = new GenericUrl(u);
			HttpRequest request = HTTP_TRANSPORT.createRequestFactory().buildGetRequest(url);
			response = request.execute();
			if (!Network.isRateLimitRemaining(response))
				throw new Exception("Rate Limit Exceeded");
			// System.out.println(response.getStatusCode());
			
			InputStream is = response.getContent();
			JSONParser jsonParse = new JSONParser();
			obj = jsonParse.parse(new InputStreamReader(is, "UTF-8"));
		} catch (Exception e) {
			logger.error("Exception in parsing.", e);

		} finally {
			if(response!=null)
				response.disconnect();
		}
		return obj;
	}

}
