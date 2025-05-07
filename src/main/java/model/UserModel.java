package model;

import dao.TimetableDAO;
import dao.UserDAO;
import dao.UserGroupDAO;
import dto.UserDTO;
import entity.Role;
import entity.TimetableEntity;
import entity.UserEntity;
import entity.UserGroupEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Model class responsible for managing user-related operations.
 * Handles user authentication, registration, profile updates, and user data retrieval.
 */
public class UserModel {
	private static final Logger logger = LoggerFactory.getLogger(UserModel.class);
	private static final UserDAO userDAO = new UserDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();
	private static final UserGroupDAO groupDAO = new UserGroupDAO();

	/**
	 * Retrieves the currently logged-in user.
	 *
	 * @return Data transfer object containing user information
	 * @throws IllegalArgumentException if the user is not found
	 */
	public UserDTO getLoggedInUser() {
		UserEntity user = userDAO.findById(fetchCurrentUserId());

		if (user == null) {
			logout();
			throw new IllegalArgumentException("User not found");
		}

		return convertToDTO(user);
	}

	/**
	 * Retrieves all students in the system.
	 *
	 * @return List of data transfer objects containing student information
	 */
	public List<UserDTO> fetchAllStudents() {
		List<UserDTO> users = new ArrayList<>();
		for (UserEntity user : userDAO.findAll()) {
			users.add(convertToDTO(user));
		}
		return users;
	}

