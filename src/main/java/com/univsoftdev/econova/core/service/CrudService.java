package com.univsoftdev.econova.core.service;

import java.util.List;
import java.util.Optional;

public interface CrudService<T> {

    void save(T entity);

    void update(T entity);

    boolean delete(T entity);

    Optional<T> findById(Long id);

    List<T> findAll();

    List<T> findAll(int page, int pageSize);
}
