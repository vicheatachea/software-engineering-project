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

	public String getFullName() {
		return userModel.getFirstName() + " " + userModel.getLastName();
	}

	public String getRole() {
		return userModel.getRole();
	}

	public String getSocialNumber() {
		return userModel.getSocialNumber();
	}
}