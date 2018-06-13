package gitapi.model;

public class RepositoryDetails {
	private String repoName;
	private long numberOfCommits;

	public RepositoryDetails() {
	}

	public RepositoryDetails(String repoName, long numberOfCommits) {
		this.repoName = repoName;
		this.numberOfCommits = numberOfCommits;
	}

	public String getRepoName() {
		return repoName;
	}

	public long getNumberOfCommits() {
		return numberOfCommits;
	}


	@Override
	public String toString() {
		return "RepositoryDetails [repoName=" + repoName + ", numberOfCommits=" + numberOfCommits + "]";
	}

}
