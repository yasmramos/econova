package com.univsoftdev.econova.core.repository;

import io.ebean.Query;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz base para operaciones CRUD básicas
 *
 * @param <T> Tipo de entidad manejada por el repositorio
 */
public interface CrudRepository<T> {

    /**
     * Guarda una entidad en la base de datos
     *
     * @param entity Entidad a guardar
     */
    void save(T entity);

    /**
     * Actualiza una entidad existente
     *
     * @param entity Entidad a actualizar
     */
    void update(T entity);

    /**
     * Elimina una entidad
     *
     * @param entity Entidad a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean delete(T entity);

    /**
     * Crea una consulta para el tipo de entidad especificado
     *
     * @param beanType Tipo de entidad
     * @return Query para la entidad especificada
     */
    <T> Query<T> find(Class<T> beanType);

    /**
     * Busca una entidad por su ID
     *
     * @param id Identificador de la entidad
     * @return Optional con la entidad encontrada o vacío si no existe
     */
    Optional<T> findById(Long id);

    /**
     * Obtiene todas las entidades
     *
     * @return Lista de todas las entidades
     */
    List<T> findAll();

    /**
     * Obtiene todas las entidades con paginación
     *
     * @param page Número de página (comenzando en 0)
     * @param pageSize Tamaño de página
     * @return Lista de entidades para la página especificada
     */
    List<T> findAll(int page, int pageSize);

    /**
     * Verifica si existe una entidad con el ID especificado
     *
     * @param id Identificador de la entidad
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long id);

    /**
     * Obtiene el número total de registros
     *
     * @return Cantidad total de entidades
     */
    long count();

    /**
     * Busca entidades por criterio
     *
     * @param criteria Criterio de búsqueda
     * @return Lista de entidades que coinciden con el criterio
     */
    List<T> findByCriteria(String criteria);

    /**
     * Crea una consulta para el tipo de entidad especificado
     *
     * @param type Tipo de entidad
     * @return Query para la entidad especificada
     */
    <T> Query<T> createQuery(Class<T> type);

    <T extends Object> T find(Class<T> type, Object o);
}
