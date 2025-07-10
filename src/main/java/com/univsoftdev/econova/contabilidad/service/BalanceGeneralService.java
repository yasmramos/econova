package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.config.service.EjercicioService;
import com.univsoftdev.econova.config.service.PeriodoService;
import com.univsoftdev.econova.contabilidad.LineaBalance;
import com.univsoftdev.econova.contabilidad.NaturalezaCuenta;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.TipoTransaccion;
import com.univsoftdev.econova.config.model.Periodo;

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

        List<Cuenta> cuentas = cuentaService.findAll();
        return cuentas.stream()
                .map(cuenta -> mapearCuentaALineaBalance(cuenta, inicioPeriodo, finPeriodo))
                .collect(Collectors.toList());
    }

    private LineaBalance mapearCuentaALineaBalance(Cuenta cuenta, LocalDate inicio, LocalDate fin) {
        LineaBalance lineaBalance = new LineaBalance();
        lineaBalance.setCodigo(cuenta.getCodigo());
        lineaBalance.setDescripcion(cuenta.getNombre());

        BigDecimal[] saldosPeriodo = calcularSaldosPeriodo(cuenta, inicio, fin);
        asignarSaldosPorNaturaleza(lineaBalance, cuenta.getNaturaleza(), saldosPeriodo[0], saldosPeriodo[1]);

        BigDecimal[] saldosAcumulados = calcularSaldosAcumulados(cuenta);
        lineaBalance.setDebitoAcumulado(saldosAcumulados[0]);
        lineaBalance.setCreditoAcumulado(saldosAcumulados[1]);

        return lineaBalance;
    }

    private BigDecimal[] calcularSaldosPeriodo(Cuenta cuenta, LocalDate inicio, LocalDate fin) {
        List<Transaccion> transaccionesPeriodo = cuenta.getLibroMayor().getTransacciones().stream()
                .filter(t -> t.getFecha().isAfter(inicio.minusDays(1)) && t.getFecha().isBefore(fin.plusDays(1)))
                .collect(Collectors.toList());

        BigDecimal debitos = transaccionesPeriodo.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.DEBITO)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditos = transaccionesPeriodo.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.CREDITO)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BigDecimal[]{debitos, creditos};
    }

    private BigDecimal[] calcularSaldosAcumulados(Cuenta cuenta) {
        BigDecimal debitos = cuenta.getLibroMayor().getTransacciones().stream()
                .filter(t -> t.getTipo() == TipoTransaccion.DEBITO)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditos = cuenta.getLibroMayor().getTransacciones().stream()
                .filter(t -> t.getTipo() == TipoTransaccion.CREDITO)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BigDecimal[]{debitos, creditos};
    }

    private void asignarSaldosPorNaturaleza(LineaBalance lineaBalance,
            NaturalezaCuenta naturaleza,
            BigDecimal debitos,
            BigDecimal creditos) {
        if (naturaleza == NaturalezaCuenta.DEUDORA) {
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

    public List<LineaBalance> generarBalanceGeneralMensual(Periodo periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo no puede ser nulo.");
        }

        LocalDate inicioPeriodo = periodo.getFechaInicio();
        LocalDate finPeriodo = periodo.getFechaFin();

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