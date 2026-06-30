package service;

import java.util.List;

public interface BaseService<T> {
    boolean add(T entity);
    boolean update(T entity);
    boolean delete(int id);
    T findById(int id);
    List<T> getAll();
}