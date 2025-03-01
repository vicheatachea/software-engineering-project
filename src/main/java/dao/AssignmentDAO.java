package dao;

import entity.AssignmentEntity;
import entity.UserGroupEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class AssignmentDAO {

	private static final EntityManagerFactory emf = datasource.MariaDBConnection.getEntityManagerFactory();

	public void persist(AssignmentEntity assignment) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			if (assignment.getId() == null || findById(assignment.getId()) == null) {
				em.persist(assignment);
			} else {
				em.merge(assignment);
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

	public void persistForGroup(AssignmentEntity assignment, String groupName) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			UserGroupEntity group =
					em.createQuery("SELECT g FROM UserGroupEntity g WHERE g.name = :groupName", UserGroupEntity.class)
					  .setParameter("groupName", groupName).getSingleResult();

			assignment.setTimetable(group.getTimetable());

			if (assignment.getId() == null || findById(assignment.getId()) == null) {
				em.persist(assignment);
			} else {
				em.merge(assignment);
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

	public AssignmentEntity findById(Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(AssignmentEntity.class, id);
		} catch (Exception e) {
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
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public void deleteById(Long id) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			AssignmentEntity assignment = em.find(AssignmentEntity.class, id);
			if (assignment != null) {
				em.remove(assignment);
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

	public void delete(AssignmentEntity assignment) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.remove(em.contains(assignment) ? assignment : em.merge(assignment));
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
			em.createQuery("DELETE FROM AssignmentEntity").executeUpdate();
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

	public void deleteByGroupName(AssignmentEntity assignment, String groupName) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			UserGroupEntity group =
					em.createQuery("SELECT g FROM UserGroupEntity g WHERE g.name = :groupName", UserGroupEntity.class)
					  .setParameter("groupName", groupName).getSingleResult();

			if (assignment.getTimetable().getId().equals(group.getTimetable().getId())) {
				em.remove(em.contains(assignment) ? assignment : em.merge(assignment));
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

	public List<AssignmentEntity> findAllByTimetableIdDuringPeriod(Long timetableId, LocalDateTime start,
	                                                               LocalDateTime end) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT a FROM AssignmentEntity a WHERE a.timetable.id = :timetableId AND " +
			                      "a.deadline BETWEEN :start AND :end", AssignmentEntity.class)
			         .setParameter("timetableId", timetableId)
			         .setParameter("start", start)
			         .setParameter("end", end)
			         .getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
}
