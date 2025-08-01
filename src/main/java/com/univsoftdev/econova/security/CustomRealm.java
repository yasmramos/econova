package com.univsoftdev.econova.security;

import com.univsoftdev.econova.UserContext;
import com.univsoftdev.econova.config.model.Rol;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.ebean.config.MyTenantSchemaProvider;
import com.univsoftdev.econova.security.shiro.ShiroUserPrincipal;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
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
 * @see Rol
 */
@Slf4j
@Singleton
public class CustomRealm extends AuthorizingRealm {

    /**
     * Base de datos Ebean para acceso a datos.
     */
    private final Database database;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param database Instancia de base de datos Ebean (no null)
     *
     * @throws IllegalArgumentException si alguna dependencia es null
     */
    @Inject
    public CustomRealm(Database database) {
        if (database == null) {
            throw new IllegalArgumentException("Database cannot be null");
        }

        this.database = database;
        setAuthenticationTokenClass(UsernamePasswordToken.class);

        log.info("CustomRealm initialized with database and tenant provider");
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
     * @param principals Colección de principals del usuario autenticado
     * @return Información de autorización con roles y permisos del usuario
     *
     * @see ShiroUserPrincipal
     * @see SimpleAuthorizationInfo
     * @see MyTenantSchemaProvider#setCurrentTenant(String)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // Validar que existan principals
        if (principals == null || principals.isEmpty()) {
            log.warn("Authorization attempt with null or empty principals");
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

        log.debug("Getting authorization info for user: {} in tenant: {}",
                user.getUserName(), user.getTenantId());

        // Establecer el tenant antes de realizar cualquier operación
        UserContext.set(user.getUserName(), user.getTenantId());

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        Set<Rol> roles = user.getRoles();
        if (roles != null && !roles.isEmpty()) {
            for (Rol role : roles) {
                if (role != null && role.getName() != null) {
                    info.addRole(role.getName());

                    // Obtener permisos del rol
                    if (role.getPermissions() != null) {
                        List<String> permissions = role.getPermissions().stream()
                                .map(permission -> permission != null ? permission.getName() : null)
                                .filter(permissionName -> permissionName != null)
                                .collect(Collectors.toList());

                        Collection<String> permisos = permissions;
                        info.addStringPermissions(permisos);
                    }
                }
            }
        }

        log.debug("Authorization info retrieved for user: {}. Roles: {}, Permissions: {}",
                user.getUserName(), info.getRoles().size(), info.getStringPermissions().size());

        return info;

    }

    /**
     * Realiza la autenticación de un usuario mediante credenciales.
     *
     * <p>
     * Este método:
     * <ol>
     * <li>Valida el token de autenticación</li>
     * <li>Busca el usuario en la base de datos (schema público)</li>
     * <li>Verifica que la cuenta esté activa</li>
     * <li>Crea la información de autenticación</li>
     * </ol>
     * </p>
     *
     * @param token Token de autenticación con credenciales del usuario
     * @return Información de autenticación con credenciales verificadas
     * @throws AuthenticationException si la autenticación falla
     * @throws UnknownAccountException si el usuario no existe
     * @throws DisabledAccountException si la cuenta está deshabilitada
     * @throws IncorrectCredentialsException si las credenciales son incorrectas
     *
     * @see UsernamePasswordToken
     * @see SimpleAuthenticationInfo
     * @see User
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // Validar token
        if (token == null) {
            throw new AuthenticationException("Authentication token cannot be null");
        }

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        // Validar nombre de usuario
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be null or empty");
        }

        log.debug("Attempting authentication for user: {}", username);

        // Buscar usuario sin tenant específico (schema público)
        User user = database.find(User.class)
                .where()
                .eq("userName", username)
                .findOne();

        if (user == null) {
            log.warn("Authentication failed: user not found - {}", username);
            throw new UnknownAccountException("No existe usuario con nombre: " + username);
        }

        // Verificar si el usuario está activo
        if (!user.isActive()) {
            log.warn("Authentication failed: user account disabled - {}", username);
            throw new DisabledAccountException("Cuenta deshabilitada para el usuario: " + username);
        }

        log.debug("Authentication successful for user: {} in tenant: {}",
                user.getUserName(), user.getTenantId());

        // Crear principal con información del tenant
        ShiroUserPrincipal principal = new ShiroUserPrincipal(user);

        return new SimpleAuthenticationInfo(
                principal,
                user.getPassword(), // El hash de contraseña almacenado
                getName() // Nombre del realm
        );
    }

    /**
     * Obtiene la base de datos Ebean configurada.
     *
     * @return La instancia de base de datos
     */
    protected Database getDatabase() {
        return database;
    }

}
