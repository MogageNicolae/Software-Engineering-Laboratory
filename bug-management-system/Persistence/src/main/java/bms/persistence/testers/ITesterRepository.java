package bms.persistence.testers;

import bms.domain.Tester;
import bms.persistence.Repository;

public interface ITesterRepository extends Repository<Tester, Integer> {
    Tester findByUsername(String username);
}
