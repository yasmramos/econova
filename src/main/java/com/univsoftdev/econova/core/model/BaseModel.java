package com.univsoftdev.econova.core.model;

import com.univsoftdev.econova.config.model.User;
import io.ebean.annotation.Index;
import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.TenantId;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import io.ebean.annotation.WhoCreated;
import io.ebean.annotation.WhoModified;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Enhanced base model with comprehensive multitenancy and auditing support.
 * Provides automatic management of tenant ID, timestamps, and user modification tracking.
 * This abstract class serves as the foundation for all entity classes in the system.
 * 
 * Features include:
 * - Automatic tenant isolation through @TenantId
 * - Soft delete capability with @SoftDelete
 * - Creation and modification timestamps
 * - User audit tracking (who created/modified records)
 * - Optimistic concurrency control with @Version
 * - Serialization support for distributed environments
 * 
 * @extends io.ebean.Model for Ebean ORM functionality
 * @implements Serializable for object transmission support
 * @annotation MappedSuperclass for JPA inheritance mapping
 */
@MappedSuperclass
public abstract class BaseModel extends io.ebean.Model implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key identifier with auto-increment strategy.
     * Indexed for optimized query performance.
     */
    @Index
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * Soft delete flag indicating whether the record is logically deleted.
     * When true, the record is considered deleted but remains in the database.
     * Managed automatically by Ebean's @SoftDelete annotation.
     */
    @SoftDelete
    protected boolean deleted = false;

    /**
     * Tenant identifier for multitenancy support.
     * Ensures data isolation between different tenants in a shared database.
     * Maximum length of 50 characters and mandatory for all records.
     */
    @TenantId
    @Column(length = 50, nullable = false)
    protected String tenantId;

    /**
     * Version number for optimistic concurrency control.
     * Automatically incremented on each update to prevent lost updates.
     */
    @Version
    protected Long version;

    /**
     * Timestamp indicating when the record was initially created.
     * Set automatically upon entity creation and not updatable.
     */
    @WhenCreated
    @Column(updatable = false)
    protected Instant whenCreated;

    /**
     * Timestamp indicating when the record was last modified.
     * Updated automatically on each entity modification.
     */
    @WhenModified
    protected Instant whenModified;

    /**
     * Reference to the user who created this record.
     * Lazy-loaded to optimize performance when user data isn't immediately needed.
     * Not updatable after initial creation.
     */
    @WhoCreated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", updatable = false)
    protected User createdBy;

    /**
     * Reference to the user who last modified this record.
     * Lazy-loaded for performance optimization.
     * Updated automatically on each modification.
     */
    @WhoModified
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by_id")
    protected User modifiedBy;

    /**
     * Gets the tenant identifier for multitenancy isolation.
     * @return the tenant ID string
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets the tenant identifier for multitenancy isolation.
     * @param tenantId the tenant ID to set
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Gets the primary key identifier of the entity.
     * @return the ID value, or null if not persisted
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the primary key identifier.
     * Typically used only for testing or specific migration scenarios.
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Checks if the record is logically deleted (soft delete).
     * @return true if the record is marked as deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the soft delete status of the record.
     * Prefer using softDelete() and restore() methods for business logic.
     * @param deleted the deletion status to set
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Gets the current version number for optimistic locking.
     * @return the version number
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version number for optimistic locking.
     * Typically managed automatically by the persistence framework.
     * @param version the version number to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Gets the creation timestamp of the record.
     * @return the Instant when the record was created
     */
    public Instant getWhenCreated() {
        return whenCreated;
    }

    /**
     * Sets the creation timestamp.
     * Typically used only for data migration or testing scenarios.
     * @param whenCreated the creation timestamp to set
     */
    public void setWhenCreated(Instant whenCreated) {
        this.whenCreated = whenCreated;
    }

    /**
     * Gets the last modification timestamp of the record.
     * @return the Instant when the record was last modified
     */
    public Instant getWhenModified() {
        return whenModified;
    }

    /**
     * Sets the modification timestamp.
     * Typically managed automatically by the persistence framework.
     * @param whenModified the modification timestamp to set
     */
    public void setWhenModified(Instant whenModified) {
        this.whenModified = whenModified;
    }

    /**
     * Gets the user who created this record.
     * @return the User entity that created the record
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created this record.
     * Typically managed automatically by the @WhoCreated annotation.
     * @param createdBy the User entity that created the record
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the user who last modified this record.
     * @return the User entity that last modified the record
     */
    public User getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the user who last modified this record.
     * Typically managed automatically by the @WhoModified annotation.
     * @param modifiedBy the User entity that modified the record
     */
    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Helper method to check if the entity has been persisted to the database.
     * @return true if the entity has no ID (not persisted), false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    /**
     * Performs a logical delete operation on the entity.
     * Marks the record as deleted while keeping it in the database.
     * Used instead of physical deletion for audit and recovery purposes.
     */
    public void softDelete() {
        this.deleted = true;
    }

    /**
     * Restores a logically deleted entity to active status.
     * Reverses the soft delete operation and makes the record active again.
     */
    public void restore() {
        this.deleted = false;
    }
}