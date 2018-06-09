package gitapi.api;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class Network {
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final Logger logger = LogManager.getLogger(Network.class);
	private static HttpRequest request;
	private static HttpResponse response;
	
    public void getRequest(String reqUrl) throws Exception {
    	
    	URIBuilder builder = new URIBuilder();
    	builder.setScheme("https");
    	builder.setHost("api.github.com");
    	builder.setPath("/search/users");
    	builder.addParameter("q", "location:Gurgaon Chunky Garg in:fullname");
    	builder.addParameter("type", "Users");
    	//builder.addParameter("access_token", "*TOKEN HERE*");
    	String u = builder.build().toURL().toString();
    	System.out.println(u);
    	GenericUrl url = new GenericUrl(u);
        
        
        HttpRequest request = HTTP_TRANSPORT.createRequestFactory().buildGetRequest(url);
        
        HttpResponse response = request.execute();
        //System.out.println(response.getStatusCode());
        InputStream is = response.getContent();
        JSONParser jsonParse = new JSONParser();
        JSONObject obj = (JSONObject)jsonParse.parse(
        	      new InputStreamReader(is, "UTF-8"));
        System.out.println(obj);
        System.out.println("*********");
        JSONArray userList = (JSONArray) obj.get("items"); 
        System.out.println(userList.size());
        for(int i=0;i<userList.size();i++)
        {
        	JSONObject user = (JSONObject)userList.get(i);
        	String reposUrl = (String)user.get("repos_url");
        	String login = (String)user.get("login");
        	JSONArray reposResult = Network.fetchResult(reposUrl);
        	Network.fetchCommits(reposResult,login);
        	
        	
        }
        response.disconnect();
        
    }
    
    public static JSONArray fetchResult(String u) throws Exception
    {
    	//u+="?access_token="+"*TOKEN HERE*";
    	JSONArray obj = null;
    	System.out.println(u);
    	try {
    		GenericUrl url = new GenericUrl(u);
            
            request = HTTP_TRANSPORT.createRequestFactory().buildGetRequest(url);
            
            response = request.execute();
            //System.out.println(response.getStatusCode());
            InputStream is = response.getContent();
            JSONParser jsonParse = new JSONParser();
            obj = (JSONArray)jsonParse.parse(new InputStreamReader(is, "UTF-8"));
		} catch (Exception e) {
			logger.error("Exception in parsing.", e);
			
		} finally {
			response.disconnect();
		}
        return obj;
    }
    
    
    public static void fetchCommits(JSONArray repos,String userId) throws Exception
    {
    	for(int i=0;i<repos.size();i++)
    	{
    		JSONObject repo = (JSONObject)repos.get(i);
    		System.out.println(repo.get("name"));
    		String contributersUrl= (String)repo.get("contributors_url");
    		JSONArray contributers= fetchResult(contributersUrl);
    		Network.fetchCommitsByRepo(contributers,userId);
    		
    	}
    }
    
    public static void fetchCommitsByRepo(JSONArray contributers,String userId)
    {
    	for(int i=0;i<contributers.size();i++)
    	{
    		JSONObject contributer = (JSONObject)contributers.get(i);
    		String loginId= (String)contributer.get("login");
    		Long contributions= (Long)contributer.get("contributions");
    		if(userId.equals(loginId)) {
    			System.out.println(loginId+"  "+contributions);
    			break;
    		}
    			
    	}
    }
    
    
}
