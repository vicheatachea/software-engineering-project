package dao;

import datasource.MariaDBConnection;
import entity.UserGroupEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserGroupDAO {

    private static final String ERROR_MESSAGE = "Error: ";
    private static final Logger logger = LoggerFactory.getLogger(UserGroupDAO.class);

    private static final EntityManagerFactory emf = MariaDBConnection.getEntityManagerFactory();

    private void logErrorMessage(final Exception e) {
        logger.error(ERROR_MESSAGE, e);
    }

    public void persist(final UserGroupEntity userGroup) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            if (userGroup.getId() == null || findById(userGroup.getId()) == null) {
                em.persist(userGroup);
            } else {
                em.merge(userGroup);
            }
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

    public UserGroupEntity findById(final Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(UserGroupEntity.class, id);
        } catch (NoResultException e) {
            logErrorMessage(e);
            return null;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public UserGroupEntity findByTimetableId(final long timetableId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT g FROM UserGroupEntity g WHERE g.timetable.id = :timetableId",
                            UserGroupEntity.class)
                    .setParameter("timetableId", timetableId)
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

    public UserGroupEntity findByName(final String groupName) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT g FROM UserGroupEntity g WHERE g.name = :groupName", UserGroupEntity.class)
                    .setParameter("groupName", groupName)
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

    public List<UserGroupEntity> findAllByUserId(final Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT g FROM UserGroupEntity g "
                                    + "WHERE g.teacher.id = :userId "
                                    + "OR "
                                    + "g IN (SELECT g2 FROM UserGroupEntity g2 JOIN g2.students s WHERE s.id = :userId)",
                            UserGroupEntity.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (NoResultException e) {
            logErrorMessage(e);
            return List.of();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<UserGroupEntity> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM UserGroupEntity u", UserGroupEntity.class).getResultList();
        } catch (NoResultException e) {
            logErrorMessage(e);
            return List.of();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public void delete(final UserGroupEntity userGroup) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            userGroup.getStudents().forEach(student -> student.getGroups().remove(userGroup));
            em.remove(em.contains(userGroup) ? userGroup : em.merge(userGroup));
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
            em.createQuery("DELETE FROM UserGroupEntity").executeUpdate();
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
