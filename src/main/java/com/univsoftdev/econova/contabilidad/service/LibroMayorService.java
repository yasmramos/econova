package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.Ledger;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.contabilidad.report.LibroMayorEntry;
import com.univsoftdev.econova.contabilidad.report.LibroMayorReport;
import com.univsoftdev.econova.contabilidad.repository.LibroMayorRepository;
import com.univsoftdev.econova.core.service.BaseService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class LibroMayorService extends BaseService<Ledger, LibroMayorRepository> {

    private final CuentaService cuentaService;

    @Inject
    public LibroMayorService(LibroMayorRepository libroMayorRepository, CuentaService cuentaService) {
        super(libroMayorRepository);
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
    public LibroMayorReport generarLibroMayor(Account cuenta, Period periodo) {
        // Calcula el saldo de apertura hasta el inicio del período.
        BigDecimal saldoInicial = obtenerSaldoInicial(cuenta, periodo.getStartDate());

        // Reúne todas las transacciones asociadas a la cuenta (tanto como débito y crédito)
        // y filtra vistas dentro del período.
        List<Transaction> transacciones = cuenta.getLedger().getTransactions().stream()
                .filter(t -> !t.getDate().isBefore(periodo.getStartDate())
                && !t.getDate().isAfter(periodo.getEndDate()))
                .sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate()))
                .collect(Collectors.toList());

        BigDecimal saldoAcumulado = saldoInicial;
        List<LibroMayorEntry> entradas = new ArrayList<>();

        // Itera por cada transacción para formar el reporte
        for (Transaction t : transacciones) {
            BigDecimal debito = t.getTipo() == TipoTransaccion.DEBITO ? t.getBalance() : BigDecimal.ZERO;
            BigDecimal credito = t.getTipo() == TipoTransaccion.CREDITO ? t.getBalance() : BigDecimal.ZERO;
            saldoAcumulado = saldoAcumulado.add(debito).subtract(credito);

            // Puedes construir una descripción utilizando otro dato relevante, por ejemplo,
            // el número del asiento o una descripción propia.
            String descripcion = t.getTipo() + " - "
                    + (t.getAsiento() != null ? ("Asiento #" + t.getAsiento().getNro()) : "");

            entradas.add(new LibroMayorEntry(t.getDate(), descripcion, debito, credito, saldoAcumulado));
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
    private BigDecimal obtenerSaldoInicial(Account cuenta, LocalDate fechaInicio) {
        // Como ejemplo básico, se asume que el saldo actual de la cuenta es el saldo de apertura.
        // En un sistema real, deberías sumar o restar las transacciones previas a 'fechaInicio'.
        return cuenta.getBalance();
    }
}
