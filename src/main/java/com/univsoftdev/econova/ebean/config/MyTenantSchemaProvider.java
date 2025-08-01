package com.univsoftdev.econova.ebean.config;

import io.ebean.config.TenantSchemaProvider;

public class MyTenantSchemaProvider implements TenantSchemaProvider {

    @Override
    public String schema(Object tenantId) {
        return "tenant_" + tenantId;
    }
}
