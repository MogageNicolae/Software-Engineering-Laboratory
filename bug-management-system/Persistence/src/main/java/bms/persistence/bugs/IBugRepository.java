package bms.persistence.bugs;

import bms.domain.Bug;
import bms.persistence.Repository;

import java.util.Collection;

public interface IBugRepository extends Repository<Bug, Integer> {
    Collection<Bug> getUnsolved();
    Collection<Bug> getUnsolvedByTester(int id);
}
