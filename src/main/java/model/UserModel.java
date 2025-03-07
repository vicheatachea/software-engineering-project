package model;

import dao.TimetableDAO;
import dao.UserDAO;
import dto.UserDTO;
import entity.Role;
import entity.TimetableEntity;
import entity.UserEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserModel {
	private static final UserDAO userDAO = new UserDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();

	public UserDTO getLoggedInUser() {
		UserEntity user = userDAO.findById(fetchCurrentUserId());

		if (user == null) {
			logout();
			throw new IllegalArgumentException("User not found");
		}

		return convertToDTO(user);
	}

	public List<UserDTO> fetchAllStudents() {
		List<UserDTO> users = new ArrayList<>();
		for (UserEntity user : userDAO.findAll()) {
			users.add(convertToDTO(user));
		}
		return users;
	}

	public boolean authenticate(String username, String password) {
		UserEntity user = userDAO.authenticate(username, password);

		if (user != null) {
			UserPreferences.setUser(user.getId(), user.getRole());
			return true;
		}

		return false;
	}

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
			System.out.println("Registration failed: " + e.getMessage());
			return false; // Registration failed
		}
	}

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
			System.out.println("Update failed: " + e.getMessage());
			throw e; // Re-throw to notify caller of the failure
		}
	}

	private boolean isValid(UserDTO userDTO) {
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
		return true;
	}

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

	public void logout() {
		UserPreferences.deleteUser();
	}

	private UserDTO convertToDTO(UserEntity user) {
		return new UserDTO(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(),
		                   user.getDateOfBirth().toLocalDateTime(), user.getSocialNumber(),
		                   user.getRole().toString());
	}

	private UserEntity convertToEntity(UserDTO user, TimetableEntity timetable) {
		return new UserEntity(user.firstName(), user.lastName(), user.username(), user.password(),
		                      Timestamp.valueOf(user.dateOfBirth()), user.socialNumber(), Role.valueOf(user.role()),
		                      timetable);
	}

	public boolean isUsernameTaken(String username) {
		return userDAO.findByUsername(username) != null;
	}

	public void deleteAllUsers() {
		userDAO.deleteAll();
		timetableDAO.deleteAll();
		logout();
	}

	public boolean isCurrentUserTeacher() {
		return UserPreferences.getUserRole().equals(Role.TEACHER);
	}

	public long fetchCurrentUserId() {
		return UserPreferences.getUserId();
	}
}