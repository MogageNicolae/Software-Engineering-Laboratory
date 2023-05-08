package bms.persistence.developers;

import bms.domain.Developer;
import bms.persistence.Repository;

public interface IDeveloperRepository extends Repository<Developer, Integer> {
    Developer findByUsername(String username);
}
