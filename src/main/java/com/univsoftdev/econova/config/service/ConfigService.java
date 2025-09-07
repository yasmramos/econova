package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.config.model.User;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestionar operaciones de configuración y usuarios. Utiliza
 * Ebean ORM para operaciones de base de datos y Google Guice para inyección de
 * dependencias.
 */
@Slf4j
@Singleton
public class ConfigService {

    private final UserService userService;
    private final PeriodoService periodoService;

    @Inject
    public ConfigService(
            UserService userService,
            PeriodoService periodoService
    ) {
        this.userService = userService;
        this.periodoService = periodoService;
    }

    /**
     * Crea un usuario principal con el mismo nombre de usuario y nombre
     * completo.
     *
     * @param userName Nombre de usuario único
     * @param fullName
     * @param email
     * @param password Contraseña del usuario
     * @return Usuario creado
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws RuntimeException si ocurre un error durante la creación
     */
    public User createUserPrincipal(String userName, String fullName, String email,
            char[] password) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
        }
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }

        try {
            log.info("Creando usuario principal: {}", userName);
            User user = userService.createUserPrincipal(userName, fullName, email, password);
            log.info("Usuario principal creado exitosamente: {}", userName);
            return user;
        } catch (Exception e) {
            log.error("Error creando usuario principal: {}", userName, e);
            throw new RuntimeException("Error al crear usuario principal", e);
        }
    }

    /**
     * Crea un nuevo usuario con nombre de usuario y nombre completo.
     *
     * @param userName Nombre de usuario único
     * @param fullName Nombre completo del usuario
     * @param password Contraseña del usuario
     * @return Usuario creado
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws RuntimeException si ocurre un error durante la creación
     */
    public User createUser(String userName, String fullName, char[] password) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo no puede ser nulo o vacío");
        }
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }

        try {
            log.info("Creando usuario: {} - {}", userName, fullName);
            User user = userService.createUser(userName, fullName, password);
            log.info("Usuario creado exitosamente: {} - {}", userName, fullName);
            return user;
        } catch (Exception e) {
            log.error("Error creando usuario: {} - {}", userName, fullName, e);
            throw new RuntimeException("Error al crear usuario", e);
        }
    }

    public User createUser(String userName, String fullName, String email, char[] password) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo no puede ser nulo o vacío");
        }
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }

        try {
            log.info("Creando usuario: {} - {}", userName, fullName);
            User user = userService.createUser(userName, fullName, password);
            log.info("Usuario creado exitosamente: {} - {}", userName, fullName);
            return user;
        } catch (Exception e) {
            log.error("Error creando usuario: {} - {}", userName, fullName, e);
            throw new RuntimeException("Error al crear usuario", e);
        }
    }

    /**
     * Verifica si existe un usuario con el nombre especificado.
     *
     * @param userName Nombre de usuario a verificar
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean existsUser(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return false;
        }
        try {
            return userService.findByUsername(userName).isPresent();
        } catch (Exception e) {
            log.warn("Error verificando existencia de usuario: {}", userName, e);
            return false;
        }
    }

    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param userName Nombre de usuario
     * @return Usuario encontrado o null si no existe
     */
    public User getUserByUsername(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return null;
        }
        try {
            return userService.findByUsername(userName).orElse(null);
        } catch (Exception e) {
            log.error("Error obteniendo usuario: {}", userName, e);
            return null;
        }
    }

    public Period crearPeriodo(String nombre, LocalDate inicio, LocalDate fin, Exercise ejercicio) {
        return periodoService.crearPeriodo(nombre, inicio, fin, ejercicio);
    }
}
