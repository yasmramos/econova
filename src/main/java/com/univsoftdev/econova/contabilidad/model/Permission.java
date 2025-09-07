package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.config.model.Role;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false, of = {"id", "code"})
@Entity
@Table(name = "conf_permission")
public class Permission extends BaseModel {

    @NotBlank(message = "Permission name is required")
    @Size(max = 100, message = "Permission name cannot exceed 100 characters")
    @Column(length = 100, nullable = false)
    private String name;

    @NotBlank(message = "Permission code is required")
    @Size(max = 50, message = "Permission code cannot exceed 50 characters")
    @Column(length = 50, unique = true, nullable = false)
    private String code;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Column(length = 255)
    private String description;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    public Permission() {
        // Default constructor required by JPA
    }

    public Permission(String code, String name) {
        this(code, name, null);
    }

    public Permission(String code, String name, String description) {
        setCode(code);
        setName(name);
        setDescription(description);
    }

    /**
     * Add a role to this permission (bidirectional relationship)
     */
    public void addRole(@NotNull Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (this.roles.add(role)) {
            if (!role.getPermissions().contains(this)) {
                role.addPermission(this);
            }
        }
    }

    /**
     * Remove a role from this permission (bidirectional relationship)
     */
    public void removeRole(@NotNull Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (this.roles.remove(role)) {
            if (role.getPermissions().contains(this)) {
                role.removePermission(this);
            }
        }
    }

    /**
     * Check if this permission is assigned to a specific role
     */
    public boolean hasRole(Role role) {
        return role != null && this.roles.contains(role);
    }

    /**
     * Check if this permission is assigned to any role
     */
    public boolean isAssignedToAnyRole() {
        return !this.roles.isEmpty();
    }

    /**
     * Clear all role associations
     */
    public void clearRoles() {
        // Break bidirectional relationships first
        for (Role role : new HashSet<>(this.roles)) {
            removeRole(role);
        }
    }

    /**
     * Replace all roles with a new set (bidirectional update)
     */
    public void setRoles(@NotNull Set<Role> newRoles) {
        if (newRoles == null) {
            throw new IllegalArgumentException("Roles set cannot be null");
        }

        // Remove roles that are no longer in the new set
        for (Role existingRole : new HashSet<>(this.roles)) {
            if (!newRoles.contains(existingRole)) {
                removeRole(existingRole);
            }
        }

        // Add new roles
        for (Role newRole : newRoles) {
            if (!this.roles.contains(newRole)) {
                addRole(newRole);
            }
        }
    }

    /**
     * Check if this permission has a specific code (case-insensitive)
     */
    public boolean hasCode(String code) {
        return code != null && this.code.equalsIgnoreCase(code.trim());
    }

    @Override
    public String toString() {
        return "Permission{"
                + "id=" + getId()
                + ", code='" + code + '\''
                + ", name='" + name + '\''
                + ", tenantId='" + getTenantId() + '\''
                + '}';
    }

    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Permission code cannot be null or empty");
        }
        this.code = code.trim().toUpperCase(); // ✅ Store codes in uppercase for consistency
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Permission name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
