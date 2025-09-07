package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.config.finder.PeriodoFinder;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.core.model.AuditBaseModel;
import io.ebean.annotation.Cache;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Cache
@Entity
@Table(name = "sys_periods")
public class Period extends AuditBaseModel implements Serializable {

    public static PeriodoFinder finder = new PeriodoFinder();
    private static final long serialVersionUID = 1L;

    @NotNull(message = "El nombre no puede ser nulo.")
    private String name;

    @NotNull(message = "La fecha de inicio no puede ser nula.")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin no puede ser nula.")
    private LocalDate endDate;

    private boolean current;
    private boolean active;

    public Period() {
    }

    public Period(String nombre, @NotNull LocalDate inicio, @NotNull LocalDate fin) {
        this(nombre, inicio, fin, null);
    }

    public Period(String nombre, @NotNull LocalDate inicio, @NotNull LocalDate fin, @NotNull Exercise ejercicio) {
        this.name = nombre;
        this.startDate = inicio;
        this.endDate = fin;
        this.exercise = ejercicio;
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
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
    }

    public void addTransaccion(Transaction transaccion) {
        if (!isDateWithinPeriod(transaccion.getDate())) {
            throw new IllegalArgumentException("La transacción no corresponde al período.");
        }
        transactions.add(transaccion);
    }

    public List<Transaction> getTransacciones() {
        return transactions;
    }

    public void setTransacciones(List<Transaction> transacciones) {
        this.transactions = transacciones;
    }

    @Override
    public void setExercise(@NotNull Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("El ejercicio no puede ser nulo.");
        }

        if (this.exercise != null && this.exercise.getPeriodos().contains(this)) {
            this.exercise.getPeriodos().remove(this); // Limpiar referencia anterior
        }

        this.exercise = exercise;
        exercise.getPeriodos().add(this); // Asegurar vinculación inversa
    }

    public void addTransacciones(@NotNull List<Transaction> nuevas) {
        for (Transaction t : nuevas) {
            if (!isDateWithinPeriod(t.getDate())) {
                throw new IllegalArgumentException("Una transacción no corresponde al período.");
            }
        }
        transactions.addAll(nuevas);
    }

    public String getName() {
        return name;
    }

    public boolean isActivo() {
        return !startDate.isAfter(LocalDate.now()) && !endDate.isBefore(LocalDate.now());
    }

    public int getYear() {
        return startDate.getYear();
    }

    public String getNombreConFechas() {
        return String.format("%s (%s - %s)", name, startDate, endDate);
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull LocalDate endDate) {
        this.endDate = endDate;
        validateDates();
    }

    public boolean isDateWithinPeriod(@org.jetbrains.annotations.NotNull LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

}
