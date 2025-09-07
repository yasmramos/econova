package com.univsoftdev.econova.core.repository;

import io.ebean.Database;
import io.ebean.PagedList;
import io.ebean.Query;
import jakarta.persistence.OptimisticLockException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseRepository<T> implements CrudRepository<T> {

    protected final Database database;

    protected BaseRepository(Database database) {
        this.database = database;
    }

    @Override
    public void save(T entity) {
        database.save(entity);
    }

    @Override
    public void update(T entity) {
        database.update(entity);
    }

    @Override
    public boolean delete(T entity) {
        try {
            database.delete(entity);
            return true;
        } catch (OptimisticLockException e) {
            log.error("Error deleting entity", e);
            return false;
        }
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(database.find(getEntityType(), id));
    }

    @Override
    public List<T> findAll() {
        return database.find(getEntityType()).findList();
    }

    @Override
    public List<T> findAll(int page, int pageSize) {
        return database.find(getEntityType())
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findList();
    }

    @Override
    public boolean existsById(Long id) {
        return database.find(getEntityType())
                .where()
                .eq("id", id)
                .exists();
    }

    @Override
    public long count() {
        return database.find(getEntityType()).findCount();
    }

    @Override
    public List<T> findByCriteria(String criteria) {
        // Este método debe ser implementado por las clases hijas
        // o puedes implementar una versión por defecto
        return database.find(getEntityType())
                .where()
                .ilike("name", "%" + criteria + "%")
                .findList();
    }

    @Override
    public <T> Query<T> find(Class<T> beanType) {
        return database.find(beanType);
    }

    @Override
    public <T> T find(Class<T> beanType, Object o) {
        return database.find(beanType, o);
    }

    @Override
    public <T> Query<T> createQuery(Class<T> type) {
        return database.find(type);
    }

    // Método abstracto que debe ser implementado por las clases hijas
    protected abstract Class<T> getEntityType();

    public Query<T> query() {
        return database.find(getEntityType());
    }

    public PagedList<T> findPagedList(int page, int pageSize) {
        return query()
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findPagedList();
    }

    public int getTotalPageCount(int pageSize) {
        long totalCount = getTotalCount();
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    public long getTotalCount() {
        return query().findCount();
    }
}
