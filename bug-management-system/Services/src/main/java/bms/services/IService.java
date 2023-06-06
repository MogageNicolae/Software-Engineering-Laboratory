package bms.services;

import bms.domain.Bug;
import bms.domain.Developer;
import bms.domain.Tester;

import java.util.Collection;

public interface IService {
    Tester login(Tester person, IObserver client) throws Exception;
    Developer login(Developer person, IObserver client) throws Exception;
    void logout(Tester person, IObserver client) throws Exception;
    void logout(Developer person, IObserver client) throws Exception;
    void addBug(Bug bug) throws Exception;
    void solveBug(Bug bug) throws Exception;
    void removeBug(Bug bug) throws Exception;
    Collection<Bug> getUnsolvedBugs() throws Exception;
    Collection<Bug> getUnsolvedBugsByTester(int id) throws Exception;
    Collection<Bug> getAllBugs() throws Exception;
}
