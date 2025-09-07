package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.config.model.Unit;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.SubSystem;
import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.core.model.AuditBaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "accountingEntry")
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "cont_accounting_entrys")
public class AccountingEntry extends AuditBaseModel {

    private static final long serialVersionUID = 1L;

    @NotNull
    private int nro;

    @NotBlank(message = "La descripción no puede estar vacía.")
    @Column(length = 500)
    private String description;

    @NotNull(message = "La fecha no puede ser nula.")
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoAsiento estadoAsiento = EstadoAsiento.EDICION;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private SubSystem subSystem;

    public AccountingEntry() {
        // Constructor por defecto requerido por Ebean
    }

    public AccountingEntry(int nro, String descripcion, LocalDate fecha, Period periodo, Unit unidad) {
        this(nro, descripcion, fecha, periodo, unidad, EstadoAsiento.EDICION);
    }

    public AccountingEntry(int nro, String descripcion, LocalDate fecha, Period periodo, Unit unidad, EstadoAsiento estadoAsiento) {
        this.nro = nro;
        this.description = descripcion;
        setFecha(fecha);
        setPeriod(periodo);
        setUnidad(unidad);
        this.estadoAsiento = estadoAsiento;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula.");
        // Validar que la fecha esté dentro del period si ya está asignado
        if (this.period != null && !this.period.isDateWithinPeriod(fecha)) {
            throw new IllegalArgumentException("La fecha debe estar dentro del periodo.");
        }
    }

    @Override
    public void setPeriod(@NotNull Period periodo) {
        super.setPeriod(periodo); // Llama al setter de AuditBaseModel
        // Validación adicional específica para AccountingEntry
        if (this.fecha != null && !periodo.isDateWithinPeriod(this.fecha)) {
            throw new IllegalArgumentException("La fecha del asiento no está dentro del periodo.");
        }
    }

    @Override
    public void setUnidad(@NotNull Unit unidad) {
        super.setUnidad(unidad); // Llama al setter de AuditBaseModel
        // Validación adicional específica para AccountingEntry
        if (!unidad.isActive()) {
            throw new IllegalArgumentException("No se pueden crear asientos para unidades inactivas.");
        }
    }

    public BigDecimal getTotalDebitos() {
        return transactions.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.DEBITO)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCreditos() {
        return transactions.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.CREDITO)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDiferencia() {
        return getTotalDebitos().subtract(getTotalCreditos()).abs();
    }

    public boolean estaCuadrado() {
        return getTotalDebitos().compareTo(getTotalCreditos()) == 0;
    }

    public boolean tieneTransacciones() {
        return !transactions.isEmpty();
    }

    public boolean esEditable() {
        return estadoAsiento == EstadoAsiento.EDICION;
    }

    public boolean esConfirmado() {
        return estadoAsiento == EstadoAsiento.CONFIRMADO;
    }

    public void confirmar() {
        if (!estaCuadrado()) {
            throw new IllegalStateException("No se puede confirmar un asiento desequilibrado. Diferencia: " + getDiferencia());
        }
        if (!esDelPeriodoActivo()) {
            throw new IllegalStateException("No se puede confirmar un asiento fuera del periodo activo.");
        }
        this.estadoAsiento = EstadoAsiento.CONFIRMADO;
    }

    public void revertirConfirmacion() {
        this.estadoAsiento = EstadoAsiento.EDICION;
    }

    public boolean esDelPeriodoActivo() {
        return period != null && period.isActivo() && period.isDateWithinPeriod(fecha);
    }

    public boolean esDelEjercicioActivo() {
        return period != null && period.getExercise() != null && period.getExercise().isActivo();
    }

    public String getDescripcionResumida(int maxLength) {
        if (description == null) {
            return "";
        }
        return description.length() > maxLength
                ? description.substring(0, maxLength) + "..." : description;
    }

    // Métodos para manejo de la relación bidireccional
    public void addTransaccion(Transaction transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }
        if (!this.transactions.contains(transaccion)) {
            this.transactions.add(transaccion);
            transaccion.setAsiento(this);
        }
    }

    public void removeTransaccion(Transaction transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }
        if (this.transactions.remove(transaccion)) {
            transaccion.setAsiento(null);
        }
    }

    public void clearTransacciones() {
        // Romper relación bidireccional primero
        for (Transaction transaccion : new ArrayList<>(transactions)) {
            removeTransaccion(transaccion);
        }
    }

    // Método toString mejorado
    @Override
    public String toString() {
        return "Asiento{"
                + "id=" + getId()
                + ", nro=" + nro
                + ", descripcion='" + getDescripcionResumida(30) + '\''
                + ", fecha=" + fecha
                + ", estado=" + estadoAsiento
                + ", unidad=" + (unit != null ? unit.getCode() : "null")
                + ", tenantId='" + getTenantId() + '\''
                + '}';
    }
}
