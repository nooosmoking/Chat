package edu.school21.repositories;

import java.util.List;

public interface CrudRepository<T> {

    List<T> findAll();

    boolean save(T entity);

    void update(T entity);

    void delete(String name);
}
