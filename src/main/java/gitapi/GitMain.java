package gitapi;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gitapi.service.RateLimiter;

public class GitMain {
	private static final Logger logger = LogManager.getLogger(GitMain.class);

	public static void main(String[] args) {
		logger.info("Start");
		String location = "London";
		String fullName = "Nishan Perera";
		RateLimiter.computeResults(fullName, location);

	}
}
