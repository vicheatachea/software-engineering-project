package dao;

import datasource.MariaDBConnection;
import entity.SubjectEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * SubjectDAO is a Data Access Object (DAO) class that provides methods to interact with the database
 * for the {@link SubjectEntity}. It handles CRUD operations and other specific queries related to subjects.
 */
public class SubjectDAO {

	private static final String ERROR_MESSAGE = "Error: ";
	private static final Logger logger = LoggerFactory.getLogger(SubjectDAO.class);
	private static final EntityManagerFactory emf = MariaDBConnection.getEntityManagerFactory();
	private static final String SUBJECT = "subject";

	/**
	 * Logs an error message using the configured logger.
	 *
	 * @param e The exception to log.
	 */
	private void logErrorMessage(final Exception e) {
		logger.error(ERROR_MESSAGE, e);
	}

	/**
	 * Persists a new subject entity to the database.
	 * The method starts a transaction, persists the entity, and commits the transaction.
	 * If an exception occurs, the transaction is rolled back and the error is logged.
	 *
	 * @param subject The subject entity to be persisted
	 */
	public void persist(final SubjectEntity subject) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(subject);
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
	 * Updates an existing subject entity in the database.
	 * The method starts a transaction, merges the entity, and commits the transaction.
	 * If an exception occurs, the transaction is rolled back and the error is logged.
	 *
	 * @param subject The subject entity to be updated
	 */
	public void update(final SubjectEntity subject) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(subject);
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
	 * Finds a subject entity by its ID.
	 *
	 * @param id The ID of the subject to find
	 * @return The subject entity if found, null otherwise
	 */
	public SubjectEntity findById(final Long id) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			return em.find(SubjectEntity.class, id);
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
	 * Finds all subject entities associated with a specific user ID.
	 * This includes subjects from groups where the user is a student
	 * and subjects from teaching sessions where the user is a teacher.
	 *
	 * @param userId The ID of the user
	 * @return A list of subject entities or an empty list if none are found
	 */
	public List<SubjectEntity> findAllByUserId(final long userId) {
		EntityManager em = emf.createEntityManager();
		try {
			// This query finds subjects from groups where the user is a student
			// AND subjects from teaching sessions where the user is a teacher
			return em.createQuery("SELECT DISTINCT s FROM SubjectEntity s WHERE " +
			                      "s IN (SELECT g.subject FROM UserGroupEntity g JOIN g.students u WHERE u.id = :userId) " +
			                      "OR " +
			                      "s IN (SELECT g.subject FROM UserGroupEntity g WHERE g.teacher.id = :userId) ",
			                      SubjectEntity.class).setParameter("userId", userId).getResultList();
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
	 * Finds a subject entity by its code.
	 *
	 * @param code The code of the subject to find
	 * @return The subject entity if found, null otherwise
	 */
	public SubjectEntity findByCode(final String code) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			return em.createQuery("SELECT s FROM SubjectEntity s WHERE s.code = :code", SubjectEntity.class)
			         .setParameter("code", code).getSingleResult();
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
	 * Retrieves all subject entities from the database.
	 *
	 * @return A list of all subject entities or an empty list if none are found
	 */
	public List<SubjectEntity> findAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			return em.createQuery("SELECT s FROM SubjectEntity s", SubjectEntity.class).getResultList();
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
	 * Deletes a subject entity and all its related entities from the database.
	 * This method first deletes related assignments, teaching sessions, and user groups,
	 * then removes the subject entity itself.
	 *
	 * @param subject The subject entity to be deleted.
	 */
	public void delete(SubjectEntity subject) {
		EntityManager em = emf.createEntityManager();
		subject = findByCode(subject.getCode());
		em.getTransaction().begin();
		try {
			// Delete related assignments
			em.createQuery("DELETE FROM AssignmentEntity a WHERE a.subject = :subject")
			  .setParameter(SUBJECT, subject).executeUpdate();

			// Delete related teaching sessions
			em.createQuery("DELETE FROM TeachingSessionEntity ts WHERE ts.subject = :subject")
			  .setParameter(SUBJECT, subject).executeUpdate();

			// Delete related user groups
			em.createQuery("DELETE FROM UserGroupEntity g WHERE g.subject = :subject")
			  .setParameter(SUBJECT, subject).executeUpdate();

			// Delete the subject itself
			em.remove(em.contains(subject) ? subject : em.merge(subject));
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
	 * Deletes all subject-related entities and subject entities from the database.
	 * This method first clears all related tables (assignments, teaching sessions, user groups)
	 * and then deletes all subject entities.
	 */
	public void deleteAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.createQuery("DELETE FROM AssignmentEntity").executeUpdate();
			em.createQuery("DELETE FROM TeachingSessionEntity").executeUpdate();
			em.createQuery("DELETE FROM UserGroupEntity").executeUpdate();
			em.createQuery("DELETE FROM SubjectEntity").executeUpdate();
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
