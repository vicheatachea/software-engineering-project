package dao;

import datasource.MariaDBConnection;
import entity.AssignmentEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;

/**
 * AssignmentDAO is a Data Access Object (DAO) class that provides methods to interact with the database
 * for the {@link AssignmentEntity}. It handles CRUD operations and other specific queries related to assignments.
 */
public class AssignmentDAO {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentDAO.class);
	private static final String ERROR_MESSAGE = "Error: ";
	private static final EntityManagerFactory emf =
			MariaDBConnection.getEntityManagerFactory();

	/**
	 * Logs an error message using the configured logger.
	 *
	 * @param e The exception to log.
	 */
	private void logErrorMessage(final Exception e) {
		logger.error(ERROR_MESSAGE, e);
	}

	/**
	 * Persists a new assignment entity to the database.
	 *
	 * @param assignment The assignment entity to persist.
	 */
	public void persist(final AssignmentEntity assignment) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(assignment);
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
	 * Updates an existing assignment entity in the database.
	 *
	 * @param assignment The assignment entity with updated information.
	 */
	public void update(final AssignmentEntity assignment) {
		EntityManager em = emf.createEntityManager();
		AssignmentEntity existingAssignment = em.find(AssignmentEntity.class, assignment.getId());

		if (existingAssignment != null) {
			existingAssignment.setName(assignment.getName());
			existingAssignment.setDescription(assignment.getDescription());
			existingAssignment.setDeadline(assignment.getDeadline());
			existingAssignment.setTimetable(assignment.getTimetable());
		}

		em.getTransaction().begin();
		try {
			em.merge(assignment);
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
	 * Finds an assignment entity by its ID.
	 *
	 * @param id The ID of the assignment to find.
	 * @return The found assignment entity, or null if no assignment is found with the given ID or an error occurs.
	 */
	public AssignmentEntity findById(final Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(AssignmentEntity.class, id);
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
	 * Retrieves all assignment entities from the database.
	 *
	 * @return A list of all assignment entities, or an empty list if no assignments are found or an error occurs.
	 */
	public List<AssignmentEntity> findAll() {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT a FROM AssignmentEntity a", AssignmentEntity.class).getResultList();
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
	 * Deletes an assignment entity from the database.
	 *
	 * @param assignment The assignment entity to delete.
	 */
	public void delete(AssignmentEntity assignment) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		assignment = findById(assignment.getId());
		try {
			em.remove(em.contains(assignment) ? assignment : em.merge(assignment));
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
	 * Deletes all assignment entities from the database.
	 */
	public void deleteAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.createQuery("DELETE FROM AssignmentEntity").executeUpdate();
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
	 * Finds all assignments for a specific timetable ID that fall within a given time period.
	 *
	 * @param timetableId The ID of the timetable.
	 * @param start       The start timestamp of the period.
	 * @param end         The end timestamp of the period.
	 * @return A list of assignment entities matching the criteria, or an empty list if none are found or an error occurs.
	 */
	public List<AssignmentEntity> findAllByTimetableIdDuringPeriod(final Long timetableId, final Timestamp start,
	                                                               final Timestamp end) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT a FROM AssignmentEntity a WHERE a.timetable.id = :timetableId AND " +
			                      "a.deadline BETWEEN :start AND :end", AssignmentEntity.class)
			         .setParameter("timetableId", timetableId).setParameter("start", start).setParameter("end", end)
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
	 * Finds all assignments for a specific timetable ID and locale code that fall within a given time period.
	 *
	 * @param start       The start timestamp of the period.
	 * @param end         The end timestamp of the period.
	 * @param localeCode  The locale code (e.g., "en_US", "fi_FI").
	 * @param timetableId The ID of the timetable.
	 * @return A list of assignment entities matching the criteria, or an empty list if none are found or an error occurs.
	 */
	public List<AssignmentEntity> findAllByLocaleDuringPeriod(final Timestamp start, final Timestamp end,
	                                                          final String localeCode, final Long timetableId) {

		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT a FROM AssignmentEntity a WHERE a.timetable.id = :timetableId AND " +
			                      "a.deadline BETWEEN :start AND :end AND a.localeCode = :localeCode",
			                      AssignmentEntity.class)
			         .setParameter("localeCode", localeCode).setParameter("start", start).setParameter("end", end)
			         .setParameter("timetableId", timetableId).getResultList();
		} catch (NoResultException e) {
			logErrorMessage(e);
			return List.of();
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
}
