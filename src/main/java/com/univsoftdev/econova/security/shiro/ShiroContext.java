package com.univsoftdev.econova.security.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class ShiroContext {

    public static ShiroUserPrincipal getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return (ShiroUserPrincipal) subject.getPrincipal();
        }
        return null;
    }

    public static String getCurrentUsername() {
        ShiroUserPrincipal principal = getCurrentUser();
        return principal != null ? principal.getUsername() : null;
    }

    public static String getCurrentTenantId() {
        ShiroUserPrincipal principal = getCurrentUser();
        return principal != null ? principal.getTenantId() : "default";
    }

    public static boolean hasRole(String role) {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.hasRole(role);
    }

    public static boolean isAuthenticated() {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.isAuthenticated();
    }
}
