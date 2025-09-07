package com.univsoftdev.econova.security.argon2;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

@Singleton
@Slf4j
public class Argon2CredentialsMatcher implements CredentialsMatcher {

    private final PasswordHasher passwordHasher;

    @Inject
    public Argon2CredentialsMatcher(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        // Validación temprana del tipo de token
        if (!(token instanceof UsernamePasswordToken)) {
            log.error("Token no es del tipo UsernamePasswordToken: {}",
                    token != null ? token.getClass().getSimpleName() : "null");
            return false;
        }

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        try {
            // Validación de username
            if (upToken.getUsername() == null || upToken.getUsername().trim().isEmpty()) {
                log.warn("Username vacío o nulo recibido");
                return false;
            }

            // Validación de contraseña
            char[] passwordChars = upToken.getPassword();
            if (passwordChars == null || passwordChars.length == 0) {
                log.warn("Contraseña vacía recibida para usuario: {}", upToken.getUsername());
                return false;
            }

            // Validación de credenciales almacenadas
            Object credentials = info.getCredentials();
            if (credentials == null) {
                log.error("Credenciales nulas para usuario: {}", upToken.getUsername());
                return false;
            }

            if (!(credentials instanceof String)) {
                log.error("Credenciales no son del tipo String para usuario: {}. Tipo recibido: {}",
                        upToken.getUsername(), credentials.getClass().getSimpleName());
                return false;
            }

            String storedHash = (String) credentials;
            if (storedHash.isEmpty()) {
                log.error("Hash almacenado es vacío para usuario: {}", upToken.getUsername());
                return false;
            }

            // Depuración detallada (solo en desarrollo)
            if (log.isDebugEnabled()) {
                log.debug("Verificando contraseña para usuario: {}", upToken.getUsername());
                log.debug("Hash almacenado longitud: {}", storedHash.length());
                log.debug("Contraseña ingresada longitud: {}", passwordChars.length);
            }

            // Verificación de la contraseña
            boolean result = passwordHasher.verify(storedHash, passwordChars);

            if (log.isDebugEnabled()) {
                log.debug("Resultado de verificación para usuario {}: {}",
                        upToken.getUsername(), result);
                if (!result) {
                    log.debug("Verificación fallida para usuario {}. Posibles causas: "
                            + "1. Contraseña incorrecta "
                            + "2. Hash corrupto "
                            + "3. Configuración de Argon2 diferente", upToken.getUsername());
                }
            } else if (!result) {
                // Log básico en producción
                log.info("Intento de login fallido para usuario: {}", upToken.getUsername());
            }

            return result;

        } catch (Exception e) {
            log.error("Error crítico durante verificación de contraseña para usuario: {}",
                    upToken != null && upToken.getUsername() != null ? upToken.getUsername() : "unknown", e);
            return false;
        } finally {
            // Limpiar el token siempre, incluso si hay excepciones
            if (upToken != null) {
                try {
                    upToken.clear();
                } catch (Exception e) {
                    log.warn("Error al limpiar token para usuario: {}",
                            upToken.getUsername() != null ? upToken.getUsername() : "unknown", e);
                }
            }
        }
    }

    /**
     * Método para verificación directa (uso en pruebas o utilidades)
     *
     * @param hash Hash almacenado
     * @param password Contraseña a verificar
     * @return true si coinciden, false en caso contrario
     */
    public boolean verifyDirectly(String hash, char[] password) {
        if (hash == null || hash.isEmpty() || password == null || password.length == 0) {
            return false;
        }
        try {
            return passwordHasher.verify(hash, password);
        } catch (Exception e) {
            log.error("Error en verificación directa", e);
            return false;
        }
    }
}
