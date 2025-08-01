package com.univsoftdev.econova.core.multitenancy;

import com.univsoftdev.econova.core.model.BaseModel;
import io.ebean.Database;
import io.ebean.Query;
import io.ebean.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio base que proporciona operaciones CRUD con soporte automático
 * para multitenancy. Todas las operaciones son conscientes del tenant actual.
 */
@Slf4j
public abstract class TenantAwareRepository<T extends BaseModel> {
    
    protected final Database database;
    protected final Class<T> entityClass;
    protected final EbeanMultitenancyConfig multitenancyConfig;
    
    public TenantAwareRepository(@NotNull Database database, 
                                @NotNull Class<T> entityClass,
                                @NotNull EbeanMultitenancyConfig multitenancyConfig) {
        this.database = database;
        this.entityClass = entityClass;
        this.multitenancyConfig = multitenancyConfig;
    }
    
    /**
     * Obtiene la base de datos para el tenant actual
     */
    protected Database getCurrentDatabase() {
        return multitenancyConfig.getCurrentDatabase();
    }
    
    /**
     * Crea una consulta consciente del tenant
     */
    protected Query<T> createQuery() {
        Database db = getCurrentDatabase();
        Query<T> query = db.find(entityClass);
        
        // El interceptor se encarga de agregar filtros de tenant automáticamente
        return query;
    }
    
    /**
     * Busca una entidad por ID en el tenant actual
     */
    public Optional<T> findById(@NotNull Object id) {
        log.debug("Buscando {} con ID: {} en tenant: {}", 
                entityClass.getSimpleName(), id, TenantContext.getCurrentTenantId());
        
        T entity = createQuery().setId(id).findOne();
        return Optional.ofNullable(entity);
    }
    
    /**
     * Busca todas las entidades en el tenant actual
     */
    public List<T> findAll() {
        log.debug("Buscando todas las entidades {} en tenant: {}", 
                entityClass.getSimpleName(), TenantContext.getCurrentTenantId());
        
        return createQuery().findList();
    }
    
    /**
     * Busca entidades con paginación en el tenant actual
     */
    public List<T> findPaginated(int offset, int limit) {
        log.debug("Buscando {} entidades con paginación (offset: {}, limit: {}) en tenant: {}", 
                entityClass.getSimpleName(), offset, limit, TenantContext.getCurrentTenantId());
        
        return createQuery()
                .setFirstRow(offset)
                .setMaxRows(limit)
                .findList();
    }
    
    /**
     * Cuenta las entidades en el tenant actual
     */
    public long count() {
        log.debug("Contando entidades {} en tenant: {}", 
                entityClass.getSimpleName(), TenantContext.getCurrentTenantId());
        
        return createQuery().findCount();
    }
    
    /**
     * Guarda una entidad en el tenant actual
     */
    public T save(@NotNull T entity) {
        log.debug("Guardando {} en tenant: {}", 
                entityClass.getSimpleName(), TenantContext.getCurrentTenantId());
        
        Database db = getCurrentDatabase();
        db.save(entity);
        return entity;
    }
    
    /**
     * Guarda una entidad en el tenant actual dentro de una transacción
     */
    public T save(@NotNull T entity, @NotNull Transaction transaction) {
        log.debug("Guardando {} en transacción para tenant: {}", 
                entityClass.getSimpleName(), TenantContext.getCurrentTenantId());
        
        Database db = getCurrentDatabase();
        db.save(entity, transaction);
        return entity;
    }
    
    /**
     * Actualiza una entidad en el tenant actual
     */
    public T update(@NotNull T entity) {
        log.debug("Actualizando {} en tenant: {}", 
                entityClass.getSimpleName(), TenantContext.getCurrentTenantId());
        
        Database db = getCurrentDatabase();
        db.update(entity);
        return entity;
    }
    
    /**
     * Elimina una entidad por ID en el tenant actual
     */
    public boolean deleteById(@NotNull Object id) {
        log.debug("Eliminando {} con ID: {} en tenant: {}", 
                entityClass.getSimpleName(), id, TenantContext.getCurrentTenantId());
        
        Database db = getCurrentDatabase();
        return db.delete(entityClass, id);
    }
    
    /**
     * Elimina una entidad en el tenant actual
     */
    public boolean delete(@NotNull T entity) {
        log.debug("Eliminando {} en tenant: {}", 
                entityClass.getSimpleName(), TenantContext.getCurrentTenantId());
        
        Database db = getCurrentDatabase();
        return db.delete(entity);
    }
    
    /**
     * Ejecuta una operación en el contexto de un tenant específico
     */
    public <R> R executeInTenant(@NotNull String tenantId, @NotNull TenantOperation<R> operation) {
        return multitenancyConfig.executeInTenant(tenantId, (database) -> operation.execute(this));
    }
    
    /**
     * Ejecuta una operación en el contexto de un tenant específico sin retorno
     */
    public void executeInTenant(@NotNull String tenantId, @NotNull TenantRunnable runnable) {
        executeInTenant(tenantId, (repository) -> {
            runnable.run(repository);
            return null;
        });
    }
    
    /**
     * Inicia una transacción en el tenant actual
     */
    public Transaction beginTransaction() {
        Database db = getCurrentDatabase();
        return db.beginTransaction();
    }
    
    /**
     * Ejecuta una operación dentro de una transacción en el tenant actual
     */
    public <R> R executeInTransaction(@NotNull TransactionOperation<R> operation) {
        Database db = getCurrentDatabase();
        try (Transaction transaction = db.beginTransaction()) {
            R result = operation.execute(this, transaction);
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Error en transacción para tenant: {}", TenantContext.getCurrentTenantId(), e);
            throw new RuntimeException("Error en transacción", e);
        }
    }
    
    /**
     * Ejecuta una operación dentro de una transacción sin retorno
     */
    public void executeInTransaction(@NotNull TransactionRunnable runnable) {
        executeInTransaction((repository, transaction) -> {
            runnable.run(repository, transaction);
            return null;
        });
    }
    
    /**
     * Valida que una entidad pertenezca al tenant actual
     */
    protected void validateTenantOwnership(@NotNull T entity) {
        // Implementar validación específica según tu modelo de datos
        // Por ejemplo, verificar que entity.getTenantId() == TenantContext.getCurrentTenantId()
    }
    
    /**
     * Interfaz funcional para operaciones de tenant en repository
     */
    @FunctionalInterface
    public interface TenantOperation<R> {
        R execute(TenantAwareRepository<T> repository) throws Exception;
    }
    
    /**
     * Interfaz funcional para operaciones de tenant sin retorno
     */
    @FunctionalInterface
    public interface TenantRunnable {
        void run(TenantAwareRepository<T> repository) throws Exception;
    }
    
    /**
     * Interfaz funcional para operaciones con transacción
     */
    @FunctionalInterface
    public interface TransactionOperation<R> {
        R execute(TenantAwareRepository<T> repository, Transaction transaction) throws Exception;
    }
    
    /**
     * Interfaz funcional para operaciones con transacción sin retorno
     */
    @FunctionalInterface
    public interface TransactionRunnable {
        void run(TenantAwareRepository<T> repository, Transaction transaction) throws Exception;
    }
}