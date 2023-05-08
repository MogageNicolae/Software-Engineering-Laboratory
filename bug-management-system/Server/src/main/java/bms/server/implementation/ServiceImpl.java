package bms.server.implementation;

import bms.domain.Developer;
import bms.domain.Tester;
import bms.persistence.developers.IDeveloperRepository;
import bms.persistence.testers.ITesterRepository;
import bms.services.IObserver;
import bms.services.IService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceImpl implements IService {
    private final ITesterRepository testerRepository;
    private final IDeveloperRepository developerRepository;

    private final Map<Integer, IObserver> loggedPeople;

    public ServiceImpl(ITesterRepository testerRepository, IDeveloperRepository developerRepository) {
        this.testerRepository = testerRepository;
        this.developerRepository = developerRepository;

        this.loggedPeople = new ConcurrentHashMap<>();
    }

    @Override
    public Tester login(Tester person, IObserver client) throws Exception {
        Tester testerToLogin = testerRepository.findByUsername(person.getUsername());
        if (testerToLogin != null) {
            if (loggedPeople.get(testerToLogin.getId()) != null)
                throw new Exception("Tester already logged in.");

            loggedPeople.put(testerToLogin.getId(), client);
            return testerToLogin;
        }
        return null;
    }

    @Override
    public Developer login(Developer person, IObserver client) throws Exception {
        Developer developerToLogin = developerRepository.findByUsername(person.getUsername());
        System.out.println(developerToLogin);
        if (developerToLogin != null) {
            if (loggedPeople.get(developerToLogin.getId()) != null)
                throw new Exception("Developer already logged in.");

            loggedPeople.put(developerToLogin.getId(), client);
            return developerToLogin;
        }
        return null;
    }

    @Override
    public void logout(Tester person, IObserver client) throws Exception {
        IObserver loggedPerson = loggedPeople.remove(person.getId());
        if (loggedPerson == null) {
            throw new Exception("Tester " + person.getId().toString() + " is not logged in.");
        }
    }

    @Override
    public void logout(Developer person, IObserver client) throws Exception {
        IObserver loggedPerson = loggedPeople.remove(person.getId());
        if (loggedPerson == null) {
            throw new Exception("Developer " + person.getId().toString() + " is not logged in.");
        }
    }
}
