package dao;

import entity.TeachingSessionEntity;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TeachingSessionDAO {
	public void persist(TeachingSessionEntity teachingSession) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			if (teachingSession.getId() == null || findById(teachingSession.getId()) == null) {
				em.persist(teachingSession);
			} else {
				em.merge(teachingSession);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
	
	public TeachingSessionEntity findById(Long id) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		try {
			return em.find(TeachingSessionEntity.class, id);
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<TeachingSessionEntity> findAll() {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t", TeachingSessionEntity.class).getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public void delete(TeachingSessionEntity teachingSession) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			em.remove(em.contains(teachingSession) ? teachingSession : em.merge(teachingSession));
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public TeachingSessionEntity findBySubjectId(Long id) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.subject.id = :id", TeachingSessionEntity.class)
			         .setParameter("id", id).getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public TeachingSessionEntity findByLocationId(Long id) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.location.id = :id", TeachingSessionEntity.class)
			         .setParameter("id", id).getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<TeachingSessionEntity> findAllByTimetableId(Long id) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.timetable.id = :id", TeachingSessionEntity.class)
			         .setParameter("id", id).getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public void deleteAll() {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			em.createQuery("DELETE FROM TeachingSessionEntity").executeUpdate();
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
}
