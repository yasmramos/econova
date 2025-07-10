package com.univsoftdev.econova.config.service;

import jakarta.inject.Inject;
import com.univsoftdev.econova.config.model.Rol;
import com.univsoftdev.econova.contabilidad.model.Permiso;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.Service;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import io.ebean.Database;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio para gestión de roles y permisos del sistema.
 */
@Slf4j
@Singleton
public class RolService extends Service<Rol> {

    @Inject
    public RolService(Database database) {
        super(database, Rol.class);
    }

    /**
     * Crea un nuevo rol con validaciones básicas
     */
    public Rol crearRol(String nombre, String descripcion, Set<Long> permisosIds) {
        validarNombreUnico(nombre);

        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(nombre);
        nuevoRol.setDescripcion(descripcion);

        Set<Permiso> permisos = new HashSet<>();
        for (Long permisoId : permisosIds) {
            Permiso permiso = database.find(Permiso.class, permisoId);
            permisos.add(permiso);
        }
        nuevoRol.setPermisos(permisos);

        database.save(nuevoRol);
        log.info("Nuevo rol creado: {}", nombre);
        return nuevoRol;
    }

    /**
     * Asigna permisos a un rol existente
     */
    public Rol asignarPermisos(Long rolId, Set<Long> permisosIds) {
        Rol rol = obtenerRolPorId(rolId);
        Set<Permiso> nuevosPermisos = new HashSet<>();

        for (Long permisoId : permisosIds) {
            Permiso permiso = database.find(Permiso.class, permisoId);
            nuevosPermisos.add(permiso);
        }

        rol.setPermisos(nuevosPermisos);
        database.update(rol);
        log.info("Permisos actualizados para rol: {}", rol.getNombre());
        return rol;
    }

    /**
     * Obtiene un rol por su nombre
     */
    public Optional<Rol> obtenerRolPorNombre(String nombre) {
        return database.createQuery(Rol.class)
                .where()
                .eq("nombre", nombre)
                .findOneOrEmpty();
    }

    /**
     * Obtiene todos los roles ordenados por nombre
     */
    public List<Rol> obtenerTodosLosRoles() {
        return database.createQuery(Rol.class)
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Obtiene los usuarios que tienen asignado un rol específico
     */
    public List<User> obtenerUsuariosPorRol(Long rolId) {
        return database.createQuery(User.class)
                .where()
                .eq("roles.id", rolId)
                .orderBy("nombre asc")
                .findList();
    }

    /**
     * Valida que el nombre del rol sea único
     */
    private void validarNombreUnico(String nombre) {
        if (obtenerRolPorNombre(nombre).isPresent()) {
            throw new BusinessLogicException("Ya existe un rol con el nombre: " + nombre);
        }
    }

    /**
     * Obtiene un rol por ID con manejo de excepciones
     */
    public Rol obtenerRolPorId(Long rolId) {
        return database.find(Rol.class, rolId);
    }

    /**
     * Elimina un rol (solo si no está asignado a usuarios)
     */
    public void eliminarRol(Long rolId) {
        Rol rol = obtenerRolPorId(rolId);

        if (!obtenerUsuariosPorRol(rolId).isEmpty()) {
            throw new BusinessLogicException("No se puede eliminar un rol asignado a usuarios");
        }

        database.delete(rol);
        log.info("Rol eliminado: {}", rol.getNombre());
    }

    /**
     * Verifica si un rol tiene un permiso específico
     */
    public boolean tienePermiso(Long rolId, String codigoPermiso) {
        Rol rol = obtenerRolPorId(rolId);
        return rol.getPermisos().stream()
                .anyMatch(p -> p.getCodigo().equals(codigoPermiso));
    }

    /**
     * Actualiza la descripción de un rol
     */
    public Rol actualizarDescripcion(Long rolId, String descripcion) {
        Rol rol = obtenerRolPorId(rolId);
        rol.setDescripcion(descripcion);
        database.update(rol);
        log.info("Descripción actualizada para rol: {}", rol.getNombre());
        return rol;
    }

    /**
     * Crea los roles básicos del sistema si no existen
     */
    public void inicializarRolesBasicos() {
        crearRolSiNoExiste("ADMIN_SISTEMA", "Administrador del sistema", Set.of());
        crearRolSiNoExiste("ADMIN_ECONOMICO", "Administrador económico", Set.of());
        crearRolSiNoExiste("CONTABILIDAD", "Usuario de contabilidad", Set.of());
        crearRolSiNoExiste("CONSULTA", "Usuario de consulta", Set.of());
    }

    private void crearRolSiNoExiste(String nombre, String descripcion, Set<Long> permisosIds) {
        if (obtenerRolPorNombre(nombre).isEmpty()) {
            crearRol(nombre, descripcion, permisosIds);
            log.info("Rol básico creado: {}", nombre);
        }
    }
}
