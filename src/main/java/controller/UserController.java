package controller;

import dto.UserDTO;
import model.UserModel;

import java.util.List;
import java.util.Set;

/**
 * Controller class for managing user-related operations in the system.
 * Handles authentication, registration, user information retrieval, and user management
 * by interfacing with the UserModel layer.
 */
public class UserController {
	private final UserModel userModel = new UserModel();

	/**
	 * Checks if a user is currently logged into the system.
	 *
	 * @return true if a user is logged in, false otherwise
	 */
	public boolean isUserLoggedIn() {
		return userModel.isUserLoggedIn();
	}

	/**
	 * Authenticates a user with the provided credentials.
	 *
	 * @param username The username for authentication
	 * @param password The password for authentication
	 * @return true if authentication is successful, false otherwise
	 */
	public boolean authenticateUser(String username, String password) {
		return userModel.authenticate(username, password);
	}

	/**
	 * Registers a new user in the system.
	 *
	 * @param userDTO The user data to register
	 * @return true if registration is successful, false otherwise
	 */
	public boolean registerUser(UserDTO userDTO) {
		return userModel.register(userDTO);
	}

	/**
	 * Retrieves the currently logged-in user's information.
	 *
	 * @return The UserDTO of the logged-in user or null if no user is logged in
	 */
	public UserDTO getLoggedInUser() {
		return userModel.getLoggedInUser();
	}

	/**
	 * Fetches all students registered in the system.
	 *
	 * @return A list of all student user DTOs
	 */
	public List<UserDTO> fetchAllStudents() {
		return userModel.fetchAllStudents();
	}

	/**
	 * Retrieves all students belonging to a specific group.
	 *
	 * @param groupName The name of the group to fetch students from
	 * @return A set of user DTOs for students in the specified group
	 */
	public Set<UserDTO> fetchStudentsInGroup(String groupName) {
		return userModel.fetchStudentsInGroup(groupName);
	}

	/**
	 * Updates an existing user's information.
	 *
	 * @param userDTO The updated user data
	 */
	public void updateUser(UserDTO userDTO) {
		userModel.update(userDTO);
	}

	/**
	 * Logs out the currently logged-in user.
	 */
	public void logout() {
		userModel.logout();
	}

	/**
	 * Deletes all users from the system.
	 * This is a maintenance operation and should be used with caution.
	 */
	public void deleteAllUsers() {
		userModel.deleteAllUsers();
	}

	/**
	 * Checks if a username is already taken in the system.
	 *
	 * @param username The username to check
	 * @return true if the username is taken, false otherwise
	 */
	public boolean isUsernameTaken(String username) {
		return userModel.isUsernameTaken(username);
	}

	/**
	 * Determines if the currently logged-in user has teacher privileges.
	 *
	 * @return true if the current user is a teacher, false otherwise
	 */
	public boolean isCurrentUserTeacher() {
		return userModel.isCurrentUserTeacher();
	}

	/**
	 * Retrieves the ID of the currently logged-in user.
	 *
	 * @return The user ID of the current user
	 */
	public long fetchCurrentUserId() {
		return userModel.fetchCurrentUserId();
	}

	/**
	 * Checks if a social security number is already registered in the system.
	 *
	 * @param socialNumber The social number to check
	 * @return true if the social number is taken, false otherwise
	 */
	public boolean isSocialNumberTaken(String socialNumber) {
		return userModel.isSocialNumberTaken(socialNumber);
	}
}