package com.univsoftdev.econova.contabilidad.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LibroMayorEntry {

    private LocalDate fecha;
    private String descripcion;
    private BigDecimal debito;
    private BigDecimal credito;
    private BigDecimal saldoAcumulado;

    public LibroMayorEntry(LocalDate fecha, String descripcion, BigDecimal debito,
            BigDecimal credito, BigDecimal saldoAcumulado) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.debito = debito;
        this.credito = credito;
        this.saldoAcumulado = saldoAcumulado;
    }

    // Getters y setters
    public LocalDate getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public BigDecimal getSaldoAcumulado() {
        return saldoAcumulado;
    }
}
