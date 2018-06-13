package gitapi.model;

import java.util.List;

public class UsersList {
	private String fullName;
	private String location;
	private List<DevInfo> developers;

	public UsersList() {
	}

	public UsersList(String fullName, String location) {
		this.fullName = fullName;
		this.location = location;
	}

	public UsersList(String fullName) {
		this.fullName = fullName;
		this.location = "";
	}

	public String getFullName() {
		return fullName;
	}

	public String getLocation() {
		return location;
	}

	
	public void setDevInfo(List<DevInfo> developers) {
		this.developers = developers;
	}

	public List<DevInfo> getDevelopers() {
		return developers;
	}
	
}
