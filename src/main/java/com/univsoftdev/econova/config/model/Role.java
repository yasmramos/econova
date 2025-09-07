package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.contabilidad.model.Permission;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_roles" , schema = "accounting")
public class Rol extends BaseModel {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Column(unique = true)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rol_permission",
            joinColumns = @JoinColumn(name = "rol_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Rol() {
    }

    public Rol(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede ser nulo o vacío.");
        }
        this.name = name.trim();
    }

    public boolean tienePermiso(String codigoPermiso) {
        return permissions.stream().anyMatch(p -> p.getCode().equals(codigoPermiso));
    }

    public void agregarPermiso(@NotNull Permission permiso) {
        permissions.add(permiso);
        permiso.getRoles().add(this);
    }

    public void removerPermiso(@NotNull Permission permiso) {
        permissions.remove(permiso);
        permiso.getRoles().remove(this);
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

    public void setPermisos(@NotNull Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
