package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.model.BaseModel;
import com.univsoftdev.econova.security.argon2.PasswordHasher;
import jakarta.persistence.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.EqualsAndHashCode;

/**
 * Entity representing a user's password history registry.
 *
 * This class maintains a historical record of a user's previous passwords to
 * enforce security policies that prevent password reuse. The history is stored
 * as hashed values for security purposes.
 *
 * Key features: - Limits stored passwords to prevent unlimited growth
 * (configurable via PASSWORD_LIMIT) - Uses lazy loading for performance
 * optimization - Maintains relationship with User entity through one-to-one
 * mapping - Provides security methods to verify password reuse - Inherits full
 * auditing capabilities from BaseModel
 *
 * @entity Maps to "sys_password_registry" table
 * @extends BaseModel for auditing and multitenancy support
 */
@EqualsAndHashCode(callSuper = false, of = {"id"})
@Entity
@Table(name = "sys_password_registry")
public class PasswordHistory extends BaseModel {

    /**
     * Maximum number of historical passwords to maintain. When this limit is
     * reached, oldest passwords are removed automatically. Configurable
     * constant that enforces security policies.
     */
    private static final int PASSWORD_LIMIT = 10;

    private static final long serialVersionUID = 1L;

    /**
     * Bidirectional one-to-one relationship with the User entity. This
     * relationship is mapped by the User entity's "passwordHistory" field.
     * Cascade operations ensure proper persistence lifecycle management. Orphan
     * removal automatically cleans up unused password history records.
     */
    @OneToOne(mappedBy = "passwordHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;

    /**
     * Collection of historical password hashes. Stored in a separate
     * "password_history" table with lazy fetching for performance. Each hash is
     * stored as TEXT to accommodate various hashing algorithm outputs. Linked
     * list implementation provides efficient insertion and removal operations.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "password_history",
            joinColumns = @JoinColumn(name = "registry_id"))
    @Column(name = "password_hash", columnDefinition = "TEXT", nullable = false)
    private List<String> passwordHistory = new LinkedList<>();

    /**
     * Default constructor initializes an empty password history. Required for
     * JPA entity instantiation.
     */
    public PasswordHistory() {
        this.passwordHistory = new LinkedList<>();
    }

    /**
     * Constructor that initializes with a predefined list of password hashes.
     * Useful for migration scenarios or testing purposes.
     *
     * @param passwords List of hashed passwords to initialize the history
     */
    public PasswordHistory(List<String> passwords) {
        if (passwords != null) {
            this.passwordHistory = new LinkedList<>(passwords);
        }
    }

    /**
     * Adds a hashed password to the history while maintaining size limits.
     * Prevents duplicate entries and removes oldest passwords when limit is
     * exceeded.
     *
     * @param hashedPassword The argon2 or bcrypt hashed password to add
     * @return true if password was added successfully, false if it was a
     * duplicate
     * @throws IllegalArgumentException if the hashed password is null or empty
     */
    public boolean addPassword(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }

        if (passwordHistory.contains(hashedPassword)) {
            return false;
        }

        if (passwordHistory.size() >= PASSWORD_LIMIT) {
            passwordHistory.remove(0);
        }

        passwordHistory.add(hashedPassword);
        return true;
    }

    /**
     * Verifies if a raw password matches any previously used password in the
     * history. Uses the provided PasswordHasher implementation for secure
     * password verification.
     *
     * @param rawPassword The plain text password to check against history
     * @param hasher The PasswordHasher implementation (e.g., Argon2) for
     * verification
     * @return true if the password was previously used, false otherwise
     */
    public boolean isPasswordUsed(String rawPassword, PasswordHasher hasher) {
        return passwordHistory.stream()
                .anyMatch(hash -> hasher.verify(hash, rawPassword));
    }

    /**
     * Clears the entire password history. Useful for password reset scenarios
     * or administrative actions.
     */
    public void clearHistory() {
        passwordHistory.clear();
    }

    /**
     * Internal method for adding hashed passwords (Spanish naming convention).
     * This method delegates to addPassword() and exists for backward
     * compatibility.
     *
     * @param passwordHashed The hashed password to add to history
     */
    public void agregarContrasenna(String passwordHashed) {
        addPassword(passwordHashed);
    }

    /**
     * Returns an unmodifiable view of the password history. Prevents external
     * modification while allowing read access.
     *
     * @return Unmodifiable list of historical password hashes
     */
    public List<String> getPasswordHistory() {
        return Collections.unmodifiableList(passwordHistory);
    }

    /**
     * Gets the current size of the password history.
     *
     * @return Number of historical passwords currently stored
     */
    public int getHistorySize() {
        return passwordHistory.size();
    }

    /**
     * Replaces the entire password history with a new list of hashes. Clears
     * existing history before adding new entries.
     *
     * @param passwords List of hashed passwords to set as new history
     */
    public void setPasswordHistory(List<String> passwords) {
        this.passwordHistory.clear();
        if (passwords != null) {
            this.passwordHistory.addAll(passwords);
        }
    }

    /**
     * Gets the associated User entity for this password history.
     *
     * @return The User entity that owns this password history
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the associated User entity for this password history. Typically
     * managed by the ORM framework during persistence operations.
     *
     * @param user The User entity to associate with this password history
     */
    public void setUser(User user) {
        this.user = user;
    }
}
