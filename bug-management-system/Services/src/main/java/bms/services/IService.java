package bms.services;

import bms.domain.Developer;
import bms.domain.Tester;

public interface IService {
    Tester login(Tester person, IObserver client) throws Exception;
    Developer login(Developer person, IObserver client) throws Exception;
    void logout(Tester person, IObserver client) throws Exception;
    void logout(Developer person, IObserver client) throws Exception;
}
