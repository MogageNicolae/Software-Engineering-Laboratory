package bms.persistence;

import java.util.Collection;

public interface Repository<T, Tid> {
    boolean add(T elem);
    boolean delete(T elem);
    boolean update(T elem, Tid id);
    T findById(Tid id);
    Collection<T> getAll();
}
