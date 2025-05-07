package dao;

import datasource.MariaDBConnection;
import entity.LocationEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * LocationDAO is a Data Access Object (DAO) class that provides methods to interact with the database
 * for the {@link LocationEntity}. It handles CRUD operations and other specific queries related to locations.
 */
public class LocationDAO {

	private static final String ERROR_MESSAGE = "Error: ";

	private static final Logger logger = LoggerFactory.getLogger(LocationDAO.class);

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
	 * Persists a new location entity to the database.
	 * The method starts a transaction, persists the entity, and commits the transaction.
	 * If an exception occurs, the transaction is rolled back and the error is logged.
	 *
	 * @param location The location entity to be persisted
	 */
	public void persist(final LocationEntity location) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(location);
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
	 * Updates an existing location entity in the database.
	 * The method starts a transaction, merges the entity, and commits the transaction.
	 * If an exception occurs, the transaction is rolled back and the error is logged.
	 *
	 * @param location The location entity to be updated
	 */
	public void update(final LocationEntity location) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(location);
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
	 * Finds a location entity by its ID.
	 *
	 * @param id The ID of the location to find
	 * @return The location entity if found, null otherwise
	 */
	public LocationEntity findById(final Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(LocationEntity.class, id);
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
	 * Retrieves all location entities from the database.
	 *
	 * @return A list of all location entities or an empty list if none are found
	 */
	public List<LocationEntity> findAll() {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT l FROM LocationEntity l", LocationEntity.class).getResultList();
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
	 * Finds a location entity by its name.
	 *
	 * @param name The name of the location to find
	 * @return The location entity if found, null otherwise
	 */
	public LocationEntity findByName(final String name) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT l FROM LocationEntity l WHERE l.name = :name", LocationEntity.class)
			         .setParameter("name", name).getSingleResult();
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
	 * Deletes a location entity from the database.
	 * This method first deletes all teaching sessions associated with the location
	 * and then removes the location entity itself.
	 *
	 * @param location The location entity to be deleted
	 */
	public void delete(LocationEntity location) {
		EntityManager em = emf.createEntityManager();
		location = findByName(location.getName());
		em.getTransaction().begin();
		try {
			em.createQuery("DELETE FROM TeachingSessionEntity ts WHERE ts.location = :location")
			  .setParameter("location", location).executeUpdate();
			em.remove(em.contains(location) ? location : em.merge(location));
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
	 * Deletes all location entities from the database.
	 * This method executes a JPQL query to remove all location entities.
	 */
	public void deleteAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.createQuery("DELETE FROM LocationEntity").executeUpdate();
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
}
