package com.univsoftdev.econova.security.shiro;

import com.univsoftdev.econova.config.model.Role;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.UserContext;
import com.univsoftdev.econova.ebean.config.MyTenantSchemaProvider;
import com.univsoftdev.econova.security.argon2.Argon2CredentialsMatcher;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Realm personalizado de Apache Shiro para autenticación y autorización.
 *
 * <p>
 * Esta clase implementa la lógica de seguridad de la aplicación integrando:
 * <ul>
 * <li>Autenticación de usuarios contra base de datos Ebean</li>
 * <li>Autorización basada en roles y permisos</li>
 * <li>Soporte multi-tenant mediante {@link MyTenantSchemaProvider}</li>
 * <li>Integración con {@link ShiroUserPrincipal} para información de
 * usuario</li>
 * </ul>
 * </p>
 *
 * <p>
 * El realm maneja automáticamente el cambio de contexto de tenant durante las
 * operaciones de autorización para garantizar el aislamiento de datos.</p>
 *
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 *
 * @see AuthorizingRealm
 * @see ShiroUserPrincipal
 * @see MyTenantSchemaProvider
 * @see User
 * @see Role
 */
@Slf4j
@Singleton
public class CustomRealm extends AuthorizingRealm {

    /**
     * Base de datos Ebean para acceso a datos.
     */
    private final Database database;
    private final Argon2CredentialsMatcher argon2CredentialsMatcher;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param database Instancia de base de datos Ebean (no null)
     * @param argon2CredentialsMatcher
     *
     * @throws IllegalArgumentException si alguna dependencia es null
     */
    @Inject
    public CustomRealm(Database database, Argon2CredentialsMatcher argon2CredentialsMatcher) {
        this.database = Objects.requireNonNull(database);
        this.argon2CredentialsMatcher = Objects.requireNonNull(argon2CredentialsMatcher);
        setAuthorizationCachingEnabled(true);
        setAuthorizationCacheName("authorizationCache");
        setAuthenticationTokenClass(UsernamePasswordToken.class);
        setCredentialsMatcher(this.argon2CredentialsMatcher);
    }

    /**
     * Obtiene la información de autorización para un usuario autenticado.
     *
     * <p>
     * Este método:
     * <ol>
     * <li>Extrae el principal del usuario autenticado</li>
     * <li>Establece el contexto de tenant apropiado</li>
     * <li>Recupera roles y permisos del usuario</li>
     * <li>Construye la información de autorización</li>
     * <li>Limpia el contexto de tenant</li>
     * </ol>
     * </p>
     *
     * @param token
     * @return Información de autorización con roles y permisos del usuario
     *
     * @see ShiroUserPrincipal
     * @see SimpleAuthorizationInfo
     * @see MyTenantSchemaProvider#setCurrentTenant(String)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        if (StringUtils.isBlank(username)) {
            log.error("Token con username null o vacío recibido");
            throw new AuthenticationException("Username cannot be null or empty");
        }

        // Verificar intentos fallidos
        if (LoginAttemptCache.isAccountLocked(username)) {
            throw new ExcessiveAttemptsException("Cuenta bloqueada temporalmente por muchos intentos fallidos");
        }

        try {
            User user = database.find(User.class)
                    .where()
                    .eq("userName", username)
                    .setMaxRows(1)
                    .findOne();

            if (user == null) {
                com.univsoftdev.econova.security.SecurityUtils.delayRandom(500, 1500);
                LoginAttemptCache.recordFailedAttempt(username);
                throw new UnknownAccountException("Usuario no encontrado");
            }

            if (!user.isActive()) {
                throw new DisabledAccountException("Cuenta deshabilitada");
            }

            // Verificar que el hash almacenado es válido
            if (StringUtils.isBlank(user.getPassword())) {
                throw new CredentialsException("Credenciales no configuradas correctamente");
            }

            return new SimpleAuthenticationInfo(
                    new ShiroUserPrincipal(user),
                    user.getPassword(),
                    getName()
            );
        } catch (AuthenticationException e) {
            LoginAttemptCache.recordFailedAttempt(username);
            throw e;
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null || principals.isEmpty()) {
            log.warn("Attempted authorization with null or empty principals");
            return null;
        }

        ShiroUserPrincipal principal = (ShiroUserPrincipal) principals.getPrimaryPrincipal();
        if (principal == null) {
            log.warn("No primary principal found in principals collection");
            return null;
        }

        User user = principal.getUser();
        if (user == null) {
            log.warn("User not found in principal");
            return null;
        }

        log.debug("Building authorization info for user: {} in tenant: {}",
                user.getUserName(), user.getTenantId());

        UserContext.get().setUser(user);
        UserContext.set(user.getUserName(), user.getTenantId());

        try {
            SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

            User freshUser = database.find(User.class)
                    .setId(user.getId())
                    .fetch("roles") // Primero fetch de roles
                    .fetch("roles.permissions", "name") // Luego fetch de permisos
                    .setMaxRows(1)
                    .findOne();

            if (freshUser != null && freshUser.getRoles() != null) {
                freshUser.getRoles().stream()
                        .filter(role -> role != null && role.getName() != null)
                        .forEach(role -> {
                            authorizationInfo.addRole(role.getName());

                            if (role.getPermissions() != null) {
                                role.getPermissions().stream()
                                        .filter(permission -> permission != null && permission.getName() != null)
                                        .map(permission -> permission.getName())
                                        .forEach(authorizationInfo::addStringPermission);
                            }
                        });
            }

            log.debug("Authorization info built successfully for user: {}. Roles: {}, Permissions: {}",
                    user.getUserName(),
                    authorizationInfo.getRoles().size(),
                    authorizationInfo.getStringPermissions().size());

            return authorizationInfo;
        } finally {
            UserContext.reset();
        }
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        String username = null;
        try {
            // Obtenemos el username del token
            UsernamePasswordToken upToken = (UsernamePasswordToken) token;
            username = upToken.getUsername();

            // Verificamos que tengamos credenciales válidas
            if (username == null || upToken.getPassword() == null) {
                throw new AuthenticationException("Credenciales inválidas");
            }

            // Llamamos al método original para verificar credenciales
            super.assertCredentialsMatch(token, info);

            // Si llegamos aquí, la autenticación fue exitosa
            if (username != null) {
                LoginAttemptCache.clearFailedAttempts(username);
            }

        } catch (AuthenticationException e) {
            // Autenticación fallida - registramos el intento
            if (username != null) {
                log.error("Falló autenticación para: {}", username, e);

                int attempts = LoginAttemptCache.recordFailedAttempt(username);

                // Bloqueamos la cuenta si se supera el límite
                if (attempts >= LoginAttemptCache.MAX_ATTEMPTS) {
                    log.warn("Bloqueo de cuenta por intentos fallidos: {}", username);

                    try {
                        database.update(User.class)
                                .set("active", false)
                                .where()
                                .eq("userName", username)
                                .update();
                    } catch (Exception dbException) {
                        log.error("Error al bloquear cuenta del usuario: {}", username, dbException);
                    }
                }
            } else {
                log.error("Falló autenticación con username null", e);
            }

            // Relanzamos la excepción para que Shiro maneje el fallo de autenticación
            throw e;
        }
    }
}
