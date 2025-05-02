package dao;

import datasource.MariaDBConnection;
import entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static util.PasswordHashUtil.verifyPassword;

public class UserDAO {

	private static final String ERROR_MESSAGE = "Error: ";
	private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

	private static final EntityManagerFactory emf =
			MariaDBConnection.getEntityManagerFactory();

	private void logErrorMessage(final Exception e) {
		logger.error(ERROR_MESSAGE, e);
	}

	public void persist(UserEntity user) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(user);
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

	public void update(UserEntity user) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(user);
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

	public UserEntity findById(Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(UserEntity.class, id);
		} catch (NoResultException e) {
			logErrorMessage(e);
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public UserEntity findTeacherById(Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em
					.createQuery("SELECT u FROM UserEntity u WHERE u.id = :id AND u.role = 'TEACHER'", UserEntity.class)
					.setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
			logErrorMessage(e);
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public UserEntity findByUsername(String username) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class)
			         .setParameter("username", username)
			         .getSingleResult();
		} catch (NoResultException e) {
			logErrorMessage(e);
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<UserEntity> findAll() {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();
		} catch (NoResultException e) {
			logErrorMessage(e);
			return List.of();
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public UserEntity authenticate(String username, String password) {
		UserEntity user = findByUsername(username);
		if (user == null) {
			return null;
		}
		
		boolean verified = verifyPassword(password, user.getPassword(), user.getSalt());

		if (!verified) {
			return null;
		}

		return user;
	}

	public void delete(UserEntity user) {
		EntityManager em = emf.createEntityManager();
		user = findByUsername(user.getUsername());
		em.getTransaction().begin();
		try {
			em.remove(em.contains(user) ? user : em.merge(user));
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

	public void deleteAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.createQuery("DELETE FROM UserEntity").executeUpdate();
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

	public boolean findBySocialNumber(String socialNumber) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT u FROM UserEntity u WHERE u.socialNumber = :socialNumber", UserEntity.class)
			         .setParameter("socialNumber", socialNumber)
			         .getSingleResult() != null;
		} catch (NoResultException e) {
			logErrorMessage(e);
			return false;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
}
