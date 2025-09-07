package com.univsoftdev.econova.security;

import com.google.crypto.tink.KeysetHandle;
import com.licify.Licify;
import com.univsoftdev.econova.config.model.User;
import java.security.PublicKey;
import java.util.Optional;

public class SecurityContext {

    private static final ThreadLocal<AuthenticatedUser> currentUser = new ThreadLocal<>();
    private static KeysetHandle privateKeysetHandle;
    private static KeysetHandle publicKeysetHandle;
    private static PublicKey publicKey;
    private static Licify.License license;

    public static PublicKey getPublicKey() {
        return publicKey;
    }

    public static void setPublicKey(PublicKey publicKey) {
        SecurityContext.publicKey = publicKey;
    }

    public static void setLicense(Licify.License license) {
        SecurityContext.license = license;
    }

    public static Licify.License getLicense() {
        return license;
    }

    public static class AuthenticatedUser {

        private final User user;
        private final String tenantId;

        public AuthenticatedUser(User user, String tenantId) {
            this.user = user;
            this.tenantId = tenantId;
        }

        public User getUser() {
            return user;
        }

        public String getTenantId() {
            return tenantId;
        }
    }

    /**
     * Establece el usuario autenticado en el contexto actual
     *
     * @param user
     * @param tenantId
     */
    public static void setCurrentUser(User user, String tenantId) {
        currentUser.set(new AuthenticatedUser(user, tenantId));
    }

    /**
     * Limpia el contexto de seguridad
     */
    public static void clear() {
        currentUser.remove();
    }

    /**
     * Obtiene el usuario actualmente autenticado
     *
     * @return
     */
    public static Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser.get())
                .map(AuthenticatedUser::getUser);
    }

    /**
     * Obtiene el tenant ID del usuario actual
     *
     * @return
     */
    public static Optional<String> getCurrentTenantId() {
        return Optional.ofNullable(currentUser.get())
                .map(AuthenticatedUser::getTenantId);
    }

    /**
     * Verifica si hay un usuario autenticado
     *
     * @return
     */
    public static boolean isAuthenticated() {
        return currentUser.get() != null;
    }

    public static KeysetHandle getPrivateKeysetHandle() {
        return privateKeysetHandle;
    }

    public static void setPrivateKeysetHandle(KeysetHandle privateKeysetHandle) {
        SecurityContext.privateKeysetHandle = privateKeysetHandle;
    }

    public static KeysetHandle getPublicKeysetHandle() {
        return publicKeysetHandle;
    }

    public static void setPublicKeysetHandle(KeysetHandle publicKeysetHandle) {
        SecurityContext.publicKeysetHandle = publicKeysetHandle;
    }
    
    

}
