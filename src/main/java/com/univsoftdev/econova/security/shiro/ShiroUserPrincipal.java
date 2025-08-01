package com.univsoftdev.econova.security.shiro;

import com.univsoftdev.econova.config.model.User;

public class ShiroUserPrincipal {

    private final User user;

    public ShiroUserPrincipal(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return user.getUserName();
    }

    public String getTenantId() {
        return user.getTenantId();
    }

    @Override
    public String toString() {
        return user.getUserName();
    }
}
