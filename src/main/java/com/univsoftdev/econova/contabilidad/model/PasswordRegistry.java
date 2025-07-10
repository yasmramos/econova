package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.seguridad.PasswordHasher;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_password_registry")
public class PasswordRegistry extends BaseModel {

    private static final int PASSWORD_LIMIT = 10;
    private static final long serialVersionUID = 1L;

    @OneToOne(mappedBy = "passwordRegistry", cascade = CascadeType.ALL)
    private User usuario;

    @Column(columnDefinition = "TEXT")
    private final LinkedList<String> passwords;

    public PasswordRegistry() {
        this.passwords = new LinkedList<>();
    }

    public PasswordRegistry(LinkedList<String> passwords) {
        this();
        this.passwords.addAll(passwords);
    }

    /**
     * Agrega una contraseña al historial. Si excede el límite, elimina la más
     * antigua.
     *
     * @param password Contraseña hasheada
     * @return true si se agregó correctamente, false si ya existía
     */
    public boolean addPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía.");
        }

        if (passwords.contains(password)) {
            System.out.println("La contraseña ya ha sido utilizada con anterioridad por el usuario.");
            return false;
        }

        if (passwords.size() >= PASSWORD_LIMIT) {
            passwords.removeFirst(); // Elimina la más antigua
        }

        passwords.addLast(password); // Agrega la nueva al final
        return true;
    }

    /**
     * Verifica si una contraseña ya fue usada antes.
     *
     * @param rawPassword Contraseña sin hash
     * @param hasher Implementación del hasher (ej: Argon2)
     * @return true si la contraseña fue usada previamente
     */
    public boolean esContrasennaUsada(String rawPassword, PasswordHasher hasher) {
        for (String hash : passwords) {
            if (hasher.verify(hash, rawPassword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Agrega una contraseña directamente (para uso interno).
     * @param passwordHashed
     */
    public void agregarContrasenna(String passwordHashed) {
        addPassword(passwordHashed);
    }

    // --- Getters y Setters ---
    public LinkedList<String> getPasswords() {
        return passwords;
    }

    public void setPasswords(LinkedList<String> passwords) {
        this.passwords.clear();
        this.passwords.addAll(passwords);
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }
}
