package dao;

import entity.UserEntity;
import jakarta.persistence.EntityManager;

import static util.PasswordHashUtil.verifyPassword;

public class UserDAO {

	public void persist(UserEntity user) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(user);
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

	public UserEntity findByUsername(String username) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		UserEntity user;
		try {
			user = em.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class)
			         .setParameter("username", username)
			         .getSingleResult();
			if (!authenticate(username, user.getPassword())) {
				return null;
			}
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		return user;
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
}
