package com.univsoftdev.econova.config.repository;

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
public class UserRepository extends BaseRepository<User> {

    @Inject
    public UserRepository(Database database) {
        super(database);
    }
    
    @Override
    protected Class<User> getEntityType() {
        return User.class;
    }
    
    @Override
    public List<User> findByCriteria(String criteria) {
        return database.find(User.class)
                .where()
                .ilike("fullName", "%" + criteria + "%")
                .or()
                .ilike("userName", "%" + criteria + "%")
                .or()
                .ilike("email", "%" + criteria + "%")
                .findList();
    }
    
    // Métodos adicionales específicos para User
    public Optional<User> findByUserName(String userName) {
        return Optional.ofNullable(
            database.find(User.class)
                .where()
                .eq("userName", userName)
                .findOne()
        );
    }
    
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(
            database.find(User.class)
                .where()
                .eq("email", email)
                .findOne()
        );
    }
    
    public List<User> findByRol(String nombreRol) {
        return database.find(User.class)
                .where()
                .eq("roles.name", nombreRol)
                .orderBy("fullName asc")
                .findList();
    }
    
    public List<User> findByRolConDetalles(String nombreRol) {
        return database.find(User.class)
                .where()
                .eq("roles.name", nombreRol)
                .orderBy("fullName asc")
                .findList();
    }
    
    public List<User> obtenerUsuariosActivos() {
        return database.find(User.class)
                .where()
                .eq("active", true)
                .orderBy("fullName asc")
                .findList();
    }
    
    public List<User> obtenerUsuariosPorRolYEstado(String nombreRol, boolean activo) {
        return database.find(User.class)
                .where()
                .eq("roles.name", nombreRol)
                .eq("active", activo)
                .orderBy("fullName asc")
                .findList();
    }
    
    public boolean existeUsuarioConNombre(String userName) {
        return database.find(User.class)
                .where()
                .eq("userName", userName)
                .exists();
    }
    
    public boolean existeUsuarioConEmail(String email) {
        return database.find(User.class)
                .where()
                .eq("email", email)
                .exists();
    }
    
    public List<User> obtenerTodosOrdenados() {
        return database.find(User.class)
                .orderBy("fullName asc")
                .findList();
    }
    
    public List<User> obtenerUsuariosAdministradores() {
        return database.find(User.class)
                .where()
                .or()
                .eq("adminSistema", true)
                .eq("adminEconomico", true)
                .orderBy("fullName asc")
                .findList();
    }
    
    public List<User> obtenerUsuariosConRoles() {
        return database.find(User.class)
                .orderBy("fullName asc")
                .findList();
    }
}