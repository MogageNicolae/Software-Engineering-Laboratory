package bms.persistence.bugs;

import bms.domain.Bug;
import bms.persistence.AbstractRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collection;

public class BugRepository extends AbstractRepository<Bug, Integer> implements IBugRepository {
    public BugRepository() {
    }

    @Override
    protected Bug createQuery(Session session, Integer id) {
        return session.createQuery("from Bug where id = :id", Bug.class)
                .setParameter("id", id)
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    protected Collection<Bug> createQuery(Session session) {
        return session.createQuery("from Bug", Bug.class).list();
    }

    @Override
    public Collection<Bug> getUnsolved() {
        logger.traceEntry("Getting all unsolved entities");

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Collection<Bug> entities = session.createQuery("from Bug where status = 'UNSOLVED'", Bug.class).list();
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
