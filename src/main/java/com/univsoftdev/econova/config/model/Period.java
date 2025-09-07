package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.config.finder.PeriodoFinder;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.core.model.BaseModel;
import io.ebean.annotation.Cache;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Cache
@Entity
@Table(name = "sys_periodos")
public class Periodo extends BaseModel implements Serializable {

    public static PeriodoFinder finder = new PeriodoFinder();
    private static final long serialVersionUID = 1L;

    @NotNull(message = "El nombre no puede ser nulo.")
    private String nombre;

    @NotNull(message = "La fecha de inicio no puede ser nula.")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin no puede ser nula.")
    private LocalDate fechaFin;

    @ManyToOne
    @JoinColumn(name = "ejercicio_id")
    private Ejercicio ejercicio;
    private boolean current;
    private boolean active;

    @OneToMany(mappedBy = "periodo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> transacciones = new ArrayList<>();

    public Periodo() {
    }

    public Periodo(String nombre, @NotNull LocalDate inicio, @NotNull LocalDate fin) {
        this(nombre, inicio, fin, null);
    }

    public Periodo(String nombre, @NotNull LocalDate inicio, @NotNull LocalDate fin, @NotNull Ejercicio ejercicio) {
        this.nombre = nombre;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
        this.ejercicio = ejercicio;
        validateDates();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    private void validateDates() {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
    }

    public void addTransaccion(Transaccion transaccion) {
        if (!isDateWithinPeriod(transaccion.getFecha())) {
            throw new IllegalArgumentException("La transacción no corresponde al período.");
        }
        transacciones.add(transaccion);
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(@NotNull Ejercicio ejercicio) {
        if (ejercicio == null) {
            throw new IllegalArgumentException("El ejercicio no puede ser nulo.");
        }

        if (this.ejercicio != null && this.ejercicio.getPeriodos().contains(this)) {
            this.ejercicio.getPeriodos().remove(this); // Limpiar referencia anterior
        }

        this.ejercicio = ejercicio;
        ejercicio.getPeriodos().add(this); // Asegurar vinculación inversa
    }

    public void addTransacciones(@NotNull List<Transaccion> nuevas) {
        for (Transaccion t : nuevas) {
            if (!isDateWithinPeriod(t.getFecha())) {
                throw new IllegalArgumentException("Una transacción no corresponde al período.");
            }
        }
        transacciones.addAll(nuevas);
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return !fechaInicio.isAfter(LocalDate.now()) && !fechaFin.isBefore(LocalDate.now());
    }

    public int getYear() {
        return fechaInicio.getYear();
    }

    public String getNombreConFechas() {
        return String.format("%s (%s - %s)", nombre, fechaInicio, fechaFin);
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(@NotNull LocalDate fechaFin) {
        this.fechaFin = fechaFin;
        validateDates();
    }

    public boolean isDateWithinPeriod(@org.jetbrains.annotations.NotNull LocalDate date) {
        return !date.isBefore(fechaInicio) && !date.isAfter(fechaFin);
    }

}
