package dao;

import entity.TimetableEntity;
import jakarta.persistence.EntityManager;

public class TimetableDAO {

	public void persist(TimetableEntity timetable) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(timetable);
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

	public TimetableEntity findById(Long id) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		try {
			return em.find(TimetableEntity.class, id);
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public void delete(TimetableEntity timetable) {
		EntityManager em = datasource.MariaDBConnection.getEntityManager();
		em.getTransaction().begin();
		try {
			em.remove(em.contains(timetable) ? timetable : em.merge(timetable));
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
