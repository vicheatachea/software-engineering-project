package dao;

import datasource.MariaDBConnection;
import entity.LocationEntity;
import entity.SubjectEntity;
import entity.TeachingSessionEntity;
import entity.TimetableEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeachingSessionDAOTest {

	@BeforeAll
	static void ensureDatabase() throws SQLException {
		MariaDBConnection.verifyDatabase();
	}

	@AfterAll
	static void tearDown() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();
		teachingSessionDAO.deleteAll();
		locationDAO.deleteAll();
		subjectDAO.deleteAll();
		timetableDAO.deleteAll();
	}

	@BeforeEach
	void setUp() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();
		teachingSessionDAO.deleteAll();
		locationDAO.deleteAll();
		subjectDAO.deleteAll();
		timetableDAO.deleteAll();
	}

	@Test
	void persist() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";

		TeachingSessionEntity teachingSession = new TeachingSessionEntity(start, end, description, location, timetable,
		                                                                  subject);

		teachingSessionDAO.persist(teachingSession);

		TeachingSessionEntity foundTeachingSession = teachingSessionDAO.findById(teachingSession.getId());

		assertEquals(teachingSession.getStartDate(), foundTeachingSession.getStartDate());
		assertEquals(teachingSession.getEndDate(), foundTeachingSession.getEndDate());
		assertEquals(teachingSession.getLocation().getId(), foundTeachingSession.getLocation().getId());
		assertEquals(teachingSession.getTimetable().getId(), foundTeachingSession.getTimetable().getId());
		assertEquals(teachingSession.getSubject().getId(), foundTeachingSession.getSubject().getId());
	}

	@Test
	void persistUpdate() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";

		TeachingSessionEntity teachingSession = new TeachingSessionEntity(start, end, description, location, timetable,
		                                                                  subject);
		teachingSessionDAO.persist(teachingSession);

		// Update the teaching session
		Timestamp newStart = Timestamp.valueOf("2025-02-11 13:00:00");
		Timestamp newEnd = Timestamp.valueOf("2025-02-11 16:00:00");
		teachingSession.setStartDate(newStart);
		teachingSession.setEndDate(newEnd);
		teachingSessionDAO.persist(teachingSession);

		TeachingSessionEntity updatedSession = teachingSessionDAO.findById(teachingSession.getId());
		assertEquals(newStart, updatedSession.getStartDate());
		assertEquals(newEnd, updatedSession.getEndDate());
		assertEquals(1, teachingSessionDAO.findAll().size());
	}

	@Test
	void findById() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";

		TeachingSessionEntity teachingSession = new TeachingSessionEntity(start, end, description, location, timetable,
		                                                                  subject);

		teachingSessionDAO.persist(teachingSession);

		assertEquals(teachingSession, teachingSessionDAO.findById(teachingSession.getId()));
	}

	@Test
	void findAll() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";

		TeachingSessionEntity teachingSession1 = new TeachingSessionEntity(start, end, description, location, timetable,
		                                                                   subject);
		TeachingSessionEntity teachingSession2 = new TeachingSessionEntity(start, end, description, location, timetable,
		                                                                   subject);

		teachingSessionDAO.persist(teachingSession1);
		teachingSessionDAO.persist(teachingSession2);

		assertEquals(2, teachingSessionDAO.findAll().size());
	}

	@Test
	void delete() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";

		TeachingSessionEntity teachingSession = new TeachingSessionEntity(start, end, description, location, timetable,
		                                                                  subject);

		teachingSessionDAO.persist(teachingSession);

		assertEquals(1, teachingSessionDAO.findAll().size());

		teachingSessionDAO.delete(teachingSession);

		assertEquals(0, teachingSessionDAO.findAll().size());
	}

	@Test
	void findBySubjectId() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";

		TeachingSessionEntity teachingSession = new TeachingSessionEntity(start, end, description, location, timetable,
		                                                                  subject);

		teachingSessionDAO.persist(teachingSession);

		assertEquals(teachingSession, teachingSessionDAO.findBySubjectId(subject.getId()));
	}

	@Test
	void findByLocationId() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";

		TeachingSessionEntity teachingSession = new TeachingSessionEntity(start, end, description, location, timetable,
		                                                                  subject);

		teachingSessionDAO.persist(teachingSession);

		assertEquals(teachingSession, teachingSessionDAO.findByLocationId(location.getId()));
	}

	@Test
	void deleteAll() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";
		String description2 = "Math class 2";

		TeachingSessionEntity teachingSession1 = new TeachingSessionEntity(start, end, description, location,
		                                                                   timetable, subject);
		TeachingSessionEntity teachingSession2 = new TeachingSessionEntity(start, end, description2, location,
		                                                                   timetable,
		                                                                   subject);

		teachingSessionDAO.persist(teachingSession1);
		teachingSessionDAO.persist(teachingSession2);

		assertEquals(2, teachingSessionDAO.findAll().size());

		teachingSessionDAO.deleteAll();

		assertEquals(0, teachingSessionDAO.findAll().size());
	}

	@Test
	void findAllByTimetableId() {
		TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
		LocationDAO locationDAO = new LocationDAO();
		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		TimetableEntity timetable2 = new TimetableEntity();
		timetableDAO.persist(timetable2);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";
		String description2 = "Math class 2";

		TeachingSessionEntity teachingSession1 = new TeachingSessionEntity(start, end, description, location, timetable,
		                                                                   subject);
		TeachingSessionEntity teachingSession2 =
				new TeachingSessionEntity(start, end, description2, location, timetable2,
				                          subject);

		teachingSessionDAO.persist(teachingSession1);
		teachingSessionDAO.persist(teachingSession2);

		assertEquals(1, teachingSessionDAO.findAllByTimetableId(timetable.getId()).size());
		assertEquals(1, teachingSessionDAO.findAllByTimetableId(timetable2.getId()).size());
	}
}