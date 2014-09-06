package ua.com.zaibalo.db.api;

import java.util.List;

import ua.com.zaibalo.model.User;

public interface UsersDAO{
	User getUserByEmail(String email);
	User getUserByLoginName(String name);
	User getUserByLoginOrDisplayName(String name);
	void updateUserPassword(int userId, String newPassword);
	void updateUserPassword(int userId, String newPassword, boolean encode);
	User insert(User user);
	User getUserById(int userId);
	void updateUserDisplayName(int userId, String newDisplayName);
	void updateUserImage(int id, String bigImg, String smallImg);
	void updateUserAbout(int id, String about);
	List<String> getAllUserNamesList();
	User getUserByDisplayName(String value);
	List<User> getAllUsers();
	void updateUserNotifyOnPM(int id, boolean notifyOnPM);
}
