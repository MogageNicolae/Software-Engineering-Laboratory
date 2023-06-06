package bms.persistence;

import java.util.Collection;

public interface Repository<T, Tid> {
    boolean add(T elem);
    void delete(T elem);
    void update(T elem, Tid id);
    T findById(Tid id);
    Collection<T> getAll();
}
