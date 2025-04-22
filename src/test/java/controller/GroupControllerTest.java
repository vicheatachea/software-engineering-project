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

import static org.junit.jupiter.api.Assertions.*;

class GroupControllerTest {
	private static final BaseController baseController = new BaseController();
	private static final GroupController groupController = baseController.getGroupController();
	private static final UserController userController = baseController.getUserController();
	private static final TimetableController timetableController = baseController.getTimetableController();
	private static final SubjectController subjectController = baseController.getSubjectController();

	private static UserDTO createTeacher(String socialNumber) {
		return new UserDTO("teacher", "password", "John", "Doe", LocalDateTime.parse("2000-01-01T12:00:00"),
		                   socialNumber, "TEACHER");
	}

	private static void resetDatabase() {
		subjectController.deleteAllSubjects();
		userController.deleteAllUsers();
		timetableController.deleteAllTimetables();
	}

	@BeforeAll
	static void ensureDatabase() {
		try {
			new MariaDBConnection().verifyDatabase();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void teardown() {
		resetDatabase();
	}

	@BeforeEach
	void setup() {
		resetDatabase();
	}

	private UserDTO createStudent() {
		return new UserDTO("student", "password", "John", "Doe", LocalDateTime.parse("2000-01-01T12:00:00"),
		                   "BA123456789", "STUDENT");
	}

	SubjectDTO createSubject(String code) {
		return new SubjectDTO("ICT", code);
	}

	@Test
	void fetchAllGroups() {
		UserDTO teacher = createTeacher("123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT101");
		SubjectDTO subject2 = createSubject("ICT102");

		try {
			subjectController.addSubject(subject1);
			subjectController.addSubject(subject2);
		} catch (IllegalArgumentException e) {
			System.out.println("Error adding subject: " + e.getMessage());
		}

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		try {
			groupController.addGroup(group);
		} catch (Exception e) {
			System.out.println("Error adding group: " + e.getMessage());
		}

		GroupDTO group2 = new GroupDTO("Group2", "TST2", 10, teacherId, subject2.code());

		try {
			groupController.addGroup(group2);
		} catch (Exception e) {
			System.out.println("Error adding group: " + e.getMessage());
		}

		assertEquals(2, groupController.fetchAllGroups().size());
		assertEquals(group, groupController.fetchGroupByName("Group1"));
	}

	@Test
	void fetchGroupsByUser() {
		UserDTO teacher = createTeacher("123456789AB");
		UserDTO student = createStudent();

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		userController.logout();

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT101");
		subjectController.addSubject(subject1);

		GroupDTO group1 = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());
		groupController.addGroup(group1);

		GroupDTO group2 = new GroupDTO("Group2", "TST2", 10, teacherId, subject1.code());
		groupController.addGroup(group2);

		groupController.addStudentToGroup(group1, student.username());
		groupController.addStudentToGroup(group2, student.username());

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		assertEquals(2, groupController.fetchGroupsByUser().size());
		assertEquals(group1, groupController.fetchGroupsByUser().getFirst());
		assertEquals(group2, groupController.fetchGroupsByUser().getLast());
	}

	@Test
	void fetchGroupByName() {
		UserDTO teacher = createTeacher("BA987654321");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT101");
		subjectController.addSubject(subject1);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		assertEquals(group, groupController.fetchGroupByName("Group1"));
	}

	@Test
	void fetchByTimetableId() {
		UserDTO teacher = createTeacher("BA987654321");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT101");
		subjectController.addSubject(subject1);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		long timetableId = timetableController.fetchTimetableForGroup(group.name());

		assertEquals(group, groupController.fetchGroupByTimetableId(timetableId));
	}

	@Test
	void isUserGroupOwner() {
		UserDTO teacher = createTeacher("123456789AB");
		UserDTO student = createStudent();

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		userController.logout();

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT101");
		subjectController.addSubject(subject1);

		GroupDTO group1 = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());
		groupController.addGroup(group1);

		GroupDTO group2 = new GroupDTO("Group2", "TST2", 10, teacherId, subject1.code());
		groupController.addGroup(group2);

		groupController.addStudentToGroup(group1, student.username());
		groupController.addStudentToGroup(group2, student.username());

		assertTrue(groupController.isUserGroupOwner("Group1"));
		assertTrue(groupController.isUserGroupOwner("Group2"));
	}

	@Test
	void addGroup() {
		UserDTO teacher = createTeacher("123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT101");
		subjectController.addSubject(subject1);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		assertEquals(group, groupController.fetchGroupByName("Group1"));
		assertEquals(1, groupController.fetchAllGroups().size());
	}

	@Test
	void updateGroup() {
		UserDTO teacher = createTeacher("123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT101");
		subjectController.addSubject(subject1);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		GroupDTO updateGroup = new GroupDTO("Group1", "TST2", 20, teacherId, subject1.code());

		groupController.updateGroup(updateGroup, group.name());

		assertEquals(updateGroup, groupController.fetchGroupByName(updateGroup.name()));
		assertEquals(1, groupController.fetchAllGroups().size());
	}

	@Test
	void deleteGroup() {
		UserDTO teacher = createTeacher("123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT101");
		subjectController.addSubject(subject1);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		groupController.deleteGroup(group);

		assertEquals(0, groupController.fetchAllGroups().size());
		assertThrows(IllegalArgumentException.class, () -> groupController.fetchGroupByName("Group1"));
	}

	@Test
	void addStudentToGroup() {
		UserDTO teacher = createTeacher("123456789AB");
		UserDTO student = createStudent();

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		userController.logout();

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT101");
		subjectController.addSubject(subject1);

		GroupDTO group1 = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());
		groupController.addGroup(group1);

		groupController.addStudentToGroup(group1, student.username());

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		assertEquals(1, groupController.fetchGroupsByUser().size());
		assertEquals(group1, groupController.fetchGroupsByUser().getFirst());
	}

	@Test
	void removeStudentFromGroup() {
		UserDTO student = createStudent();
		UserDTO teacher = createTeacher("123456789AB");

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		userController.logout();

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT101");
		subjectController.addSubject(subject1);

		GroupDTO group1 = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());
		groupController.addGroup(group1);

		groupController.addStudentToGroup(group1, student.username());
		userController.logout();

		userController.authenticateUser(student.username(), student.password());

		assertEquals(1, groupController.fetchGroupsByUser().size());
		assertEquals(group1, groupController.fetchGroupsByUser().getFirst());

		userController.logout();
		userController.authenticateUser(teacher.username(), teacher.password());

		groupController.removeStudentFromGroup(group1, student.username());

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		assertEquals(0, groupController.fetchGroupsByUser().size());
	}
}