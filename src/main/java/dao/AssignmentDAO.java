package dao;

import entity.AssignmentEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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
}
