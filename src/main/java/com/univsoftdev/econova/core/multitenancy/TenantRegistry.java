package com.univsoftdev.econova.core.multitenancy;

import com.univsoftdev.econova.core.multitenancy.TenantContext;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro de tenants que gestiona el ciclo de vida de los tenants
 * y proporciona funcionalidades de gestión centralizada.
 */
@Singleton
@Slf4j
public class TenantRegistry {
    
    private final ConcurrentHashMap<String, TenantRegistryEntry> tenants = new ConcurrentHashMap<>();
    private final TenantContextManager contextManager;
    
    /**
     * Entrada del registro de tenant
     */
    public static class TenantRegistryEntry {
        private final String tenantId;
        private final String schemaName;
        private final String displayName;
        private final boolean active;
        private final long createdAt;
        private final TenantMetrics metrics;
        
        public TenantRegistryEntry(String tenantId, String schemaName, String displayName, boolean active) {
            this.tenantId = tenantId;
            this.schemaName = schemaName;
            this.displayName = displayName;
            this.active = active;
            this.createdAt = System.currentTimeMillis();
            this.metrics = new TenantMetrics();
        }
        
        // Getters
        public String getTenantId() { return tenantId; }
        public String getSchemaName() { return schemaName; }
        public String getDisplayName() { return displayName; }
        public boolean isActive() { return active; }
        public long getCreatedAt() { return createdAt; }
        public TenantMetrics getMetrics() { return metrics; }
    }
    
    /**
     * Métricas del tenant
     */
    public static class TenantMetrics {
        private long queryCount = 0;
        private long insertCount = 0;
        private long updateCount = 0;
        private long deleteCount = 0;
        private long lastAccessTime = 0;
        
        public void recordQuery() {
            queryCount++;
            lastAccessTime = System.currentTimeMillis();
        }
        
        public void recordInsert() {
            insertCount++;
            lastAccessTime = System.currentTimeMillis();
        }
        
        public void recordUpdate() {
            updateCount++;
            lastAccessTime = System.currentTimeMillis();
        }
        
        public void recordDelete() {
            deleteCount++;
            lastAccessTime = System.currentTimeMillis();
        }
        
        // Getters
        public long getQueryCount() { return queryCount; }
        public long getInsertCount() { return insertCount; }
        public long getUpdateCount() { return updateCount; }
        public long getDeleteCount() { return deleteCount; }
        public long getLastAccessTime() { return lastAccessTime; }
        public long getTotalOperations() { return queryCount + insertCount + updateCount + deleteCount; }
    }
    
    public TenantRegistry(TenantContextManager contextManager) {
        this.contextManager = contextManager;
    }
    
    /**
     * Registra un nuevo tenant
     */
    public void registerTenant(@NotNull String tenantId, @NotNull String schemaName, 
                              @NotNull String displayName, boolean active) {
        TenantRegistryEntry entry = new TenantRegistryEntry(tenantId, schemaName, displayName, active);
        tenants.put(tenantId, entry);
        
        // Registrar también en el context manager
        contextManager.registerTenant(tenantId, schemaName, displayName, active);
        
        log.info("Tenant registrado: {} ({})", displayName, tenantId);
    }
    
    /**
     * Desregistra un tenant
     */
    public void unregisterTenant(@NotNull String tenantId) {
        TenantRegistryEntry entry = tenants.remove(tenantId);
        if (entry != null) {
            contextManager.unregisterTenant(tenantId);
            log.info("Tenant desregistrado: {} ({})", entry.getDisplayName(), tenantId);
        }
    }
    
    /**
     * Obtiene una entrada del registro
     */
    public TenantRegistryEntry getTenant(@NotNull String tenantId) {
        return tenants.get(tenantId);
    }
    
    /**
     * Verifica si un tenant existe
     */
    public boolean existsTenant(@NotNull String tenantId) {
        return tenants.containsKey(tenantId);
    }
    
    /**
     * Verifica si un tenant está activo
     */
    public boolean isActive(@NotNull String tenantId) {
        TenantRegistryEntry entry = tenants.get(tenantId);
        return entry != null && entry.isActive();
    }
    
    /**
     * Obtiene todas las entradas del registro
     */
    public java.util.Collection<TenantRegistryEntry> getAllTenants() {
        return tenants.values();
    }
    
    /**
     * Obtiene todos los tenants activos
     */
    public java.util.List<TenantRegistryEntry> getActiveTenants() {
        return tenants.values().stream()
                .filter(TenantRegistryEntry::isActive)
                .toList();
    }
    
    /**
     * Registra una operación para métricas
     */
    public void recordOperation(@NotNull String tenantId, @NotNull String operation) {
        TenantRegistryEntry entry = tenants.get(tenantId);
        if (entry != null) {
            TenantMetrics metrics = entry.getMetrics();
            switch (operation.toLowerCase()) {
                case "query", "select" -> metrics.recordQuery();
                case "insert" -> metrics.recordInsert();
                case "update" -> metrics.recordUpdate();
                case "delete" -> metrics.recordDelete();
                default -> metrics.recordQuery(); // Por defecto, tratar como consulta
            }
        }
    }
    
    /**
     * Obtiene métricas de un tenant
     */
    public TenantMetrics getMetrics(@NotNull String tenantId) {
        TenantRegistryEntry entry = tenants.get(tenantId);
        return entry != null ? entry.getMetrics() : null;
    }
    
    /**
     * Obtiene estadísticas generales del registro
     */
    public RegistryStats getStats() {
        int totalTenants = tenants.size();
        int activeTenants = (int) tenants.values().stream().filter(TenantRegistryEntry::isActive).count();
        
        long totalOperations = tenants.values().stream()
                .mapToLong(entry -> entry.getMetrics().getTotalOperations())
                .sum();
        
        return new RegistryStats(totalTenants, activeTenants, totalOperations);
    }
    
    /**
     * Estadísticas del registro
     */
    public static class RegistryStats {
        private final int totalTenants;
        private final int activeTenants;
        private final long totalOperations;
        
        public RegistryStats(int totalTenants, int activeTenants, long totalOperations) {
            this.totalTenants = totalTenants;
            this.activeTenants = activeTenants;
            this.totalOperations = totalOperations;
        }
        
        public int getTotalTenants() { return totalTenants; }
        public int getActiveTenants() { return activeTenants; }
        public long getTotalOperations() { return totalOperations; }
    }
    
    /**
     * Limpia tenants inactivos por más de cierto tiempo
     */
    public void cleanupInactiveTenants(long maxIdleTimeMs) {
        long currentTime = System.currentTimeMillis();
        
        tenants.entrySet().removeIf(entry -> {
            TenantRegistryEntry tenantEntry = entry.getValue();
            long lastAccess = tenantEntry.getMetrics().getLastAccessTime();
            
            if (lastAccess > 0 && (currentTime - lastAccess) > maxIdleTimeMs) {
                log.info("Limpiando tenant inactivo: {} ({})", 
                        tenantEntry.getDisplayName(), tenantEntry.getTenantId());
                contextManager.unregisterTenant(tenantEntry.getTenantId());
                return true;
            }
            return false;
        });
    }
    
    /**
     * Inicializa el registro con tenants predefinidos
     */
    public void initializeDefaultTenants() {
        // Registrar tenant por defecto
        registerTenant("accounting", "accounting", "Contabilidad por defecto", true);
        
        log.info("Registro de tenants inicializado con tenants por defecto");
    }
}