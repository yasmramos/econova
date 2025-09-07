package com.univsoftdev.econova.config.dto;

import com.univsoftdev.econova.config.model.Role;
import com.univsoftdev.econova.contabilidad.model.Permission;
import java.util.HashSet;
import java.util.Set;

public class RolDto {

    private String name;

    private String description;

    private Set<Permission> permissions = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Role toEntity() {
        return new Role(name, description, permissions);
    }

}
