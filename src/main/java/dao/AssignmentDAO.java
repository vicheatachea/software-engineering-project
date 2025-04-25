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

public class AssignmentDAO {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentDAO.class);
	private static final String ERROR_MESSAGE = "Error: ";
	private static final EntityManagerFactory emf =
			MariaDBConnection.getEntityManagerFactory();

	private void logErrorMessage(final Exception e) {
		logger.error(ERROR_MESSAGE, e);
	}

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
