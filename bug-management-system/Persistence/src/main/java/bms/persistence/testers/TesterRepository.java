package bms.persistence.testers;

import bms.domain.Bug;
import bms.domain.Tester;
import bms.persistence.AbstractRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collection;

public class TesterRepository extends AbstractRepository<Tester, Integer> implements ITesterRepository {
    public TesterRepository() {
    }

    @Override
    public Tester findByUsername(String username) {
        logger.traceEntry("Finding entity with username {} ", username);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Tester tester = session.createQuery("from Tester where username = :username", Tester.class)
                        .setParameter("username", username)
                        .setMaxResults(1)
                        .uniqueResult();
                transaction.commit();
                logger.traceExit("Successfully found entity");
                return tester;
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
    protected Tester createQuery(Session session, Integer id) {
        return session.createQuery("from Tester where id = :id", Tester.class)
                .setParameter("id", id)
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    protected Collection<Tester> createQuery(Session session) {
        return session.createQuery("from Tester", Tester.class).list();
    }
}
