package com.univsoftdev.econova;

import com.univsoftdev.econova.core.multitenancy.TenantContext;
import io.ebean.config.CurrentTenantProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * Proveedor mejorado de tenant actual que usa el nuevo sistema de contexto de tenant.
 * Integra con TenantContext para proporcionar información consistente del tenant.
 */
@Slf4j
public class MyCurrentTenantProvider implements CurrentTenantProvider {

    @Override
    public Object currentId() {
        // Usar el nuevo sistema de contexto de tenant
        String tenantId = TenantContext.getCurrentTenantId();
        log.debug("Obteniendo tenant ID actual: {}", tenantId);
        return tenantId;
    }
}
