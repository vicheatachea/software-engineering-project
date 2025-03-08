package controller;

import dto.UserDTO;
import model.UserModel;

import java.util.List;
import java.util.Set;

public class UserController {
	private final UserModel userModel = new UserModel();

	public boolean isUserLoggedIn() {
		return userModel.isUserLoggedIn();
	}

	public boolean authenticateUser(String username, String password) {
		return userModel.authenticate(username, password);
	}

	public boolean registerUser(UserDTO userDTO) {
		return userModel.register(userDTO);
	}

	public UserDTO getLoggedInUser() {
		return userModel.getLoggedInUser();
	}

	public List<UserDTO> fetchAllStudents() {
		return userModel.fetchAllStudents();
	}

	public Set<UserDTO> fetchStudentsInGroup(String groupName) {
		return userModel.fetchStudentsInGroup(groupName);
	}

	public void updateUser(UserDTO userDTO) {
		userModel.update(userDTO);
	}

	public void logout() {
		userModel.logout();
	}

	public void deleteAllUsers() {
		userModel.deleteAllUsers();
	}

	public boolean isUsernameTaken(String username) {
		return userModel.isUsernameTaken(username);
	}

	public boolean isCurrentUserTeacher() {
		return userModel.isCurrentUserTeacher();
	}

	public long fetchCurrentUserId() {
		return userModel.fetchCurrentUserId();
	}
}