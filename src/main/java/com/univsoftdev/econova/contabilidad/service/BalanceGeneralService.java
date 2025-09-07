package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.config.service.EjercicioService;
import com.univsoftdev.econova.config.service.PeriodoService;
import com.univsoftdev.econova.contabilidad.LineaBalance;
import com.univsoftdev.econova.contabilidad.NatureOfAccount;
import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.config.model.Period;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class BalanceGeneralService {

    private final CuentaService cuentaService;
    private final EjercicioService ejercicioService;
    private final PeriodoService periodoService;

    @Inject
    public BalanceGeneralService(CuentaService cuentaService, EjercicioService ejercicioService, PeriodoService periodoService) {
        this.cuentaService = cuentaService;
        this.ejercicioService = ejercicioService;
        this.periodoService = periodoService;
    }

    public List<LineaBalance> generarBalanceGeneral(LocalDate inicioPeriodo, LocalDate finPeriodo) {
        if (inicioPeriodo == null || finPeriodo == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin del periodo no pueden ser nulas.");
        }

        List<Account> cuentas = cuentaService.findAll();
        return cuentas.stream()
                .map(cuenta -> mapearCuentaALineaBalance(cuenta, inicioPeriodo, finPeriodo))
                .collect(Collectors.toList());
    }

    private LineaBalance mapearCuentaALineaBalance(Account cuenta, LocalDate inicio, LocalDate fin) {
        LineaBalance lineaBalance = new LineaBalance();
        lineaBalance.setCodigo(cuenta.getCode());
        lineaBalance.setDescripcion(cuenta.getName());

        BigDecimal[] saldosPeriodo = calcularSaldosPeriodo(cuenta, inicio, fin);
        asignarSaldosPorNaturaleza(lineaBalance, cuenta.getNatureOfAccount(), saldosPeriodo[0], saldosPeriodo[1]);

        BigDecimal[] saldosAcumulados = calcularSaldosAcumulados(cuenta);
        lineaBalance.setDebitoAcumulado(saldosAcumulados[0]);
        lineaBalance.setCreditoAcumulado(saldosAcumulados[1]);

        return lineaBalance;
    }

    private BigDecimal[] calcularSaldosPeriodo(Account cuenta, LocalDate inicio, LocalDate fin) {
        List<Transaction> transaccionesPeriodo = cuenta.getLedger().getTransactions().stream()
                .filter(t -> t.getDate().isAfter(inicio.minusDays(1)) && t.getDate().isBefore(fin.plusDays(1)))
                .collect(Collectors.toList());

        BigDecimal debitos = transaccionesPeriodo.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.DEBITO)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditos = transaccionesPeriodo.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.CREDITO)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BigDecimal[]{debitos, creditos};
    }

    private BigDecimal[] calcularSaldosAcumulados(Account cuenta) {
        BigDecimal debitos = cuenta.getLedger().getTransactions().stream()
                .filter(t -> t.getTipo() == TipoTransaccion.DEBITO)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditos = cuenta.getLedger().getTransactions().stream()
                .filter(t -> t.getTipo() == TipoTransaccion.CREDITO)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BigDecimal[]{debitos, creditos};
    }

    private void asignarSaldosPorNaturaleza(LineaBalance lineaBalance,
            NatureOfAccount naturaleza,
            BigDecimal debitos,
            BigDecimal creditos) {
        if (naturaleza == NatureOfAccount.DEBTOR) {
            BigDecimal saldoNeto = debitos.subtract(creditos);
            if (saldoNeto.compareTo(BigDecimal.ZERO) >= 0) {
                lineaBalance.setDebitoPeriodo(saldoNeto);
            } else {
                lineaBalance.setCreditoPeriodo(saldoNeto.abs());
            }
        } else {
            BigDecimal saldoNeto = creditos.subtract(debitos);
            if (saldoNeto.compareTo(BigDecimal.ZERO) >= 0) {
                lineaBalance.setCreditoPeriodo(saldoNeto);
            } else {
                lineaBalance.setDebitoPeriodo(saldoNeto.abs());
            }
        }
    }

    public List<LineaBalance> generarBalanceGeneralMensual(Period periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo no puede ser nulo.");
        }

        LocalDate inicioPeriodo = periodo.getStartDate();
        LocalDate finPeriodo = periodo.getEndDate();

        return generarBalanceGeneral(inicioPeriodo, finPeriodo);
    }

    public List<LineaBalance> generarBalanceGeneralAnual(String year) {
        if (year == null || year.trim().isEmpty()) {
            throw new IllegalArgumentException("El año no puede ser nulo ni vacío.");
        }

        LocalDate inicioPeriodo = LocalDate.of(Integer.parseInt(year), 1, 1);
        LocalDate finPeriodo = LocalDate.of(Integer.parseInt(year), 12, 31);

        return generarBalanceGeneral(inicioPeriodo, finPeriodo);
    }
}