package com.univsoftdev.econova.core.multitenancy;

import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.config.model.Empresa;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.MyCurrentUserProvider;
import com.univsoftdev.econova.AppSession;
import com.univsoftdev.econova.AppContext;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * Servicio de alto nivel para gestión de multitenancy que integra
 * todos los componentes y proporciona una API simple para el uso
 * en la aplicación.
 */
@Singleton
@Slf4j
public class MultitenancyService {
    
    private final TenantContextManager contextManager;
    private final TenantRegistry tenantRegistry;
    private final EbeanMultitenancyConfig multitenancyConfig;
    
    public MultitenancyService(TenantContextManager contextManager, 
                              TenantRegistry tenantRegistry,
                              EbeanMultitenancyConfig multitenancyConfig) {
        this.contextManager = contextManager;
        this.tenantRegistry = tenantRegistry;
        this.multitenancyConfig = multitenancyConfig;
    }
    
    /**
     * Inicializa el sistema de multitenancy
     */
    public void initialize() {
        log.info("Inicializando sistema de multitenancy");
        
        // Inicializar componentes
        contextManager.initialize();
        tenantRegistry.initializeDefaultTenants();
        
        // Configurar estrategia de resolución por defecto
        contextManager.setResolutionStrategy(TenantContextManager.TenantResolutionStrategy.MIXED);
        
        log.info("Sistema de multitenancy inicializado correctamente");
    }
    
    /**
     * Establece el contexto de tenant basado en el usuario actual
     */
    public void setTenantFromCurrentUser() {
        User currentUser = MyCurrentUserProvider.getUser();
        if (currentUser != null) {
            contextManager.setCurrentTenantForUser(currentUser);
            log.info("Contexto de tenant establecido para usuario: {}", currentUser.getUserName());
        } else {
            log.warn("No hay usuario actual, usando tenant por defecto");
            contextManager.setCurrentTenant("accounting");
        }
    }
    
    /**
     * Establece el contexto de tenant basado en la sesión actual
     */
    public void setTenantFromSession() {
        AppSession session = AppContext.getInstance().getSession();
        if (session != null) {
            User currentUser = session.getCurrentUser();
            Unidad currentUnidad = session.getUnidad();
            
            if (currentUnidad != null) {
                contextManager.setCurrentTenantForUnidad(currentUnidad, currentUser);
                log.info("Contexto de tenant establecido para unidad: {}", currentUnidad.getNombre());
            } else if (currentUser != null) {
                contextManager.setCurrentTenantForUser(currentUser);
                log.info("Contexto de tenant establecido para usuario: {}", currentUser.getUserName());
            } else {
                log.warn("No hay información de sesión, usando tenant por defecto");
                contextManager.setCurrentTenant("accounting");
            }
        }
    }
    
    /**
     * Establece el contexto de tenant para una empresa específica
     */
    public void setTenantForEmpresa(@NotNull Empresa empresa) {
        User currentUser = MyCurrentUserProvider.getUser();
        contextManager.setCurrentTenantForEmpresa(empresa, currentUser);
        log.info("Contexto de tenant establecido para empresa: {}", empresa.getName());
    }
    
    /**
     * Establece el contexto de tenant para una unidad específica
     */
    public void setTenantForUnidad(@NotNull Unidad unidad) {
        User currentUser = MyCurrentUserProvider.getUser();
        contextManager.setCurrentTenantForUnidad(unidad, currentUser);
        log.info("Contexto de tenant establecido para unidad: {}", unidad.getNombre());
    }
    
    /**
     * Obtiene el contexto de tenant actual
     */
    public TenantContext getCurrentTenantContext() {
        return contextManager.getCurrentOrResolve();
    }
    
    /**
     * Obtiene el ID del tenant actual
     */
    public String getCurrentTenantId() {
        return TenantContext.getCurrentTenantId();
    }
    
    /**
     * Obtiene el esquema actual
     */
    public String getCurrentSchema() {
        return TenantContext.getCurrentSchema();
    }
    
    /**
     * Verifica si el tenant actual es el tenant por defecto
     */
    public boolean isDefaultTenant() {
        return getCurrentTenantContext().isDefaultTenant();
    }
    
    /**
     * Ejecuta una operación en el contexto de un tenant específico
     */
    public <T> T executeInTenant(@NotNull String tenantId, @NotNull TenantOperation<T> operation) {
        return contextManager.executeInTenant(tenantId, operation::execute);
    }
    
