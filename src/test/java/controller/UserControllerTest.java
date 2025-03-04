package controller;

import datasource.MariaDBConnection;
import dto.UserDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

	private static final UserController userController = new UserController();

	UserDTO createUserDTO(String username, String socialNumber) {
		String password = "testPassword";
		String firstName = "Test";
		String lastName = "User";
		LocalDateTime dateOfBirth = LocalDateTime.of(2000, 1, 1, 0, 0);
		String role = "STUDENT";
		return new UserDTO(username, password, firstName, lastName, dateOfBirth, socialNumber, role);
	}

	@BeforeAll
	static void ensureDatabase() {
		try {
			MariaDBConnection.verifyDatabase();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@BeforeEach
	void setUp() {
		userController.deleteAllUsers();
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void tearDown() {
		userController.deleteAllUsers();
	}

	@Test
	void registerUser() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");

		assertTrue(userController.registerUser(userDTO));
	}

	@Test
	void registerUserWithInvalidData() {
		UserDTO userDTO = new UserDTO("", "", "", "", LocalDateTime.now(), "", "");

		assertThrows(IllegalArgumentException.class, () -> userController.registerUser(userDTO));
	}

	@Test
	void authenticateUser() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
	}

	@Test
	void authenticateUserWithInvalidCredentials() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertThrows(IllegalArgumentException.class, () -> userController.authenticateUser("wrongUsername",
		                                                                                   "wrongPassword"));
	}

	@Test
	void isUserLoggedIn() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
		assertTrue(userController.isUserLoggedIn());
	}

	@Test
	void getLoggedInUser() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
		assertEquals(userController.getLoggedInUser().username(), userDTO.username());
	}

	@Test
	void updateUser() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));

		UserDTO updatedUserDTO = new UserDTO("updated user", "updated password", "updated first name",
		                                     "updated last name", LocalDateTime.now().minusMonths(6), "123456789AB",
		                                     "TEACHER");

		userController.updateUser(updatedUserDTO);

		assertEquals(userController.getLoggedInUser().username(), updatedUserDTO.username());
	}

	@Test
	void updateUserWithInvalidData() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
		assertThrows(IllegalArgumentException.class, () -> userController.updateUser(new UserDTO("", "", "", "",
		                                                                                         LocalDateTime.now(),
		                                                                                         "", "")));
	}

	@Test
	void updateUserWithUserNotLoggedIn() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertThrows(IllegalArgumentException.class, () -> userController.updateUser(new UserDTO("", "", "", "",
		                                                                                         LocalDateTime.now(),
		                                                                                         "", "")));
	}

	@Test
	void logout() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
		assertTrue(userController.isUserLoggedIn());

		userController.logout();

		assertFalse(userController.isUserLoggedIn());
	}

	@Test
	void isUsernameTaken() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.isUsernameTaken(userDTO.username()));
		assertFalse(userController.isUsernameTaken("nonExistentUsername"));
	}

	@Test
	void fetchAllStudents() {
		UserDTO userDTO = createUserDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		UserDTO userDTO2 = createUserDTO("username2", "ba987654321");
		userController.registerUser(userDTO2);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
		assertTrue(userController.authenticateUser(userDTO2.username(), userDTO2.password()));

		assertFalse(userController.fetchAllStudents().isEmpty());
	}
}