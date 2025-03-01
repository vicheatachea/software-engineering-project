package dao;

import datasource.MariaDBConnection;
import entity.UserGroupEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class UserGroupDAO {

	private static final EntityManagerFactory emf = MariaDBConnection.getEntityManagerFactory();

	public void persist(UserGroupEntity userGroup) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			if (userGroup.getId() == null || findById(userGroup.getId()) == null) {
				em.persist(userGroup);
			} else {
				em.merge(userGroup);
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

	public UserGroupEntity findById(Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(UserGroupEntity.class, id);
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public UserGroupEntity findByName(String groupName) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT g FROM UserGroupEntity g WHERE g.name = :groupName", UserGroupEntity.class)
					.setParameter("groupName", groupName)
					.getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<UserGroupEntity> findAllByUserId(Long userId) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT g FROM UserGroupEntity g JOIN g.students s WHERE s.id = :userId", UserGroupEntity.class)
					.setParameter("userId", userId)
					.getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<UserGroupEntity> findAll() {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT u FROM UserGroupEntity u", UserGroupEntity.class).getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public void delete(UserGroupEntity userGroup) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			userGroup.getStudents().forEach(student -> student.getGroups().remove(userGroup));
			em.remove(em.contains(userGroup) ? userGroup : em.merge(userGroup));
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
			em.createQuery("DELETE FROM UserGroupEntity").executeUpdate();
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
