package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.contabilidad.model.Permission;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false, of = {"id", "name"})
@Entity
@Table(name = "sys_roles")
public class Role extends BaseModel {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Column(unique = true, length = 50)
    private String code;

    @NotBlank
    @Column(length = 100)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "conf_role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Role() {
    }

    public Role(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede ser nulo o vacío.");
        }
        this.name = name.trim();
    }

    public Role(String name, String description) {
        this(name);
        this.description = description;
    }

    public Role(String name, String description, Set<Permission> permissions) {
        this(name);
        this.description = description;
        this.permissions = permissions;
    }

    public boolean tienePermiso(String codigoPermiso) {
        return permissions.stream().anyMatch(p -> p.getCode().equals(codigoPermiso));
    }

    public void addPermission(Permission permission) {
        if (permission != null && this.permissions.add(permission)) {
            if (!permission.getRoles().contains(this)) {
                permission.addRole(this);
            }
        }
    }

    public void removePermission(Permission permission) {
        if (permission != null && this.permissions.remove(permission)) {
            if (permission.getRoles().contains(this)) {
                permission.removeRole(this);
            }
        }
    }

    public boolean hasPermission(String permissionCode) {
        return permissionCode != null
                && permissions.stream()
                        .anyMatch(p -> p.hasCode(permissionCode));
    }

    /**
     * Clear all permissions from this role (bidirectional)
     */
    public void clearPermissions() {
        // Break bidirectional relationships first
        for (Permission permission : new HashSet<>(this.permissions)) {
            removePermission(permission);
        }
    }
    
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(@NotNull Set<User> users) {
        this.users = users;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

}
