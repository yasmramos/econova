package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.contabilidad.model.PasswordHistory;
import com.univsoftdev.econova.core.Validations;
import com.univsoftdev.econova.core.model.BaseModel;
import com.univsoftdev.econova.security.Roles;
import com.univsoftdev.econova.security.argon2.Argon2PasswordHasher;
import com.univsoftdev.econova.security.argon2.PasswordHasher;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false, of = {"userName"})
@Entity
@Table(name = "sys_users")
public class User extends BaseModel {

    private static final long serialVersionUID = 1L;
    private final transient PasswordHasher hasher = new Argon2PasswordHasher();

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Column(nullable = false)
    private String fullName;

    @NotBlank(message = "El identificador del usuario no puede estar vacío.")
    @Column(unique = true, nullable = false)
    private String userName;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password; // Almacenará el hash generado por Argon2

    @Email
    @Column(unique = true)
    private String email;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private PasswordHistory passwordHistory = new PasswordHistory(new LinkedList<>());
    private boolean active;
    private boolean adminSistema;
    private boolean adminEconomico;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_rol",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Role> roles;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(String userName) {
        this();
        this.userName = userName;
    }

    public User(String userName, String contrasenna) {
        this(userName);
        this.password = hasher.hash(contrasenna); // Hashea la contraseña antes de almacenarla
    }

    public void agregarRol(@NotNull Role rol) {
        if (rol == null) {
            return;
        }

        if (!this.roles.contains(rol)) {
            this.roles.add(rol);
            actualizarFlagsAdministrador(rol);
            if (!rol.getUsers().contains(this)) {
                rol.getUsers().add(this);
            }
        }
    }

    public void removerRol(@NotNull Role rol) {
        if (rol == null) {
            return;
        }

        if (this.roles.remove(rol)) {
            actualizarFlagsAdministrador();
            if (rol.getUsers().contains(this)) {
                rol.getUsers().remove(this);
            }
        }
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void setRoles(@NotNull Set<Role> roles) {
        if (roles == null) {
            this.roles.clear();
            return;
        }

        // Primero removemos los roles que ya no están
        this.roles.stream()
                .filter(r -> !roles.contains(r))
                .forEach(this::removerRol);

        // Luego agregamos los nuevos
        roles.forEach(this::agregarRol);

        actualizarFlagsAdministrador();
    }

    public boolean esContrasennaRepetida(String rawPassword) {
        if (passwordHistory == null) {
            return false;
        }

        return passwordHistory.isPasswordUsed(rawPassword, new Argon2PasswordHasher());
    }

    public void actualizarRegistroDeContrasenna(String nuevaContrasenna) {
        if (passwordHistory == null) {
            passwordHistory = new PasswordHistory();
        }
        passwordHistory.agregarContrasenna(nuevaContrasenna);
    }

    public boolean hasRol(String nombreRol) {
        return roles.stream().anyMatch(r -> r.getName().equals(nombreRol));
    }

    public boolean tienePermiso(String permiso) {
        return roles.stream().anyMatch(r -> r.tienePermiso(permiso));
    }

    public String getPassword() {
        return password;
    }

    @NotNull
    public PasswordHistory getPasswordHistory() {
        return passwordHistory;
    }

    public void setPasswordHistory(@NotNull PasswordHistory passwordHistory) {
        this.passwordHistory = passwordHistory;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAdminSistema() {
        return adminSistema;
    }

    public void setAdminSistema(boolean adminSistema) {
        this.adminSistema = adminSistema;
    }

    public boolean isAdminEconomico() {
        return adminEconomico;
    }

    public void setAdminEconomico(boolean adminEconomico) {
        this.adminEconomico = adminEconomico;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        Validations.isValidEmail(email);
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRole(@NotNull Role role) {
        addRole(role);
    }

    public void addRole(@NotNull Role role) {
        if (!this.hasRol(role.getName())) {
            this.roles.add(role);
            this.adminSistema = role.getName().equalsIgnoreCase(Roles.SYSTEM_ADMIN);
            this.adminEconomico = role.getName().equalsIgnoreCase(Roles.ECONOMIC_ADMIN);
        }
    }

    public boolean esAdministrador() {
        return adminSistema || adminEconomico;
    }

    public boolean puedeAccederAContabilidad() {
        return adminSistema || adminEconomico || hasRol("CONTABILIDAD");
    }

    private void actualizarFlagsAdministrador() {
        this.adminSistema = roles.stream()
                .anyMatch(r -> "ADMIN_SISTEMA".equalsIgnoreCase(r.getName()));
        this.adminEconomico = roles.stream()
                .anyMatch(r -> "ADMIN_ECONOMICO".equalsIgnoreCase(r.getName()));
    }

    private void actualizarFlagsAdministrador(Role rol) {
        if ("ADMIN_SISTEMA".equalsIgnoreCase(rol.getName())) {
            this.adminSistema = true;
        } else if ("ADMIN_ECONOMICO".equalsIgnoreCase(rol.getName())) {
            this.adminEconomico = true;
        }
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + getId()
                + ", name='" + fullName + '\''
                + ", userName='" + userName + '\''
                + ", email='" + email + '\''
                + '}';
    }
}
