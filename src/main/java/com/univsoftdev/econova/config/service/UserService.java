package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.model.Role;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.config.repository.UserRepository;
import com.univsoftdev.econova.contabilidad.model.PasswordHistory;
import com.univsoftdev.econova.core.Validations;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import com.univsoftdev.econova.core.service.BaseService;
import com.univsoftdev.econova.security.Permissions;
import com.univsoftdev.econova.security.Roles;
import com.univsoftdev.econova.security.argon2.PasswordHasher;
import com.univsoftdev.econova.security.shiro.annotations.RequiresPermissions;
import com.univsoftdev.econova.security.shiro.annotations.RequiresRoles;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;

/**
 * Servicio para gestión de usuarios, autenticación y control de acceso.
 */
@Slf4j
@Singleton
public class UserService extends BaseService<User, UserRepository> {

    private final PasswordHasher passwordHasher;
    private final RoleService roleService;

    @Inject
    public UserService(UserRepository database, RoleService roleService, PasswordHasher passwordHasher) {
        super(database);
        this.roleService = roleService;
        this.passwordHasher = passwordHasher;
    }

    public List<User> findByActivos() {
        return findAll().stream().filter(u -> u.isActive() == true).toList();
    }

    public List<User> findByInactivos() {
        return findAll().stream().filter(u -> u.isActive() == false).toList();
    }

    /**
     * Crea un nuevo usuario con validaciones y seguridad
     *
     * @param fullName
     * @param userName
     * @param email
     * @param password
     * @return
     */
    @RequiresPermissions(value = {Permissions.CREATE_USER})
    @RequiresRoles(value = {
        Roles.SYSTEM_ADMIN
    })
    public User createUser(String userName, String fullName, String email,
            char[] password) {
        validarIdentificadorUnico(userName);
        Validations.validatePasswordStrength(password);

        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setUserName(userName);

        if (email != null || !email.isEmpty()) {
            validarEmailUnico(email);
            Validations.isValidEmail(email);
            newUser.setEmail(email);
        }

        String hash = passwordHasher.hash(password);
        newUser.setPassword(hash);
        newUser.getPasswordHistory().addPassword(hash);
        newUser.setActive(true);

        repository.save(newUser);
        log.info("Nuevo usuario creado: {}", userName);
        return newUser;
    }

    public User createUserPrincipal(String userName, String fullName, String email,
            char[] password) {
        validarIdentificadorUnico(userName);
        Validations.validatePasswordStrength(password);

        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setUserName(userName);

        if (email != null && !email.isEmpty()) {  // Solo entra si email NO es null y NO está vacío
            validarEmailUnico(email);
            Validations.isValidEmail(email);
            newUser.setEmail(email);
        }

        String hash = passwordHasher.hash(password);
        newUser.setPassword(hash);
        newUser.getPasswordHistory().addPassword(hash);
        newUser.setActive(true);
        newUser.setAdminSistema(true);

        repository.save(newUser);
        log.info("Nuevo usuario creado: {}", userName);
        return newUser;
    }

    public void updatePasswordHistory(Long id, String rawPassword) {
        User userById = this.getUserById(id);
        PasswordHistory passwordHistory = userById.getPasswordHistory();
        boolean passwordUsed = passwordHistory.isPasswordUsed(rawPassword, passwordHasher);
        if (!passwordUsed) {
            passwordHistory.addPassword(passwordHasher.hash(rawPassword));
        }
        repository.update(userById);
    }

    public User createUser(String userName, String fullName,
            char[] password) {
        return createUser(userName, fullName, null, password);
    }

    /**
     * Autentica un usuario por identificador y contraseña
     *
     * @param userName
     * @param password
     * @return
     */
    public User autenticar(String userName, String password) {
        User usuario = findByUsername(userName)
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!usuario.isActive()) {
            throw new UnauthorizedException("Usuario inactivo");
        }

