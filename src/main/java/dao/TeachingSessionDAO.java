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

public class TeachingSessionDAO {

	private static final EntityManagerFactory emf = MariaDBConnection.getEntityManagerFactory();
	private static final Logger logger = LoggerFactory.getLogger(TeachingSessionDAO.class);

	private static final String ERROR_MESSAGE = "Error: ";

	private void logErrorMessage(final Exception e) {
		logger.error(ERROR_MESSAGE, e);
	}

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
