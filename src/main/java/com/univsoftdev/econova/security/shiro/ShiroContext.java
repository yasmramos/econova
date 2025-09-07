package com.univsoftdev.econova.security.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Utilidad para acceder a la información de seguridad del contexto actual de Shiro.
 * Proporciona métodos convenientes para obtener información del usuario autenticado
 * y verificar permisos.
 */
@Slf4j
public class ShiroContext {
    
    private ShiroContext() {
        // Clase de utilidad, no instanciable
    }

    /**
     * Obtiene el usuario principal autenticado actual.
     *
     * @return ShiroUserPrincipal del usuario actual, o null si no hay usuario autenticado
     */
    public static ShiroUserPrincipal getCurrentUser() {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject != null && subject.isAuthenticated()) {
                Object principal = subject.getPrincipal();
                if (principal instanceof ShiroUserPrincipal shiroUserPrincipal) {
                    return shiroUserPrincipal;
                }
                log.warn("Principal no es instancia de ShiroUserPrincipal: {}", 
                        principal != null ? principal.getClass().getName() : "null");
            }
            return null;
        } catch (Exception e) {
            log.error("Error obteniendo usuario actual", e);
            return null;
        }
    }

    /**
     * Obtiene el nombre de usuario del usuario autenticado actual.
     *
     * @return Nombre de usuario, o null si no hay usuario autenticado
     */
    public static String getCurrentUsername() {
        ShiroUserPrincipal principal = getCurrentUser();
        return principal != null ? principal.getUsername() : null;
    }

    /**
     * Obtiene el ID del tenant del usuario autenticado actual.
     *
     * @return ID del tenant, o "default" si no hay usuario autenticado o no tiene tenant
     */
    public static String getCurrentTenantId() {
        ShiroUserPrincipal principal = getCurrentUser();
        String tenantId = principal != null ? principal.getTenantId() : null;
        return tenantId != null && !tenantId.isEmpty() ? tenantId : "default";
    }

    /**
     * Verifica si el usuario actual tiene un rol específico.
     *
     * @param role Nombre del rol a verificar
     * @return true si el usuario tiene el rol, false en caso contrario
     * @throws IllegalArgumentException si el rol es null o vacío
     */
    public static boolean hasRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            log.warn("Verificación de rol con valor null o vacío");
            return false;
        }
        
        try {
            Subject subject = SecurityUtils.getSubject();
            return subject != null && subject.hasRole(role);
        } catch (Exception e) {
            log.error("Error verificando rol: {}", role, e);
            return false;
        }
    }

    /**
     * Verifica si el usuario actual tiene uno de los roles especificados.
     *
     * @param roles Array de nombres de roles a verificar
     * @return true si el usuario tiene al menos uno de los roles, false en caso contrario
     */
    public static boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject == null) {
                return false;
            }
            
            for (String role : roles) {
                if (role != null && subject.hasRole(role)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Error verificando roles", e);
            return false;
        }
    }

    /**
     * Verifica si el usuario actual tiene un permiso específico.
     *
     * @param permission Nombre del permiso a verificar
     * @return true si el usuario tiene el permiso, false en caso contrario
     */
    public static boolean isPermitted(String permission) {
        if (permission == null || permission.trim().isEmpty()) {
            log.warn("Verificación de permiso con valor null o vacío");
            return false;
        }
        
        try {
            Subject subject = SecurityUtils.getSubject();
            return subject != null && subject.isPermitted(permission);
        } catch (Exception e) {
            log.error("Error verificando permiso: {}", permission, e);
            return false;
        }
    }

    /**
     * Verifica si el usuario actual está autenticado.
     *
     * @return true si hay un usuario autenticado, false en caso contrario
     */
    public static boolean isAuthenticated() {
        try {
            Subject subject = SecurityUtils.getSubject();
            return subject != null && subject.isAuthenticated();
        } catch (Exception e) {
            log.error("Error verificando autenticación", e);
            return false;
        }
    }

    /**
     * Verifica si hay un usuario (autenticado o recordado) en sesión.
     *
     * @return true si hay un usuario en sesión, false en caso contrario
     */
    public static boolean isUser() {
        try {
            Subject subject = SecurityUtils.getSubject();
            return subject != null && subject.getPrincipal() != null;
        } catch (Exception e) {
            log.error("Error verificando si hay usuario", e);
            return false;
        }
    }

    /**
     * Obtiene el ID del usuario actual.
     *
     * @return ID del usuario, o null si no hay usuario autenticado
     */
    public static Long getCurrentUserId() {
        ShiroUserPrincipal principal = getCurrentUser();
        return principal != null ? principal.getUserId() : null;
    }

    /**
     * Obtiene el nombre completo del usuario actual.
     *
     * @return Nombre completo del usuario, o null si no hay usuario autenticado
     */
    public static String getCurrentUserFullName() {
        ShiroUserPrincipal principal = getCurrentUser();
        return principal != null ? principal.getFullName() : null;
    }
}