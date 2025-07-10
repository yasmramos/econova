package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.config.finder.EjercicioFinder;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.core.model.BaseModel;
import io.ebean.annotation.Cache;
import io.ebean.annotation.DbDefault;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Cache
@Entity
@Table(name = "sys_ejercicios")
public class Ejercicio extends BaseModel {

    public static EjercicioFinder finder = new EjercicioFinder();
    private static final long serialVersionUID = 1L;

    @NotNull(message = "El nombre no puede ser nulo.")
    @Column(unique = true)
    private String nombre;

    @Pattern(regexp = "\\d{4}", message = "El año debe estar en formato YYYY.")
    @Column(name = "ejercicio_year", unique = true)
    @DbDefault(value = "2025")
    @Size(min = 4, max = 4)
    private int year;

    @NotNull(message = "La fecha de inicio no puede ser nula.")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin no puede ser nula.")
    private LocalDate fechaFin;

    private boolean iniciado = false;
    private boolean current = false;

    @OneToMany(mappedBy = "ejercicio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<Periodo> periodos = new ArrayList<>();

    @OneToMany(mappedBy = "ejercicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Transaccion> transacciones = new ArrayList<>();

    public Ejercicio() {
    }

    public Ejercicio(String nombre, int year, LocalDate inicio, LocalDate fin, List<Periodo> periodos) {
        this.nombre = nombre;
        this.year = year;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
        this.setPeriodos(periodos);
        validateDates();
    }

    public Ejercicio(String nombre, LocalDate inicio, LocalDate fin, List<Periodo> periodos) {
        this.nombre = nombre;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
        this.setPeriodos(periodos);
        validateDates();
    }

    public boolean estaEnEjercicio(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public boolean isActivo() {
        LocalDate hoy = LocalDate.now();
        return estaEnEjercicio(hoy);
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public void validateDates() {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(@NotNull List<Transaccion> transacciones) {
        if (transacciones == null) {
            throw new IllegalArgumentException("Las transacciones no pueden ser nulas.");
        }
        for (Transaccion trans : transacciones) {
            addTransaccion(trans);
        }
    }

    public void addTransaccion(@NotNull Transaccion transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }

        transaccion.setEjercicio(this);
        transacciones.add(transaccion);
    }

    public void addPeriodo(@NotNull Periodo periodo) {
        periodo.setEjercicio(this);
        periodos.add(periodo);
    }

    public void addPeriodos(@NotNull List<Periodo> periodos) {
        setPeriodos(periodos);
    }

    private void setPeriodos(@NotNull List<Periodo> periodos) {
        this.periodos.clear();
        for (Periodo periodo : periodos) {
            periodo.setEjercicio(this);
            this.periodos.add(periodo);
        }
    }

    public boolean isIniciado() {
        return iniciado;
    }

    public void setIniciado(boolean iniciado) {
        this.iniciado = iniciado;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(@NotNull LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
        validateDates();
    }

    public List<Periodo> getPeriodos() {
        return periodos;
    }
}
