package com.univsoftdev.econova;

import io.ebean.config.TenantSchemaProvider;

public class MyTenantSchemaProvider implements TenantSchemaProvider {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private static final String DEFAULT_SCHEMA = "accounting";

    public static void setTenant(String tenant) {
        currentTenant.set(tenant);
    }

    public static void clearTenant() {
        currentTenant.remove();
    }

    @Override
    public String schema(Object o) {
        String tenant = currentTenant.get();
        return (tenant != null && !tenant.isBlank()) ? tenant : DEFAULT_SCHEMA;
    }

    public static ThreadLocal<String> getCurrentTenant() {
        return currentTenant;
    }

}
