package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.model.Role;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.config.repository.RoleRepository;
import com.univsoftdev.econova.contabilidad.model.Permission;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import com.univsoftdev.econova.core.service.BaseService;
import com.univsoftdev.econova.security.Roles;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for system roles and permissions management.
 */
@Slf4j
@Singleton
public class RoleService extends BaseService<Role, RoleRepository> {

    private final PermissionService permissionService;
    @Inject
    public RoleService(RoleRepository repository, PermissionService permissionService) {
        super(repository);
        this.permissionService = permissionService;
    }

    /**
     * Creates a new role with basic validations
     */
    @Transactional
    public Role createRole(String name, String description, Set<Long> permissionIds) {
        validateUniqueName(name);

        Role newRole = new Role();
        newRole.setName(name);
        newRole.setDescription(description);

        // Add permissions if provided
        if (permissionIds != null) {
            for (Long permissionId : permissionIds) {
                Permission permission = repository.find(Permission.class, permissionId);
                if (permission != null) {
                    newRole.addPermission(permission);
                }
            }
        }

        save(newRole);
        log.info("New role created: {}", name);
        return newRole;
    }

    /**
     * Assign permissions to an existing role
     */
    @Transactional
    public Role assignPermissions(Long roleId, Set<Long> permissionIds) {
        Role role = getRoleById(roleId);

        if (permissionIds != null) {
            for (Long permissionId : permissionIds) {
                Permission permission = repository.find(Permission.class, permissionId);
                if (permission != null) {
                    role.addPermission(permission);
                }
            }
        }

        save(role);
        log.info("Permissions updated for role: {}", role.getName());
        return role;
    }

    /**
     * Get a role by its name
     */
    public Optional<Role> getRoleByName(String name) {
        return repository.findByName(name);
    }

    /**
     * Get all roles ordered by name
     */
    public List<Role> getAllRoles() {
        return repository.findAllOrderedByName();
    }

    /**
     * Get users assigned to a specific role
     */
    public List<User> getUsersByRole(Long roleId) {
        return repository.findUsersByRoleId(roleId);
    }

    /**
     * Validate that the role name is unique
     */
    private void validateUniqueName(String name) {
        if (getRoleByName(name).isPresent()) {
            throw new BusinessLogicException("A role with the name already exists: " + name);
        }
    }

    /**
     * Get a role by ID with exception handling
     */
    public Role getRoleById(Long roleId) {
        return repository.findById(roleId)
                .orElseThrow(() -> new BusinessLogicException("Role not found with ID: " + roleId));
    }

    /**
     * Delete a role (only if not assigned to users)
     */
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = getRoleById(roleId);

        if (!getUsersByRole(roleId).isEmpty()) {
            throw new BusinessLogicException("Cannot delete a role assigned to users");
        }

        // Clear permissions before deletion to maintain referential integrity
        role.clearPermissions();
        save(role);

        repository.delete(role);
        log.info("Role deleted: {}", role.getName());
    }

    /**
     * Check if a role has a specific permission
     */
    public boolean hasPermission(Long roleId, String permissionCode) {
        Role role = getRoleById(roleId);
        return role.getPermissions().stream()
                .anyMatch(p -> p.getCode().equalsIgnoreCase(permissionCode));
    }

    /**
     * Update role description
     */
    @Transactional
    public Role updateDescription(Long roleId, String description) {
        Role role = getRoleById(roleId);
        role.setDescription(description);
        save(role);
        log.info("Description updated for role: {}", role.getName());
        return role;
    }

    /**
     * Create system basic roles if they don't exist
     */
    @Transactional
    public void initializeBasicRoles() {
        createRoleIfNotExists(Roles.SYSTEM_ADMIN, "System Administrator", Set.of());
        createRoleIfNotExists(Roles.ECONOMIC_ADMIN, "Economic Administrator", Set.of());
        createRoleIfNotExists(Roles.AUDITOR, "Auditor", Set.of());
        createRoleIfNotExists(Roles.CONSULTANT, "Consultant", Set.of());
        createRoleIfNotExists(Roles.ACCOUNTING_OPERATOR, "Accounting Operator", Set.of());
    }

    /**
     * Remove a permission from a role
     */
    @Transactional
    public Role removePermission(Long roleId, Long permissionId) {
        Role role = getRoleById(roleId);
        Permission permission = repository.find(Permission.class, permissionId);

        if (permission != null) {
            role.removePermission(permission);
            save(role);
            log.info("Permission removed from role: {}", role.getName());
        }

        return role;
    }

    /**
     * Get roles by permission code
     */
    public List<Role> getRolesByPermission(String permissionCode) {
        return repository.findByPermissionCode(permissionCode);
    }

    /**
     * Check if a role is assigned to any user
     */
    public boolean isRoleAssignedToUsers(Long roleId) {
        return !getUsersByRole(roleId).isEmpty();
    }

    private void createRoleIfNotExists(String name, String description, Set<Long> permissionIds) {
        if (getRoleByName(name).isEmpty()) {
            createRole(name, description, permissionIds);
            log.info("Basic role created: {}", name);
        }
    }

    /**
     * Clone an existing role with a new name
     */
    @Transactional
    public Role cloneRole(Long sourceRoleId, String newRoleName, String newDescription) {
        validateUniqueName(newRoleName);
        Role sourceRole = getRoleById(sourceRoleId);

        Role clonedRole = new Role();
        clonedRole.setName(newRoleName);
        clonedRole.setDescription(newDescription != null ? newDescription : sourceRole.getDescription());

        // Copy permissions
        sourceRole.getPermissions().forEach(clonedRole::addPermission);

        save(clonedRole);
        log.info("Role cloned from {} to {}", sourceRole.getName(), newRoleName);
        return clonedRole;
    }

    public void assignPermissionByCode(Long roleId, String permissionCode) {
        Role role = getRoleById(roleId);
        Permission permission = permissionService.getPermissionByCode(permissionCode)
                .orElseThrow(() -> new BusinessLogicException("Permission not found: " + permissionCode));

        role.addPermission(permission);
        save(role);
    }
}