        if (!passwordHasher.verify(usuario.getPassword(), password)) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        log.info("Usuario autenticado: {}", userName);
        return usuario;
    }

    /**
     * Actualiza la contraseña de un usuario con validación de historial
     *
     * @param userId
     * @param newPassword
     */
    public void updatePassword(Long userId, char[] newPassword) {
        Validations.validatePasswordStrength(newPassword);

        User user = UserService.this.getUserById(userId);

        if (passwordHasher.verify(user.getPassword(), newPassword)) {
            throw new BusinessLogicException("No puede usar una contraseña anterior");
        }

        user.setPassword(passwordHasher.hash(newPassword));
        user.actualizarRegistroDeContrasenna(passwordHasher.hash(newPassword));
        repository.update(user);
        log.info("Contraseña actualizada para usuario: {}", user.getUserName());
    }

    /**
     * Asigna roles a un usuario
     *
     * @param usuarioId
     * @param rolesIds
     * @return
     */
    public User asignarRoles(Long usuarioId, Set<Long> rolesIds) {
        final User usuario = UserService.this.getUserById(usuarioId);
        Set<Role> roles = new HashSet<>();

        for (final Long rolId : rolesIds) {
            final Role rol = roleService.getRoleById(rolId);
            roles.add(rol);
        }

        usuario.setRoles(roles);
        repository.update(usuario);
        log.info("Roles actualizados para usuario: {}", usuario.getUserName());
        return usuario;
    }

    /**
     * Obtiene un usuario por su identificador único
     *
     * @param userName
     * @return
     */
    public Optional<User> findByUsername(String userName) {
        return repository.createQuery(User.class)
                .where()
                .eq("userName", userName)
                .setMaxRows(1)
                .findOneOrEmpty();
    }

    /**
     * Obtiene un usuario por su email
     *
     * @param email
     * @return
     */
    public Optional<User> findByEmail(String email) {
        return repository.createQuery(User.class)
                .where()
                .eq("email", email)
                .setMaxRows(1)
                .findOneOrEmpty();
    }

    /**
     * Obtiene todos los usuarios activos
     *
     * @return
     */
    public List<User> getUserByActives() {
        return repository.createQuery(User.class)
                .where()
                .eq("active", true)
                .orderBy("name asc")
                .findList();
    }

    /**
     * Activa/desactiva un usuario
     *
     * @param usuarioId
     * @param activo
     * @return
     */
    public User cambiarEstadoUsuario(Long usuarioId, boolean activo) {
        User usuario = this.getUserById(usuarioId);
        usuario.setActive(activo);
        repository.update(usuario);
        log.info("Estado de usuario actualizado: {} - {}", usuario.getUserName(), activo ? "ACTIVO" : "INACTIVO");
        return usuario;
    }

    /**
     * Valida que el identificador de usuario sea único
     */
    private void validarIdentificadorUnico(String userName) {
        if (this.findByUsername(userName).isPresent()) {
            throw new BusinessLogicException("Ya existe un usuario con el identificador: " + userName);
        }
    }

    /**
     * Valida que el email sea único (si está presente)
     */
    private void validarEmailUnico(String email) {
        if (email != null && !email.isEmpty() && findByEmail(email).isPresent()) {
            throw new BusinessLogicException("Ya existe un usuario con el email: " + email);
        }
    }

    /**
     * Obtiene un usuario por ID con manejo de excepciones
     *
     * @param usuarioId
     * @return
     */
    public User getUserById(Long usuarioId) {
        return repository.find(User.class, usuarioId);
    }

    /**
     * Verifica si un usuario tiene un permiso específico
     */
    public boolean tienePermiso(Long usuarioId, String permiso) {
        User usuario = UserService.this.getUserById(usuarioId);
        return usuario.tienePermiso(permiso);
    }

    /**
     * Verifica si un usuario tiene un rol específico
     */
    public boolean hasRol(Long usuarioId, String nombreRol) {
        User usuario = UserService.this.getUserById(usuarioId);
        return usuario.hasRol(nombreRol);
    }

    /**
     * Actualiza los datos básicos de un usuario
     */
    public User actualizarDatosUsuario(Long usuarioId, String nombre, String email) {
        User usuario = UserService.this.getUserById(usuarioId);

        if (nombre != null) {
            usuario.setFullName(nombre);
        }
        if (email != null) {
            validarEmailUnico(email);
            usuario.setEmail(email);
        }

        repository.update(usuario);
        log.info("Datos actualizados para usuario: {}", usuarioId);
        return usuario;
    }

    /**
     * Genera un token de reseteo de contraseña (implementación básica)
     */
    public String generarTokenReseteoPassword(String email) {
        User usuario = findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException("Usuario no encontrado"));

        if (!usuario.isActive()) {
            throw new BusinessLogicException("Usuario inactivo");
        }

        // En una implementación real, generaríamos un token seguro con expiración
        String token = "reset-token-" + System.currentTimeMillis();
        log.info("Token de reseteo generado para: {}", email);
        return token;
    }

    /**
     * Resetea la contraseña usando un token válido
     */
    public void resetearPasswordConToken(String token, String nuevaPassword) {
        // En una implementación real, validaríamos el token primero
        log.info("Password reset requested with token: {}", token);
        // Implementación real usaría un servicio de tokens
    }

    /**
     * Verifica si un usuario es administrador del sistema
     *
     * @param usuarioId
     * @return
     */
    public boolean isSystemAdmin(Long usuarioId) {
        User usuario = UserService.this.getUserById(usuarioId);
        return usuario.isAdminSistema();
    }

    /**
     * Verifica si un usuario es administrador económico
     *
     * @param usuarioId
     * @return
     */
    public boolean isEconomicAdmin(Long usuarioId) {
        User usuario = UserService.this.getUserById(usuarioId);
        return usuario.isAdminEconomico();
    }

    public void assignRoleToUser(Long userId, Long roleId) {
        User user = getUserById(userId);
        Role role = roleService.getRoleById(roleId);

        user.addRole(role);
        save(user);
    }
}
