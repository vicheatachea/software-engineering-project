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
	private static final TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
	private static final LocationDAO locationDAO = new LocationDAO();
	private static final SubjectDAO subjectDAO = new SubjectDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();

	@BeforeAll
	static void ensureDatabase() throws SQLException {
		MariaDBConnection.verifyDatabase();
	}

	@AfterAll
	static void tearDown() {
		teachingSessionDAO.deleteAll();
		locationDAO.deleteAll();
		subjectDAO.deleteAll();
		timetableDAO.deleteAll();

	}

	@BeforeEach
	void setUp() {
		teachingSessionDAO.deleteAll();
		locationDAO.deleteAll();
		subjectDAO.deleteAll();
		timetableDAO.deleteAll();
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void persist() {
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";
		String localeCode = "en";

		TeachingSessionEntity teachingSession =
				new TeachingSessionEntity(start, end, description, location, timetable, subject, localeCode);

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
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";
		String localeCode = "en";

		TeachingSessionEntity teachingSession =
				new TeachingSessionEntity(start, end, description, location, timetable, subject, localeCode);
		teachingSessionDAO.persist(teachingSession);

		// Update the teaching session
		Timestamp newStart = Timestamp.valueOf("2025-02-11 13:00:00");
		Timestamp newEnd = Timestamp.valueOf("2025-02-11 16:00:00");
		teachingSession.setStartDate(newStart);
		teachingSession.setEndDate(newEnd);
		teachingSessionDAO.update(teachingSession);

		TeachingSessionEntity updatedSession = teachingSessionDAO.findById(teachingSession.getId());
		assertEquals(newStart, updatedSession.getStartDate());
		assertEquals(newEnd, updatedSession.getEndDate());
		assertEquals(1, teachingSessionDAO.findAll().size());
	}

	@Test
	void findById() {
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";
		String localeCode = "en";

		TeachingSessionEntity teachingSession =
				new TeachingSessionEntity(start, end, description, location, timetable, subject, localeCode);

		teachingSessionDAO.persist(teachingSession);

		assertEquals(teachingSession, teachingSessionDAO.findById(teachingSession.getId()));
	}

	@Test
	void findAll() {
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";
		String localeCode = "en";

		TeachingSessionEntity teachingSession1 =
				new TeachingSessionEntity(start, end, description, location, timetable, subject, localeCode);
		TeachingSessionEntity teachingSession2 =
				new TeachingSessionEntity(start, end, description, location, timetable, subject, localeCode);

		teachingSessionDAO.persist(teachingSession1);
		teachingSessionDAO.persist(teachingSession2);

		assertEquals(2, teachingSessionDAO.findAll().size());
	}

	@Test
	void delete() {
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";
		String localeCode = "en";

		TeachingSessionEntity teachingSession =
				new TeachingSessionEntity(start, end, description, location, timetable, subject, localeCode);

		teachingSessionDAO.persist(teachingSession);

		assertEquals(1, teachingSessionDAO.findAll().size());

		teachingSessionDAO.delete(teachingSession);

		assertEquals(0, teachingSessionDAO.findAll().size());
	}

	@Test
	void findBySubjectId() {
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";
		String localeCode = "en";

		TeachingSessionEntity teachingSession =
				new TeachingSessionEntity(start, end, description, location, timetable, subject, localeCode);

		teachingSessionDAO.persist(teachingSession);

		assertEquals(teachingSession, teachingSessionDAO.findBySubjectId(subject.getId()));
	}

	@Test
	void findByLocationId() {
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		locationDAO.persist(location);

		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");
		subjectDAO.persist(subject);

		TimetableEntity timetable = new TimetableEntity();
		timetableDAO.persist(timetable);

		Timestamp start = Timestamp.valueOf("2025-02-11 12:00:00");
		Timestamp end = Timestamp.valueOf("2025-02-11 15:00:00");

		String description = "Math class";
		String localeCode = "en";

		TeachingSessionEntity teachingSession =
				new TeachingSessionEntity(start, end, description, location, timetable, subject, localeCode);

		teachingSessionDAO.persist(teachingSession);

		assertEquals(teachingSession, teachingSessionDAO.findByLocationId(location.getId()));
	}

	@Test
	void deleteAll() {
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

		String localeCode = "en";
		String localeCode2 = "jp";

		TeachingSessionEntity teachingSession1 =
				new TeachingSessionEntity(start, end, description, location, timetable, subject, localeCode);
		TeachingSessionEntity teachingSession2 =
				new TeachingSessionEntity(start, end, description2, location, timetable, subject, localeCode2);

		teachingSessionDAO.persist(teachingSession1);
		teachingSessionDAO.persist(teachingSession2);

		assertEquals(2, teachingSessionDAO.findAll().size());

		teachingSessionDAO.deleteAll();

		assertEquals(0, teachingSessionDAO.findAll().size());
	}

	@Test
	void findAllByTimetableId() {
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

		String localeCode = "en";
		String localeCode2 = "jp";

		TeachingSessionEntity teachingSession1 =
				new TeachingSessionEntity(start, end, description, location, timetable, subject, localeCode);
		TeachingSessionEntity teachingSession2 =
				new TeachingSessionEntity(start, end, description2, location, timetable2, subject, localeCode2);

		teachingSessionDAO.persist(teachingSession1);
		teachingSessionDAO.persist(teachingSession2);

		assertEquals(1, teachingSessionDAO.findAllByTimetableId(timetable.getId()).size());
		assertEquals(1, teachingSessionDAO.findAllByTimetableId(timetable2.getId()).size());
	}
}