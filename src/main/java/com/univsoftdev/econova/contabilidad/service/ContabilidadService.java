package com.univsoftdev.econova.contabilidad.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.TipoTransaccion;
import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.SubSistemas;
import com.univsoftdev.econova.contabilidad.TipoCuenta;
import com.univsoftdev.econova.contabilidad.model.PlanDeCuentas;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import io.ebean.Database;
import io.ebean.Model;
import io.ebean.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class ContabilidadService {

    private Database database;
    private CuentaService cuentaService;
    private BalanceGeneralService balanceGeneralService;
    private PlanDeCuentasService planDeCuentasService;
    private AsientoService asientoService;
    private TransaccionService transaccionService;

    public ContabilidadService() {

    }

    @Inject
    public ContabilidadService(Database database, CuentaService cuentaService, BalanceGeneralService balanceGeneralService, PlanDeCuentasService planDeCuentasService, AsientoService asientoService, TransaccionService transaccionService) {
        this.database = database;
        this.cuentaService = cuentaService;
        this.balanceGeneralService = balanceGeneralService;
        this.planDeCuentasService = planDeCuentasService;
        this.asientoService = asientoService;
        this.transaccionService = transaccionService;
    }

    @Transactional
    public Asiento crearAsientoContable(
            int numeroAsiento,
            String descripcion,
            LocalDate fecha,
            SubSistemas subSistema,
            List<Transaccion> transacciones, User usuario, Unidad unidad, Periodo periodo, Ejercicio ejercicio) throws ContabilidadException {

        // Validaciones básicas
        Objects.requireNonNull(descripcion, "La descripción no puede ser nula");
        Objects.requireNonNull(fecha, "La fecha no puede ser nula");
        Objects.requireNonNull(periodo, "El periodo no puede ser nulo");
        Objects.requireNonNull(subSistema, "El subsistema no puede ser nulo");
        Objects.requireNonNull(transacciones, "Las transacciones no pueden ser nulas");
        Objects.requireNonNull(unidad, "La unidad no puede ser nula");
        Objects.requireNonNull(ejercicio, "El ejercicio no pueden ser nulo");

        // Validar número de asiento positivo
        if (numeroAsiento <= 0) {
            throw new ContabilidadException("El número de asiento debe ser positivo");
        }

        // Validar fecha dentro del período
        if (!periodo.isDateWithinPeriod(fecha)) {
            throw new ContabilidadException("La fecha del asiento no está dentro del período contable");
        }

        // 4. Validar transacciones no vacías
        if (transacciones.isEmpty()) {
            throw new ContabilidadException("El asiento debe contener al menos una transacción");
        }

        // Crear el asiento
        Asiento asiento = new Asiento(numeroAsiento, descripcion, fecha, periodo);
        asiento.setSubSistema(subSistema);

        // Agregar y validar transacciones
        for (Transaccion transaccion : transacciones) {
            validarTransaccion(transaccion);
            asiento.addTransaccion(convertirATransaccion(transaccion, asiento));
        }

        // Validar cuadre del asiento
        if (!asiento.estaCuadrado()) {
            throw new ContabilidadException("El asiento no está cuadrado. Débitos: "
                    + asiento.getTotalDebitos() + " vs Créditos: " + asiento.getTotalCreditos());
        }

        //Persistir el asiento
        database.save(asiento);

        // Aplicar transacciones a las cuentas (si está confirmado)
        if (asiento.getEstadoAsiento() == EstadoAsiento.CONFIRMADO) {
            aplicarTransaccionesACuentas(asiento);
        }

        return asiento;
    }

    private void validarTransaccion(Transaccion transaccion) throws ContabilidadException {
        if (transaccion.getCuenta() == null) {
            throw new ContabilidadException("La cuenta de la transacción no puede ser nula");
        }
        if (transaccion.getMonto() == null || transaccion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ContabilidadException("El monto debe ser positivo");
        }
        if (transaccion.getTipo() == null) {
            throw new ContabilidadException("El tipo de transacción no puede ser nulo");
        }
    }

    private Transaccion convertirATransaccion(Transaccion transaccionContable, Asiento asiento) {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(transaccionContable.getTipo());
        transaccion.setMonto(transaccionContable.getMonto());
        transaccion.setCuenta(transaccionContable.getCuenta());
        transaccion.setDescripcion(transaccionContable.getDescripcion());
        transaccion.setAsiento(asiento);
        return transaccion;
    }

    private void aplicarTransaccionesACuentas(Asiento asiento) throws ContabilidadException {
        for (Transaccion transaccion : asiento.getTransacciones()) {
            Cuenta cuenta = transaccion.getCuenta();
            if (transaccion.getTipo() == TipoTransaccion.DEBITO) {
                cuentaService.aplicarDebito(cuenta, transaccion.getMonto());
            } else {
                cuentaService.aplicarCredito(cuenta, transaccion.getMonto());
            }
        }
    }

    public void crearPlanDeCuentas() {
        PlanDeCuentas planDeCuentas = planDeCuentasService.getPlanDeCuentas();
        if (planDeCuentas == null) {
            planDeCuentas = new PlanDeCuentas("Cuentas");
            planDeCuentas.setId(1L);
        }
        planDeCuentasService.createPlanDeCuentas(planDeCuentas);
    }

    @Transactional
    public void registrarAsiento(Asiento asiento) {
        if (asiento == null || asiento.getTransacciones() == null || asiento.getTransacciones().isEmpty()) {
            throw new IllegalArgumentException("El asiento no puede ser nulo ni tener transacciones vacías.");
        }

        // Persistir el asiento contable
        database.save(asiento);

        // Recorrer cada transacción del asiento y actualizar el saldo de la cuenta
        asiento.getTransacciones().forEach(transaccion -> {
            if (transaccion.getTipo() == TipoTransaccion.DEBITO) {
                cuentaService.aplicarDebito(transaccion.getCuenta(), transaccion.getMonto());
            } else if (transaccion.getTipo() == TipoTransaccion.CREDITO) {
                cuentaService.aplicarCredito(transaccion.getCuenta(), transaccion.getMonto());
            }
        });
    }

    @Transactional
    public void cierreEjercicioFiscalAnual(String year) {
        if (year == null || year.trim().isEmpty()) {
            throw new IllegalArgumentException("El año no puede ser nulo ni vacío.");
        }

        // Validar que todas las cuentas estén cuadradas antes de proceder con el cierre
        validarCuentasCuadradas();

        // Formatear el año para uso en fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        LocalDate startDate = LocalDate.parse(year + "-01-01", formatter);
        LocalDate endDate = LocalDate.parse(year + "-12-31", formatter);

        // Generar el balance general del año
        balanceGeneralService.generarBalanceGeneralAnual(year);

        // Cerrar cuentas de ingresos y gastos
        cerrarCuentasTemporales(year);

        // Transferir saldos de cuentas de ingresos y gastos a la cuenta de patrimonio
        transferirSaldosAPatrimonio(year);

        log.info("Cierre del ejercicio fiscal anual {} completado exitosamente.", year);
    }

    @Transactional
    public void cierreEjercicioFiscalMensual(Periodo periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo no puede ser nulo.");
        }

        // Validar que todas las cuentas estén cuadradas antes de proceder con el cierre
        validarCuentasCuadradas();

        // Lógica para generar el balance general del mes o actualizar estados
        balanceGeneralService.generarBalanceGeneralMensual(periodo);
    }

    @Transactional
    public void addCuenta(Cuenta cuenta) throws ContabilidadException {
        Objects.requireNonNull(cuenta, "La cuenta no puede ser nula");

        // Validación de código único
        cuentaService.findByCodigo(cuenta.getCodigo())
                .ifPresent(existing -> {
                    try {
                        throw new ContabilidadException(
                                String.format("Ya existe una cuenta con el código %s", cuenta.getCodigo())
                        );
                    } catch (ContabilidadException ex) {
                        log.error(ex.getMessage());
                    }
                });
        if (cuenta.getSaldo() == null || cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            throw new ContabilidadException("El saldo de la cuenta no puede ser negativo");
        }

        cuentaService.save(cuenta);
    }

    private void validarCuentasCuadradas() {
        var cuentas = cuentaService.findAll();
        for (Cuenta cuenta : cuentas) {
            BigDecimal saldoJerarquico = cuentaService.getSaldoTotalJerarquico(cuenta.getId());
            if (cuenta.getSaldo().compareTo(saldoJerarquico) != 0) {
                log.error("La cuenta {} no está cuadrada. Saldo actual: {}, Saldo esperado: {}",
                        cuenta.getCodigo(), cuenta.getSaldo(), saldoJerarquico);
                throw new RuntimeException("La cuenta " + cuenta.getCodigo() + " no está cuadrada.");
            }
        }
    }

    private void cerrarCuentasTemporales(String year) {
        // Obtener todas las cuentas de ingresos y gastos
        List<Cuenta> cuentasIngresos = cuentaService.findByTipoCuenta(TipoCuenta.INGRESO);
        List<Cuenta> cuentasGastos = cuentaService.findByTipoCuenta(TipoCuenta.GASTO);

        // Crear un asiento para cerrar las cuentas de ingresos
        Asiento asientoIngresos = new Asiento();
        asientoIngresos.setDescripcion("Cierre de cuentas de ingresos para el año " + year);
        asientoIngresos.setFecha(LocalDate.parse(year + "-12-31"));
        for (Cuenta cuenta : cuentasIngresos) {
            asientoIngresos.addTransaccion(new Transaccion(TipoTransaccion.CREDITO, cuenta.getSaldo(), cuenta));
        }
        registrarAsiento(asientoIngresos);

        // Crear un asiento para cerrar las cuentas de gastos
        Asiento asientoGastos = new Asiento();
        asientoGastos.setDescripcion("Cierre de cuentas de gastos para el año " + year);
        asientoGastos.setFecha(LocalDate.parse(year + "-12-31"));
        for (Cuenta cuenta : cuentasGastos) {
            asientoGastos.addTransaccion(new Transaccion(TipoTransaccion.DEBITO, cuenta.getSaldo(), cuenta));
        }
        registrarAsiento(asientoGastos);
    }

    private void transferirSaldosAPatrimonio(String year) {
        // Obtener la cuenta de patrimonio
        Cuenta cuentaPatrimonio = cuentaService.findByCodigo("1000").get(); // Ejemplo de código de cuenta de patrimonio

        // Calcular el saldo neto de ingresos y gastos
        BigDecimal saldoNeto = calcularSaldoNeto(year);

        // Crear un asiento para transferir el saldo neto a la cuenta de patrimonio
        Asiento asientoTransferencia = new Asiento();
        asientoTransferencia.setDescripcion("Transferencia de saldo neto a patrimonio para el año " + year);
        asientoTransferencia.setFecha(LocalDate.parse(year + "-12-31"));
        if (saldoNeto.compareTo(BigDecimal.ZERO) > 0) {
            asientoTransferencia.addTransaccion(new Transaccion(TipoTransaccion.DEBITO, saldoNeto, cuentaPatrimonio));
        } else {
            asientoTransferencia.addTransaccion(new Transaccion(TipoTransaccion.CREDITO, saldoNeto.negate(), cuentaPatrimonio));
        }
        registrarAsiento(asientoTransferencia);
    }

    private BigDecimal calcularSaldoNeto(String year) {
        // Obtener todas las cuentas de ingresos y gastos
        List<Cuenta> cuentasIngresos = cuentaService.findByTipoCuenta(TipoCuenta.INGRESO);
        List<Cuenta> cuentasGastos = cuentaService.findByTipoCuenta(TipoCuenta.GASTO);

        // Calcular el saldo total de ingresos
        BigDecimal totalIngresos = cuentasIngresos.stream()
                .map(Cuenta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular el saldo total de gastos
        BigDecimal totalGastos = cuentasGastos.stream()
                .map(Cuenta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular el saldo neto
        return totalIngresos.subtract(totalGastos);
    }

    public int obtenerSiguienteCodigoDeAsiento(Periodo periodo) {
        return asientoService.obtenerSiguienteCodigo(periodo);
    }

    public List<Cuenta> findAllCuentas() {
        return cuentaService.findAll();
    }

    public boolean validateAsiento(Asiento asiento) {
        return asientoService.validateAsiento(asiento);
    }

    public void save(Model model) {
        database.save(model);
    }

    public Optional<Cuenta> findCuentaByCodigo(String codigoCompleto) {
        return cuentaService.findByCodigo(codigoCompleto);
    }
}
