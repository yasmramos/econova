package com.univsoftdev.econova.core.multitenancy;

import io.ebean.event.BeanPersistAdapter;
import io.ebean.event.BeanPersistRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * Listener de persistencia que se ejecuta durante las operaciones de base de datos
 * para asegurar que el contexto de tenant sea válido y se mantenga la integridad
 * de los datos por tenant.
 */
@Slf4j
public class TenantPersistenceListener extends BeanPersistAdapter {
    
    private final TenantInterceptor tenantInterceptor;
    
    public TenantPersistenceListener(TenantInterceptor tenantInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
    }
    
    @Override
    public boolean isRegisterFor(@NotNull Class<?> cls) {
        // Registrar para todas las entidades
        return true;
    }
    
    @Override
    public boolean preInsert(@NotNull BeanPersistRequest<?> request) {
        tenantInterceptor.interceptWrite(request.getBean());
        tenantInterceptor.logTenantUsage("INSERT", request.getBean().getClass().getSimpleName());
        return true;
    }
    
    @Override
    public boolean preUpdate(@NotNull BeanPersistRequest<?> request) {
        tenantInterceptor.interceptWrite(request.getBean());
        tenantInterceptor.logTenantUsage("UPDATE", request.getBean().getClass().getSimpleName());
        return true;
    }
    
    @Override
    public boolean preDelete(@NotNull BeanPersistRequest<?> request) {
        tenantInterceptor.interceptDelete(request.getBean());
        tenantInterceptor.logTenantUsage("DELETE", request.getBean().getClass().getSimpleName());
        return true;
    }
    
    @Override
    public void postInsert(@NotNull BeanPersistRequest<?> request) {
        log.debug("Entidad insertada para tenant: {}", 
                TenantContext.getCurrentTenantId());
    }
    
    @Override
    public void postUpdate(@NotNull BeanPersistRequest<?> request) {
        log.debug("Entidad actualizada para tenant: {}", 
                TenantContext.getCurrentTenantId());
    }
    
    @Override
    public void postDelete(@NotNull BeanPersistRequest<?> request) {
        log.debug("Entidad eliminada para tenant: {}", 
                TenantContext.getCurrentTenantId());
    }
}