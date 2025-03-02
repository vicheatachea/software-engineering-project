package controller;

import dto.UserDTO;
import model.UserModel;

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

	public void updateUser(UserDTO userDTO) {
		userModel.update(userDTO);
	}

	public void logout() {
		userModel.logout();
	}
}