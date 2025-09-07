package com.univsoftdev.econova.contabilidad.report;

import com.univsoftdev.econova.contabilidad.model.Account;
import java.math.BigDecimal;
import java.util.List;

public class LibroMayorReport {

    private final Account cuenta;
    private final BigDecimal saldoInicial;
    private final List<LibroMayorEntry> entradas;

    public LibroMayorReport(Account cuenta, BigDecimal saldoInicial, List<LibroMayorEntry> entradas) {
        this.cuenta = cuenta;
        this.saldoInicial = saldoInicial;
        this.entradas = entradas;
    }

    // Getters y setters
    public Account getCuenta() {
        return cuenta;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public List<LibroMayorEntry> getEntradas() {
        return entradas;
    }
}
