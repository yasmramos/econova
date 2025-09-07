package com.univsoftdev.econova.config.repository;

import com.univsoftdev.econova.contabilidad.model.Permission;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class PermissionRepository extends BaseRepository<Permission> {

    @Inject
    public PermissionRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<Permission> getEntityType() {
        return Permission.class;
    }

    @Override
    public List<Permission> findByCriteria(String criteria) {
        return database.find(Permission.class)
                .where()
                .ilike("name", "%" + criteria + "%")
                .findList();
    }

    public Optional<Permission> findByName(String name) {
        return Optional.ofNullable(
                database.find(Permission.class)
                        .where()
                        .eq("name", name)
                        .findOne()
        );
    }
    
    public Optional<Permission> findByCode(String code) {
        return Optional.ofNullable(
                database.find(Permission.class)
                        .where()
                        .eq("code", code)
                        .findOne()
        );
    }

}
