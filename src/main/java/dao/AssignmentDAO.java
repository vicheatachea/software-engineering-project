package dao;

import entity.AssignmentEntity;
import entity.UserGroupEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.sql.Timestamp;
import java.util.List;

public class AssignmentDAO {

	private static final EntityManagerFactory emf = datasource.MariaDBConnection.getEntityManagerFactory();

	public void persist(AssignmentEntity assignment) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(assignment);
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

	public void update(AssignmentEntity assignment) {
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

	// TODO: Delete this method if unused in the view
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
		assignment = findById(assignment.getId());
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

	// TODO: Delete this method if unused in the view
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

	public List<AssignmentEntity> findAllByTimetableIdDuringPeriod(Long timetableId, Timestamp start,
	                                                               Timestamp end) {
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
