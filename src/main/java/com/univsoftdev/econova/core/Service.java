package com.univsoftdev.econova.core;

import com.univsoftdev.econova.contabilidad.service.CrudService;
import com.univsoftdev.econova.contabilidad.Filter;
import com.univsoftdev.econova.exceptions.EntityNotFoundException;
import io.ebean.Database;
import io.ebean.Query;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase base abstracta para servicios con operaciones CRUD genéricas. Requiere
 * que las subclases especifiquen el tipo de entidad que gestionan.
 *
 * @param <T> Tipo de entidad gestionada por el servicio
 */
@Slf4j
public abstract class Service<T> implements CrudService<T> {

    protected final Database database;
    private final Class<T> entityClass;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param database Instancia de Ebean Database
     * @param entityClass Clase de la entidad gestionada (e.g., Unidad.class)
     */
    @Inject
    protected Service(Database database, Class<T> entityClass) {
        this.database = database;
        this.entityClass = entityClass;
    }

    @Transactional
    @Override
    public void update(T entity) {
        database.update(entity);
    }

    /**
     * Persiste una entidad en la base de datos.
     *
     * @param entity Entidad a guardar
     */
    @Transactional
    @Override
    public void save(T entity) {
        database.save(entity);
    }

    /**
     * Elimina una entidad de la base de datos.
     *
     * @param entity Entidad a eliminar
     * @return
     */
    @Transactional
    @Override
    public boolean delete(T entity) {
        return database.delete(entity);
    }

    @Transactional
    public boolean delete(long id) {
        T entity = findById(id); // Lanza excepción si no existe
        return database.delete(entity);
    }

    /**
     * Busca una entidad por su ID.
     *
     * @param id Identificador único
     * @return Entidad encontrada
     * @throws EntityNotFoundException Si no se encuentra la entidad
     */
    @Transactional
    @Override
    public T findById(Object id) {
        return Optional.ofNullable(database.find(entityClass).where().eq("deleted", false).findOne())
                .orElseThrow(() -> new EntityNotFoundException(
                String.format("Entidad %s con ID %s no encontrada", entityClass.getSimpleName(), id)
        ));
    }

    @Transactional
    public Optional<T> findBy(String property, Object value) {
        return database.find(entityClass).where().eq(property, value).findOneOrEmpty();
    }

    /**
     * Recupera todas las entidades del tipo gestionado.
     *
     * @return Lista de entidades
     */
    @Transactional
    @Override
    public List<T> findAll() {
        return database.find(entityClass).where().eq("deleted", false).findList();
    }

    @Transactional
    public List<T> findAllPaginated(int page, int pageSize) {
        return database.find(entityClass)
                .where().eq("deleted", false)
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findList();
    }

    /**
     * Crea una consulta base para la entidad.
     *
     * @return Consulta inicializada
     */
    @Transactional
    protected Query<T> query() {
        return database.find(entityClass);
    }

    @Transactional
    public List<T> findBy(List<Filter> filters) {
        Query<T> query = database.find(entityClass);

        // Aplicar cada filtro dinámicamente
        filters.forEach(filter -> {
            switch (filter.getOperator()) {
                case "eq" ->
                    query.where().eq(filter.getProperty(), filter.getValue());
                case "lt" ->
                    query.where().lt(filter.getProperty(), filter.getValue());
                case "gt" ->
                    query.where().gt(filter.getProperty(), filter.getValue());
                case "contains" ->
                    query.where().icontains(filter.getProperty(), (String) filter.getValue());
                default ->
                    throw new IllegalArgumentException("Operador no soportado: " + filter.getOperator());
            }
        });

        return query.findList();
    }

}
