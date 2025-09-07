package com.univsoftdev.econova;

import com.univsoftdev.econova.config.model.User;

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

    public static void set(String userId, String tenantId) {
        set(userId, tenantId, "tenant_" + tenantId.toLowerCase());
    }

    public static void set(String tenantId) {
        set("System", tenantId, "tenant_" + tenantId.toLowerCase());
    }

    public static void set(String userId, String tenantId, String tenantSchema) {
        context.set(new UserContext(userId, tenantId, tenantSchema));
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
