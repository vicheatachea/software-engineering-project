package controller;

import datasource.MariaDBConnection;
import dto.*;
import model.UserPreferences;
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
		eventController.deleteAllEvents();
		locationController.deleteAllLocations();
		subjectController.deleteAllSubjects();
		timetableController.deleteAllTimetables();
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void tearDown() {
		eventController.deleteAllEvents();
		locationController.deleteAllLocations();
		subjectController.deleteAllSubjects();
		timetableController.deleteAllTimetables();
	}

	UserDTO createStudent(String username, String socialNumber) {
		return new UserDTO(username, "password", "John", "Doe", LocalDateTime.now().minusYears(16), socialNumber,
		                   "STUDENT");
	}

	UserDTO createTeacher(String username, String socialNumber) {
		return new UserDTO(username, "password", "John", "Doe", LocalDateTime.now().minusYears(32), socialNumber,
		                   "TEACHER");
	}

	@Test
	void fetchEventsByUser() {
		UserDTO teacher = createTeacher("teacher", "987654321BA");
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = UserPreferences.getUserId();
		userController.logout();

		UserDTO student = createStudent("student", "123456789AB");
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		long studentId = UserPreferences.getUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, studentId);

		TeachingSessionDTO teachingSession = new TeachingSessionDTO(null, LocalDateTime.parse("2023-10-01T10:00:00"),
		                                                            LocalDateTime.parse("2023-10-01T12:00:00"),
		                                                            location.name(), subject.code(),
		                                                            "This is a teaching Session", groupTimetableId);

		eventController.addEvent(teachingSession);

		AssignmentDTO assignment = new AssignmentDTO(null, "Individual", LocalDateTime.parse("2023-10-01T10:00:00"),
		                                             LocalDateTime.parse("2023-10-01T12:00:00"), "Assignment 1",
		                                             subject.code(), "This is an assignment", groupTimetableId);

		eventController.addEvent(assignment);

		assertEquals(2, eventController.fetchEventsByUser(LocalDateTime.parse("2023-09-01T10:00:00"),
		                                                  LocalDateTime.parse("2023-11-01T12:00:00")).size());
	}

	@Test
	void addEventTeachingSession() {
		UserDTO teacher = createTeacher("teacher", "987654321BA");
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = UserPreferences.getUserId();
		userController.logout();

		UserDTO student = createStudent("student", "123456789AB");
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		long studentId = UserPreferences.getUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, studentId);

		TeachingSessionDTO teachingSession = new TeachingSessionDTO(null, LocalDateTime.parse("2023-10-01T10:00:00"),
		                                                            LocalDateTime.parse("2023-10-01T12:00:00"),
		                                                            location.name(), subject.code(),
		                                                            "This is a teaching Session", groupTimetableId);

		eventController.addEvent(teachingSession);

		assertEquals(1, eventController.fetchEventsByUser(teachingSession.startDate(), teachingSession.endDate()).size());
	}

	@Test
	void addEventAssignment() {
		UserDTO teacher = createTeacher("teacher", "987654321BA");
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = UserPreferences.getUserId();
		userController.logout();

		UserDTO student = createStudent("student", "123456789AB");
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		long studentId = UserPreferences.getUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, studentId);

		AssignmentDTO assignment = new AssignmentDTO(null, "Individual", LocalDateTime.parse("2023-10-01T10:00:00"),
		                                             LocalDateTime.parse("2023-10-01T12:00:00"), "Assignment 1",
		                                             subject.code(), "This is an assignment", groupTimetableId);

		eventController.addEvent(assignment);

		assertEquals(1, eventController.fetchEventsByUser(assignment.publishingDate(), assignment.deadline()).size());
	}

	@Test
	void updateEventTeachingSession() {
		UserDTO teacher = createTeacher("teacher", "987654321BA");
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = UserPreferences.getUserId();
		userController.logout();

		UserDTO student = createStudent("student", "123456789AB");
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		long studentId = UserPreferences.getUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, studentId);

		TeachingSessionDTO teachingSession = new TeachingSessionDTO(null, LocalDateTime.parse("2023-10-01T10:00:00"),
		                                                            LocalDateTime.parse("2023-10-01T12:00:00"),
		                                                            location.name(), subject.code(),
		                                                            "This is a teaching Session", groupTimetableId);

		eventController.addEvent(teachingSession);

		SubjectDTO newSubject = new SubjectDTO("MATH", "MATH101");
		subjectController.addSubject(newSubject);

		TeachingSessionDTO foundTeachingSession = (TeachingSessionDTO) eventController
				.fetchEventsByUser(teachingSession.startDate(), teachingSession.endDate()).getFirst();

		TeachingSessionDTO updatedTeachingSession =
				new TeachingSessionDTO(foundTeachingSession.id(), LocalDateTime.parse("2023-12-01T10:00:00"),
				                       LocalDateTime.parse("2023-12-03T13:54:34"), null, newSubject.code(),
				                       "This is an updated teaching Session", groupTimetableId);

		eventController.updateEvent(updatedTeachingSession);

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
		UserDTO teacher = createTeacher("teacher", "987654321BA");
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = UserPreferences.getUserId();
		userController.logout();

		UserDTO student = createStudent("student", "123456789AB");
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		long studentId = UserPreferences.getUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, studentId);

		AssignmentDTO assignment = new AssignmentDTO(null, "Individual", LocalDateTime.parse("2023-10-01T10:00:00"),
		                                             LocalDateTime.parse("2023-10-01T12:00:00"), "Assignment 1",
		                                             subject.code(), "This is an assignment", groupTimetableId);

		eventController.addEvent(assignment);

		AssignmentDTO foundAssignment =
				(AssignmentDTO) eventController.fetchEventsByUser(assignment.publishingDate(), assignment.deadline())
				                               .getFirst();

		SubjectDTO newSubject = new SubjectDTO("MATH", "MATH101");
		subjectController.addSubject(newSubject);

		AssignmentDTO updatedAssignment =
				new AssignmentDTO(foundAssignment.id(), "Group", LocalDateTime.parse("2023-12-01T10:00:00"),
				                  LocalDateTime.parse("2023-12-03T13:54:34"), "Assignment 2", newSubject.code(),
				                  "This is an updated assignment", groupTimetableId);

			eventController.updateEvent(updatedAssignment);

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
		UserDTO teacher = createTeacher("teacher", "987654321BA");
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = UserPreferences.getUserId();
		userController.logout();

		UserDTO student = createStudent("student", "123456789AB");
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		long studentId = UserPreferences.getUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, studentId);

		TeachingSessionDTO teachingSession = new TeachingSessionDTO(null, LocalDateTime.parse("2023-10-01T10:00:00"),
		                                                            LocalDateTime.parse("2023-10-01T12:00:00"),
		                                                            location.name(), subject.code(),
		                                                            "This is a teaching Session", groupTimetableId);

		eventController.addEvent(teachingSession);

		teachingSession = (TeachingSessionDTO) eventController
				.fetchEventsByUser(teachingSession.startDate(), teachingSession.endDate()).getFirst();

		eventController.deleteEvent(teachingSession);

		assertEquals(0,
		             eventController.fetchEventsByUser(teachingSession.startDate(), teachingSession.endDate()).size());
	}

	@Test
	void deleteEventAssignment() {
		UserDTO teacher = createTeacher("teacher", "987654321BA");
		userController.registerUser(teacher);
		userController.authenticateUser("teacher", "password");
		long teacherId = UserPreferences.getUserId();
		userController.logout();

		UserDTO student = createStudent("student", "123456789AB");
		userController.registerUser(student);
		userController.authenticateUser("student", "password");
		long studentId = UserPreferences.getUserId();

		LocationDTO location = new LocationDTO("B2005", "Metropolia Myllypuro", "Building B");
		locationController.addLocation(location);

		SubjectDTO subject = new SubjectDTO("ICT", "ICT101");
		subjectController.addSubject(subject);

		GroupDTO group = new GroupDTO("Group1", "TST1", 10, teacherId, subject.code());
		groupController.addGroup(group);

		long groupTimetableId = timetableController.fetchTimetableForGroup(group.name());

		groupController.addStudentToGroup(group, studentId);

		AssignmentDTO assignment = new AssignmentDTO(null, "Individual", LocalDateTime.parse("2023-10-01T10:00:00"),
		                                             LocalDateTime.parse("2023-10-01T12:00:00"), "Assignment 1",
		                                             subject.code(), "This is an assignment", groupTimetableId);

		eventController.addEvent(assignment);

		assignment =
				(AssignmentDTO) eventController.fetchEventsByUser(assignment.publishingDate(), assignment.deadline())
				                               .getFirst();

		eventController.deleteEvent(assignment);

		assertEquals(0, eventController.fetchEventsByUser(assignment.publishingDate(), assignment.deadline()).size());
	}
}