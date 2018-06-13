package gitapi.model;

import java.util.List;

public class DevInfo {
	private String fullName;
	private String location;
	private List<RepositoryDetails> repositories;
	private String userId;
	private String profileLink;

	public DevInfo() {
	}

	public DevInfo(String fullName, String location) {
		this.fullName = fullName;
		this.location = location;
	}

	public DevInfo(String fullName) {
		this.fullName = fullName;
		this.location = "";
	}

	public String getFullName() {
		return fullName;
	}

	public String getLocation() {
		return location;
	}

	public void setRepositories(List<RepositoryDetails> repositories) {
		this.repositories = repositories;
	}

	public List<RepositoryDetails> getRepositories() {
		return repositories;
	}
	
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setProfileLink(String profileLink) {
		this.profileLink = profileLink;
	}

	public String getProfileLink() {
		return profileLink;
	}

}
