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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupControllerTest {

	private static final GroupController groupController = new GroupController();
	private static final UserController userController = new UserController();
	private static final TimetableController timetableController = new TimetableController();
	private static final SubjectController subjectController = new SubjectController();

	private UserDTO createStudent(String username, String socialNumber) {
		return new UserDTO(username, "password", "John", "Doe", LocalDateTime.parse("2000-01-01T12:00:00"),
		                   socialNumber, "STUDENT");
	}

	private static UserDTO createTeacher(String username, String socialNumber) {
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
		subjectController.deleteAllSubjects();
		userController.deleteAllUsers();
		timetableController.deleteAllTimetables();
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
		timetableController.deleteAllTimetables();
	}

	@Test
	void fetchAllGroups() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		SubjectDTO subject2 = createSubject("ICT", "ICT102");

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
		UserDTO teacher = createTeacher("teacher", "123456789AB");
		UserDTO student = createStudent("student", "BA123456789");

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		userController.logout();

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
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
		UserDTO teacher = createTeacher("teacher", "BA987654321");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.addSubject(subject1);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject1.code());

		groupController.addGroup(group);

		assertEquals(group, groupController.fetchGroupByName("Group1"));
	}

	@Test
	void fetchAllStudentsByGroupName() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject = createSubject("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, "ICT101");
		groupController.addGroup(group);

		List<UserDTO> students = List.of(
				createStudent("student1", "BA123456789"),
				createStudent("student2", "BA987654321"),
				createStudent("student3", "BA654321987"),
				createStudent("student4", "BA321987654"),
				createStudent("student5", "BA789654321")
		);

		for (UserDTO student : students) {
			userController.registerUser(student);
			userController.authenticateUser(student.username(), student.password());
			userController.logout();
			userController.authenticateUser(teacher.username(), teacher.password());
			groupController.addStudentToGroup(group, student.username());
			userController.logout();
		}

		userController.fetchStudentsInGroup(group.name()).forEach(userDTO -> {
			assertTrue(students.stream().anyMatch(student -> student.username().equals(userDTO.username())));
		});
	}

	@Test
	void isUserGroupOwner() {
		UserDTO teacher = createTeacher("teacher", "123456789AB");
		UserDTO student = createStudent("student", "BA123456789");

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		userController.logout();

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
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
		UserDTO teacher = createTeacher("teacher", "123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.addSubject(subject1);

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
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
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
		UserDTO teacher = createTeacher("teacher", "123456789AB");

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
		subjectController.addSubject(subject1);

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

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		userController.logout();

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
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
		UserDTO student = createStudent("student", "BA123456789");
		UserDTO teacher = createTeacher("teacher", "123456789AB");

		userController.registerUser(student);
		userController.authenticateUser(student.username(), student.password());
		userController.logout();

		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());
		long teacherId = userController.fetchCurrentUserId();

		SubjectDTO subject1 = createSubject("ICT", "ICT101");
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