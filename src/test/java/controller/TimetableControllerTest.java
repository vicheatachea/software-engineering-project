package controller;

import datasource.MariaDBConnection;
import dto.GroupDTO;
import dto.SubjectDTO;
import dto.UserDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimetableControllerTest {

	private static final TimetableController timetableController = new TimetableController();
	private static final UserController userController = new UserController();
	private static final GroupController groupController = new GroupController();
	private static final SubjectController subjectController = new SubjectController();

	private static UserDTO createTeacher() {
		return new UserDTO("testTimetable", "testPassword", "Test", "Teacher",
		                   LocalDateTime.of(2000, 1, 1, 0, 0), "BA987654321", "TEACHER");
	}

	private static void resetDatabase() {
		subjectController.deleteAllSubjects();
		userController.deleteAllUsers();
		timetableController.deleteAllTimetables();
	}

	@BeforeAll
	static void ensureDatabase() {
		try {
			MariaDBConnection.getInstance().verifyDatabase();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void tearDown() {
		resetDatabase();
	}

	UserDTO createStudent() {
		return new UserDTO("testUser", "testPassword", "Test", "User",
		                   LocalDateTime.of(2000, 1, 1, 0, 0), "123456789AB", "STUDENT");
	}

	@BeforeEach
	void setUp() {
		resetDatabase();
	}

	@Test
	void fetchTimetableForUser() {
		UserDTO student = createStudent();

		userController.registerUser(student);

		userController.authenticateUser(student.username(), student.password());

		assertNotNull(timetableController.fetchTimetableForUser());
	}

	@Test
	void invalidFetchTimetableForUser() {
		UserDTO student = createStudent();

		userController.registerUser(student);

		assertThrows(IllegalArgumentException.class, timetableController::fetchTimetableForUser);
	}

	@Test
	void fetchTimetableForGroup() {
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		SubjectDTO subjectDTO = new SubjectDTO("Math", "MATH101");

		subjectController.addSubject(subjectDTO);

		GroupDTO groupDTO =
				new GroupDTO("testGroup", "testCode", 35, userController.fetchCurrentUserId(), subjectDTO.code());

		groupController.addGroup(groupDTO);

		assertNotNull(timetableController.fetchTimetableForGroup(groupDTO.name()));
	}

	@Test
	void invalidFetchTimetableForGroup() {
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		SubjectDTO subjectDTO = new SubjectDTO("Math", "MATH101");
		subjectController.addSubject(subjectDTO);

		GroupDTO groupDTO = new GroupDTO("testGroup", "testCode", 35, userController.fetchCurrentUserId(), "MATH101");

		groupController.addGroup(groupDTO);

		assertThrows(NullPointerException.class, () -> timetableController.fetchTimetableForGroup("invalidGroup"));
	}
}