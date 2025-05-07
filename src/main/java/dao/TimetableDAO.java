package dao;

import datasource.MariaDBConnection;
import entity.TimetableEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * TimetableDAO is a Data Access Object (DAO) class that provides methods to interact with the database
 * for the {@link TimetableEntity}. It handles CRUD operations and other specific queries related to timetables,
 * including retrieving timetables associated with users and groups.
 */
public class TimetableDAO {

	private static final EntityManagerFactory emf = MariaDBConnection.getEntityManagerFactory();

	private static final Logger logger = LoggerFactory.getLogger(TimetableDAO.class);

	private static final String TIMETABLE = "timetable";
	private static final String USER_ID = "userId";
	private static final String ERROR_MESSAGE = "Error: ";

	/**
	 * Logs an error message using the configured logger.
	 *
	 * @param e The exception to log.
	 */
	private void logErrorMessage(final Exception e) {
		logger.error(ERROR_MESSAGE, e);
	}

	/**
	 * Persists a new timetable entity to the database.
	 * The method starts a transaction, persists the entity, and commits the transaction.
	 * If an exception occurs, the transaction is rolled back and the error is logged.
	 *
	 * @param timetable The timetable entity to be persisted
	 */
	public void persist(final TimetableEntity timetable) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(timetable);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logErrorMessage(e);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Retrieves all timetable entities from the database.
	 *
	 * @return A list of all timetable entities or an empty list if none are found
	 */
	public List<TimetableEntity> findAll() {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TimetableEntity t", TimetableEntity.class).getResultList();
		} catch (Exception e) {
			logErrorMessage(e);
			return List.of();
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Retrieves all timetable entities associated with a specific user.
	 * This includes timetables directly associated with the user,
	 * timetables of groups where the user is a student,
	 * and timetables of groups where the user is a teacher.
	 *
	 * @param userId The ID of the user
	 * @return A list of timetable entities associated with the user or an empty list if none are found
	 */
	public List<TimetableEntity> findAllByUserId(final Long userId) {
		EntityManager em = emf.createEntityManager();
		try {
			List<TimetableEntity> timetables =
					em.createQuery("SELECT u.timetable FROM UserEntity u WHERE u.id = :userId", TimetableEntity.class)
					  .setParameter(USER_ID, userId).getResultList();

			List<TimetableEntity> groupTimetables = em
					.createQuery("SELECT g.timetable FROM UserGroupEntity g JOIN g.students s WHERE s.id = :userId",
					             TimetableEntity.class).setParameter(USER_ID, userId).getResultList();

			timetables.addAll(groupTimetables);

			List<TimetableEntity> teacherTimetables = em
					.createQuery("SELECT g.timetable FROM UserGroupEntity g WHERE g.teacher.id = :userId",
					             TimetableEntity.class).setParameter(USER_ID, userId).getResultList();

			timetables.addAll(teacherTimetables);

			return timetables;
		} catch (Exception e) {
			logErrorMessage(e);
			return List.of();
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Finds a timetable entity by its ID.
	 *
	 * @param id The ID of the timetable to find
	 * @return The timetable entity if found, null otherwise
	 */
	public TimetableEntity findById(final Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(TimetableEntity.class, id);
		} catch (Exception e) {
			logErrorMessage(e);
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Deletes a timetable entity from the database.
	 * This method first deletes all related entities (assignments, teaching sessions, users, user groups)
	 * that reference the timetable, and then removes the timetable entity itself.
	 *
	 * @param timetable The timetable entity to be deleted
	 */
	public void delete(final TimetableEntity timetable) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		try {
			// Delete related assignments
			em.createQuery("DELETE FROM AssignmentEntity a WHERE a.timetable = :timetable")
			  .setParameter(TIMETABLE, timetable).executeUpdate();

			// Delete related teaching sessions
			em.createQuery("DELETE FROM TeachingSessionEntity ts WHERE ts.timetable = :timetable")
			  .setParameter(TIMETABLE, timetable).executeUpdate();

			// Delete related user
			em.createQuery("DELETE FROM UserEntity u WHERE u.timetable = :timetable").setParameter(TIMETABLE, timetable)
			  .executeUpdate();

			// Delete related user group
			em.createQuery("DELETE FROM UserGroupEntity ug WHERE ug.timetable = :timetable")
			  .setParameter(TIMETABLE, timetable).executeUpdate();

			// Finally, delete the timetable
			em.remove(em.contains(timetable) ? timetable : em.merge(timetable));
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logErrorMessage(e);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Deletes all timetable entities from the database.
	 * This method also deletes all related entities (user groups, assignments, teaching sessions, users)
	 * before removing all timetable entities.
	 */
	public void deleteAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			// Delete related user group
			em.createQuery("DELETE FROM UserGroupEntity").executeUpdate();
			// Delete related assignments
			em.createQuery("DELETE FROM AssignmentEntity").executeUpdate();
			// Delete related teaching sessions
			em.createQuery("DELETE FROM TeachingSessionEntity").executeUpdate();
			// Delete related user
			em.createQuery("DELETE FROM UserEntity").executeUpdate();
			// Finally, delete the timetable
			em.createQuery("DELETE FROM TimetableEntity").executeUpdate();
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			logErrorMessage(e);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Finds a timetable entity associated with a specific user.
	 *
	 * @param userId The ID of the user
	 * @return The timetable entity if found, null otherwise
	 */
	public TimetableEntity findByUserId(final long userId) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT u.timetable FROM UserEntity u WHERE u.id = :userId", TimetableEntity.class)
			         .setParameter(USER_ID, userId).getSingleResult();
		} catch (Exception e) {
			logErrorMessage(e);
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Finds a timetable entity associated with a specific group name.
	 *
	 * @param groupName The name of the group
	 * @return The timetable entity if found, null otherwise
	 */
	public TimetableEntity findByGroupName(final String groupName) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT g.timetable FROM UserGroupEntity g WHERE g.name = :groupName",
			                      TimetableEntity.class).setParameter("groupName", groupName).getSingleResult();
		} catch (Exception e) {
			logErrorMessage(e);
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
}
