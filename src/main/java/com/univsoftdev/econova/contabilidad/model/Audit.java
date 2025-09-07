package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_auditoria")
public class Auditoria extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String accion;

    @Column(nullable = false)
    private String entidad;

    @Column(columnDefinition = "TEXT")
    private String detalles;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String usuario;

    public Auditoria() {
    }

    public Auditoria(String accion, String entidad, String detalles, LocalDate fecha, String usuario) {
        this.accion = accion;
        this.entidad = entidad;
        this.detalles = detalles;
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.usuario = usuario;
    }

    // Getters y Setters
    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s en %s", fecha, usuario, accion, entidad);
    }

    // Método factory opcional
    public static Auditoria registrar(String accion, String entidad, String detalles, String usuario) {
        return new Auditoria(accion, entidad, detalles, LocalDate.now(), usuario);
    }
}
