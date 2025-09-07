package com.univsoftdev.econova.security.shiro;

import com.univsoftdev.econova.config.model.User;
import java.io.Serializable;
import java.util.Objects;

/**
 * Principal de usuario para Apache Shiro. Envuelve un objeto User del dominio
 * para ser utilizado por Shiro.
 */
public class ShiroUserPrincipal implements Serializable {

    private static final long serialVersionUID = 1L;

    private final User user;

    public ShiroUserPrincipal(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
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

    public Long getUserId() {
        return user.getId();
    }

    public String getFullName() {
        return user.getFullName();
    }

    public boolean isActive() {
        return user.isActive();
    }

    @Override
    public String toString() {
        return "ShiroUserPrincipal{username='" + getUsername() + "', userId=" + getUserId() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShiroUserPrincipal that = (ShiroUserPrincipal) o;
        return Objects.equals(getUserId(), that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}
