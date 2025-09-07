package com.univsoftdev.econova.config.repository;

import com.univsoftdev.econova.config.model.Role;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class RoleRepository extends BaseRepository<Role> {

    @Inject
    public RoleRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<Role> getEntityType() {
        return Role.class;
    }
    
    /**
     * Método para buscar roles por código de permiso (case-insensitive)
     */
    public List<Role> findByPermissionCode(String permissionCode) {
        return database.find(Role.class)
                .where()
                .ieq("permissions.code", permissionCode) // Búsqueda case-insensitive
                .findList();
    }

    // Método que necesita el servicio
    public List<User> findUsersByRoleId(Long roleId) {
        return database.find(User.class)
                .where()
                .eq("roles.id", roleId)
                .orderBy("username asc")
                .findList();
    }

    // Método que necesita el servicio (equivalente a obtenerTodosOrdenados)
    public List<Role> findAllOrderedByName() {
        return database.find(Role.class)
                .orderBy("name asc")
                .findList();
    }

    // Método alternativo usando la nomenclatura de Spring Data
    public List<Role> findAllByOrderByNameAsc() {
        return database.find(Role.class)
                .orderBy("name asc")
                .findList();
    }

    public Optional<Role> findByName(String nombre) {
        return Optional.ofNullable(
                database.find(Role.class)
                        .where()
                        .eq("name", nombre)
                        .findOne()
        );
    }

    public List<Role> obtenerRolesConPermisos() {
        return database.find(Role.class)
                .fetch("permissions")
                .orderBy("name asc")
                .findList();
    }

    public List<Role> obtenerRolesPorUsuario(Long userId) {
        return database.find(Role.class)
                .where()
                .eq("users.id", userId)
                .orderBy("name asc")
                .findList();
    }

    public boolean existeRolConNombre(String nombre) {
        return database.find(Role.class)
                .where()
                .eq("name", nombre)
                .exists();
    }

    public List<Role> obtenerTodosOrdenados() {
        return database.find(Role.class)
                .orderBy("name asc")
                .findList();
    }

    public List<Role> obtenerRolesConUsuarios() {
        return database.find(Role.class)
                .fetch("users")
                .orderBy("name asc")
                .findList();
    }

    //Método adicional para buscar por criterios
    public List<Role> findByCriteria(String criteria) {
        return database.find(Role.class)
                .where()
                .ilike("name", "%" + criteria + "%")
                .or()
                .ilike("description", "%" + criteria + "%")
                .findList();
    }

    //Método para eliminar relaciones de permisos antes de borrar un rol
    public void breakPermissionRelationships(Long roleId) {
        String sql = "DELETE FROM conf_role_permissions WHERE role_id = :roleId";
        database.sqlUpdate(sql)
                .setParameter("roleId", roleId)
                .execute();
    }
}
