package dao;

import datasource.MariaDBConnection;
import entity.Role;
import entity.TimetableEntity;
import entity.UserEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

	@BeforeAll
	static void ensureDatabase() throws SQLException {
		MariaDBConnection.verifyDatabase();
	}

	@AfterAll
	static void tearDown() {
		UserDAO userDao = new UserDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();
		userDao.deleteAll();
		timeTableDAO.deleteAll();
	}


	@BeforeEach
	void setUp() {
		UserDAO userDao = new UserDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();
		userDao.deleteAll();
		timeTableDAO.deleteAll();
	}

	@Test
	void persist() {
		UserDAO userDao = new UserDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		Timestamp DoB = Timestamp.valueOf("2000-01-01 00:00:00");

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", DoB, "123456789AB", Role.STUDENT, timetable);

		userDao.persist(user);

		UserEntity foundUser = userDao.findByUsername("JohnDoe");

		assertEquals(user.getFirstName(), foundUser.getFirstName());
		assertEquals(user.getLastName(), foundUser.getLastName());
		assertEquals(user.getUsername(), foundUser.getUsername());
		assertEquals(user.getPassword(), foundUser.getPassword());
		assertEquals(user.getDateOfBirth(), foundUser.getDateOfBirth());
		assertEquals(user.getRole(), foundUser.getRole());
		assertEquals(user.getTimetable(), foundUser.getTimetable());
	}
	
	@Test
	void persistUpdate() {
		UserDAO userDao = new UserDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		Timestamp DoB = Timestamp.valueOf("2000-01-01 00:00:00");

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", DoB, "123456789AB", Role.STUDENT, timetable);

		userDao.persist(user);

		user.setFirstName("Jane");
		user.setLastName("Smith");
		user.setUsername("JaneSmith");
		user.setPassword("newpassword");
		user.setDateOfBirth(Timestamp.valueOf("2001-01-01 00:00:00"));
		user.setSocialNumber("987654321AB");
		user.setRole(Role.TEACHER);
		
		userDao.persist(user);

		UserEntity foundUser = userDao.findByUsername("JaneSmith");

		assertEquals(user.getFirstName(), foundUser.getFirstName());
		assertEquals(user.getLastName(), foundUser.getLastName());
		assertEquals(user.getUsername(), foundUser.getUsername());
		assertEquals(user.getPassword(), foundUser.getPassword());
		assertEquals(user.getDateOfBirth(), foundUser.getDateOfBirth());
		assertEquals(user.getRole(), foundUser.getRole());
		assertEquals(user.getTimetable(), foundUser.getTimetable());
	}

	@Test
	void findByUsername() {
		UserDAO userDao = new UserDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		Timestamp DoB = Timestamp.valueOf("2000-01-01 00:00:00");

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", DoB, "123456789AB", Role.STUDENT, timetable);

		userDao.persist(user);

		assertEquals(user, userDao.findByUsername(user.getUsername()));
	}

	@Test
	void authenticate() {
		UserDAO userDao = new UserDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		Timestamp DoB = Timestamp.valueOf("2000-01-01 00:00:00");

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", DoB, "123456789AB", Role.STUDENT, timetable);

		userDao.persist(user);

		assertTrue(userDao.authenticate(user.getUsername(), "password"));
		assertFalse(userDao.authenticate(user.getUsername(), "wrongpassword"));
	}

	@Test
	void delete() {
		UserDAO userDao = new UserDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		Timestamp DoB = Timestamp.valueOf("2000-01-01 00:00:00");

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", DoB, "123456789AB", Role.STUDENT, timetable);

		userDao.persist(user);

		userDao.delete(user);

		assertNull(userDao.findByUsername(user.getUsername()));
	}

	@Test
	void deleteAll() {
		UserDAO userDao = new UserDAO();
		TimetableDAO timetableDAO = new TimetableDAO();

		Timestamp DoB1 = Timestamp.valueOf("2000-01-01 00:00:00");
		Timestamp DoB2 = Timestamp.valueOf("2000-01-02 00:00:00");

		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);

		UserEntity user1 =
				new UserEntity("John", "Doe", "JohnDoe", "password", DoB1, "123456789AB", Role.STUDENT, timetable1);

		UserEntity user2 =
				new UserEntity("Jane", "Doe", "JaneDoe", "password12", DoB2, "234567891AB", Role.TEACHER, timetable2);

		userDao.persist(user1);
		userDao.persist(user2);

		assertEquals(2, userDao.findAll().size());

		userDao.deleteAll();

		assertEquals(0, userDao.findAll().size());
	}

}