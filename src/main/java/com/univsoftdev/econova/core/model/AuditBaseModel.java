package com.univsoftdev.econova.core.model;

import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.config.model.User;
import io.ebean.annotation.WhoCreated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

@MappedSuperclass
public abstract class AuditBaseModel extends BaseModel {

    @NotNull(message = "El usuario es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    @WhoCreated
    protected User usuario;

    @NotNull(message = "La unidad organizativa es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", nullable = false, updatable = false)
    protected Unidad unidad;

    @NotNull(message = "El período contable es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false, updatable = false)
    protected Periodo periodo;

    @NotNull(message = "El ejercicio contable es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejercicio_id", nullable = false, updatable = false)
    protected Ejercicio ejercicio;
  
    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(@NotNull User usuario) {
        this.usuario = usuario;
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidad unidad) {
        this.unidad = unidad;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(@NotNull Periodo periodo) {
        this.periodo = periodo;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(@NotNull Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }

}
