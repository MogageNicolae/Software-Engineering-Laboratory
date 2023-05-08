package bms.persistence.developers;

import bms.domain.Developer;
import bms.persistence.AbstractRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collection;

public class DeveloperRepository extends AbstractRepository<Developer, Integer> implements IDeveloperRepository {
    public DeveloperRepository() {
    }

    @Override
    public Developer findByUsername(String username) {
        logger.traceEntry("Finding entity with username {} ", username);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Developer developer = session.createQuery("from Developer where username = :username", Developer.class)
                        .setParameter("username", username)
                        .setMaxResults(1)
                        .uniqueResult();
                transaction.commit();
                logger.traceExit("Successfully found entity");
                return developer;
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
    protected Developer createQuery(Session session, Integer id) {
        return session.createQuery("from Developer where id = :id", Developer.class)
                .setParameter("id", id)
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    protected Collection<Developer> createQuery(Session session) {
        return session.createQuery("from Developer", Developer.class).list();
    }
}
