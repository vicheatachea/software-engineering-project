package dao;

import entity.TimetableEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TimetableDAO {

	private static final EntityManagerFactory emf = datasource.MariaDBConnection.getEntityManagerFactory();

	public void persist(TimetableEntity timetable) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(timetable);
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

	public List<TimetableEntity> findAll() {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT t FROM TimetableEntity t", TimetableEntity.class).getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<TimetableEntity> findAllByUserId(Long userId) {
		EntityManager em = emf.createEntityManager();
		try {
			List<TimetableEntity> timetables = em.createQuery(
					                                     "SELECT u.timetable FROM UserEntity u WHERE u.id = :userId",
					                                     TimetableEntity.class)
			                                     .setParameter("userId", userId)
			                                     .getResultList();

			List<TimetableEntity> groupTimetables = em.createQuery(
					                                          "SELECT g.timetable FROM UserGroupEntity g JOIN g.students s WHERE s.id = :userId",
					                                          TimetableEntity.class)
			                                          .setParameter("userId", userId)
			                                          .getResultList();

			timetables.addAll(groupTimetables);

			List<TimetableEntity> teacherTimetables = em.createQuery(
					                                            "SELECT g.timetable FROM UserGroupEntity g WHERE g.teacher.id = :userId",
					                                            TimetableEntity.class)
			                                            .setParameter("userId", userId)
			                                            .getResultList();

			timetables.addAll(teacherTimetables);

			return timetables;
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}


	public TimetableEntity findById(Long id) {
		EntityManager em = emf.createEntityManager();
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
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		try {
			// Delete related assignments
			em.createQuery("DELETE FROM AssignmentEntity a WHERE a.timetable = :timetable")
			  .setParameter("timetable", timetable).executeUpdate();

			// Delete related teaching sessions
			em.createQuery("DELETE FROM TeachingSessionEntity ts WHERE ts.timetable = :timetable")
			  .setParameter("timetable", timetable).executeUpdate();

			// Delete related user
			em.createQuery("DELETE FROM UserEntity u WHERE u.timetable = :timetable")
			  .setParameter("timetable", timetable).executeUpdate();

			// Delete related user group
			em.createQuery("DELETE FROM UserGroupEntity ug WHERE ug.timetable = :timetable")
			  .setParameter("timetable", timetable).executeUpdate();

			// Finally, delete the timetable
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

	public void deleteAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			// Delete related user group
			em.createQuery("DELETE FROM UserGroupEntity").executeUpdate();
			// Delete related assignments
			em.createQuery("DELETE FROM AssignmentEntity").executeUpdate();
			// Delete related teaching sessions
			em.createQuery("DELETE FROM TeachingSessionEntity").executeUpdate();
			// Delete related user
			em.createQuery("DELETE FROM UserEntity").executeUpdate();
			// Finally, delete the timetable
			em.createQuery("DELETE FROM TimetableEntity").executeUpdate();
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

	public TimetableEntity findByUserId(long userId) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT u.timetable FROM UserEntity u WHERE u.id = :userId", TimetableEntity.class)
			         .setParameter("userId", userId)
			         .getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public TimetableEntity findByGroupName(String groupName) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT g.timetable FROM UserGroupEntity g WHERE g.name = :groupName",
			                      TimetableEntity.class)
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
}
