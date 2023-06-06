package bms.server.implementation;

import bms.domain.Bug;
import bms.domain.Developer;
import bms.domain.StatusBug;
import bms.domain.Tester;
import bms.persistence.bugs.IBugRepository;
import bms.persistence.developers.IDeveloperRepository;
import bms.persistence.testers.ITesterRepository;
import bms.services.IObserver;
import bms.services.IService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceImpl implements IService {
    private final ITesterRepository testerRepository;
    private final IDeveloperRepository developerRepository;
    private final IBugRepository bugRepository;

    private final Map<String, IObserver> loggedPeople;

    public ServiceImpl(ITesterRepository testerRepository, IDeveloperRepository developerRepository, IBugRepository bugRepository) {
        this.testerRepository = testerRepository;
        this.developerRepository = developerRepository;
        this.bugRepository = bugRepository;

        this.loggedPeople = new ConcurrentHashMap<>();
    }

    @Override
    public Tester login(Tester person, IObserver client) throws Exception {
        Tester testerToLogin = testerRepository.findByUsername(person.getUsername());
        if (testerToLogin != null) {
            if (loggedPeople.get(testerToLogin.getUsername()) != null)
                throw new Exception("Tester already logged in.");

            loggedPeople.put(testerToLogin.getUsername(), client);
            return testerToLogin;
        }
        return null;
    }

    @Override
    public Developer login(Developer person, IObserver client) throws Exception {
        Developer developerToLogin = developerRepository.findByUsername(person.getUsername());
        if (developerToLogin != null) {
            if (loggedPeople.get(developerToLogin.getUsername()) != null)
                throw new Exception("Developer already logged in.");

            loggedPeople.put(developerToLogin.getUsername(), client);
            return developerToLogin;
        }
        return null;
    }

    @Override
    public void logout(Tester person, IObserver client) throws Exception {
        IObserver loggedPerson = loggedPeople.remove(person.getUsername());
        if (loggedPerson == null) {
            throw new Exception("Tester " + person.getId().toString() + " is not logged in.");
        }
    }

    @Override
    public void logout(Developer person, IObserver client) throws Exception {
        IObserver loggedPerson = loggedPeople.remove(person.getUsername());
        if (loggedPerson == null) {
            throw new Exception("Developer " + person.getId().toString() + " is not logged in.");
        }
    }

    @Override
    public void addBug(Bug bug) throws Exception {
        bugRepository.add(bug);
        for (IObserver person : loggedPeople.values()) {
            person.bugListChanged(bugRepository.getUnsolved());
        }
    }

    @Override
    public void solveBug(Bug bug) throws Exception {
        Bug updatedBug = new Bug(bug.getId(), bug.getName(), bug.getDescription(), LocalDateTime.now(), bug.getSeverity(), bug.getTesterId(), StatusBug.SOLVED);
        bugRepository.update(updatedBug, bug.getId());
        for (IObserver person : loggedPeople.values()) {
            person.bugListChanged(bugRepository.getUnsolved());
        }
    }

    @Override
    public void removeBug(Bug bug) throws Exception {
        bugRepository.delete(bug);
        for (IObserver person : loggedPeople.values()) {
            person.bugListChanged(bugRepository.getUnsolved());
        }
    }

    @Override
    public Collection<Bug> getUnsolvedBugs() {
        return bugRepository.getUnsolved();
    }

    @Override
    public Collection<Bug> getUnsolvedBugsByTester(int id) {
        return bugRepository.getUnsolvedByTester(id);
    }

    @Override
    public Collection<Bug> getAllBugs() {
        return bugRepository.getAll();
    }
}
