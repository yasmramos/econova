package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.repository.PermissionRepository;
import com.univsoftdev.econova.contabilidad.model.Permission;
import com.univsoftdev.econova.core.service.BaseService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class PermissionService extends BaseService<Permission, PermissionRepository> {

    @Inject
    public PermissionService(PermissionRepository repository) {
        super(repository);
    }

    public Optional<Permission> getPermissionByCode(String code) {
        return repository.findByCode(code);
    }

}
