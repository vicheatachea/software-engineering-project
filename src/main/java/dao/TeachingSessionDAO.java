package dao;

import entity.TeachingSessionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.sql.Timestamp;
import java.util.List;

public class TeachingSessionDAO {

	private static final EntityManagerFactory emf = datasource.MariaDBConnection.getEntityManagerFactory();

	public void persist(TeachingSessionEntity teachingSession) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(teachingSession);
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

	public void update(TeachingSessionEntity entity) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(entity);
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
		EntityManager em = emf.createEntityManager();
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
		EntityManager em = emf.createEntityManager();
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
		EntityManager em = emf.createEntityManager();
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
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.subject.id = :id",
			                      TeachingSessionEntity.class).setParameter("id", id).getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public TeachingSessionEntity findByLocationId(Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.location.id = :id",
			                      TeachingSessionEntity.class).setParameter("id", id).getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<TeachingSessionEntity> findAllByTimetableId(Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.timetable.id = :id",
			                      TeachingSessionEntity.class).setParameter("id", id).getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public void deleteById(long id) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			TeachingSessionEntity teachingSession = findById(id);
			if (teachingSession != null) {
				em.remove(em.contains(teachingSession) ? teachingSession : em.merge(teachingSession));
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

	public void deleteAll() {
		EntityManager em = emf.createEntityManager();
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

	public List<TeachingSessionEntity> findAllByTimetableIdDuringPeriod(Long id, Timestamp start,
	                                                                    Timestamp end) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TeachingSessionEntity t WHERE t.timetable.id = :id AND t.StartDate " +
			                      "BETWEEN :start AND :end", TeachingSessionEntity.class).setParameter("id", id)
			         .setParameter("start", start).setParameter("end", end).getResultList();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}


}
