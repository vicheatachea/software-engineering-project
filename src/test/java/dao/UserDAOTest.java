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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserDAOTest {

	private static final UserDAO userDao = new UserDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();

	private static void resetDatabase() {
		userDao.deleteAll();
		timetableDAO.deleteAll();
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
		Timestamp doB = Timestamp.valueOf("2000-01-01 00:00:00");

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", doB, "123456789AB", Role.STUDENT, timetable);

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
	void Update() {
		Timestamp doB = Timestamp.valueOf("2000-01-01 00:00:00");

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", doB, "123456789AB", Role.STUDENT, timetable);

		userDao.persist(user);

		user.setFirstName("Jane");
		user.setLastName("Smith");
		user.setUsername("JaneSmith");
		user.setPassword("newpassword");
		user.setDateOfBirth(Timestamp.valueOf("2001-01-01 00:00:00"));
		user.setSocialNumber("987654321AB");
		user.setRole(Role.TEACHER);

		userDao.update(user);

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
		Timestamp dOB = Timestamp.valueOf("2000-01-01 00:00:00");

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", dOB,
				               "123456789AB", Role.STUDENT, timetable);

		userDao.persist(user);

		assertEquals(user, userDao.findByUsername(user.getUsername()));
	}

	@Test
	void authenticate() {
		Timestamp doB = Timestamp.valueOf("2000-01-01 00:00:00");

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", doB, "123456789AB", Role.STUDENT, timetable);

		userDao.persist(user);

		assertEquals(user.getId(), userDao.authenticate(user.getUsername(), "password").getId());
		assertNull(userDao.authenticate(user.getUsername(), "wrongpassword"));
	}

	@Test
	void delete() {
		Timestamp doB = Timestamp.valueOf("2000-01-01 00:00:00");

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", doB, "123456789AB", Role.STUDENT, timetable);

		userDao.persist(user);

		userDao.delete(user);

		assertNull(userDao.findByUsername(user.getUsername()));
	}

	@Test
	void deleteAll() {
		Timestamp doB1 = Timestamp.valueOf("2000-01-01 00:00:00");
		Timestamp doB2 = Timestamp.valueOf("2000-01-02 00:00:00");

		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);

		UserEntity user1 =
				new UserEntity("John", "Doe", "JohnDoe", "password", doB1, "123456789AB", Role.STUDENT, timetable1);

		UserEntity user2 =
				new UserEntity("Jane", "Doe", "JaneDoe", "password12", doB2, "234567891AB", Role.TEACHER, timetable2);

		userDao.persist(user1);
		userDao.persist(user2);

		assertEquals(2, userDao.findAll().size());

		userDao.deleteAll();

		assertEquals(0, userDao.findAll().size());
	}

}