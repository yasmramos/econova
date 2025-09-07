package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.core.model.AuditBaseModel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_audit")
public class Audit extends AuditBaseModel {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entity;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "audit_date", nullable = false)
    private LocalDateTime date;

    public Audit() {
    }

    public Audit(String accion, String entidad, String detalles, LocalDateTime fecha) {
        this.action = accion;
        this.entity = entidad;
        this.details = detalles;
        this.date = fecha != null ? fecha : LocalDateTime.now();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s en %s", date, user, action, entity);
    }

}
