package dao;

import entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

import static util.PasswordHashUtil.verifyPassword;

public class UserDAO {

	public void persist(UserEntity user) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			if (user.getId() == null || findById(user.getId()) == null) {
				em.persist(user);
			} else {
				em.merge(user);
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
	
	private UserEntity findById(Long id) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		try {
			return em.find(UserEntity.class, id);
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public UserEntity findByUsername(String username) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		try {
			return em.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class)
			         .setParameter("username", username)
			         .getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<UserEntity> findAll() {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		try {
			return em.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public boolean authenticate(String username, String password) {
		UserEntity user = findByUsername(username);
		if (user == null) {
			return false;
		}
		return verifyPassword(password, user.getPassword(), user.getSalt());
	}

	public void delete(UserEntity user) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			em.remove(em.contains(user) ? user : em.merge(user));
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
			em.createQuery("DELETE FROM UserEntity").executeUpdate();
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
