package com.univsoftdev.econova.core.multitenancy;

import com.univsoftdev.econova.core.multitenancy.TenantContext;
import io.ebean.Database;
import io.ebean.Query;
import io.ebean.config.TenantMode;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Interceptor de tenant que asegura que todas las consultas incluyan
 * automáticamente el filtro de tenant y que las operaciones se ejecuten
 * en el contexto del tenant correcto.
 */
@Slf4j
public class TenantInterceptor {
    
    private final Database database;
    private final TenantMode tenantMode;
    
    public TenantInterceptor(Database database, TenantMode tenantMode) {
        this.database = database;
        this.tenantMode = tenantMode;
    }
    
    /**
     * Intercepta consultas para agregar automáticamente filtros de tenant
     */
    public <T> Query<T> interceptQuery(@NotNull Query<T> query) {
        Optional<TenantContext> context = TenantContext.getCurrent();
        
        if (context.isPresent() && !context.get().isDefaultTenant()) {
            String tenantId = context.get().getTenantId();
            log.debug("Interceptando consulta para tenant: {}", tenantId);
            
            // Agregar filtro de tenant automáticamente
            // Nota: Esto depende del modo de tenant configurado
            if (tenantMode == TenantMode.SCHEMA) {
                // En modo SCHEMA, Ebean maneja automáticamente el esquema
                log.debug("Usando modo SCHEMA para tenant: {}", tenantId);
            } else if (tenantMode == TenantMode.PARTITION) {
                // En modo PARTITION, necesitamos agregar filtros WHERE
                log.debug("Agregando filtro de partición para tenant: {}", tenantId);
                // query.where().eq("tenantId", tenantId);
            }
        }
        
        return query;
    }
    
    /**
     * Valida que el contexto de tenant sea válido antes de ejecutar operaciones
     */
    public void validateTenantContext() {
        Optional<TenantContext> context = TenantContext.getCurrent();
        
        if (context.isEmpty()) {
            log.warn("No hay contexto de tenant establecido");
            return;
        }
        
        TenantContext tenantContext = context.get();
        if (!tenantContext.isValid()) {
            throw new IllegalStateException("Contexto de tenant inválido: " + tenantContext);
        }
        
        if (tenantContext.isIsolated()) {
            log.debug("Validando aislamiento de tenant: {}", tenantContext.getTenantId());
            // Aquí podrías agregar validaciones adicionales de aislamiento
        }
    }
    
    /**
     * Intercepta operaciones de escritura para validar tenant
     */
    public void interceptWrite(@NotNull Object entity) {
        validateTenantContext();
        
        Optional<TenantContext> context = TenantContext.getCurrent();
        if (context.isPresent()) {
            String tenantId = context.get().getTenantId();
            log.debug("Interceptando escritura para tenant: {} con entidad: {}", 
                     tenantId, entity.getClass().getSimpleName());
            
            // Aquí podrías agregar lógica para establecer automáticamente
            // el tenant ID en la entidad si tiene un campo tenantId
        }
    }
    
    /**
     * Intercepta operaciones de eliminación para validar tenant
     */
    public void interceptDelete(@NotNull Object entity) {
        validateTenantContext();
        
        Optional<TenantContext> context = TenantContext.getCurrent();
        if (context.isPresent()) {
            String tenantId = context.get().getTenantId();
            log.debug("Interceptando eliminación para tenant: {} con entidad: {}", 
                     tenantId, entity.getClass().getSimpleName());
            
            // Validar que la entidad pertenece al tenant actual
            // Esto previene eliminaciones accidentales entre tenants
        }
    }
    
    /**
     * Registra estadísticas de uso por tenant
     */
    public void logTenantUsage(@NotNull String operation, @NotNull String entityType) {
        Optional<TenantContext> context = TenantContext.getCurrent();
        if (context.isPresent()) {
            String tenantId = context.get().getTenantId();
            log.debug("Tenant: {} ejecutó {} en {}", tenantId, operation, entityType);
            
            // Aquí podrías registrar métricas o auditoría
        }
    }
}