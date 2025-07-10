package com.univsoftdev.econova.config.service;

import jakarta.inject.Inject;
import com.univsoftdev.econova.config.model.Rol;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.Service;
import com.univsoftdev.econova.seguridad.PasswordHasher;
import com.univsoftdev.econova.seguridad.Argon2PasswordHasher;
import com.univsoftdev.econova.Validations;
import io.ebean.Database;
import jakarta.inject.Singleton;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import java.util.Set;
import org.apache.shiro.authz.UnauthorizedException;

/**
 * Servicio para gestión de usuarios, autenticación y control de acceso.
 */
@Slf4j
@Singleton
public class UsuarioService extends Service<User> {

    private final PasswordHasher passwordHasher;
    private final RolService rolService;

    @Inject
    public UsuarioService(Database database, RolService rolService) {
        super(database, User.class);
        this.passwordHasher = new Argon2PasswordHasher();
        this.rolService = rolService;
    }

    public List<User> findByActivos() {
        return findAll().stream().filter(u -> u.isActivo() == true).toList();
    }

    public List<User> findByInactivos() {
        return findAll().stream().filter(u -> u.isActivo() == false).toList();
    }

    /**
     * Crea un nuevo usuario con validaciones y seguridad
     */
    public User crearUsuario(String nombre, String identificador, String email,
            String password, boolean activo) {
        validarIdentificadorUnico(identificador);
        validarEmailUnico(email);
        Validations.isValidEmail(email);

        User nuevoUsuario = new User();
        nuevoUsuario.setFullName(nombre);
        nuevoUsuario.setUserName(identificador);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(passwordHasher.hash(password));
        nuevoUsuario.setActivo(activo);

        database.save(nuevoUsuario);
        log.info("Nuevo usuario creado: {}", identificador);
        return nuevoUsuario;
    }

    /**
     * Autentica un usuario por identificador y contraseña
     */
    public User autenticar(String identificador, String password) {
        User usuario = obtenerUsuarioPorIdentificador(identificador)
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!usuario.isActivo()) {
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
     */
    public void actualizarPassword(Long usuarioId, String nuevaPassword) {
        User usuario = obtenerUsuarioPorId(usuarioId);

        if (usuario.esContrasennaRepetida(nuevaPassword)) {
            throw new BusinessLogicException("No puede usar una contraseña anterior");
        }

        usuario.setPassword(nuevaPassword);
        usuario.actualizarRegistroDeContrasenna(nuevaPassword);
        database.update(usuario);
        log.info("Contraseña actualizada para usuario: {}", usuario.getUserName());
    }

    /**
     * Asigna roles a un usuario
     */
    public User asignarRoles(Long usuarioId, Set<Long> rolesIds) {
        User usuario = obtenerUsuarioPorId(usuarioId);
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
     */
    public Optional<User> obtenerUsuarioPorIdentificador(String identificador) {
        return database.createQuery(User.class)
                .where()
                .eq("identificador", identificador)
                .findOneOrEmpty();
    }

    /**
     * Obtiene un usuario por su email
     */
    public Optional<User> obtenerUsuarioPorEmail(String email) {
        return database.createQuery(User.class)
                .where()
                .eq("email", email)
                .findOneOrEmpty();
    }

    /**
     * Obtiene todos los usuarios activos
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
     */
    public User cambiarEstadoUsuario(Long usuarioId, boolean activo) {
        User usuario = obtenerUsuarioPorId(usuarioId);
        usuario.setActivo(activo);
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
     */
    public User obtenerUsuarioPorId(Long usuarioId) {
        return database.find(User.class, usuarioId);
    }

    /**
     * Verifica si un usuario tiene un permiso específico
     */
    public boolean tienePermiso(Long usuarioId, String permiso) {
        User usuario = obtenerUsuarioPorId(usuarioId);
        return usuario.tienePermiso(permiso);
    }

    /**
     * Verifica si un usuario tiene un rol específico
     */
    public boolean tieneRol(Long usuarioId, String nombreRol) {
        User usuario = obtenerUsuarioPorId(usuarioId);
        return usuario.tieneRol(nombreRol);
    }

    /**
     * Actualiza los datos básicos de un usuario
     */
    public User actualizarDatosUsuario(Long usuarioId, String nombre, String email) {
        User usuario = obtenerUsuarioPorId(usuarioId);

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

        if (!usuario.isActivo()) {
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
     */
    public boolean esAdminSistema(Long usuarioId) {
        User usuario = obtenerUsuarioPorId(usuarioId);
        return usuario.isAdminSistema();
    }

    /**
     * Verifica si un usuario es administrador económico
     */
    public boolean esAdminEconomico(Long usuarioId) {
        User usuario = obtenerUsuarioPorId(usuarioId);
        return usuario.isAdminEconomico();
    }
}
