package dao;

import entity.SubjectEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class SubjectDAO {

	private static final EntityManagerFactory emf = datasource.MariaDBConnection.getEntityManagerFactory();

	public void persist(SubjectEntity subject) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(subject);
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

	public void update(SubjectEntity subject) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.merge(subject);
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

	public SubjectEntity findById(Long id) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			return em.find(SubjectEntity.class, id);
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<SubjectEntity> findAllByUserId(long userId) {
		EntityManager em = emf.createEntityManager();
		try {
			// This query finds subjects from groups where the user is a student
			// AND subjects from teaching sessions where the user is a teacher
			return em.createQuery(
					         "SELECT DISTINCT s FROM SubjectEntity s WHERE " +
					         "s IN (SELECT g.subject FROM UserGroupEntity g JOIN g.students u WHERE u.id = :userId) " +
					         "OR " +
					         "s IN (SELECT g.subject FROM UserGroupEntity g WHERE g.teacher.id = :userId) ",
					         SubjectEntity.class)
			         .setParameter("userId", userId)
			         .getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public SubjectEntity findByCode(String code) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			return em.createQuery("SELECT s FROM SubjectEntity s WHERE s.code = :code", SubjectEntity.class)
			         .setParameter("code", code)
			         .getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<SubjectEntity> findAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			return em.createQuery("SELECT s FROM SubjectEntity s", SubjectEntity.class).getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public void delete(SubjectEntity subjectEntity) {
		EntityManager em = emf.createEntityManager();
		subjectEntity = findByCode(subjectEntity.getCode());
		em.getTransaction().begin();
		try {
			// Delete related assignments
			em.createQuery("DELETE FROM AssignmentEntity a WHERE a.subject = :subject")
			  .setParameter("subject", subjectEntity).executeUpdate();

			// Delete related teaching sessions
			em.createQuery("DELETE FROM TeachingSessionEntity ts WHERE ts.subject = :subject")
			  .setParameter("subject", subjectEntity).executeUpdate();

			// Delete related user groups
			em.createQuery("DELETE FROM UserGroupEntity g WHERE g.subject = :subject")
			  .setParameter("subject", subjectEntity).executeUpdate();

			// Delete the subject itself
			em.remove(em.contains(subjectEntity) ? subjectEntity : em.merge(subjectEntity));
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
			em.createQuery("DELETE FROM AssignmentEntity").executeUpdate();
			em.createQuery("DELETE FROM TeachingSessionEntity").executeUpdate();
			em.createQuery("DELETE FROM UserGroupEntity").executeUpdate();
			em.createQuery("DELETE FROM SubjectEntity").executeUpdate();
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
