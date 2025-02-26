package dao;

import datasource.MariaDBConnection;
import entity.SubjectEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubjectDAOTest {

	@BeforeAll
	static void ensureDatabase() throws SQLException {
		MariaDBConnection.verifyDatabase();
	}

	@AfterAll
	static void tearDown() {
		SubjectDAO subjectDAO = new SubjectDAO();
		subjectDAO.deleteAll();
	}

	@BeforeEach
	void setUp() {
		SubjectDAO subjectDAO = new SubjectDAO();
		subjectDAO.deleteAll();
	}

	@Test
	void persist() {
		SubjectDAO subjectDAO = new SubjectDAO();
		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");

		subjectDAO.persist(subject);

		SubjectEntity foundSubject = subjectDAO.findById(subject.getId());

		assertEquals("Math", foundSubject.getName());
		assertEquals("Mathematics-101", foundSubject.getCode());
	}
	
	@Test
	void persistUpdate() {
		SubjectDAO subjectDAO = new SubjectDAO();
		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");

		subjectDAO.persist(subject);

		subject.setName("Advanced Math");
		subject.setCode("Mathematics-201");
		subjectDAO.persist(subject);

		SubjectEntity foundSubject = subjectDAO.findById(subject.getId());

		assertEquals("Advanced Math", foundSubject.getName());
		assertEquals("Mathematics-201", foundSubject.getCode());
	}

	@Test
	void findById() {
		SubjectDAO subjectDAO = new SubjectDAO();
		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");

		subjectDAO.persist(subject);

		assertEquals(subject, subjectDAO.findById(subject.getId()));
	}

	@Test
	void findAll() {
		SubjectDAO subjectDAO = new SubjectDAO();
		SubjectEntity subject1 = new SubjectEntity("Math", "Mathematics-101");
		SubjectEntity subject2 = new SubjectEntity("Physics", "Physics-101");

		subjectDAO.persist(subject1);
		subjectDAO.persist(subject2);

		assertEquals(2, subjectDAO.findAll().size());
	}

	@Test
	void delete() {
		SubjectDAO subjectDAO = new SubjectDAO();
		SubjectEntity subject = new SubjectEntity("Math", "Mathematics-101");

		subjectDAO.persist(subject);
		subjectDAO.delete(subject);

		assertNull(subjectDAO.findById(subject.getId()));
	}

	@Test
	void deleteAll() {
		SubjectDAO subjectDAO = new SubjectDAO();
		SubjectEntity subject1 = new SubjectEntity("Math", "Mathematics-101");
		SubjectEntity subject2 = new SubjectEntity("Physics", "Physics-101");

		subjectDAO.persist(subject1);
		subjectDAO.persist(subject2);

		assertEquals(2, subjectDAO.findAll().size());

		subjectDAO.deleteAll();

		assertEquals(0, subjectDAO.findAll().size());
	}
}