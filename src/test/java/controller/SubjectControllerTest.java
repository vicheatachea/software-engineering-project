package controller;

import datasource.MariaDBConnection;
import dto.GroupDTO;
import dto.SubjectDTO;
import dto.TeachingSessionDTO;
import dto.UserDTO;
import model.UserPreferences;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubjectControllerTest {

	private static final SubjectController subjectController = new SubjectController();
	private static final UserController userController = new UserController();
	private static final EventController eventController = new EventController();
	private static final TimetableController timetableController = new TimetableController();
	private static final GroupController groupController = new GroupController();

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
		subjectController.deleteAllSubjects();
		userController.deleteAllUsers();
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void tearDown() {
		subjectController.deleteAllSubjects();
		userController.deleteAllUsers();
		userController.logout();
	}

	SubjectDTO createSubject(String name, String code) {
		return new SubjectDTO(name, code);
	}

	UserDTO createTeacher() {
		return new UserDTO("testTeacher", "testPassword", "Test", "Teacher",
		                   LocalDateTime.of(2000, 1, 1, 0, 0), "BA987654321", "TEACHER");
	}

	UserDTO createStudent() {
		return new UserDTO("testUser", "testPassword", "Test", "User",
		                   LocalDateTime.of(2000, 1, 1, 0, 0), "123456789AB", "STUDENT");
	}

	GroupDTO createGroup(long teacherId, String subjectName) {
		return new GroupDTO("Test Group", "TG101", 35, teacherId, subjectName);
	}

	@Test
	void fetchAllSubjects() {
		SubjectDTO subject1 = createSubject("Math", "MATH101");
		SubjectDTO subject2 = createSubject("Physics", "PHYS101");

		subjectController.saveSubject(subject1);
		subjectController.saveSubject(subject2);

		assertNotNull(subjectController.fetchAllSubjects());
	}

	@Test
	void fetchSubjectsByUser() {

		// Create a subject
		SubjectDTO subject = createSubject("Math", "MATH101");
		subjectController.saveSubject(subject);

		// Create a teacher and log in
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		// Fetch timetableId for teacher
		long teacherId = UserPreferences.getUserId();

		// Log out teacher
		userController.logout();

		// Create a student
		UserDTO student = createStudent();
		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());

		// Fetch timetableId for student
		long studentId = UserPreferences.getUserId();

		GroupDTO group = createGroup(teacherId, subject.name());

		groupController.addGroup(group);

		groupController.addStudentToGroup(group, studentId);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		TeachingSessionDTO teachingSessionDTO = new TeachingSessionDTO(null,
		                                                               LocalDateTime.parse("2025-01-01T10:00:00"),
		                                                               LocalDateTime.parse("2025-01-01T12:00:00"),
		                                                               null,
		                                                               subject.code(),
		                                                               "test description",
		                                                               groupTimetableId);

		eventController.addEvent(teachingSessionDTO);

		assertNotNull(subjectController.fetchSubjectsByUser());
	}

	@Test
	void saveSubject() {
		SubjectDTO subject = createSubject("Math", "MATH101");
		subjectController.saveSubject(subject);
		assertNotNull(subjectController.fetchAllSubjects());
	}

	@Test
	void deleteSubject() {
		SubjectDTO subject = createSubject("Math", "MATH101");
		subjectController.saveSubject(subject);

		SubjectDTO foundSubject = subjectController.fetchSubjectByCode(subject);
		assertNotNull(foundSubject);

		subjectController.deleteSubject(foundSubject);
	}
}