package com.univsoftdev.econova.core;

import com.univsoftdev.econova.config.model.User;
import io.ebean.DB;

public final class UserContext {

    private static final UserContextThreadLocal context = new UserContextThreadLocal();

    private String userId;
    private String tenantId;
    private String tenantSchema;
    private final String defaultSchema = "accounting";
    private User user;

    private UserContext(String userId, String tenantId, String tenantSchema) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.tenantSchema = tenantSchema;
    }

    private UserContext() {
    }

    public String getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public static UserContext get() {
        UserContext ctx = context.get();
        if (ctx == null) {
            throw new IllegalStateException("UserContext no establecido");
        }
        return ctx;
    }

    public static void reset() {
        context.remove();
    }

    public static void set(String tenantId) {
        String schema = tenantId.toLowerCase();
        // Verificar que el esquema existe
        if (!schemaExists(schema)) {
            throw new IllegalStateException("Schema " + schema + " does not exist");
        }
        set("System", tenantId, schema);
    }

    private static boolean schemaExists(String schema) {
        return DB.getDefault()
                .sqlQuery("SELECT 1 FROM information_schema.schemata WHERE schema_name = :schema")
                .setParameter("schema", schema)
                .findOne() != null;
    }

    public static void set(String userId, String tenantId) {
        set(userId, tenantId, tenantId.toLowerCase());
    }

    public static void set(String userId, String tenantId, String tenantSchema) {
        context.set(new UserContext(userId, tenantId, tenantSchema));
        DB.getDefault().sqlUpdate("SET search_path TO " + tenantSchema).execute();
    }

    public String getTenantSchema() {
        return tenantSchema;
    }

    public void setTenantSchema(String tenantSchema) {
        this.tenantSchema = tenantSchema;
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getUserName();
    }

    private static class UserContextThreadLocal extends ThreadLocal<UserContext> {

        @Override
        protected UserContext initialValue() {
            return new UserContext();
        }
    }
}
