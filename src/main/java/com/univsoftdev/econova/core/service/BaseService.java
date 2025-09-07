package com.univsoftdev.econova.core.service;

import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.PagedList;
import io.ebean.Query;
import io.ebean.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public abstract class BaseService<T, R extends BaseRepository<T>> implements CrudService<T>{

    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }
    
    @Transactional
    @Override
    public void save(T entity) {
        repository.save(entity);
    }

    @Transactional
    @Override
    public void update(T entity) {
        repository.update(entity);
    }

    @Transactional
    @Override
    public boolean delete(T entity) {
        return repository.delete(entity);
    }

    @Override
    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAll(int page, int pageSize) {
        return repository.findAll(page, pageSize);
    }
    
    public Query<T> query() {
        return repository.query();
    }

    public PagedList<T> findPagedList(int page, int pageSize) {
        return repository.findPagedList(page, pageSize);
    }

    public int getTotalPageCount(int pageSize) {
        return repository.getTotalPageCount(pageSize);
    }

    public long getTotalCount() {
        return repository.getTotalCount();
    }

}
