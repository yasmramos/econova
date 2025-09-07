package com.univsoftdev.econova.config.service;

import jakarta.inject.Inject;
import com.univsoftdev.econova.config.model.Rol;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.Service;
import com.univsoftdev.econova.security.PasswordHasher;
import com.univsoftdev.econova.Validations;
import io.ebean.Database;
import jakarta.inject.Singleton;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import com.univsoftdev.econova.security.Permissions;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import java.util.Set;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Servicio para gestión de usuarios, autenticación y control de acceso.
 */
@Slf4j
@Singleton
public class UsuarioService extends Service<User> {

    private final PasswordHasher passwordHasher;
    private final RolService rolService;

    @Inject
    public UsuarioService(Database database, RolService rolService, PasswordHasher passwordHasher) {
        super(database, User.class);
        this.rolService = rolService;
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
     * @param nombre
     * @param identificador
     * @param email
     * @param password
     * @return
     */
    @RequiresPermissions(value = {Permissions.CREATE_USER})
    public User crearUsuario(String identificador, String nombre, String email,
            String password) {
        validarIdentificadorUnico(identificador);
        validarEmailUnico(email);
        Validations.isValidEmail(email);
        Validations.validatePasswordStrength(password);

        User nuevoUsuario = new User();
        nuevoUsuario.setFullName(nombre);
        nuevoUsuario.setUserName(identificador);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(passwordHasher.hash(password));
        nuevoUsuario.setActive(true);

        database.save(nuevoUsuario);
        log.info("Nuevo usuario creado: {}", identificador);
        return nuevoUsuario;
    }

    public User crearUsuario(String identificador, String nombre,
            String password) {
        return crearUsuario(identificador, nombre, null, password);
    }

    /**
     * Autentica un usuario por identificador y contraseña
     *
     * @param identificador
     * @param password
     * @return
     */
    public User autenticar(String identificador, String password) {
        User usuario = obtenerUsuarioPorIdentificador(identificador)
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!usuario.isActive()) {
            throw new UnauthorizedException("Usuario inactivo");
        }

        if (!passwordHasher.verify(usuario.getPassword(), password)) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        log.info("Usuario autenticado: {}", identificador);
        return usuario;
    }

    /**
     * Actualiza la contraseña de un usuario con validación de historial
     *
     * @param usuarioId
     * @param nuevaPassword
     */
    public void actualizarPassword(Long usuarioId, String nuevaPassword) {
        Validations.validatePasswordStrength(nuevaPassword);

        User usuario = getUserById(usuarioId);

        if (passwordHasher.verify(usuario.getPassword(), nuevaPassword)) {
            throw new BusinessLogicException("No puede usar una contraseña anterior");
        }

        usuario.setPassword(passwordHasher.hash(nuevaPassword));
        usuario.actualizarRegistroDeContrasenna(passwordHasher.hash(nuevaPassword));
        database.update(usuario);
        log.info("Contraseña actualizada para usuario: {}", usuario.getUserName());
    }

    /**
     * Asigna roles a un usuario
     *
     * @param usuarioId
     * @param rolesIds
     * @return
     */
    public User asignarRoles(Long usuarioId, Set<Long> rolesIds) {
        User usuario = getUserById(usuarioId);
        Set<Rol> roles = new HashSet<>();

        for (Long rolId : rolesIds) {
            Rol rol = rolService.obtenerRolPorId(rolId);
            roles.add(rol);
        }

        usuario.setRoles(roles);
        database.update(usuario);
        log.info("Roles actualizados para usuario: {}", usuario.getUserName());
        return usuario;
    }

    /**
     * Obtiene un usuario por su identificador único
     *
     * @param identificador
     * @return
     */
    public Optional<User> obtenerUsuarioPorIdentificador(String identificador) {
        return database.createQuery(User.class)
                .where()
                .eq("identificador", identificador)
                .findOneOrEmpty();
    }

    /**
     * Obtiene un usuario por su email
     *
     * @param email
     * @return
     */
    public Optional<User> obtenerUsuarioPorEmail(String email) {
        return database.createQuery(User.class)
                .where()
                .eq("email", email)
                .findOneOrEmpty();
    }

    /**
     * Obtiene todos los usuarios activos
     *
     * @return
     */
    public List<User> obtenerUsuariosActivos() {
        return database.createQuery(User.class)
                .where()
                .eq("activo", true)
                .orderBy("nombre asc")
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
        User usuario = getUserById(usuarioId);
        usuario.setActive(activo);
        database.update(usuario);
        log.info("Estado de usuario actualizado: {} - {}", usuario.getUserName(), activo ? "ACTIVO" : "INACTIVO");
        return usuario;
    }

    /**
     * Valida que el identificador de usuario sea único
     */
    private void validarIdentificadorUnico(String identificador) {
        if (obtenerUsuarioPorIdentificador(identificador).isPresent()) {
            throw new BusinessLogicException("Ya existe un usuario con el identificador: " + identificador);
        }
    }

    /**
     * Valida que el email sea único (si está presente)
     */
    private void validarEmailUnico(String email) {
        if (email != null && !email.isEmpty() && obtenerUsuarioPorEmail(email).isPresent()) {
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
        return database.find(User.class, usuarioId);
    }

    /**
     * Verifica si un usuario tiene un permiso específico
     */
    public boolean tienePermiso(Long usuarioId, String permiso) {
        User usuario = getUserById(usuarioId);
        return usuario.tienePermiso(permiso);
    }

    /**
     * Verifica si un usuario tiene un rol específico
     */
    public boolean hasRol(Long usuarioId, String nombreRol) {
        User usuario = getUserById(usuarioId);
        return usuario.tieneRol(nombreRol);
    }

    /**
     * Actualiza los datos básicos de un usuario
     */
    public User actualizarDatosUsuario(Long usuarioId, String nombre, String email) {
        User usuario = getUserById(usuarioId);

        if (nombre != null) {
            usuario.setFullName(nombre);
        }
        if (email != null) {
            validarEmailUnico(email);
            usuario.setEmail(email);
        }

        database.update(usuario);
        log.info("Datos actualizados para usuario: {}", usuarioId);
        return usuario;
    }

    /**
     * Genera un token de reseteo de contraseña (implementación básica)
     */
    public String generarTokenReseteoPassword(String email) {
        User usuario = obtenerUsuarioPorEmail(email)
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
     */
    public boolean isSystemAdmin(Long usuarioId) {
        User usuario = getUserById(usuarioId);
        return usuario.isAdminSistema();
    }

    /**
     * Verifica si un usuario es administrador económico
     */
    public boolean isEconomicAdmin(Long usuarioId) {
        User usuario = getUserById(usuarioId);
        return usuario.isAdminEconomico();
    }
}
