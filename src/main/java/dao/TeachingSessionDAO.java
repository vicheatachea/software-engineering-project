package dao;

import datasource.MariaDBConnection;
import entity.TeachingSessionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;

/**
 * TeachingSessionDAO is a Data Access Object (DAO) class that provides methods to interact with the database
 * for the {@link TeachingSessionEntity}. It handles CRUD operations and other specific queries related to teaching sessions.
 */
public class TeachingSessionDAO {

	private static final EntityManagerFactory emf = MariaDBConnection.getEntityManagerFactory();
	private static final Logger logger = LoggerFactory.getLogger(TeachingSessionDAO.class);

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
	 * Persists a new teaching session entity to the database.
	 *
	 * @param teachingSession The teaching session entity to persist.
	 */
	public void persist(final TeachingSessionEntity teachingSession) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(teachingSession);
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
	 * Updates an existing teaching session entity in the database.
	 *
	 * @param entity The teaching session entity with updated information.
	 * @throws IllegalArgumentException if the teaching session to update is not found.
	 */
	public void update(final TeachingSessionEntity entity) {
		EntityManager em = emf.createEntityManager();
		TeachingSessionEntity teachingSession = em.find(TeachingSessionEntity.class, entity.getId());

		if (teachingSession == null) {
			throw new IllegalArgumentException("Teaching session not found");
		}

		if (entity.getStartDate() != teachingSession.getStartDate()) {
			teachingSession.setStartDate(entity.getStartDate());
		}
		if (entity.getEndDate() != teachingSession.getEndDate()) {
			teachingSession.setEndDate(entity.getEndDate());
		}
		if (entity.getDescription() != null) {
			teachingSession.setDescription(entity.getDescription());
		}
		if (entity.getLocation() != null) {
			teachingSession.setLocation(entity.getLocation());
		}
		if (entity.getTimetable() != teachingSession.getTimetable()) {
			teachingSession.setTimetable(entity.getTimetable());
		}
		if (entity.getSubject() != teachingSession.getSubject()) {
			teachingSession.setSubject(entity.getSubject());
		}

		em.getTransaction().begin();
		try {
			em.merge(entity);
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
	 * Finds a teaching session entity by its ID.
	 *
	 * @param id The ID of the teaching session to find.
	 * @return The found teaching session entity, or null if not found or an error occurs.
	 */
	public TeachingSessionEntity findById(final Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(TeachingSessionEntity.class, id);
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
	 * Retrieves all teaching session entities from the database.
	 *
	 * @return A list of all teaching session entities, or an empty list if none are found or an error occurs.
	 */
	public List<TeachingSessionEntity> findAll() {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t", TeachingSessionEntity.class).getResultList();
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
	 * Deletes a teaching session entity from the database.
	 *
	 * @param teachingSession The teaching session entity to delete.
	 */
	public void delete(final TeachingSessionEntity teachingSession) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.remove(em.contains(teachingSession) ? teachingSession : em.merge(teachingSession));
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
	 * Finds a teaching session entity by its associated subject ID.
	 *
	 * @param id The ID of the subject.
	 * @return The found teaching session entity, or null if not found or an error occurs.
	 */
	public TeachingSessionEntity findBySubjectId(final Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.subject.id = :id",
			                      TeachingSessionEntity.class).setParameter("id", id).getSingleResult();
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
	 * Finds a teaching session entity by its associated location ID.
	 *
	 * @param id The ID of the location.
	 * @return The found teaching session entity, or null if not found or an error occurs.
	 */
	public TeachingSessionEntity findByLocationId(final Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.location.id = :id",
			                      TeachingSessionEntity.class).setParameter("id", id).getSingleResult();
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
	 * Finds all teaching session entities associated with a specific timetable ID.
	 *
	 * @param id The ID of the timetable.
	 * @return A list of teaching session entities for the given timetable ID, or an empty list if none are found or an error occurs.
	 */
	public List<TeachingSessionEntity> findAllByTimetableId(final Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.timetable.id = :id",
			                      TeachingSessionEntity.class).setParameter("id", id).getResultList();
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
	 * Deletes all teaching session entities from the database.
	 */
	public void deleteAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.createQuery("DELETE FROM TeachingSessionEntity").executeUpdate();
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
	 * Finds all teaching sessions for a specific timetable ID that fall within a given time period.
	 *
	 * @param id    The ID of the timetable.
	 * @param start The start timestamp of the period.
	 * @param end   The end timestamp of the period.
	 * @return A list of teaching session entities matching the criteria, or an empty list if none are found or an error occurs.
	 */
	public List<TeachingSessionEntity> findAllByTimetableIdDuringPeriod(final Long id, final Timestamp start,
	                                                                    final Timestamp end) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.timetable.id = :id AND t.startDate " +
			                      "BETWEEN :start AND :end", TeachingSessionEntity.class).setParameter("id", id)
			         .setParameter("start", start).setParameter("end", end).getResultList();
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
	 * Finds all teaching sessions for a specific locale and timetable ID that fall within a given time period.
	 *
	 * @param start       The start timestamp of the period.
	 * @param end         The end timestamp of the period.
	 * @param localeCode  The locale code (e.g., "en_US", "fi_FI").
	 * @param timetableId The ID of the timetable.
	 * @return A list of teaching session entities matching the criteria, or an empty list if none are found or an error occurs.
	 */
	public List<TeachingSessionEntity> findAllByLocaleDuringPeriod(final Timestamp start, final Timestamp end,
	                                                               final String localeCode, final Long timetableId) {
		EntityManager em = emf.createEntityManager();

		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.timetable.id = :timetableId AND " +
			                      "t.startDate BETWEEN :start AND :end AND t.localeCode = :localeCode",
			                      TeachingSessionEntity.class)
			         .setParameter("localeCode", localeCode)
			         .setParameter("start", start)
			         .setParameter("end", end)
			         .setParameter("timetableId", timetableId)
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
}
