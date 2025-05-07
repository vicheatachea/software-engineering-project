package dao;

import datasource.MariaDBConnection;
import entity.UserGroupEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * UserGroupDAO is a Data Access Object (DAO) class that provides methods to interact with the database
 * for the UserGroupEntity. It handles CRUD operations and other specific queries related to user groups.
 */
public class UserGroupDAO {

	private static final String ERROR_MESSAGE = "Error: ";
	private static final Logger logger = LoggerFactory.getLogger(UserGroupDAO.class);

	private static final EntityManagerFactory emf = MariaDBConnection.getEntityManagerFactory();

	/**
	 * Logs an error message using the configured logger.
	 *
	 * @param e The exception to log.
	 */
	private void logErrorMessage(final Exception e) {
		logger.error(ERROR_MESSAGE, e);
	}

	/**
	 * Persists a new user group entity or updates an existing one in the database.
	 * If the entity has an ID and exists, it's merged; otherwise, it's persisted.
	 *
	 * @param userGroup The user group entity to persist or update.
	 */
	public void persist(final UserGroupEntity userGroup) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			if (userGroup.getId() == null || findById(userGroup.getId()) == null) {
				em.persist(userGroup);
			} else {
				em.merge(userGroup);
			}
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
	 * Finds a user group entity by its ID.
	 *
	 * @param id The ID of the user group to find.
	 * @return The found user group entity, or null if not found or an error occurs.
	 */
	public UserGroupEntity findById(final Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(UserGroupEntity.class, id);
		} catch (NoResultException e) {
			logErrorMessage(e);
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Finds a user group entity by its associated timetable ID.
	 *
	 * @param timetableId The ID of the timetable.
	 * @return The found user group entity, or null if not found or an error occurs.
	 */
	public UserGroupEntity findByTimetableId(final long timetableId) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT g FROM UserGroupEntity g WHERE g.timetable.id = :timetableId",
			                      UserGroupEntity.class)
			         .setParameter("timetableId", timetableId)
			         .getSingleResult();
		} catch (NoResultException e) {
			logErrorMessage(e);
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Finds a user group entity by its name.
	 *
	 * @param groupName The name of the user group.
	 * @return The found user group entity, or null if not found or an error occurs.
	 */
	public UserGroupEntity findByName(final String groupName) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT g FROM UserGroupEntity g WHERE g.name = :groupName", UserGroupEntity.class)
			         .setParameter("groupName", groupName)
			         .getSingleResult();
		} catch (NoResultException e) {
			logErrorMessage(e);
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Finds all user group entities associated with a specific user ID (either as a teacher or a student).
	 *
	 * @param userId The ID of the user.
	 * @return A list of user group entities associated with the user, or an empty list if none are found or an error occurs.
	 */
	public List<UserGroupEntity> findAllByUserId(final Long userId) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery(
					         "SELECT DISTINCT g FROM UserGroupEntity g "
					         + "WHERE g.teacher.id = :userId "
					         + "OR "
					         + "g IN (SELECT g2 FROM UserGroupEntity g2 JOIN g2.students s WHERE s.id = :userId)",
					         UserGroupEntity.class)
			         .setParameter("userId", userId)
			         .getResultList();
		} catch (NoResultException e) {
			logErrorMessage(e);
			return List.of();
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Retrieves all user group entities from the database.
	 *
	 * @return A list of all user group entities, or an empty list if none are found or an error occurs.
	 */
	public List<UserGroupEntity> findAll() {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT u FROM UserGroupEntity u", UserGroupEntity.class).getResultList();
		} catch (NoResultException e) {
			logErrorMessage(e);
			return List.of();
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Deletes a user group entity from the database.
	 * This method also removes the group from the collections of its associated students.
	 *
	 * @param userGroup The user group entity to delete.
	 */
	public void delete(final UserGroupEntity userGroup) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			userGroup.getStudents().forEach(student -> student.getGroups().remove(userGroup));
			em.remove(em.contains(userGroup) ? userGroup : em.merge(userGroup));
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
	 * Deletes all user group entities from the database.
	 */
	public void deleteAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.createQuery("DELETE FROM UserGroupEntity").executeUpdate();
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
}
