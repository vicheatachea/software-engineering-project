package dao;

import entity.LocationEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class LocationDAO {

	private static final EntityManagerFactory emf = datasource.MariaDBConnection.getEntityManagerFactory();

	public void persist(LocationEntity location) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(location);
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

	public void update(LocationEntity location) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(location);
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

	public LocationEntity findById(Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(LocationEntity.class, id);
		} catch (Exception e) {
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
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public LocationEntity findByName(String name) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT l FROM LocationEntity l WHERE l.name = :name", LocationEntity.class)
			         .setParameter("name", name)
			         .getSingleResult();
		} catch (Exception e) {
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
			em.createQuery("DELETE FROM LocationEntity").executeUpdate();
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
