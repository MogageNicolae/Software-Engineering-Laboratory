package bms.services;

import bms.domain.Bug;

import java.util.Collection;

public interface IObserver {
    void bugListChanged(Collection<Bug> bugs) throws Exception;
}
