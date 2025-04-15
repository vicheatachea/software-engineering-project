package controller;

import datasource.MariaDBConnection;
import dto.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventControllerTest {

	private static final EventController eventController = new EventController();
	private static final LocationController locationController = new LocationController();
	private static final UserController userController = new UserController();
	private static final SubjectController subjectController = new SubjectController();
	private static final GroupController groupController = new GroupController();
	private static final TimetableController timetableController = new TimetableController();

	private static void resetDatabase() {
		eventController.deleteAllEvents();
		locationController.deleteAllLocations();
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
	static void teardown() {
		resetDatabase();
	}

	private static UserDTO createTeacher() {
		return new UserDTO("teacher", "password", "John", "Doe", LocalDateTime.now().minusYears(32), "987654321BA",
		                   "TEACHER");
	}

	@BeforeEach
	void setup() {
		resetDatabase();
	}

	UserDTO createStudent() {
		return new UserDTO("student", "password", "John", "Doe", LocalDateTime.now().minusYears(16), "123456789AB",
		                   "STUDENT");
	}

	@Test
	void fetchEventsByUser() {
		UserDTO student = createStudent();
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		userController.logout();

		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = userController.fetchCurrentUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, student.username());

		TeachingSessionDTO teachingSession = new TeachingSessionDTO(null, LocalDateTime.parse("2023-10-01T10:00:00"),
		                                                            LocalDateTime.parse("2023-10-01T12:00:00"),
		                                                            location.name(), subject.code(),
		                                                            "This is a teaching Session", groupTimetableId,
		                                                            "en");

		eventController.addEvent(teachingSession);

		AssignmentDTO assignment = new AssignmentDTO(null, "Individual", LocalDateTime.parse("2023-10-01T10:00:00"),
		                                             LocalDateTime.parse("2023-10-01T12:00:00"), "Assignment 1",
		                                             subject.code(), "This is an assignment", groupTimetableId, "en");

		eventController.addEvent(assignment);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		assertEquals(2, eventController.fetchEventsByUser(LocalDateTime.parse("2023-09-01T10:00:00"),
		                                                  LocalDateTime.parse("2023-11-01T12:00:00")).size());
	}

	@Test
	void addEventTeachingSession() {
		UserDTO student = createStudent();
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		userController.logout();

		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = userController.fetchCurrentUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, student.username());

		TeachingSessionDTO teachingSession = new TeachingSessionDTO(null, LocalDateTime.parse("2023-10-01T10:00:00"),
		                                                            LocalDateTime.parse("2023-10-01T12:00:00"),
		                                                            location.name(), subject.code(),
		                                                            "This is a teaching Session", groupTimetableId,
		                                                            "en");

		eventController.addEvent(teachingSession);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		assertEquals(1,
		             eventController.fetchEventsByUser(teachingSession.startDate(), teachingSession.endDate()).size());
	}

	@Test
	void addEventAssignment() {
		UserDTO student = createStudent();
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		userController.logout();

		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = userController.fetchCurrentUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, student.username());

		AssignmentDTO assignment = new AssignmentDTO(null, "Individual", LocalDateTime.parse("2023-10-01T10:00:00"),
		                                             LocalDateTime.parse("2023-10-01T12:00:00"), "Assignment 1",
		                                             subject.code(), "This is an assignment", groupTimetableId, "en");

		eventController.addEvent(assignment);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		assertEquals(1, eventController.fetchEventsByUser(assignment.publishingDate(), assignment.deadline()).size());
	}

	@Test
	void updateEventTeachingSession() {
		UserDTO student = createStudent();
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		userController.logout();

		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = userController.fetchCurrentUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, student.username());

		TeachingSessionDTO teachingSession = new TeachingSessionDTO(null, LocalDateTime.parse("2023-10-01T10:00:00"),
		                                                            LocalDateTime.parse("2023-10-01T12:00:00"),
		                                                            location.name(), subject.code(),
		                                                            "This is a teaching Session", groupTimetableId,
		                                                            "en");

		eventController.addEvent(teachingSession);

		SubjectDTO newSubject = new SubjectDTO("MATH", "MATH101");
		subjectController.addSubject(newSubject);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		TeachingSessionDTO foundTeachingSession = (TeachingSessionDTO) eventController
				.fetchEventsByUser(teachingSession.startDate(), teachingSession.endDate()).getFirst();

		TeachingSessionDTO updatedTeachingSession =
				new TeachingSessionDTO(foundTeachingSession.id(), LocalDateTime.parse("2023-12-01T10:00:00"),
				                       LocalDateTime.parse("2023-12-03T13:54:34"), null, newSubject.code(),
				                       "This is an updated teaching Session", groupTimetableId, "en");

		userController.logout();
		userController.authenticateUser(teacher.username(), teacher.password());

		eventController.updateEvent(updatedTeachingSession);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		TeachingSessionDTO fetchedTeachingSession = (TeachingSessionDTO) eventController
				.fetchEventsByUser(updatedTeachingSession.startDate(), updatedTeachingSession.endDate()).getFirst();

		assertEquals(1, eventController
				.fetchEventsByUser(updatedTeachingSession.startDate(), updatedTeachingSession.endDate()).size());
		assertEquals(updatedTeachingSession.subjectCode(), fetchedTeachingSession.subjectCode());
		assertEquals(updatedTeachingSession.description(), fetchedTeachingSession.description());
		assertEquals(updatedTeachingSession.startDate(), fetchedTeachingSession.startDate());
		assertEquals(updatedTeachingSession.endDate(), fetchedTeachingSession.endDate());
	}

	@Test
	void updateEventAssignment() {
		UserDTO student = createStudent();
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		userController.logout();

		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = userController.fetchCurrentUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, student.username());

		AssignmentDTO assignment = new AssignmentDTO(null, "Individual", LocalDateTime.parse("2023-10-01T10:00:00"),
		                                             LocalDateTime.parse("2023-10-01T12:00:00"), "Assignment 1",
		                                             subject.code(), "This is an assignment", groupTimetableId, "en");

		eventController.addEvent(assignment);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		AssignmentDTO foundAssignment =
				(AssignmentDTO) eventController.fetchEventsByUser(assignment.publishingDate(), assignment.deadline())
				                               .getFirst();

		userController.logout();
		userController.authenticateUser(teacher.username(), teacher.password());

		SubjectDTO newSubject = new SubjectDTO("MATH", "MATH101");
		subjectController.addSubject(newSubject);

		AssignmentDTO updatedAssignment =
				new AssignmentDTO(foundAssignment.id(), "Group", LocalDateTime.parse("2023-12-01T10:00:00"),
				                  LocalDateTime.parse("2023-12-03T13:54:34"), "Assignment 2", newSubject.code(),
				                  "This is an updated assignment", groupTimetableId, "en");

		eventController.updateEvent(updatedAssignment);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		AssignmentDTO fetchedAssignment = (AssignmentDTO) eventController
				.fetchEventsByUser(updatedAssignment.publishingDate(), updatedAssignment.deadline()).getFirst();

		assertEquals(updatedAssignment.subjectCode(), fetchedAssignment.subjectCode());
		assertEquals(updatedAssignment.description(), fetchedAssignment.description());
		assertEquals(updatedAssignment.publishingDate(), fetchedAssignment.publishingDate());
		assertEquals(updatedAssignment.deadline(), fetchedAssignment.deadline());
		assertEquals(updatedAssignment.type(), fetchedAssignment.type());
	}

	@Test
	void deleteEventTeachingSession() {
		UserDTO student = createStudent();
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		userController.logout();

		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = userController.fetchCurrentUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, student.username());

		TeachingSessionDTO teachingSession = new TeachingSessionDTO(null, LocalDateTime.parse("2023-10-01T10:00:00"),
		                                                            LocalDateTime.parse("2023-10-01T12:00:00"),
		                                                            location.name(), subject.code(),
		                                                            "This is a teaching Session", groupTimetableId,
		                                                            "en");

		eventController.addEvent(teachingSession);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		teachingSession = (TeachingSessionDTO) eventController
				.fetchEventsByUser(teachingSession.startDate(), teachingSession.endDate()).getFirst();

		userController.logout();
		userController.authenticateUser(teacher.username(), teacher.password());

		eventController.deleteEvent(teachingSession);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		assertEquals(0,
		             eventController.fetchEventsByUser(teachingSession.startDate(), teachingSession.endDate()).size());
	}

	@Test
	void deleteEventAssignment() {
		UserDTO student = createStudent();
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		userController.logout();

		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = userController.fetchCurrentUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, student.username());

		AssignmentDTO assignment = new AssignmentDTO(null, "Individual", LocalDateTime.parse("2023-10-01T10:00:00"),
		                                             LocalDateTime.parse("2023-10-01T12:00:00"), "Assignment 1",
		                                             subject.code(), "This is an assignment", groupTimetableId, "en");

		eventController.addEvent(assignment);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		assignment =
				(AssignmentDTO) eventController.fetchEventsByUser(assignment.publishingDate(), assignment.deadline())
				                               .getFirst();

		userController.logout();
		userController.authenticateUser(teacher.username(), teacher.password());

		eventController.deleteEvent(assignment);

		userController.logout();
		userController.authenticateUser(student.username(), student.password());

		assertEquals(0, eventController.fetchEventsByUser(assignment.publishingDate(), assignment.deadline()).size());
	}
}