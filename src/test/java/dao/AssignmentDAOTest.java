package dao;

import datasource.MariaDBConnection;
import entity.AssignmentEntity;
import entity.SubjectEntity;
import entity.TimetableEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssignmentDAOTest {

	@BeforeAll
	static void setUpDatabase() throws SQLException {
		MariaDBConnection.resetDatabaseForTests();
	}

	@AfterAll
	static void tearDown() {
		AssignmentDAO assignmentDAO = new AssignmentDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		assignmentDAO.deleteAll();
		timeTableDAO.deleteAll();
		subjectDAO.deleteAll();
	}

	@BeforeEach
	void setUp() {
		AssignmentDAO assignmentDAO = new AssignmentDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		assignmentDAO.deleteAll();
		timeTableDAO.deleteAll();
		subjectDAO.deleteAll();
	}

	@Test
	void persist() {
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();
		AssignmentDAO assignmentDAO = new AssignmentDAO();

		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");
		AssignmentEntity assignment = new AssignmentEntity("Individual", publishingDate, deadline, math, timetable);
		assignmentDAO.persist(assignment);

		AssignmentEntity foundAssignment = assignmentDAO.findById(assignment.getId());
		assertEquals("Individual", foundAssignment.getType());
		assertEquals(publishingDate, foundAssignment.getPublishingDate());
		assertEquals(deadline, foundAssignment.getDeadline());
	}

	@Test
	void findById() {
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();
		AssignmentDAO assignmentDAO = new AssignmentDAO();

		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");
		AssignmentEntity assignment = new AssignmentEntity("Individual", publishingDate, deadline, math, timetable);
		assignmentDAO.persist(assignment);

		assertEquals(assignment, assignmentDAO.findById(assignment.getId()));
	}

	@Test
	void findAll() {
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();
		AssignmentDAO assignmentDAO = new AssignmentDAO();

		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);
		SubjectEntity physics = new SubjectEntity("Physics", "Physics-101");
		subjectDAO.persist(physics);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");
		AssignmentEntity assignment1 = new AssignmentEntity("Individual", publishingDate, deadline, math, timetable);
		assignmentDAO.persist(assignment1);
		AssignmentEntity assignment2 = new AssignmentEntity("Group", publishingDate, deadline, physics, timetable);
		assignmentDAO.persist(assignment2);

		assertEquals(2, assignmentDAO.findAll().size());
	}

	@Test
	void deleteAll() {
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();
		AssignmentDAO assignmentDAO = new AssignmentDAO();

		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);
		SubjectEntity physics = new SubjectEntity("Physics", "Physics-101");
		subjectDAO.persist(physics);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");
		AssignmentEntity assignment1 = new AssignmentEntity("Individual", publishingDate, deadline, math, timetable);
		assignmentDAO.persist(assignment1);
		AssignmentEntity assignment2 = new AssignmentEntity("Group", publishingDate, deadline, physics, timetable);
		assignmentDAO.persist(assignment2);

		assertEquals(2, assignmentDAO.findAll().size());

		assignmentDAO.deleteAll();

		assertEquals(0, assignmentDAO.findAll().size());
	}

	@Test
	void delete() {
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();
		AssignmentDAO assignmentDAO = new AssignmentDAO();

		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);
		SubjectEntity physics = new SubjectEntity("Physics", "Physics-101");
		subjectDAO.persist(physics);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");
		AssignmentEntity assignment1 = new AssignmentEntity("Individual", publishingDate, deadline, math, timetable);
		assignmentDAO.persist(assignment1);
		AssignmentEntity assignment2 = new AssignmentEntity("Group", publishingDate, deadline, physics, timetable);
		assignmentDAO.persist(assignment2);

		assertEquals(2, assignmentDAO.findAll().size());

		assignmentDAO.delete(assignment1);

		assertEquals(1, assignmentDAO.findAll().size());
	}
}