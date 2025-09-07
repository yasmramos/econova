package com.univsoftdev.econova.contabilidad.service;

import java.util.List;

public interface CrudService<T> {

    void save(T entity);

    void update(T entity);

    boolean delete(T entity);

    T findById(Object id);

    List<T> findAll();
}
