package dao;

import datasource.MariaDBConnection;
import entity.UserGroupEntity;
import jakarta.persistence.EntityManager;

import java.util.List;

public class UserGroupDAO {

	public void persist(UserGroupEntity userGroup) {
		EntityManager em = MariaDBConnection.getEntityManager();
		em.getTransaction().begin();
		em.persist(userGroup);
		em.getTransaction().commit();
		em.close();
	}

	public void update(UserGroupEntity userGroup) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			UserGroupEntity existingUserGroup = em.find(UserGroupEntity.class, userGroup.getId());
			if (existingUserGroup != null) {
				existingUserGroup.getStudents().forEach(student -> student.getGroups().remove(existingUserGroup));
				userGroup.getStudents().forEach(student -> student.getGroups().add(userGroup));
				em.merge(userGroup);
			} else {
				throw new IllegalArgumentException("UserGroupEntity with id " + userGroup.getId() + " does not exist.");
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
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
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

	public List<UserGroupEntity> findAll() {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
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
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
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
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
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
