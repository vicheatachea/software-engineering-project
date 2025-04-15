package dao;

import datasource.MariaDBConnection;
import entity.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserGroupDAOTest {

	private static final UserGroupDAO userGroupDAO = new UserGroupDAO();
	private static final UserDAO userDAO = new UserDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();
	private static final SubjectDAO subjectDAO = new SubjectDAO();

	private static void resetDatabase() {
		userGroupDAO.deleteAll();
		userDAO.deleteAll();
		timetableDAO.deleteAll();
	}


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
		resetDatabase();
	}

	@BeforeEach
	void setUp() {
		resetDatabase();
	}

	@Test
	void persist() {
		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();
		TimetableEntity timetable3 = new TimetableEntity();
		TimetableEntity timetable4 = new TimetableEntity();
		TimetableEntity timetable5 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);
		timetableDAO.persist(timetable3);
		timetableDAO.persist(timetable4);
		timetableDAO.persist(timetable5);

		Timestamp dOB = Timestamp.valueOf("2000-01-01 00:00:00");

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", dOB, "123456789AB", Role.STUDENT, timetable1);
		UserEntity user2 =
				new UserEntity("Matti", "Meikäläinen", "MattiMeikäläinen", "password", dOB, "987654321", Role.STUDENT,
				               timetable2);
		UserEntity user3 =
				new UserEntity("Maija", "Meikäläinen", "MaijaMeikäläinen", "password", dOB, "876543219", Role.STUDENT,
				               timetable3);

		HashSet<UserEntity> students = new HashSet<>();
		students.add(user);
		students.add(user2);

		userDAO.persist(user);
		userDAO.persist(user2);
		userDAO.persist(user3);

		UserEntity teacher =
				new UserEntity("Jane", "Doe", "JaneDoe", "password", dOB, "765432198", Role.TEACHER, timetable4);

		userDAO.persist(teacher);

		SubjectEntity subject = new SubjectEntity("ICT", "ICT1001");

		subjectDAO.persist(subject);

		UserGroupEntity userGroup =
				new UserGroupEntity("Group 1", "ICT3003", 34, teacher, students, subject, timetable5);

		userGroupDAO.persist(userGroup);

		UserGroupEntity foundUserGroup = userGroupDAO.findById(userGroup.getId());

		assertEquals(userGroup.getName(), foundUserGroup.getName());
		assertEquals(userGroup.getCode(), foundUserGroup.getCode());
		assertEquals(userGroup.getCapacity(), foundUserGroup.getCapacity());
		assertEquals(userGroup.getTeacher().getId(), foundUserGroup.getTeacher().getId());
		assertEquals(userGroup.getStudents().size(), foundUserGroup.getStudents().size());
		assertEquals(userGroup.getTimetable().getId(), foundUserGroup.getTimetable().getId());
	}

	@Test
	void persistUpdate() {
		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();
		TimetableEntity timetable3 = new TimetableEntity();
		TimetableEntity timetable4 = new TimetableEntity();
		TimetableEntity timetable5 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);
		timetableDAO.persist(timetable3);
		timetableDAO.persist(timetable4);
		timetableDAO.persist(timetable5);

		Timestamp dOB = Timestamp.valueOf("2000-01-01 00:00:00");

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", dOB, "123456789AB", Role.STUDENT, timetable1);

		UserEntity user2 =
				new UserEntity("Matti", "Meikäläinen", "MattiMeikäläinen", "password", dOB, "qwdfzsdfwefs",
				               Role.STUDENT,
				               timetable2);

		UserEntity user3 =
				new UserEntity("Maija", "Meikäläinen", "MaijaMeikäläinen", "password", dOB, "vdsadgvdsas", Role.STUDENT,
				               timetable3);

		HashSet<UserEntity> students = new HashSet<>();

		students.add(user);
		students.add(user2);
		students.add(user3);

		userDAO.persist(user);
		userDAO.persist(user2);
		userDAO.persist(user3);

		UserEntity teacher =
				new UserEntity("Jane", "Doe", "JaneDoe", "password", dOB, "vcxvsdvfbfd", Role.TEACHER, timetable4);

		userDAO.persist(teacher);

		SubjectEntity subject = new SubjectEntity("ICT", "ICT1002");
		subjectDAO.persist(subject);

		UserGroupEntity userGroup =
				new UserGroupEntity("Group 1", "ICT3003", 34, teacher, students, subject, timetable5);

		userGroupDAO.persist(userGroup);

		userGroup.setName("Updated Group");

		userGroupDAO.persist(userGroup);

		UserGroupEntity foundUserGroup = userGroupDAO.findById(userGroup.getId());

		assertEquals("Updated Group", foundUserGroup.getName());

		assertEquals(1, userGroupDAO.findAll().size());
	}

	@Test
	void findById() {
		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();
		TimetableEntity timetable3 = new TimetableEntity();
		TimetableEntity timetable4 = new TimetableEntity();
		TimetableEntity timetable5 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);
		timetableDAO.persist(timetable3);
		timetableDAO.persist(timetable4);
		timetableDAO.persist(timetable5);

		Timestamp dOB = Timestamp.valueOf("2000-01-01 00:00:00");

		UserEntity user =
				new UserEntity("John", "Doe", "JohnDoe", "password", dOB, "123456789AB", Role.STUDENT, timetable1);

		UserEntity user2 =
				new UserEntity("Matti", "Meikäläinen", "MattiMeikäläinen", "password", dOB, "asdfqwefaseg",
				               Role.STUDENT,
				               timetable2);

		UserEntity user3 =
				new UserEntity("Maija", "Meikäläinen", "MaijaMeikäläinen", "password", dOB, "bfdsrgesrt234",
				               Role.STUDENT,
				               timetable3);

		HashSet<UserEntity> students = new HashSet<>();

		students.add(user);
		students.add(user2);
		students.add(user3);

		userDAO.persist(user);
		userDAO.persist(user2);
		userDAO.persist(user3);

		UserEntity teacher =
				new UserEntity("Jane", "Doe", "JaneDoe", "password", dOB, "asdfwefzvswe", Role.TEACHER, timetable4);

		userDAO.persist(teacher);

		SubjectEntity subject = new SubjectEntity("ICT", "ICT1003");

		subjectDAO.persist(subject);

		UserGroupEntity userGroup =
				new UserGroupEntity("Group 1", "ICT3003", 34, teacher, students, subject, timetable5);

		userGroupDAO.persist(userGroup);

		UserGroupEntity foundUserGroup = userGroupDAO.findById(userGroup.getId());

		assertEquals(foundUserGroup.getId(), userGroup.getId());
		assertEquals(foundUserGroup.getName(), userGroup.getName());
		assertEquals(foundUserGroup.getCode(), userGroup.getCode());
		assertEquals(foundUserGroup.getCapacity(), userGroup.getCapacity());
		assertEquals(foundUserGroup.getTeacher().getId(), userGroup.getTeacher().getId());
		assertEquals(foundUserGroup.getStudents().size(), userGroup.getStudents().size());
		assertEquals(foundUserGroup.getTimetable().getId(), userGroup.getTimetable().getId());
	}

	@Test
	void findAll() {
		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();
		TimetableEntity timetable3 = new TimetableEntity();
		TimetableEntity timetable4 = new TimetableEntity();
		TimetableEntity timetable5 = new TimetableEntity();
		TimetableEntity timetable6 = new TimetableEntity();
		TimetableEntity timetable7 = new TimetableEntity();
		TimetableEntity timetable8 = new TimetableEntity();
		TimetableEntity timetable9 = new TimetableEntity();
		TimetableEntity timetable10 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);
		timetableDAO.persist(timetable3);
		timetableDAO.persist(timetable4);
		timetableDAO.persist(timetable5);
		timetableDAO.persist(timetable6);
		timetableDAO.persist(timetable7);
		timetableDAO.persist(timetable8);
		timetableDAO.persist(timetable9);
		timetableDAO.persist(timetable10);

		Timestamp dOB = Timestamp.valueOf("2000-01-01 00:00:00");

		UserEntity user1 =
				new UserEntity("John", "Doe", "JohnDoe", "password1", dOB, "123456789AB", Role.STUDENT, timetable1);

		UserEntity user2 = new UserEntity("Matti", "Meikäläinen", "MattiMeikäläinen", "password2", dOB, "BA987654321",
		                                  Role.STUDENT, timetable2);

		UserEntity user3 = new UserEntity("Maija", "Meikäläinen", "MaijaMeikäläinen", "password3", dOB, "A987654321B",
		                                  Role.STUDENT, timetable3);

		UserEntity user4 = new UserEntity("user1", "name1", "user1name1", "password4", dOB, "987654321BA", Role.STUDENT,
		                                  timetable4);

		UserEntity user5 = new UserEntity("user2", "name2", "user2name2", "password5", dOB, "87654321BA9", Role.STUDENT,
		                                  timetable5);

		UserEntity user6 = new UserEntity("user3", "name3", "user3name3", "password6", dOB, "7654321BA98", Role.STUDENT,
		                                  timetable6);

		HashSet<UserEntity> students1 = new HashSet<>();

		HashSet<UserEntity> students2 = new HashSet<>();

		students1.add(user1);
		students1.add(user2);
		students1.add(user3);

		students2.add(user4);
		students2.add(user5);
		students2.add(user6);

		userDAO.persist(user1);
		userDAO.persist(user2);
		userDAO.persist(user3);
		userDAO.persist(user4);
		userDAO.persist(user5);
		userDAO.persist(user6);

		UserEntity teacher =
				new UserEntity("Jane", "Doe", "JaneDoe", "password", dOB, "asdfcvbdB", Role.TEACHER, timetable7);

		UserEntity teacher2 =
				new UserEntity("Jeff", "Doe", "JeffDoe", "password", dOB, "asdbsadfsdvf", Role.TEACHER, timetable8);

		userDAO.persist(teacher);
		userDAO.persist(teacher2);

		SubjectEntity subject = new SubjectEntity("ICT", "ICT1004");
		subjectDAO.persist(subject);

		UserGroupEntity userGroup1 =
				new UserGroupEntity("Group 1", "ICT3003", 34, teacher, students1, subject, timetable9);
		UserGroupEntity userGroup2 =
				new UserGroupEntity("Group 2", "ICT3004", 30, teacher, students2, subject, timetable10);

		userGroupDAO.persist(userGroup1);
		userGroupDAO.persist(userGroup2);

		assertEquals(2, userGroupDAO.findAll().size());
	}

	@Test
	void delete() {
		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();
		TimetableEntity timetable3 = new TimetableEntity();
		TimetableEntity timetable4 = new TimetableEntity();
		TimetableEntity timetable5 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);
		timetableDAO.persist(timetable3);
		timetableDAO.persist(timetable4);
		timetableDAO.persist(timetable5);

		Timestamp dOB = Timestamp.valueOf("2000-01-01 00:00:00");

		UserEntity user1 =
				new UserEntity("John", "Doe", "JohnDoe", "password1", dOB, "987654321", Role.STUDENT, timetable1);

		UserEntity user2 = new UserEntity("Matti", "Meikäläinen", "MattiMeikäläinen", "password2", dOB, "123456789AB",
		                                  Role.STUDENT, timetable2);

		UserEntity user3 = new UserEntity("Maija", "Meikäläinen", "MaijaMeikäläinen", "password3", dOB, "BA987654321",
		                                  Role.STUDENT, timetable3);

		HashSet<UserEntity> students = new HashSet<>();

		students.add(user1);
		students.add(user2);
		students.add(user3);

		userDAO.persist(user1);
		userDAO.persist(user2);
		userDAO.persist(user3);

		UserEntity teacher =
				new UserEntity("Jane", "Doe", "JaneDoe", "password", dOB, "9876543321BA", Role.TEACHER, timetable4);

		userDAO.persist(teacher);

		SubjectEntity subject = new SubjectEntity("ICT", "ICT1005");
		subjectDAO.persist(subject);

		UserGroupEntity userGroup =
				new UserGroupEntity("Group 1", "ICT3003", 34, teacher, students, subject, timetable5);

		userGroupDAO.persist(userGroup);

		assertEquals(1, userGroupDAO.findAll().size());

		UserGroupEntity foundUserGroup = userGroupDAO.findById(userGroup.getId());

		userGroupDAO.delete(foundUserGroup);

		assertEquals(0, userGroupDAO.findAll().size());
	}

	@Test
	void deleteAll() {
		TimetableEntity timetable1 = new TimetableEntity();
		TimetableEntity timetable2 = new TimetableEntity();
		TimetableEntity timetable3 = new TimetableEntity();
		TimetableEntity timetable4 = new TimetableEntity();
		TimetableEntity timetable5 = new TimetableEntity();
		TimetableEntity timetable6 = new TimetableEntity();
		TimetableEntity timetable7 = new TimetableEntity();
		TimetableEntity timetable8 = new TimetableEntity();
		TimetableEntity timetable9 = new TimetableEntity();
		TimetableEntity timetable10 = new TimetableEntity();

		timetableDAO.persist(timetable1);
		timetableDAO.persist(timetable2);
		timetableDAO.persist(timetable3);
		timetableDAO.persist(timetable4);
		timetableDAO.persist(timetable5);
		timetableDAO.persist(timetable6);
		timetableDAO.persist(timetable7);
		timetableDAO.persist(timetable8);
		timetableDAO.persist(timetable9);
		timetableDAO.persist(timetable10);

		Timestamp dOB = Timestamp.valueOf("2000-01-01 00:00:00");

		UserEntity user1 =
				new UserEntity("John", "Doe", "JohnDoe", "password1", dOB, "987654321", Role.STUDENT, timetable1);

		UserEntity user2 = new UserEntity("Matti", "Meikäläinen", "MattiMeikäläinen", "password2", dOB, "876543219",
		                                  Role.STUDENT, timetable2);

		UserEntity user3 = new UserEntity("Maija", "Meikäläinen", "MaijaMeikäläinen", "password3", dOB, "765432198",
		                                  Role.STUDENT, timetable3);

		UserEntity user4 = new UserEntity("user1", "name1", "user1name1", "password4", dOB, "654321987", Role.STUDENT,
		                                  timetable4);

		UserEntity user5 = new UserEntity("user2", "name2", "user2name2", "password5", dOB, "543219876", Role.STUDENT,
		                                  timetable5);

		UserEntity user6 = new UserEntity("user3", "name3", "user3name3", "password6", dOB, "432198765", Role.STUDENT,
		                                  timetable6);

		HashSet<UserEntity> students1 = new HashSet<>();

		HashSet<UserEntity> students2 = new HashSet<>();

		students1.add(user1);
		students1.add(user2);
		students1.add(user3);

		students2.add(user4);
		students2.add(user5);
		students2.add(user6);

		userDAO.persist(user1);
		userDAO.persist(user2);
		userDAO.persist(user3);
		userDAO.persist(user4);
		userDAO.persist(user5);
		userDAO.persist(user6);

		UserEntity teacher =
				new UserEntity("Jane", "Doe", "JaneDoe", "password", dOB, "123456789AB", Role.TEACHER, timetable7);

		UserEntity teacher2 =
				new UserEntity("Jeff", "Doe", "JeffDoe", "password", dOB, "987654321BA", Role.TEACHER, timetable8);


		userDAO.persist(teacher);
		userDAO.persist(teacher2);

		SubjectEntity subject = new SubjectEntity("ICT", "ICT3005");
		subjectDAO.persist(subject);

		UserGroupEntity userGroup1 =
				new UserGroupEntity("Group 1", "ICT3003", 34, teacher, students1, subject, timetable9);
		UserGroupEntity userGroup2 =
				new UserGroupEntity("Group 2", "ICT3004", 30, teacher, students2, subject, timetable10);

		userGroupDAO.persist(userGroup1);
		userGroupDAO.persist(userGroup2);

		assertEquals(2, userGroupDAO.findAll().size());

		userGroupDAO.deleteAll();

		assertEquals(0, userGroupDAO.findAll().size());
	}
}