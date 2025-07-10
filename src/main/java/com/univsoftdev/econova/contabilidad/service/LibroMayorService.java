package com.univsoftdev.econova.contabilidad.service;

import jakarta.inject.Singleton;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.TipoTransaccion;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.contabilidad.report.LibroMayorEntry;
import com.univsoftdev.econova.contabilidad.report.LibroMayorReport;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class LibroMayorService {

    private final CuentaService cuentaService;

    @Inject
    public LibroMayorService(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    /**
     * Genera el Libro Mayor para una cuenta y un período determinado. Se
     * calcula el saldo de apertura, se filtran y ordenan las transacciones del
     * período y se va actualizando el saldo acumulado.
     *
     * @param cuenta La cuenta para la que se generará el reporte.
     * @param periodo El período durante el cual se filtran las transacciones.
     * @return Un objeto LibroMayorReport con la información del reporte.
     */
    public LibroMayorReport generarLibroMayor(Cuenta cuenta, Periodo periodo) {
        // Calcula el saldo de apertura hasta el inicio del período.
        BigDecimal saldoInicial = obtenerSaldoInicial(cuenta, periodo.getFechaInicio());

        // Reúne todas las transacciones asociadas a la cuenta (tanto como débito y crédito)
        // y filtra vistas dentro del período.
        List<Transaccion> transacciones = cuenta.getLibroMayor().getTransacciones().stream()
                .filter(t -> !t.getFecha().isBefore(periodo.getFechaInicio())
                && !t.getFecha().isAfter(periodo.getFechaFin()))
                .sorted((t1, t2) -> t1.getFecha().compareTo(t2.getFecha()))
                .collect(Collectors.toList());

        BigDecimal saldoAcumulado = saldoInicial;
        List<LibroMayorEntry> entradas = new ArrayList<>();

        // Itera por cada transacción para formar el reporte
        for (Transaccion t : transacciones) {
            BigDecimal debito = t.getTipo() == TipoTransaccion.DEBITO ? t.getMonto() : BigDecimal.ZERO;
            BigDecimal credito = t.getTipo() == TipoTransaccion.CREDITO ? t.getMonto() : BigDecimal.ZERO;
            saldoAcumulado = saldoAcumulado.add(debito).subtract(credito);

            // Puedes construir una descripción utilizando otro dato relevante, por ejemplo,
            // el número del asiento o una descripción propia.
            String descripcion = t.getTipo() + " - "
                    + (t.getAsiento() != null ? ("Asiento #" + t.getAsiento().getNro()) : "");

            entradas.add(new LibroMayorEntry(t.getFecha(), descripcion, debito, credito, saldoAcumulado));
        }

        return new LibroMayorReport(cuenta, saldoInicial, entradas);
    }

    /**
     * Calcula el saldo inicial de la cuenta hasta el inicio del período. La
     * implementación real debería considerar todas las transacciones anteriores
     * al período.
     *
     * @param cuenta La cuenta de la cual se calculará el saldo de apertura.
     * @param fechaInicio La fecha de inicio del período.
     * @return El saldo inicial en esa fecha.
     */
    private BigDecimal obtenerSaldoInicial(Cuenta cuenta, LocalDate fechaInicio) {
        // Como ejemplo básico, se asume que el saldo actual de la cuenta es el saldo de apertura.
        // En un sistema real, deberías sumar o restar las transacciones previas a 'fechaInicio'.
        return cuenta.getSaldo();
    }
}