	/**
	 * Retrieves all students in a specific group.
	 *
	 * @param groupName The name of the group
	 * @return Set of data transfer objects containing student information
	 * @throws IllegalArgumentException if the group does not exist
	 */
	public Set<UserDTO> fetchStudentsInGroup(String groupName) {
		UserGroupEntity group = groupDAO.findByName(groupName);

		if (group == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		Set<UserDTO> students = new HashSet<>();

		for (UserEntity student : group.getStudents()) {
			students.add(convertToDTO(student));
		}

		return students;
	}

	/**
	 * Authenticates a user with username and password.
	 *
	 * @param username The username of the user
	 * @param password The password of the user
	 * @return true if authentication is successful, false otherwise
	 */
	public boolean authenticate(String username, String password) {
		UserEntity user = userDAO.authenticate(username, password);

		if (user == null) {
			return false;
		}

		UserPreferences.setUser(user.getId(), user.getRole());
		return true;
	}

	/**
	 * Registers a new user in the system.
	 *
	 * @param userDTO Data transfer object containing user information
	 * @return true if registration is successful, false otherwise
	 */
	public boolean register(UserDTO userDTO) {
		try {
			isValid(userDTO);

			if (userDAO.findByUsername(userDTO.username()) != null) {
				throw new IllegalArgumentException("Username already exists");
			}
			if (userDAO.findBySocialNumber(userDTO.socialNumber())) {
				throw new IllegalArgumentException("Social number already exists");
			}

			TimetableEntity timetable = new TimetableEntity();

			timetableDAO.persist(timetable);

			UserEntity user = convertToEntity(userDTO, timetable);

			userDAO.persist(user);

			return true;
		} catch (IllegalArgumentException e) {
			logger.info("Registration failed: {}", e.getMessage());
			return false; // Registration failed
		}
	}

	/**
	 * Updates the current user's profile information.
	 *
	 * @param userDTO Data transfer object containing updated user information
	 * @throws IllegalArgumentException if the update fails
	 */
	public void update(UserDTO userDTO) {
		try {
			// Validate input data
			isValid(userDTO);

			// Get the current user
			UserEntity user = userDAO.findById(fetchCurrentUserId());
			if (user == null) {
				logout();
				throw new IllegalArgumentException("User not found");
			}

			// Check if username is being changed and is already taken
			if (!user.getUsername().equals(userDTO.username()) &&
			    userDAO.findByUsername(userDTO.username()) != null) {
				throw new IllegalArgumentException("Username already exists");
			}

			// Check if social number is being changed and is already taken by another user
			if (!user.getSocialNumber().equalsIgnoreCase(userDTO.socialNumber()) &&
			    userDAO.findBySocialNumber(userDTO.socialNumber())) {
				throw new IllegalArgumentException("Social number already exists");
			}

			// Update user fields
			user.setUsername(userDTO.username());
			user.setPassword(userDTO.password());
			user.setFirstName(userDTO.firstName());
			user.setLastName(userDTO.lastName());
			user.setDateOfBirth(Timestamp.valueOf(userDTO.dateOfBirth()));
			user.setSocialNumber(userDTO.socialNumber());

			// Only allow role changes if necessary (or add permission check here)
			user.setRole(Role.valueOf(userDTO.role()));

			// Save changes
			userDAO.update(user);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Update failed: " + e.getMessage());
		}
	}

	/**
	 * Validates user data for registration and updates.
	 *
	 * @param userDTO Data transfer object to validate
	 * @throws IllegalArgumentException if any validation fails
	 */
	private void isValid(UserDTO userDTO) {
		if (userDTO.username() == null || userDTO.username().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be empty.");
		}

		if (userDTO.password() == null || userDTO.password().isEmpty()) {
			throw new IllegalArgumentException("Password cannot be empty.");
		}

		if (userDTO.password().length() < 8) {
			throw new IllegalArgumentException("Password must be at least 8 characters long.");
		}

		if (userDTO.firstName() == null || userDTO.firstName().isEmpty()) {
			throw new IllegalArgumentException("First name cannot be empty.");
		}

		if (userDTO.lastName() == null || userDTO.lastName().isEmpty()) {
			throw new IllegalArgumentException("Last name cannot be empty.");
		}

		if (userDTO.dateOfBirth() == null) {
			throw new IllegalArgumentException("Date of birth cannot be empty.");
		}

		if (userDTO.socialNumber() == null || userDTO.socialNumber().isEmpty()) {
			throw new IllegalArgumentException("Social number cannot be empty.");
		}

		if (userDTO.socialNumber().length() != 11) {
			throw new IllegalArgumentException("Social number must be exactly 11 characters long.");
		}

		if (userDTO.role() == null || userDTO.role().isEmpty()) {
			throw new IllegalArgumentException("Role cannot be empty.");
		}
	}

	/**
	 * Checks if a user is currently logged in.
	 *
	 * @return true if a user is logged in, false otherwise
	 */
	public boolean isUserLoggedIn() {
		if (UserPreferences.getUserId() != -1) {
			UserEntity user = userDAO.findById(fetchCurrentUserId());
			if (user == null) {
				logout();
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Logs out the current user by removing user data from preferences.
	 */
	public void logout() {
		UserPreferences.deleteUser();
	}

	/**
	 * Converts a user entity to a data transfer object.
	 *
	 * @param user The user entity to convert
	 * @return Data transfer object containing user information
	 */
	UserDTO convertToDTO(UserEntity user) {
		return new UserDTO(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(),
		                   user.getDateOfBirth().toLocalDateTime(), user.getSocialNumber(),
		                   user.getRole().toString());
	}

	/**
	 * Converts a data transfer object to a user entity.
	 *
	 * @param user      The data transfer object to convert
	 * @param timetable The timetable entity to associate with the user
	 * @return User entity
	 */
	UserEntity convertToEntity(UserDTO user, TimetableEntity timetable) {
		return new UserEntity(user.firstName(), user.lastName(), user.username(), user.password(),
		                      Timestamp.valueOf(user.dateOfBirth()), user.socialNumber(), Role.valueOf(user.role()),
		                      timetable);
	}

	/**
	 * Checks if a username is already taken.
	 *
	 * @param username The username to check
	 * @return true if the username is taken, false otherwise
	 */
	public boolean isUsernameTaken(String username) {
		return userDAO.findByUsername(username) != null;
	}

	/**
	 * Deletes all users from the system.
	 */
	public void deleteAllUsers() {
		userDAO.deleteAll();
		timetableDAO.deleteAll();
		logout();
	}

	/**
	 * Checks if the current user is a teacher.
	 *
	 * @return true if the current user is a teacher, false otherwise
	 */
	public boolean isCurrentUserTeacher() {
		return UserPreferences.getUserRole().equals(Role.TEACHER);
	}

	/**
	 * Retrieves the current user's ID.
	 *
	 * @return The user ID, or -1 if no user is logged in
	 */
	public long fetchCurrentUserId() {
		return UserPreferences.getUserId();
	}

	/**
	 * Checks if a social number is already taken.
	 *
	 * @param socialNumber The social number to check
	 * @return true if the social number is taken, false otherwise
	 */
	public boolean isSocialNumberTaken(String socialNumber) {
		return userDAO.findBySocialNumber(socialNumber);
	}
}