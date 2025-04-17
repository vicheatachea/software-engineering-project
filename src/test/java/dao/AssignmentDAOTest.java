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

	private static final AssignmentDAO assignmentDAO = new AssignmentDAO();
	private static final TimetableDAO timeTableDAO = new TimetableDAO();
	private static final SubjectDAO subjectDAO = new SubjectDAO();

	private static void resetDatabase() {
		assignmentDAO.deleteAll();
		timeTableDAO.deleteAll();
		subjectDAO.deleteAll();
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
	static void tearDown() {
		resetDatabase();
	}

	@BeforeEach
	void setUp() {
		resetDatabase();
	}

	@Test
	void persist() {
		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");

		String description = "Solve all exercises in chapter 1";

		AssignmentEntity assignment =
				new AssignmentEntity("Assignment 1", "Individual", publishingDate, deadline, description, math,
				                     timetable, "en");
		assignmentDAO.persist(assignment);

		AssignmentEntity foundAssignment = assignmentDAO.findById(assignment.getId());
		assertEquals("Individual", foundAssignment.getType());
		assertEquals(publishingDate, foundAssignment.getPublishingDate());
		assertEquals(deadline, foundAssignment.getDeadline());
	}

	@Test
	void Update() {
		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");

		String description = "Solve all exercises in chapter 1";

		AssignmentEntity assignment =
				new AssignmentEntity("Assignment 1", "Individual", publishingDate, deadline, description, math,
				                     timetable, "en");
		assignmentDAO.persist(assignment);

		Timestamp newDeadline = Timestamp.valueOf("2025-03-01 00:00:00");
		assignment.setDeadline(newDeadline);
		assignment.setType("Group");
		assignmentDAO.update(assignment);

		AssignmentEntity updatedAssignment = assignmentDAO.findById(assignment.getId());
		assertEquals("Group", updatedAssignment.getType());
		assertEquals(newDeadline, updatedAssignment.getDeadline());
		assertEquals(1, assignmentDAO.findAll().size());
	}

	@Test
	void findById() {
		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");

		String description = "Solve all exercises in chapter 1";

		AssignmentEntity assignment =
				new AssignmentEntity("Assignment 1", "Individual", publishingDate, deadline, description, math,
				                     timetable, "en");
		assignmentDAO.persist(assignment);

		assertEquals(assignment, assignmentDAO.findById(assignment.getId()));
	}

	@Test
	void findAll() {
		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);
		SubjectEntity physics = new SubjectEntity("Physics", "Physics-101");
		subjectDAO.persist(physics);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");

		String description1 = "Solve all exercises in chapter 1";
		String description2 = "Solve all exercises in chapter 2";

		AssignmentEntity assignment1 =
				new AssignmentEntity("Assignment 1", "Individual", publishingDate, deadline, description1, math,
				                     timetable, "en");
		assignmentDAO.persist(assignment1);
		AssignmentEntity assignment2 =
				new AssignmentEntity("Assignment 2", "Group", publishingDate, deadline, description2, physics,
				                     timetable, "en");
		assignmentDAO.persist(assignment2);

		assertEquals(2, assignmentDAO.findAll().size());
	}

	@Test
	void deleteAll() {
		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);
		SubjectEntity physics = new SubjectEntity("Physics", "Physics-101");
		subjectDAO.persist(physics);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");

		String description1 = "Solve all exercises in chapter 1";
		String description2 = "Solve all exercises in chapter 2";

		AssignmentEntity assignment1 =
				new AssignmentEntity("Assignment 1", "Individual", publishingDate, deadline, description1, math,
				                     timetable, "en");
		assignmentDAO.persist(assignment1);
		AssignmentEntity assignment2 =
				new AssignmentEntity("Assignment 2", "Group", publishingDate, deadline, description2, physics,
				                     timetable, "en");
		assignmentDAO.persist(assignment2);

		assertEquals(2, assignmentDAO.findAll().size());

		assignmentDAO.deleteAll();

		assertEquals(0, assignmentDAO.findAll().size());
	}

	@Test
	void delete() {
		SubjectEntity math = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(math);
		SubjectEntity physics = new SubjectEntity("Physics", "Physics-101");
		subjectDAO.persist(physics);

		TimetableEntity timetable = new TimetableEntity();
		timeTableDAO.persist(timetable);

		Timestamp publishingDate = Timestamp.valueOf("2025-02-11 00:00:00");
		Timestamp deadline = Timestamp.valueOf("2025-02-25 00:00:00");

		String description1 = "Solve all exercises in chapter 1";
		String description2 = "Solve all exercises in chapter 2";

		AssignmentEntity assignment1 =
				new AssignmentEntity("Assignment 1", "Individual", publishingDate, deadline, description1, math,
				                     timetable, "en");
		assignmentDAO.persist(assignment1);
		AssignmentEntity assignment2 =
				new AssignmentEntity("Assignment 2", "Group", publishingDate, deadline, description2, physics,
				                     timetable, "en");
		assignmentDAO.persist(assignment2);

		assertEquals(2, assignmentDAO.findAll().size());

		assignmentDAO.delete(assignment1);

		assertEquals(1, assignmentDAO.findAll().size());
	}
}