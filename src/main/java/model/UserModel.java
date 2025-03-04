package model;

import dao.TimetableDAO;
import dao.UserDAO;
import dto.UserDTO;
import entity.Role;
import entity.TimetableEntity;
import entity.UserEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.prefs.BackingStoreException;

public class UserModel {
	private static final UserDAO userDAO = new UserDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();

	public UserDTO getLoggedInUser() {
		UserEntity user = userDAO.findById(UserPreferences.getUserId());

		if (user == null) {
			logout();
			throw new IllegalArgumentException("User not found");
		}

		return convertToDTO(user);
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
		if (!isValid(userDTO)) {
			throw new IllegalArgumentException("Invalid user data");
		}

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user = convertToEntity(userDTO, timetable);

		userDAO.persist(user);

		return true;
	}

	public void update(UserDTO userDTO) {

		UserEntity user = userDAO.findById(UserPreferences.getUserId());

		if (user == null) {
			logout();
			throw new IllegalArgumentException("User not found");
		}

		if (!isValid(userDTO)) {
			throw new IllegalArgumentException("Invalid user data");
		}

		user.setUsername(userDTO.username());
		user.setPassword(userDTO.password());
		user.setFirstName(userDTO.firstName());
		user.setLastName(userDTO.lastName());
		user.setDateOfBirth(Timestamp.valueOf(userDTO.dateOfBirth()));
		user.setSocialNumber(userDTO.socialNumber());
		user.setRole(Role.valueOf(userDTO.role()));

		userDAO.persist(user);
	}

	public boolean isValid(UserDTO userDTO) {
		return isUsernameValid(userDTO.username()) && isPasswordValid(userDTO.password()) &&
		       isFirstNameValid(userDTO.firstName()) && isLastNameValid(userDTO.lastName()) &&
		       isDateOfBirthValid(userDTO.dateOfBirth()) && isSocialNumberValid(userDTO.socialNumber()) &&
		       isRoleValid(userDTO.role());
	}

	private boolean isUsernameValid(String username) {
		return username != null && !username.trim().isEmpty();
	}

	private boolean isPasswordValid(String password) {
		return password != null && password.length() >= 8;
	}

	private boolean isFirstNameValid(String firstName) {
		return firstName != null && !firstName.trim().isEmpty();
	}

	private boolean isLastNameValid(String lastName) {
		return lastName != null && !lastName.trim().isEmpty();
	}

	private boolean isDateOfBirthValid(LocalDateTime dateOfBirth) {
		return dateOfBirth != null && dateOfBirth.isBefore(LocalDateTime.now());
	}

	private boolean isSocialNumberValid(String socialNumber) {
		return socialNumber != null && socialNumber.length() == 11;
	}

	private boolean isRoleValid(String role) {
		return role != null && (role.equals("STUDENT") || role.equals("TEACHER"));
	}

	public boolean isUserLoggedIn() {
		return UserPreferences.getUserId() != -1;
	}

	public boolean logout() {
		try {
			UserPreferences.deleteUser();
			return true;
		} catch (BackingStoreException e) {
			System.out.println("Error logging out user: " + e.getMessage());
			return false;
		}
	}

	private UserDTO convertToDTO(UserEntity user) {
		return new UserDTO(user.getUsername(),
		                   user.getPassword(),
		                   user.getFirstName(),
		                   user.getLastName(),
		                   user.getDateOfBirth().toLocalDateTime(),
		                   user.getSocialNumber(),
		                   user.getRole().toString());
	}

	private UserEntity convertToEntity(UserDTO user, TimetableEntity timetable) {
		return new UserEntity(user.firstName(),
		                      user.lastName(),
		                      user.username(),
		                      user.password(),
		                      Timestamp.valueOf(user.dateOfBirth()),
		                      user.socialNumber(),
		                      Role.valueOf(user.role()),
		                      timetable);
	}

	public boolean isUsernameTaken(String username) {
		return userDAO.findByUsername(username) != null;
	}

	public void deleteAllUsers() {
		userDAO.deleteAll();
		timetableDAO.deleteAll();
	}
}