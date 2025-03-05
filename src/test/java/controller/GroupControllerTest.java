package controller;

import datasource.MariaDBConnection;
import dto.GroupDTO;
import dto.SubjectDTO;
import dto.UserDTO;
import model.UserPreferences;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GroupControllerTest {

	private static final GroupController groupController = new GroupController();
	private static final UserController userController = new UserController();
	private static final TimetableController timetableController = new TimetableController();
	private static final SubjectController subjectController = new SubjectController();

	UserDTO createStudent(String username, String socialNumber) {
		return new UserDTO(username, "password", "John", "Doe", LocalDateTime.parse("2000-01-01T12:00:00"),
		                   socialNumber, "STUDENT");
	}

	UserDTO createTeacher(String username, String socialNumber) {
		return new UserDTO(username, "password", "John", "Doe", LocalDateTime.parse("2000-01-01T12:00:00"),
		                   socialNumber, "TEACHER");
	}

	SubjectDTO createSubject(String name, String code) {
		return new SubjectDTO(name, code);
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
		timetableController.deleteAllTimetables();
		subjectController.deleteAllSubjects();
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void tearDown() {
		timetableController.deleteAllTimetables();
		subjectController.deleteAllSubjects();
	}

	@Test
	void fetchAllGroups() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		Long teacherId = UserPreferences.getUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		SubjectDTO subject2 = createSubject("ICT", "ICT102");

		subjectController.saveSubject(subject1);
		subjectController.saveSubject(subject2);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		GroupDTO group2 = new GroupDTO("Group2", "TST2", 10, teacherId, subject2.code());

		groupController.addGroup(group2);

		assertEquals(2, groupController.fetchAllGroups().size());
		assertEquals(group, groupController.fetchGroupByName("Group1"));
	}

	@Test
	void fetchGroupsByUser() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");
		UserDTO student = createStudent("student", "BA123456789");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		Long teacherId = UserPreferences.getUserId();
		userController.logout();

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		Long studentId = UserPreferences.getUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.saveSubject(subject1);

		GroupDTO group1 = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());
		groupController.addGroup(group1);

		GroupDTO group2 = new GroupDTO("Group2", "TST2", 10, teacherId, subject1.code());
		groupController.addGroup(group2);

		groupController.addStudentToGroup(group1, studentId);
		groupController.addStudentToGroup(group2, studentId);

		assertEquals(2, groupController.fetchGroupsByUser().size());
		assertEquals(group1, groupController.fetchGroupsByUser().getFirst());
		assertEquals(group2, groupController.fetchGroupsByUser().getLast());
	}

	@Test
	void fetchGroupByName() {
		UserDTO teacher = createTeacher("teacher", "BA987654321");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		Long teacherId = UserPreferences.getUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.saveSubject(subject1);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		assertEquals(group, groupController.fetchGroupByName("Group1"));
	}

	@Test
	void isUserGroupOwner() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");
		UserDTO student = createStudent("student", "BA123456789");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		Long teacherId = UserPreferences.getUserId();
		userController.logout();

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		Long studentId = UserPreferences.getUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.saveSubject(subject1);

		GroupDTO group1 = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());
		groupController.addGroup(group1);

		GroupDTO group2 = new GroupDTO("Group2", "TST2", 10, teacherId, subject1.code());
		groupController.addGroup(group2);

		groupController.addStudentToGroup(group1, studentId);
		groupController.addStudentToGroup(group2, studentId);

		assertFalse(groupController.isUserGroupOwner("Group1"));
		assertFalse(groupController.isUserGroupOwner("Group2"));
		userController.logout();

		userController.authenticateUser(teacher.username(), teacher.password());

		assertTrue(groupController.isUserGroupOwner("Group1"));
		assertTrue(groupController.isUserGroupOwner("Group2"));
	}

	@Test
	void addGroup() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		Long teacherId = UserPreferences.getUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.saveSubject(subject1);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		assertEquals(group, groupController.fetchGroupByName("Group1"));
		assertEquals(1, groupController.fetchAllGroups().size());
	}

	@Test
	void updateGroup() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		Long teacherId = UserPreferences.getUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.saveSubject(subject1);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		GroupDTO updateGroup = new GroupDTO("Group1", "TST2", 20, teacherId, subject1.code());

		groupController.updateGroup(updateGroup);

		assertEquals(updateGroup, groupController.fetchGroupByName(updateGroup.name()));
		assertEquals(1, groupController.fetchAllGroups().size());
	}

	@Test
	void deleteGroup() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		Long teacherId = UserPreferences.getUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.saveSubject(subject1);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		groupController.deleteGroup(group);

		assertEquals(0, groupController.fetchAllGroups().size());
		assertThrows(IllegalArgumentException.class, () -> groupController.fetchGroupByName(group.name()));
	}

	@Test
	void addStudentToGroup() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");
		UserDTO student = createStudent("student", "BA123456789");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = UserPreferences.getUserId();
		userController.logout();

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		long studentId = UserPreferences.getUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.saveSubject(subject1);

		GroupDTO group1 = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());
		groupController.addGroup(group1);

		groupController.addStudentToGroup(group1, studentId);

		assertEquals(1, groupController.fetchGroupsByUser().size());
		assertEquals(group1, groupController.fetchGroupsByUser().getFirst());
	}

	@Test
	void removeStudentFromGroup() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");
		UserDTO student = createStudent("student", "BA123456789");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = UserPreferences.getUserId();
		userController.logout();

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		long studentId = UserPreferences.getUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.saveSubject(subject1);

		GroupDTO group1 = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());
		groupController.addGroup(group1);

		groupController.addStudentToGroup(group1, studentId);

		assertEquals(1, groupController.fetchGroupsByUser().size());
		assertEquals(group1, groupController.fetchGroupsByUser().getFirst());

		groupController.removeStudentFromGroup(group1, studentId);

		assertEquals(0, groupController.fetchGroupsByUser().size());
	}
}