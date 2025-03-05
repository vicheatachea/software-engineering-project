package dao;

import entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

import java.util.List;

import static util.PasswordHashUtil.verifyPassword;

public class UserDAO {

	private static final EntityManagerFactory emf = datasource.MariaDBConnection.getEntityManagerFactory();

	public void persist(UserEntity user) {
		EntityManager em = emf.createEntityManager();
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

	public UserEntity findById(Long id) {
		EntityManager em = emf.createEntityManager();
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

	public UserEntity findTeacherById(Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em
					.createQuery("SELECT u FROM UserEntity u WHERE u.id = :id AND u.role = 'TEACHER'", UserEntity.class)
					.setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public UserEntity findStudentById(Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em
					.createQuery("SELECT u FROM UserEntity u WHERE u.id = :id AND u.role = 'STUDENT'", UserEntity.class)
					.setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
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
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public List<UserEntity> findAllStudents() {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT u FROM UserEntity u WHERE u.role = 'STUDENT'", UserEntity.class)
			         .getResultList();
		} catch (Exception e) {
			return null;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public UserEntity authenticate(String username, String password) {
		UserEntity user = findByUsername(username);
		if (user == null) {
			throw new IllegalArgumentException("User not found");
		}
		boolean verified = verifyPassword(password, user.getPassword(), user.getSalt());

		if (!verified) {
			throw new IllegalArgumentException("Invalid password");
		}

		return user;
	}

	public void delete(UserEntity user) {
		EntityManager em = emf.createEntityManager();
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
		EntityManager em = emf.createEntityManager();
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