    /**
     * Ejecuta una operación en el contexto de un tenant específico sin retorno
     */
    public void executeInTenant(@NotNull String tenantId, @NotNull TenantRunnable runnable) {
        executeInTenant(tenantId, () -> {
            runnable.run();
            return null;
        });
    }
    
    /**
     * Ejecuta una operación en el contexto de una empresa específica
     */
    public <T> T executeInEmpresa(@NotNull Empresa empresa, @NotNull TenantOperation<T> operation) {
        User currentUser = MyCurrentUserProvider.getUser();
        TenantContext context = TenantContext.createFromEmpresa(empresa, currentUser);
        return TenantContext.executeInTenant(context, operation::execute);
    }
    
    /**
     * Ejecuta una operación en el contexto de una unidad específica
     */
    public <T> T executeInUnidad(@NotNull Unidad unidad, @NotNull TenantOperation<T> operation) {
        User currentUser = MyCurrentUserProvider.getUser();
        TenantContext context = TenantContext.createFromUnidad(unidad, currentUser);
        return TenantContext.executeInTenant(context, operation::execute);
    }
    
    /**
     * Registra un nuevo tenant
     */
    public void registerTenant(@NotNull String tenantId, @NotNull String schemaName, 
                              @NotNull String displayName, boolean active) {
        tenantRegistry.registerTenant(tenantId, schemaName, displayName, active);
        log.info("Tenant registrado: {} ({})", displayName, tenantId);
    }
    
    /**
     * Desregistra un tenant
     */
    public void unregisterTenant(@NotNull String tenantId) {
        tenantRegistry.unregisterTenant(tenantId);
        multitenancyConfig.cleanupTenant(tenantId);
        log.info("Tenant desregistrado: {}", tenantId);
    }
    
    /**
     * Verifica si un tenant está activo
     */
    public boolean isTenantActive(@NotNull String tenantId) {
        return tenantRegistry.isActive(tenantId);
    }
    
    /**
     * Obtiene estadísticas del sistema de multitenancy
     */
    public MultitenancyStats getStats() {
        TenantRegistry.RegistryStats registryStats = tenantRegistry.getStats();
        TenantContext currentContext = getCurrentTenantContext();
        
        return new MultitenancyStats(
                registryStats.getTotalTenants(),
                registryStats.getActiveTenants(),
                registryStats.getTotalOperations(),
                currentContext.getTenantId(),
                currentContext.getSchemaName()
        );
    }
    
    /**
     * Limpia tenants inactivos
     */
    public void cleanupInactiveTenants(long maxIdleTimeMs) {
        tenantRegistry.cleanupInactiveTenants(maxIdleTimeMs);
        log.info("Limpieza de tenants inactivos completada");
    }
    
    /**
     * Limpia el contexto de tenant actual
     */
    public void clearCurrentTenant() {
        contextManager.clearCurrentTenant();
        log.debug("Contexto de tenant actual limpiado");
    }
    
    /**
     * Cierra el sistema de multitenancy
     */
    public void shutdown() {
        log.info("Cerrando sistema de multitenancy");
        
        clearCurrentTenant();
        multitenancyConfig.shutdown();
        
        log.info("Sistema de multitenancy cerrado");
    }
    
    /**
     * Estadísticas del sistema de multitenancy
     */
    public static class MultitenancyStats {
        private final int totalTenants;
        private final int activeTenants;
        private final long totalOperations;
        private final String currentTenantId;
        private final String currentSchema;
        
        public MultitenancyStats(int totalTenants, int activeTenants, long totalOperations,
                               String currentTenantId, String currentSchema) {
            this.totalTenants = totalTenants;
            this.activeTenants = activeTenants;
            this.totalOperations = totalOperations;
            this.currentTenantId = currentTenantId;
            this.currentSchema = currentSchema;
        }
        
        public int getTotalTenants() { return totalTenants; }
        public int getActiveTenants() { return activeTenants; }
        public long getTotalOperations() { return totalOperations; }
        public String getCurrentTenantId() { return currentTenantId; }
        public String getCurrentSchema() { return currentSchema; }
    }
    
    /**
     * Interfaz funcional para operaciones de tenant
     */
    @FunctionalInterface
    public interface TenantOperation<T> {
        T execute() throws Exception;
    }
    
    /**
     * Interfaz funcional para operaciones de tenant sin retorno
     */
    @FunctionalInterface
    public interface TenantRunnable {
        void run() throws Exception;
    }
}