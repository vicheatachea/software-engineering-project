package dao;

import datasource.MariaDBConnection;
import entity.LocationEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocationDAO {

	private static final String ERROR_MESSAGE = "Error: ";

	private static final Logger logger = LoggerFactory.getLogger(LocationDAO.class);

	private static final EntityManagerFactory emf =
			MariaDBConnection.getEntityManagerFactory();


	private void logErrorMessage(final Exception e) {
		logger.error(ERROR_MESSAGE, e);
	}

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
