package gitapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gitapi.model.DevInfo;
import gitapi.service.RateLimiter;

public class GitMain {
	private static final Logger logger = LogManager.getLogger(GitMain.class);
	
	private static List<DevInfo> developers;
	private static String authToken;
	
	public static String getAuthToken() {
		return authToken;
	}

	public static void main(String[] args) {
		logger.info("Start");
		developers = new ArrayList<>();
		boolean exitCode = true;
		
		try {
			authToken = args[0];
			logger.info("Auth token detected");
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.info("No auth token detected. Continuing as anonymous user.");
			logger.warn("Rate limit will be reduced as an anonymous user.");
		}
		
		while(exitCode) {
			Scanner scan = new Scanner(System.in);
			System.out.println("Enter Full Name");
			String fullName = scan.nextLine();
			System.out.println("Enter Location");
			String location = scan.nextLine();
			
			if(location.isEmpty()) {
				System.out.println("Missing location. Just using Full Name to search");
				developers.add(new DevInfo(fullName));
			} else {
				developers.add(new DevInfo(fullName, location));
			}
			
			System.out.println("Enter * to stop or return to continue");
			if(scan.nextLine().equals("*")) {
				exitCode = false;
				scan.close();
			}
		}
		
//		String location = "";
//		String fullName = "Nishan Perera";
		developers.forEach(devInfo -> {
			RateLimiter.computeResults(devInfo.getFullName(), devInfo.getLocation());
		});
//		RateLimiter.computeResults(fullName, location);

	}
}
