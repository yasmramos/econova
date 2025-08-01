package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.config.model.Unidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.univsoftdev.econova.*;
import com.univsoftdev.econova.contabilidad.finder.TransaccionFinder;
import com.univsoftdev.econova.core.model.AuditBaseModel;
import io.ebean.annotation.DbDefault;
import jakarta.persistence.CascadeType;
import java.util.Objects;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cont_transacciones")
public class Transaccion extends AuditBaseModel {

    private static final long serialVersionUID = 1L;

    transient static TransaccionFinder finder = new TransaccionFinder();

    private String descripcion;

    @NotNull(message = "El tipo no puede ser nulo.")
    private TipoTransaccion tipo;

    @Column(precision = 15, scale = 2)
    private BigDecimal monto;

    @NotNull(message = "La fecha no puede ser nula.")
    private LocalDate fecha;

    @NotNull(message = "La moneda no puede ser nula.")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "moneda_id")
    private Moneda moneda;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "asiento_id")
    private Asiento asiento;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id")
    private Cuenta cuenta;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_mayor_id")
    private LibroMayor libroMayor;

    public Transaccion() {
    }

    public Transaccion(@NotNull String descripcion,
            @NotNull TipoTransaccion tipo,
            @NotNull BigDecimal monto,
            @NotNull LocalDate fecha,
            @NotNull Moneda moneda,
            @NotNull Asiento asiento,
            @NotNull Cuenta cuenta,
            @NotNull LibroMayor libroMayor) {
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.moneda = moneda;
        this.asiento = asiento;
        this.cuenta = cuenta;
        this.libroMayor = libroMayor;
    }

    public Transaccion(
            @NotNull TipoTransaccion tipo,
            @NotNull BigDecimal monto,
            @NotNull LocalDate fecha,
            @NotNull Moneda moneda,
            @NotNull User usuario,
            @NotNull Cuenta cuenta) {
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.moneda = moneda;
        this.usuario = usuario;
        this.cuenta = cuenta;
    }

    public Transaccion(@NotNull TipoTransaccion tipoTransaccion,
            @NotNull BigDecimal saldo,
            @NotNull Cuenta cuenta) {
        this.tipo = tipoTransaccion;
        this.monto = saldo;
        this.cuenta = cuenta;
    }

    public void setLibroMayor(@NotNull LibroMayor libroMayor) {
        if (this.libroMayor != null) {
            this.libroMayor.getTransacciones().remove(this);
        }
        this.libroMayor = libroMayor;
        libroMayor.getTransacciones().add(this);
    }

    public boolean esMonedaPrincipal(String codigo) {
        return moneda.getSymbol().equals(codigo);
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        if (this.asiento != null) {
            this.asiento.getTransacciones().remove(this);
        }
        this.asiento = asiento;
        if (asiento != null) {
            asiento.getTransacciones().add(this);
        }
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(@NotNull Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public void setUnidad(@NotNull Unidad unidad) {
        this.unidad.getTransacciones().remove(this);
        this.unidad = unidad;
        unidad.getTransacciones().add(this);
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public boolean esDebito() {
        return tipo == TipoTransaccion.DEBITO;
    }

    public boolean esCredito() {
        return tipo == TipoTransaccion.CREDITO;
    }

    public BigDecimal getSaldo() {
        return esDebito() ? monto : monto.negate();
    }

    public String getDescripcion() {
        return String.format("%s - %s %s",
                tipo,
                monto.stripTrailingZeros().toPlainString(),
                moneda.getSymbol()
        );
    }

    public void setMonto(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo.");
        }
        this.monto = monto;
    }

    public LibroMayor getLibroMayor() {
        return libroMayor;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
