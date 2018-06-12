package gitapi.model;

public class RepositoryDetails {
	private String repoName;
	private int numberOfCommits;

	public RepositoryDetails() {
	}

	public RepositoryDetails(String repoName, int numberOfCommits) {
		this.repoName = repoName;
		this.numberOfCommits = numberOfCommits;
	}

	public String getRepoName() {
		return repoName;
	}

	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}

	public int getNumberOfCommits() {
		return numberOfCommits;
	}

	public void setNumberOfCommits(int numberOfCommits) {
		this.numberOfCommits = numberOfCommits;
	}

	@Override
	public String toString() {
		return "RepositoryDetails [repoName=" + repoName + ", numberOfCommits=" + numberOfCommits + "]";
	}

}
