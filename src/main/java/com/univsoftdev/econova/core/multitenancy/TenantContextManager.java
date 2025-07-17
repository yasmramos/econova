package com.univsoftdev.econova.core.multitenancy;

import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.config.model.Empresa;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.MyCurrentUserProvider;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestor mejorado de contexto de tenant con soporte para múltiples estrategias
 * de resolución de tenant y gestión de ciclo de vida.
 */
@Singleton
@Slf4j
public class TenantContextManager {
    
    private static final String DEFAULT_TENANT_ID = "accounting";
    private static final String DEFAULT_SCHEMA = "accounting";
    
    private final ConcurrentHashMap<String, TenantInfo> tenantRegistry = new ConcurrentHashMap<>();
    private TenantResolutionStrategy resolutionStrategy = TenantResolutionStrategy.USER_BASED;
    
    /**
     * Enum para diferentes estrategias de resolución de tenant
     */
    public enum TenantResolutionStrategy {
        USER_BASED,      // Basado en usuario
        EMPRESA_BASED,   // Basado en empresa
        UNIDAD_BASED,    // Basado en unidad
        MIXED            // Estrategia mixta
    }
    
    /**
     * Información del tenant registrada
     */
    public static class TenantInfo {
        private final String tenantId;
        private final String schemaName;
        private final String displayName;
        private final boolean active;
        
        public TenantInfo(String tenantId, String schemaName, String displayName, boolean active) {
            this.tenantId = tenantId;
            this.schemaName = schemaName;
            this.displayName = displayName;
            this.active = active;
        }
        
        public String getTenantId() { return tenantId; }
        public String getSchemaName() { return schemaName; }
        public String getDisplayName() { return displayName; }
        public boolean isActive() { return active; }
    }
    
    /**
     * Inicializa el gestor de contexto de tenant
     */
    public void initialize() {
        // Registrar tenant por defecto
        registerTenant(DEFAULT_TENANT_ID, DEFAULT_SCHEMA, "Tenant por defecto", true);
        log.info("TenantContextManager inicializado con tenant por defecto: {}", DEFAULT_TENANT_ID);
    }
    
    /**
     * Registra un nuevo tenant
     */
    public void registerTenant(@NotNull String tenantId, @NotNull String schemaName, 
                              @NotNull String displayName, boolean active) {
        TenantInfo tenantInfo = new TenantInfo(tenantId, schemaName, displayName, active);
        tenantRegistry.put(tenantId, tenantInfo);
        log.info("Registrado tenant: {} con esquema: {}", tenantId, schemaName);
    }
    
    /**
     * Desregistra un tenant
     */
    public void unregisterTenant(@NotNull String tenantId) {
        TenantInfo removed = tenantRegistry.remove(tenantId);
        if (removed != null) {
            log.info("Desregistrado tenant: {}", tenantId);
        }
    }
    
    /**
     * Obtiene información de un tenant
     */
    public Optional<TenantInfo> getTenantInfo(@NotNull String tenantId) {
        return Optional.ofNullable(tenantRegistry.get(tenantId));
    }
    
    /**
     * Establece la estrategia de resolución de tenant
     */
    public void setResolutionStrategy(@NotNull TenantResolutionStrategy strategy) {
        this.resolutionStrategy = strategy;
        log.info("Estrategia de resolución de tenant cambiada a: {}", strategy);
    }
    
    /**
     * Resuelve el contexto de tenant basado en la estrategia actual
     */
    public TenantContext resolveCurrentTenant() {
        return switch (resolutionStrategy) {
            case USER_BASED -> resolveFromUser();
            case EMPRESA_BASED -> resolveFromEmpresa();
            case UNIDAD_BASED -> resolveFromUnidad();
            case MIXED -> resolveFromMixed();
        };
    }
    
    /**
     * Resuelve el tenant basado en el usuario actual
     */
    private TenantContext resolveFromUser() {
        User currentUser = MyCurrentUserProvider.getUser();
        if (currentUser == null) {
            log.debug("No hay usuario actual, usando tenant por defecto");
            return TenantContext.create(DEFAULT_TENANT_ID, DEFAULT_SCHEMA);
        }
        
        return TenantContext.createFromUser(currentUser);
    }
    
    /**
     * Resuelve el tenant basado en la empresa actual
     */
    private TenantContext resolveFromEmpresa() {
        User currentUser = MyCurrentUserProvider.getUser();
        // Aquí deberías obtener la empresa actual del contexto de sesión
        // Por ahora usamos el tenant por defecto
        return TenantContext.create(DEFAULT_TENANT_ID, DEFAULT_SCHEMA);
    }
    
