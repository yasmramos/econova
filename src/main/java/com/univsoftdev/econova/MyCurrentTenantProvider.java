package com.univsoftdev.econova;

import io.ebean.config.CurrentTenantProvider;

public class MyCurrentTenantProvider implements CurrentTenantProvider {

    @Override
    public Object currentId() {
        // Usa el mismo ThreadLocal que MyTenantSchemaProvider
        String tenant = MyTenantSchemaProvider.getCurrentTenant().get();
        return (tenant != null && !tenant.isBlank()) ? tenant : "accounting";
    }
}
