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
	private static final BaseController baseController = new BaseController();
	private static final UserController userController = baseController.getUserController();

	@BeforeAll
	static void ensureDatabase() {
		try {
			new MariaDBConnection().verifyDatabase();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void tearDown() {
		userController.deleteAllUsers();
	}

	UserDTO createStudentDTO(String username, String socialNumber) {
		return new UserDTO(username, "testPassword", "Test", "User", LocalDateTime.of(2000, 1, 1, 0, 0), socialNumber,
		                   "STUDENT");
	}

	UserDTO createTeacherDTO() {
		return new UserDTO("teacherUsername", "testPassword", "Test", "User", LocalDateTime.of(2000, 1, 1, 0, 0),
		                   "ba987654321", "TEACHER");
	}

	@BeforeEach
	void setUp() {
		userController.deleteAllUsers();

	}

	@Test
	void registerUser() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");

		assertTrue(userController.registerUser(userDTO));
	}

	@Test
	void registerUserWithInvalidData() {
		UserDTO userDTO = new UserDTO("", "", "", "", LocalDateTime.now(), "", "");

		assertFalse(userController.registerUser(userDTO));
	}

	@Test
	void authenticateUser() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
	}

	@Test
	void authenticateUserWithInvalidPassword() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertThrows(IllegalArgumentException.class,
		             () -> userController.authenticateUser("username", "wrongPassword"));
	}

	@Test
	void authenticateUserWithInvalidCredentials() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertThrows(IllegalArgumentException.class,
		             () -> userController.authenticateUser("wrongUsername", "wrongPassword"));
	}

	@Test
	void isUserLoggedIn() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
		assertTrue(userController.isUserLoggedIn());
	}

	@Test
	void isUserLoggedInWithoutAuthentication() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertFalse(userController.isUserLoggedIn());
	}

	@Test
	void getLoggedInUser() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
		assertEquals(userController.getLoggedInUser().username(), userDTO.username());
	}

	@Test
	void updateUser() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));

		UserDTO updatedUserDTO =
				new UserDTO("updated user", "updated password", "updated first name", "updated last name",
				            LocalDateTime.now().minusYears(18), "123456789AB", "TEACHER");

		userController.updateUser(updatedUserDTO);

		assertEquals(userController.getLoggedInUser().username(), updatedUserDTO.username());
	}

	@Test
	void updateUserWithInvalidData() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		UserDTO newUserDTO = new UserDTO("", "", "", "", LocalDateTime.now(), "", "");

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
		assertThrows(IllegalArgumentException.class, () -> userController.updateUser(newUserDTO));
	}

	@Test
	void updateUserWithUserNotLoggedIn() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		UserDTO newUserDTO = new UserDTO("", "", "", "", LocalDateTime.now(), "", "");

		assertThrows(IllegalArgumentException.class, () -> userController.updateUser(newUserDTO));
	}

	@Test
	void logout() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
		assertTrue(userController.isUserLoggedIn());

		userController.logout();

		assertFalse(userController.isUserLoggedIn());
	}

	@Test
	void isUsernameTaken() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.isUsernameTaken(userDTO.username()));
		assertFalse(userController.isUsernameTaken("nonExistentUsername"));
	}

	@Test
	void fetchAllStudents() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		UserDTO userDTO2 = createStudentDTO("username2", "ba987654321");
		userController.registerUser(userDTO2);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));
		assertTrue(userController.authenticateUser(userDTO2.username(), userDTO2.password()));

		assertFalse(userController.fetchAllStudents().isEmpty());
	}

	@Test
	void isCurrentUserTeacher() {
		UserDTO userDTO = createStudentDTO("username", "123456789ab");
		userController.registerUser(userDTO);

		assertTrue(userController.authenticateUser(userDTO.username(), userDTO.password()));

		assertFalse(userController.isCurrentUserTeacher());

		UserDTO teacherDTO = createTeacherDTO();
		userController.registerUser(teacherDTO);

		assertTrue(userController.authenticateUser(teacherDTO.username(), teacherDTO.password()));

		assertTrue(userController.isCurrentUserTeacher());
	}
}