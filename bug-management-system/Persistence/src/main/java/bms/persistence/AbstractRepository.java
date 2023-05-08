package bms.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bms.domain.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


import java.util.Collection;

public abstract class AbstractRepository<T extends Entity<ID>, ID> implements Repository<T, ID> {
    protected static SessionFactory sessionFactory;
    protected static final Logger logger = LogManager.getLogger();

    // Class constructors //

    public AbstractRepository() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            logger.error(e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Override
    public boolean add(T elem) {
        logger.traceEntry("Adding entity {} ", elem);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(elem);
//                session.persist(elem);
                transaction.commit();
                logger.traceExit("Successfully added entity");
                return true;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("Error while adding entity: " + ex);
            }
        }

        logger.traceExit("Error");
        return false;
    }

    @Override
    public boolean delete(T elem) {
        logger.traceEntry("Deleting entity {} ", elem);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.delete(elem);
//                session.remove(elem);
                transaction.commit();
                logger.traceExit("Successfully deleted entity");
                return true;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("Error while deleting entity: " + ex);
            }
        }
        logger.traceExit("Error");
        return false;
    }

    @Override
    public boolean update(T elem, ID id) {
        logger.traceEntry("Updating entity {} ", elem);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.update(elem);
//                session.merge(elem);
                transaction.commit();
                logger.traceExit("Successfully updated entity");
                return true;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("Error while updating entity: " + ex);
            }
        }

        logger.traceExit("Error");
        return false;
    }

    protected abstract T createQuery(Session session, ID id);
    protected abstract Collection<T> createQuery(Session session);

    @Override
    public T findById(ID id) {
        logger.traceEntry("Finding entity with id {} ", id);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                T entity = createQuery(session, id);
//                        session.createQuery("from bugs where id = :id", T.class)
//                        .setParameter("id", id)
//                        .setMaxResults(1)
//                        .uniqueResult();
                transaction.commit();
                logger.traceExit("Successfully found entity");
                return entity;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("Error while finding entity: " + ex);
            }
        }

        logger.traceExit("Error");
        return null;
    }

    @Override
    public Collection<T> getAll() {
        logger.traceEntry("Getting all entities");

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
//                Collection<T> bugs = session.createQuery("from bugs", T.class).list();
                Collection<T> entities = createQuery(session);
                transaction.commit();
                logger.traceExit(entities);
                return entities;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("Error while finding entity: " + ex);
            }
        }

        logger.traceExit("Error");
        return null;
    }
}
