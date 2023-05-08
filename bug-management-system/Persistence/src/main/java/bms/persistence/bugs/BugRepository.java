package bms.persistence.bugs;

import bms.domain.Bug;
import bms.persistence.AbstractRepository;
import org.hibernate.Session;

import java.util.Collection;

public class BugRepository extends AbstractRepository<Bug, Integer> implements IBugRepository {
    public BugRepository() {
    }

    @Override
    protected Bug createQuery(Session session, Integer id) {
        return session.createQuery("from bugs where id = :id", Bug.class)
                .setParameter("id", id)
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    protected Collection<Bug> createQuery(Session session) {
        return session.createQuery("from bugs", Bug.class).list();
    }

}
