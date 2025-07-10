package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.config.model.Periodo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.univsoftdev.econova.*;
import com.univsoftdev.econova.contabilidad.SubSistemas;
import com.univsoftdev.econova.core.model.AuditBaseModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;

@XmlRootElement(name = "asiento")
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cont_asientos")
public class Asiento extends AuditBaseModel {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "El número no puede ser nulo.")
    private int nro;

    @NotBlank(message = "La descripción no puede estar vacía.")
    private String descripcion;

    @NotNull(message = "La fecha no puede ser nula.")
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private EstadoAsiento estadoAsiento;

    @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Transaccion> transacciones = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private SubSistemas subSistema;
    
    public Asiento() {
    }

    public Asiento(int nro, String descripcion, LocalDate fecha, Periodo periodo) {
        this(nro, descripcion, fecha, periodo, EstadoAsiento.EDICION);
    }

    public Asiento(int nro, String descripcion, LocalDate fecha, Periodo periodo, EstadoAsiento estadoAsiento) {
        this();
        this.nro = nro;
        this.descripcion = descripcion;
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula.");
        this.periodo = Objects.requireNonNull(periodo, "El periodo no puede ser nulo.");
        this.estadoAsiento = estadoAsiento;

        if (!periodo.isDateWithinPeriod(fecha)) {
            throw new IllegalArgumentException("La fecha debe estar dentro del periodo.");
        }
    }

    public BigDecimal getTotalDebitos() {
        return transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.DEBITO)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCreditos() {
        return transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.CREDITO)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean tieneTransacciones() {
        return !transacciones.isEmpty();
    }

    public boolean esEditable() {
        return estadoAsiento == EstadoAsiento.EDICION;
    }

    public boolean esConfirmado() {
        return estadoAsiento == EstadoAsiento.CONFIRMADO;
    }

    public void confirmar() {
        if (!estaCuadrado()) {
            throw new IllegalStateException("No se puede confirmar un asiento desequilibrado.");
        }
        this.estadoAsiento = EstadoAsiento.CONFIRMADO;
    }

    public boolean esDelPeriodoActivo() {
        return periodo != null && periodo.isDateWithinPeriod(fecha);
    }

    public boolean esDelEjercicioActivo() {
        return periodo != null && periodo.getEjercicio().isActivo();
    }

    public String getDescripcionResumida(int maxLength) {
        return descripcion.length() > maxLength
                ? descripcion.substring(0, maxLength) + "..." : descripcion;
    }

    public SubSistemas getSubSistema() {
        return subSistema;
    }

    public void setSubSistema(@NotNull SubSistemas subSistema) {
        this.subSistema = subSistema;
    }

    public void addTransaccion(Transaccion transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }
        transaccion.setAsiento(this); // Ensure bidirectional relationship
        this.transacciones.add(transaccion);
    }

    public void deleteTransaccion(@NotNull Transaccion transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }
        transaccion.setAsiento(null); // Break bidirectional relationship
        this.transacciones.remove(transaccion);
    }

    public boolean estaCuadrado() {
        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;
        for (Transaccion t : transacciones) {
            if (t.getTipo() == TipoTransaccion.DEBITO) {
                totalDebitos = totalDebitos.add(t.getMonto());
            } else if (t.getTipo() == TipoTransaccion.CREDITO) {
                totalCreditos = totalCreditos.add(t.getMonto());
            }
        }
        return totalDebitos.compareTo(totalCreditos) == 0;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public int getNro() {
        return nro;
    }

    public void setNro(int nro) {
        this.nro = nro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(@NotNull LocalDate fecha) {
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula.");
    }

    public EstadoAsiento getEstadoAsiento() {
        return estadoAsiento;
    }

    public void setEstadoAsiento(@NotNull EstadoAsiento estadoAsiento) {
        this.estadoAsiento = estadoAsiento;
    }

}
