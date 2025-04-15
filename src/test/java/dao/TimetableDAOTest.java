package dao;

import datasource.MariaDBConnection;
import entity.TimetableEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TimetableDAOTest {

	private static final TimetableDAO timetableDAO = new TimetableDAO();

	@BeforeAll
	static void ensureDatabase() {
		try {
			MariaDBConnection.getInstance().verifyDatabase();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void tearDown() {
		timetableDAO.deleteAll();
	}

	@BeforeEach
	void setUp() {
		timetableDAO.deleteAll();
	}

	@Test
	void persist() {
		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		assertNotNull(timetableDAO.findById(timetable.getId()));
	}

	@Test
	void findAll() {
		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);

		assertEquals(2, timetableDAO.findAll().size());
	}

	@Test
	void findById() {
		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		assertEquals(timetable, timetableDAO.findById(timetable.getId()));
	}

	@Test
	void delete() {
		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		timetableDAO.delete(timetable);

		assertNull(timetableDAO.findById(timetable.getId()));
	}

	@Test
	void deleteAll() {
		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);

		assertEquals(2, timetableDAO.findAll().size());

		timetableDAO.deleteAll();

		assertEquals(0, timetableDAO.findAll().size());
	}
}