package com.univsoftdev.econova.security.shiro;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;

@Slf4j
@Singleton
public class ShiroConfig {

    private final CustomRealm customRealm;

    @Inject
    public ShiroConfig(CustomRealm customRealm) {
        this.customRealm = customRealm;
        initializeShiro();
    }

    private void initializeShiro() {
        try {
            log.info("Inicializando Apache Shiro para aplicación de escritorio");

            // Crear security manager
            DefaultSecurityManager securityManager = new DefaultSecurityManager();
            securityManager.setRealm(customRealm);

            // Configurar session manager para desktop
            SessionManager sessionManager = createDesktopSessionManager();
            securityManager.setSessionManager(sessionManager);

            // Deshabilitar remember me para desktop
            securityManager.setRememberMeManager(null);

            // Establecer security manager global
            SecurityUtils.setSecurityManager(securityManager);

            log.info("Apache Shiro inicializado correctamente para desktop");

        } catch (Exception e) {
            log.error("Error inicializando Apache Shiro para desktop", e);
            throw new RuntimeException("Fallo en la inicialización de seguridad", e);
        }
    }

    private SessionManager createDesktopSessionManager() {
        DefaultSessionManager sessionManager = new DefaultSessionManager();

        // Configuración optimizada para aplicaciones de escritorio
        sessionManager.setSessionValidationSchedulerEnabled(false); // No necesitamos validación frecuente
        sessionManager.setDeleteInvalidSessions(true);

        // Timeout de sesión (30 minutos por defecto)
        sessionManager.setGlobalSessionTimeout(30 * 60 * 1000L);

        return sessionManager;
    }

    /**
     * Limpia la sesión actual si existe
     */
    public void clearCurrentSession() {
        try {
            var subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {
                subject.logout();
                log.debug("Sesión actual cerrada");
            }
        } catch (Exception e) {
            log.warn("Error cerrando sesión actual", e);
        }
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public boolean isAuthenticated() {
        try {
            return SecurityUtils.getSubject().isAuthenticated();
        } catch (Exception e) {
            return false;
        }
    }
}
