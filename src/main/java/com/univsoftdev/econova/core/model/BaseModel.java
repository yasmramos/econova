package com.univsoftdev.econova.core.model;

import com.univsoftdev.econova.MyTenantSchemaProvider;
import com.univsoftdev.econova.core.multitenancy.TenantContext;
import com.univsoftdev.econova.config.model.User;
import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.TenantId;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import io.ebean.annotation.WhoCreated;
import io.ebean.annotation.WhoModified;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

/**
 * Modelo base mejorado con soporte completo para multitenancy.
 * Incluye gestión automática de tenant ID y esquema usando el nuevo sistema de contexto.
 */
@MappedSuperclass
public abstract class BaseModel extends io.ebean.Model implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SoftDelete
    @Column(nullable = false)
    private boolean deleted = false;

    @TenantId
    @Column(name = "tenant_id", length = 50)
    protected String tenantId;

    @NotNull
    @Column(name = "schema_tenant", length = 50)
    private String schemaTenant;

    @Version
    private Long version;

    @WhenCreated
    @Column(updatable = false)
    private Instant whenCreated;

    @WhenModified
    private Instant whenModified;

    @WhoCreated
    @Column(updatable = false, length = 100)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", insertable = false, updatable = false)
    private User whoCreated;

    @WhoModified
    @Column(length = 100)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by_id", insertable = false, updatable = false)
    private User whoModified;

    @PrePersist
    protected void onCreate() {
        this.whenCreated = Instant.now();
        this.whenModified = Instant.now();
        
        // Usar el nuevo sistema de contexto de tenant
        Optional<TenantContext> contextOpt = TenantContext.getCurrent();
        if (contextOpt.isPresent()) {
            TenantContext context = contextOpt.get();
            this.tenantId = context.getTenantId();
            this.schemaTenant = context.getSchemaName();
        } else {
            // Fallback al sistema anterior para compatibilidad
            String tenant = MyTenantSchemaProvider.getCurrentTenant().get();
            this.tenantId = tenant != null ? tenant : "accounting";
            this.schemaTenant = tenant != null ? tenant : "accounting";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.whenModified = Instant.now();
        
        // Validar que el tenant ID no haya cambiado
        Optional<TenantContext> contextOpt = TenantContext.getCurrent();
        if (contextOpt.isPresent()) {
            TenantContext context = contextOpt.get();
            if (!context.getTenantId().equals(this.tenantId)) {
                throw new IllegalStateException(
                    String.format("Intento de actualizar entidad de tenant '%s' desde contexto de tenant '%s'",
                        this.tenantId, context.getTenantId()));
            }
        }
    }

    /**
     * Verifica si la entidad pertenece al tenant especificado
     */
    public boolean belongsToTenant(String tenantId) {
        return tenantId != null && tenantId.equals(this.tenantId);
    }

    /**
     * Verifica si la entidad pertenece al tenant actual
     */
    public boolean belongsToCurrentTenant() {
        return belongsToTenant(TenantContext.getCurrentTenantId());
    }

    /**
     * Valida que la entidad pertenezca al tenant actual
     */
    public void validateTenantOwnership() {
        if (!belongsToCurrentTenant()) {
            throw new IllegalStateException(
                String.format("Entidad pertenece al tenant '%s' pero el contexto actual es '%s'",
                    this.tenantId, TenantContext.getCurrentTenantId()));
        }
    }

    // Getters y setters

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSchemaTenant() {
        return schemaTenant;
    }

    public void setSchemaTenant(String schemaTenant) {
        this.schemaTenant = schemaTenant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getWhenCreated() {
        return whenCreated;
    }

    public void setWhenCreated(Instant whenCreated) {
        this.whenCreated = whenCreated;
    }

    public Instant getWhenModified() {
        return whenModified;
    }

    public void setWhenModified(Instant whenModified) {
        this.whenModified = whenModified;
    }

    public User getWhoCreated() {
        return whoCreated;
    }

    public void setWhoCreated(User whoCreated) {
        this.whoCreated = whoCreated;
    }

    public User getWhoModified() {
        return whoModified;
    }

    public void setWhoModified(User whoModified) {
        this.whoModified = whoModified;
    }
}
