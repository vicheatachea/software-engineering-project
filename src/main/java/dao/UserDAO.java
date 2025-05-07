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

/**
 * UserDAO is a Data Access Object (DAO) class that provides methods to interact with the database
 * for the {@link UserEntity}. It handles CRUD operations and other specific queries related to users,
 * including authentication.
 */
public class UserDAO {

	private static final String ERROR_MESSAGE = "Error: ";
	private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

	private static final EntityManagerFactory emf =
			MariaDBConnection.getEntityManagerFactory();

	/**
	 * Logs an error message using the configured logger.
	 *
	 * @param e The exception to log.
	 */
	private void logErrorMessage(final Exception e) {
		logger.error(ERROR_MESSAGE, e);
	}

	/**
	 * Persists a new user entity to the database.
	 * The method starts a transaction, persists the entity, and commits the transaction.
	 * If an exception occurs, the transaction is rolled back and the error is logged.
	 *
	 * @param user The user entity to be persisted
	 */
	public void persist(final UserEntity user) {
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

	/**
	 * Updates an existing user entity in the database.
	 * The method starts a transaction, merges the entity, and commits the transaction.
	 * If an exception occurs, the transaction is rolled back and the error is logged.
	 *
	 * @param user The user entity to be updated
	 */
	public void update(final UserEntity user) {
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

	/**
	 * Finds a user entity by its ID.
	 *
	 * @param id The ID of the user to find
	 * @return The user entity if found, null otherwise
	 */
	public UserEntity findById(final Long id) {
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

	/**
	 * Finds a teacher entity by its ID.
	 * This method specifically looks for users with the 'TEACHER' role.
	 *
	 * @param id The ID of the teacher to find
	 * @return The teacher entity if found, null otherwise
	 */
	public UserEntity findTeacherById(final Long id) {
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

	/**
	 * Finds a user entity by its username.
	 *
	 * @param username The username of the user to find
	 * @return The user entity if found, null otherwise
	 */
	public UserEntity findByUsername(final String username) {
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

	/**
	 * Retrieves all user entities from the database.
	 *
	 * @return A list of all user entities or an empty list if none are found
	 */
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

	/**
	 * Authenticates a user by verifying their username and password.
	 * Uses the PasswordHashUtil to verify the hashed password with the provided salt.
	 *
	 * @param username The username of the user trying to authenticate
	 * @param password The password to verify
	 * @return The authenticated user entity if credentials are valid, null otherwise
	 */
	public UserEntity authenticate(final String username, final String password) {
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

	/**
	 * Deletes a user entity from the database.
	 * The method first finds the user by username to ensure it exists,
	 * then starts a transaction, removes the entity, and commits the transaction.
	 * If an exception occurs, the transaction is rolled back and the error is logged.
	 *
	 * @param user The user entity to be deleted
	 */
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

	/**
	 * Deletes all user entities from the database.
	 * This method executes a JPQL query to remove all user entities.
	 */
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

	/**
	 * Checks if a user with the given social number exists in the database.
	 *
	 * @param socialNumber The social number to check
	 * @return true if a user with the given social number exists, false otherwise
	 */
	public boolean findBySocialNumber(final String socialNumber) {
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