    /**
     * Resuelve el tenant basado en la unidad actual
     */
    private TenantContext resolveFromUnidad() {
        User currentUser = MyCurrentUserProvider.getUser();
        // Aquí deberías obtener la unidad actual del contexto de sesión
        // Por ahora usamos el tenant por defecto
        return TenantContext.create(DEFAULT_TENANT_ID, DEFAULT_SCHEMA);
    }
    
    /**
     * Resuelve el tenant usando estrategia mixta
     */
    private TenantContext resolveFromMixed() {
        User currentUser = MyCurrentUserProvider.getUser();
        if (currentUser == null) {
            return TenantContext.create(DEFAULT_TENANT_ID, DEFAULT_SCHEMA);
        }
        
        // Prioridad: Usuario -> Empresa -> Unidad
        if (currentUser.getTenantSchema() != null) {
            return TenantContext.createFromUser(currentUser);
        }
        
        // Aquí podrías agregar lógica para empresa y unidad
        return TenantContext.create(DEFAULT_TENANT_ID, DEFAULT_SCHEMA);
    }
    
    /**
     * Establece el contexto de tenant actual
     */
    public void setCurrentTenant(@NotNull String tenantId) {
        TenantInfo tenantInfo = tenantRegistry.get(tenantId);
        if (tenantInfo == null) {
            throw new IllegalArgumentException("Tenant no registrado: " + tenantId);
        }
        
        if (!tenantInfo.isActive()) {
            throw new IllegalArgumentException("Tenant inactivo: " + tenantId);
        }
        
        TenantContext context = TenantContext.create(tenantId, tenantInfo.getSchemaName());
        TenantContext.setCurrent(context);
        log.debug("Establecido tenant actual: {}", tenantId);
    }
    
    /**
     * Establece el contexto de tenant para un usuario específico
     */
    public void setCurrentTenantForUser(@NotNull User user) {
        TenantContext context = TenantContext.createFromUser(user);
        TenantContext.setCurrent(context);
        log.debug("Establecido tenant para usuario: {}", user.getUserName());
    }
    
    /**
     * Establece el contexto de tenant para una empresa específica
     */
    public void setCurrentTenantForEmpresa(@NotNull Empresa empresa, @Nullable User user) {
        TenantContext context = TenantContext.createFromEmpresa(empresa, user);
        TenantContext.setCurrent(context);
        log.debug("Establecido tenant para empresa: {}", empresa.getName());
    }
    
    /**
     * Establece el contexto de tenant para una unidad específica
     */
    public void setCurrentTenantForUnidad(@NotNull Unidad unidad, @Nullable User user) {
        TenantContext context = TenantContext.createFromUnidad(unidad, user);
        TenantContext.setCurrent(context);
        log.debug("Establecido tenant para unidad: {}", unidad.getNombre());
    }
    
    /**
     * Obtiene el contexto actual o lo resuelve automáticamente
     */
    public TenantContext getCurrentOrResolve() {
        return TenantContext.getCurrent().orElseGet(this::resolveCurrentTenant);
    }
    
    /**
     * Limpia el contexto actual
     */
    public void clearCurrentTenant() {
        TenantContext.clear();
        log.debug("Limpiado contexto de tenant actual");
    }
    
    /**
     * Verifica si un tenant está activo
     */
    public boolean isTenantActive(@NotNull String tenantId) {
        return tenantRegistry.containsKey(tenantId) && tenantRegistry.get(tenantId).isActive();
    }
    
    /**
     * Obtiene todos los tenants registrados
     */
    public java.util.Collection<TenantInfo> getAllTenants() {
        return tenantRegistry.values();
    }
    
    /**
     * Obtiene todos los tenants activos
     */
    public java.util.List<TenantInfo> getActiveTenants() {
        return tenantRegistry.values().stream()
                .filter(TenantInfo::isActive)
                .toList();
    }
    
    /**
     * Ejecuta una operación en el contexto de un tenant específico
     */
    public <T> T executeInTenant(@NotNull String tenantId, @NotNull TenantContext.TenantOperation<T> operation) {
        TenantInfo tenantInfo = tenantRegistry.get(tenantId);
        if (tenantInfo == null) {
            throw new IllegalArgumentException("Tenant no registrado: " + tenantId);
        }
        
        TenantContext context = TenantContext.create(tenantId, tenantInfo.getSchemaName());
        return TenantContext.executeInTenant(context, operation);
    }
    
    /**
     * Ejecuta una operación en el contexto de un tenant específico sin retorno
     */
    public void executeInTenant(@NotNull String tenantId, @NotNull TenantContext.TenantRunnable runnable) {
        executeInTenant(tenantId, () -> {
            runnable.run();
            return null;
        });
    }
}