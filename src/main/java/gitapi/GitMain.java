package gitapi;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gitapi.api.Network;

public class GitMain {
	private static final Logger logger = LogManager.getLogger(GitMain.class);

	public static void main(String[] args) throws Exception {
		logger.info("Start");
		Network network = new Network();
		network.getRequest("");
	}
}
