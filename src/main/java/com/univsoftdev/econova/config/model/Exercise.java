package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.config.finder.ExerciseFinder;
import com.univsoftdev.econova.contabilidad.model.Transaction;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false, of = {"nombre", "year"})
@Cache
@Entity
@Table(name = "sys_exercises")
public class Exercise extends BaseModel {

    public static ExerciseFinder finder = new ExerciseFinder();
    private static final long serialVersionUID = 1L;

    @NotNull(message = "El nombre no puede ser nulo.")
    @Column(unique = true)
    private String name;

    @Column(name = "exercise_year", unique = true)
    @DbDefault(value = "2025")
    private int year;

    @NotNull(message = "La fecha de inicio no puede ser nula.")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin no puede ser nula.")
    private LocalDate endDate;

    private boolean initiated = false;
    private boolean current = false;

    @OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Period> periodos;

    @OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    public Exercise() {
        this.periodos = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public Exercise(String nombre, int year, LocalDate inicio, LocalDate fin,
            List<Period> periodos, List<Transaction> transacciones) {
        this(nombre, year, inicio, fin, periodos);
        if (transacciones != null) {
            transacciones.forEach(this::addTransaccion);
        }
    }

    public Exercise(String nombre, int year, LocalDate inicio, LocalDate fin, List<Period> periodos) {
        this();
        this.name = nombre;
        this.year = year;
        this.startDate = inicio;
        this.endDate = fin;
        if (periodos != null) {
            periodos.forEach(this::addPeriodo); // Usar el método que ya valida
        }
        validateDates();
    }

    public boolean estaEnEjercicio(LocalDate fecha) {
        if (fecha == null) {
            return false;
        }
        return !fecha.isBefore(startDate) && !fecha.isAfter(endDate);
    }

    public boolean isActivo() {
        LocalDate hoy = LocalDate.now();
        return estaEnEjercicio(hoy) && isInitiated();
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public void validate() {
        validateDates();
        validateYear();
        validatePeriodos();
    }

    private void validateDates() {
        if (startDate == null || endDate == null) {
            throw new IllegalStateException("Las fechas no pueden ser nulas.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
    }

    private void validateYear() {
        if (year == 0) {
            throw new IllegalArgumentException("El año no puede ser nulo.");
        }
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException("El año debe estar entre 1900 y 2100.");
        }
    }

    private void validatePeriodos() {
        if (periodos != null && !periodos.isEmpty()) {
            // Validar que los períodos estén dentro del rango del ejercicio
            for (Period periodo : periodos) {
                if (periodo.getStartDate().isBefore(startDate)
                        || periodo.getEndDate().isAfter(endDate)) {
                    throw new IllegalStateException(
                            "Los períodos deben estar dentro del rango del ejercicio.");
                }
            }

            // Validar que los períodos no se solapen
            List<Period> sortedPeriodos = new ArrayList<>(periodos);
            sortedPeriodos.sort(Comparator.comparing(Period::getStartDate));

            for (int i = 1; i < sortedPeriodos.size(); i++) {
                if (!sortedPeriodos.get(i - 1).getEndDate()
                        .isBefore(sortedPeriodos.get(i).getStartDate())) {
                    throw new IllegalStateException("Los períodos no pueden solaparse.");
                }
            }
        }
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions.clear();
        if (transactions != null) {
            transactions.forEach(this::addTransaccion);
        }
    }

    public void addTransaccion(@NotNull Transaction transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }
        if (!transactions.contains(transaccion)) {
            transactions.add(transaccion);
            transaccion.setExercise(this);
        }
    }

    public void removeTransaccion(@NotNull Transaction transaccion) {
        if (transaccion != null && transactions.remove(transaccion)) {
            transaccion.setExercise(null);
        }
    }

    public void addPeriodo(@NotNull Period periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("El período no puede ser nulo.");
        }
        if (!periodos.contains(periodo)) {
            periodos.add(periodo);
            periodo.setExercise(this);
        }
    }

    public void removePeriodo(@NotNull Period periodo) {
        if (periodo != null && periodos.remove(periodo)) {
            periodo.setExercise(null);
        }
    }

    public void addPeriodos(@NotNull List<Period> periodos) {
        setPeriodos(periodos);
    }

    public List<Period> getPeriodos() {
        return Collections.unmodifiableList(periodos);
    }

    public void setPeriodos(List<Period> periodos) {
        this.periodos.clear();
        if (periodos != null) {
            periodos.forEach(this::addPeriodo);
        }
    }

    public boolean isInitiated() {
        return initiated;
    }

    public void setInitiated(boolean initiated) {
        this.initiated = initiated;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        validateYear();
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        if (startDate != null && endDate != null) {
            validateDates(); // Validar solo si ambas fechas existen
        }
    }

    // Método para obtener la duración del ejercicio
    public long getDuracionEnDias() {
        if (startDate != null && endDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
        return 0;
    }

    public boolean seSolapaCon(Exercise otro) {
        if (otro == null || this.startDate == null || this.endDate == null
                || otro.startDate == null || otro.endDate == null) {
            return false;
        }

        return !(this.endDate.isBefore(otro.startDate)
                || this.startDate.isAfter(otro.endDate));
    }

    // Método para verificar si está cerrado
    public boolean isCerrado() {
        return !isInitiated() && LocalDate.now().isAfter(endDate);
    }

    // Método para obtener períodos ordenados
    public List<Period> getPeriodosOrdenados() {
        List<Period> sortedPeriodos = new ArrayList<>(periodos);
        sortedPeriodos.sort(Comparator.comparing(Period::getStartDate));
        return sortedPeriodos;
    }

    @Override
    public String toString() {
        return "Ejercicio{"
                + "id=" + getId()
                + ", nombre='" + name + '\''
                + ", year=" + year
                + ", fechaInicio=" + startDate
                + ", fechaFin=" + endDate
                + ", iniciado=" + initiated
                + ", current=" + current
                + '}';
    }

}
