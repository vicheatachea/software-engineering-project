package dao;

import entity.TimetableEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TimetableDAOTest {

	@BeforeAll
	static void ensureDatabase() throws SQLException {
		datasource.MariaDBConnection.verifyDatabase();
	}

	@BeforeEach
	void setUp() {
		TimetableDAO timetableDAO = new TimetableDAO();
		timetableDAO.deleteAll();
	}

	@AfterAll
	static void tearDown() {
		TimetableDAO timetableDAO = new TimetableDAO();
		timetableDAO.deleteAll();
	}

	@Test
	void persist() {
		TimetableDAO timetableDAO = new TimetableDAO();

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		assertNotNull(timetableDAO.findById(timetable.getId()));
	}

	@Test
	void findAll() {
		TimetableDAO timetableDAO = new TimetableDAO();

		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);

		assertEquals(2, timetableDAO.findAll().size());
	}

	@Test
	void findById() {
		TimetableDAO timetableDAO = new TimetableDAO();

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		assertEquals(timetable, timetableDAO.findById(timetable.getId()));
	}

	@Test
	void delete() {
		TimetableDAO timetableDAO = new TimetableDAO();

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		timetableDAO.delete(timetable);

		assertNull(timetableDAO.findById(timetable.getId()));
	}

	@Test
	void deleteAll() {
		TimetableDAO timetableDAO = new TimetableDAO();

		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);

		assertEquals(2, timetableDAO.findAll().size());

		timetableDAO.deleteAll();

		assertEquals(0, timetableDAO.findAll().size());
	}
}