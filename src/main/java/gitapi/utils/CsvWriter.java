package gitapi.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import gitapi.model.DevInfo;
import gitapi.model.UsersList;

public class CsvWriter {
	private static final String COMMA_DELIMITER = ", ";
	private static final String NEW_LINE_SEPARATOR = "\n";

	public static void writeCsvFile(List<UsersList> userList) throws IOException {
		
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter("UserInfo");
			for(UsersList userInfo : userList) {
				for(DevInfo user : userInfo.getDevelopers()) {
						fileWriter.append(String.valueOf(user.getFullName()));
						fileWriter.append(COMMA_DELIMITER);
						fileWriter.append(String.valueOf(user.getLocation()));
						fileWriter.append(COMMA_DELIMITER);
						fileWriter.append(String.valueOf(user.getProfileLink()));
						fileWriter.append(COMMA_DELIMITER);
						fileWriter.append(String.valueOf(user.getUserId()));
						fileWriter.append(COMMA_DELIMITER);
						fileWriter.append(String.valueOf(user.getRepositories()));
						fileWriter.append(NEW_LINE_SEPARATOR);
				}
				fileWriter.append(NEW_LINE_SEPARATOR);
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			System.out.println("CSV file was created successfully !!!");

		} catch(Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch	(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
