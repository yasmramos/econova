package com.univsoftdev.econova;

import com.univsoftdev.econova.core.multitenancy.TenantContext;
import io.ebean.config.TenantSchemaProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * Proveedor mejorado de esquema de tenant que usa el nuevo sistema de contexto de tenant.
 * Integra con TenantContext para proporcionar información consistente del esquema.
 */
@Slf4j
public class MyTenantSchemaProvider implements TenantSchemaProvider {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private static final String DEFAULT_SCHEMA = "accounting";

    /**
     * Establece el tenant actual en el ThreadLocal (para compatibilidad con código existente)
     */
    public static void setTenant(String tenant) {
        currentTenant.set(tenant);
        log.debug("Establecido tenant en ThreadLocal: {}", tenant);
    }

    /**
     * Limpia el tenant actual del ThreadLocal
     */
    public static void clearTenant() {
        currentTenant.remove();
        log.debug("Limpiado tenant del ThreadLocal");
    }

    /**
     * Obtiene el ThreadLocal actual (para compatibilidad con código existente)
     */
    public static ThreadLocal<String> getCurrentTenant() {
        return currentTenant;
    }

    @Override
    public String schema(Object tenantId) {
        // Prioridad: TenantContext -> ThreadLocal -> Default
        String schema = TenantContext.getCurrentSchema();
        
        if (schema == null || schema.isBlank()) {
            String tenant = currentTenant.get();
            schema = (tenant != null && !tenant.isBlank()) ? tenant : DEFAULT_SCHEMA;
        }
        
        log.debug("Obteniendo esquema para tenant ID '{}': {}", tenantId, schema);
        return schema;
    }
}
