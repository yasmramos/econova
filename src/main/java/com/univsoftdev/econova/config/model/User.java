package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.security.Argon2PasswordHasher;
import com.univsoftdev.econova.security.PasswordHasher;
import com.univsoftdev.econova.Validations;
import com.univsoftdev.econova.contabilidad.model.PasswordRegistry;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_users", schema = "accounting")
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

    @OneToOne
    private PasswordRegistry passwordRegistry;
    private boolean active;
    private boolean adminSistema;
    private boolean adminEconomico;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_rol",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    public User() {
    }

    public User(String identificador) {
        this.userName = identificador;
    }

    public User(String identificador, String contrasenna) {
        this.userName = identificador;
        this.password = hasher.hash(contrasenna); // Hashea la contraseña antes de almacenarla
    }
    
    public Set<Rol> getRoles(){
        return roles;
    }

    public boolean esContrasennaRepetida(String rawPassword) {
        if (passwordRegistry == null) {
            return false;
        }

        return passwordRegistry.esContrasennaUsada(rawPassword, new Argon2PasswordHasher());
    }

    public void actualizarRegistroDeContrasenna(String nuevaContrasenna) {
        if (passwordRegistry == null) {
            passwordRegistry = new PasswordRegistry();
        }
        passwordRegistry.agregarContrasenna(nuevaContrasenna);
    }

    public boolean tieneRol(String nombreRol) {
        return roles.stream().anyMatch(r -> r.getName().equals(nombreRol));
    }

    public boolean tienePermiso(String permiso) {
        return roles.stream().anyMatch(r -> r.tienePermiso(permiso));
    }

    public String getPassword() {
        return password;
    }

    public PasswordRegistry getPasswordRegistry() {
        return passwordRegistry;
    }

    public void setPasswordRegistry(@NotNull PasswordRegistry passwordRegistry) {
        this.passwordRegistry = passwordRegistry;
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

    public void setPasswordWithHasher(String password) {
        this.password = hasher.hash(password);
    }

    public boolean matchesPasswordWithHasher(String rawPassword) {
        return hasher.verify(password, rawPassword);
    }

    public void setPassword(String password) {
        setPassword(password, hasher);
    }

    public void setPassword(String newPassword, @NotNull PasswordHasher hasher) {
        if (passwordRegistry == null) {
            passwordRegistry = new PasswordRegistry(new LinkedList<>());
        }

        if (passwordRegistry.esContrasennaUsada(newPassword, hasher)) {
            throw new IllegalArgumentException("Esta contraseña ya fue usada anteriormente.");
        }

        passwordRegistry.addPassword(hasher.hash(newPassword));
        this.password = hasher.hash(newPassword);
    }

    public boolean matchesPassword(String rawPassword) {
        return hasher.verify(password, rawPassword);
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

    public void setRoles(@NotNull Set<Rol> roles) {
        this.roles = roles;
        this.adminSistema = roles.stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN_SISTEMA"));
        this.adminEconomico = roles.stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN_ECONOMICO"));
    }

    public boolean esAdministrador() {
        return adminSistema || adminEconomico;
    }

    public boolean puedeAccederAContabilidad() {
        return adminSistema || adminEconomico || tieneRol("CONTABILIDAD");
    }

    @Override
    public String toString() {
        return "Usuario{"
                + "id=" + getId()
                + ", nombre='" + fullName + '\''
                + ", identificador='" + userName + '\''
                + ", email='" + email + '\''
                + '}';
    }
}
