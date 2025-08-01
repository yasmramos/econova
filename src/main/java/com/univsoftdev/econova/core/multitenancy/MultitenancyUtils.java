package com.univsoftdev.econova.core.multitenancy;

import com.univsoftdev.econova.core.multitenancy.TenantContext;
import com.univsoftdev.econova.core.multitenancy.MultitenancyService;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * Utilidades para facilitar el trabajo con multitenancy en toda la aplicación.
 * Proporciona métodos de conveniencia y funciones helper.
 */
@Singleton
@Slf4j
public class MultitenancyUtils {
    
    private final MultitenancyService multitenancyService;
    
    public MultitenancyUtils(MultitenancyService multitenancyService) {
        this.multitenancyService = multitenancyService;
    }
    
    /**
     * Ejecuta una operación con manejo automático de errores de tenant
     */
    public <T> T executeWithTenantHandling(@NotNull TenantOperation<T> operation) {
        try {
            // Asegurar que hay un contexto de tenant
            ensureTenantContext();
            
            return operation.execute();
        } catch (Exception e) {
            log.error("Error ejecutando operación en tenant: {}", 
                    TenantContext.getCurrentTenantId(), e);
            throw new RuntimeException("Error en operación de tenant", e);
        }
    }
    
    /**
     * Ejecuta una operación sin retorno con manejo automático de errores de tenant
     */
    public void executeWithTenantHandling(@NotNull TenantRunnable runnable) {
        executeWithTenantHandling(() -> {
            runnable.run();
            return null;
        });
    }
    
    /**
     * Asegura que existe un contexto de tenant válido
     */
    public void ensureTenantContext() {
        if (TenantContext.getCurrent().isEmpty()) {
            multitenancyService.setTenantFromCurrentUser();
        }
    }
    
    /**
     * Valida que el tenant actual sea válido
     */
    public void validateCurrentTenant() {
        TenantContext context = TenantContext.getCurrent()
                .orElseThrow(() -> new IllegalStateException("No hay contexto de tenant"));
        
        if (!context.isValid()) {
            throw new IllegalStateException("Contexto de tenant inválido: " + context);
        }
        
        if (!multitenancyService.isTenantActive(context.getTenantId())) {
            throw new IllegalStateException("Tenant inactivo: " + context.getTenantId());
        }
    }
    
    /**
     * Obtiene información detallada del tenant actual
     */
    public TenantInfo getCurrentTenantInfo() {
        TenantContext context = multitenancyService.getCurrentTenantContext();
        
        return new TenantInfo(
                context.getTenantId(),
                context.getSchemaName(),
                context.getCurrentUser() != null ? context.getCurrentUser().getUserName() : null,
                context.getCurrentEmpresa() != null ? context.getCurrentEmpresa().getName() : null,
                context.getCurrentUnidad() != null ? context.getCurrentUnidad().getNombre() : null,
                context.isDefaultTenant(),
                context.isIsolated()
        );
    }
    
    /**
     * Convierte un tenant ID a un nombre de esquema válido
     */
    public static String tenantIdToSchemaName(@NotNull String tenantId) {
        return tenantId.toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("^[0-9]", "t$0"); // Los esquemas no pueden empezar con número
    }
    
    /**
     * Genera un tenant ID único basado en una entidad
     */
    public static String generateTenantId(@NotNull String prefix, @NotNull Object entityId) {
        return prefix + "_" + entityId.toString().replaceAll("-", "_");
    }
    
    /**
     * Verifica si dos tenants son el mismo
     */
    public static boolean isSameTenant(@NotNull String tenantId1, @NotNull String tenantId2) {
        return tenantId1.equals(tenantId2);
    }
    
    /**
     * Verifica si el tenant actual es el mismo que el especificado
     */
    public boolean isCurrentTenant(@NotNull String tenantId) {
        return isSameTenant(TenantContext.getCurrentTenantId(), tenantId);
    }
    
    /**
     * Formatea un tenant ID para mostrar
     */
    public static String formatTenantId(@NotNull String tenantId) {
        return tenantId.replace("_", " ").toUpperCase();
    }
    
    /**
     * Información detallada del tenant
     */
    public static class TenantInfo {
        private final String tenantId;
        private final String schemaName;
        private final String userName;
        private final String empresaName;
        private final String unidadName;
        private final boolean isDefault;
        private final boolean isIsolated;
        
        public TenantInfo(String tenantId, String schemaName, String userName, 
                         String empresaName, String unidadName, boolean isDefault, boolean isIsolated) {
            this.tenantId = tenantId;
            this.schemaName = schemaName;
            this.userName = userName;
            this.empresaName = empresaName;
            this.unidadName = unidadName;
            this.isDefault = isDefault;
            this.isIsolated = isIsolated;
        }
        
        public String getTenantId() { return tenantId; }
        public String getSchemaName() { return schemaName; }
        public String getUserName() { return userName; }
        public String getEmpresaName() { return empresaName; }
        public String getUnidadName() { return unidadName; }
        public boolean isDefault() { return isDefault; }
        public boolean isIsolated() { return isIsolated; }
        
        @Override
        public String toString() {
            return String.format("TenantInfo{id='%s', schema='%s', user='%s', empresa='%s', unidad='%s', default=%s, isolated=%s}",
                    tenantId, schemaName, userName, empresaName, unidadName, isDefault, isIsolated);
        }
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