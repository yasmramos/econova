package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.config.model.Unit;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.contabilidad.AccountType;
import com.univsoftdev.econova.contabilidad.ContabilidadException;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.NatureOfAccount;
import com.univsoftdev.econova.contabilidad.SubSystem;
import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.contabilidad.TypeOfOpening;
import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.AccountingEntry;
import com.univsoftdev.econova.contabilidad.model.ChartOfAccounts;
import com.univsoftdev.econova.contabilidad.model.Currency;
import com.univsoftdev.econova.contabilidad.model.Ledger;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.security.Permissions;
import io.ebean.Database;
import io.ebean.Model;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

@Slf4j
@Singleton
public class AccountingService {

    private Database database;
    private CuentaService cuentaService;
    private BalanceGeneralService balanceGeneralService;
    private PlanDeCuentasService planDeCuentasService;
    private AsientoService asientoService;
    private TransaccionService transaccionService;

    public AccountingService() {

    }

    @Inject
    public AccountingService(
            Database database,
            CuentaService cuentaService,
            BalanceGeneralService balanceGeneralService,
            PlanDeCuentasService planDeCuentasService,
            AsientoService asientoService,
            TransaccionService transaccionService
    ) {
        this.database = database;
        this.cuentaService = cuentaService;
        this.balanceGeneralService = balanceGeneralService;
        this.planDeCuentasService = planDeCuentasService;
        this.asientoService = asientoService;
        this.transaccionService = transaccionService;
    }

    @Transactional
    @RequiresPermissions(value = {Permissions.SUPER_ADMIN})
    @RequiresRoles(value = {})
    public AccountingEntry crearAsientoContable(
            int numeroAsiento,
            String descripcion,
            LocalDate fecha,
            SubSystem subSistema,
            List<Transaction> transacciones, User usuario, Unit unidad, Period periodo, Exercise ejercicio) throws ContabilidadException {

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

        // Crear el asiento nro, descripcion, fecha, periodo, unidad, estadoAsiento
        AccountingEntry asiento = asientoService.crearAsiento(numeroAsiento, descripcion, fecha, periodo, unidad, EstadoAsiento.EDICION);
        asiento.setSubSystem(subSistema);

        // Agregar y validar transacciones
        for (Transaction transaccion : transacciones) {
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

    private void validarTransaccion(Transaction transaccion) throws ContabilidadException {
        if (transaccion.getAccount() == null) {
            throw new ContabilidadException("La cuenta de la transacción no puede ser nula");
        }
        if (transaccion.getBalance() == null || transaccion.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ContabilidadException("El monto debe ser positivo");
        }
        if (transaccion.getTipo() == null) {
            throw new ContabilidadException("El tipo de transacción no puede ser nulo");
        }
    }

    private Transaction convertirATransaccion(Transaction transaccionContable, AccountingEntry asiento) {
        Transaction transaccion = new Transaction();
        transaccion.setTipo(transaccionContable.getTipo());
        transaccion.setBalance(transaccionContable.getBalance());
        transaccion.setAccount(transaccionContable.getAccount());
        transaccion.setDescription(transaccionContable.getDescription());
        transaccion.setAsiento(asiento);
        return transaccion;
    }

    private void aplicarTransaccionesACuentas(AccountingEntry asiento) throws ContabilidadException {
        for (Transaction transaccion : asiento.getTransactions()) {
            Account cuenta = transaccion.getAccount();
            if (transaccion.getTipo() == TipoTransaccion.DEBITO) {
                cuentaService.aplicarDebito(cuenta, transaccion.getBalance());
            } else {
                cuentaService.aplicarCredito(cuenta, transaccion.getBalance());
            }
        }
    }

    public ChartOfAccounts createChartOfAccounts(Long id, String nombre) {
        return planDeCuentasService.createChartOfAccounts(id, nombre);
    }

    @Transactional
    public void registrarAsiento(AccountingEntry asiento) {
        if (asiento == null || asiento.getTransactions() == null || asiento.getTransactions().isEmpty()) {
            throw new IllegalArgumentException("El asiento no puede ser nulo ni tener transacciones vacías.");
        }

        // Persistir el asiento contable
        database.save(asiento);

        // Recorrer cada transacción del asiento y actualizar el saldo de la cuenta
        asiento.getTransactions().forEach(transaccion -> {
            if (transaccion.getTipo() == TipoTransaccion.DEBITO) {
                cuentaService.aplicarDebito(transaccion.getAccount(), transaccion.getBalance());
            } else if (transaccion.getTipo() == TipoTransaccion.CREDITO) {
                cuentaService.aplicarCredito(transaccion.getAccount(), transaccion.getBalance());
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
    public void cierreEjercicioFiscalMensual(Period periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo no puede ser nulo.");
        }

        // Validar que todas las cuentas estén cuadradas antes de proceder con el cierre
        validarCuentasCuadradas();

        // Lógica para generar el balance general del mes o actualizar estados
        balanceGeneralService.generarBalanceGeneralMensual(periodo);
    }

    @Transactional
    public void addCuenta(Account cuenta) throws ContabilidadException {
        Objects.requireNonNull(cuenta, "La cuenta no puede ser nula");

        // Validación de código único
        cuentaService.findByCodigo(cuenta.getCode())
                .ifPresent(existing -> {
                    try {
                        throw new ContabilidadException(
                                String.format(
                                        "Ya existe una cuenta con el código %s",
                                        cuenta.getCode())
                        );
                    } catch (ContabilidadException ex) {
                        log.error(ex.getMessage());
                    }
                });
        if (cuenta.getBalance() == null
                || cuenta.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new ContabilidadException("El saldo de la cuenta no puede ser negativo");
        }

        cuentaService.addCuenta(cuenta);
    }

    public void addCuentas(Account... cuentas) throws ContabilidadException {
        cuentaService.addCuentas(cuentas);
    }

    private void validarCuentasCuadradas() {
        var cuentas = cuentaService.findAll();
        for (Account cuenta : cuentas) {
            BigDecimal saldoJerarquico = cuentaService.getSaldoTotalJerarquico(cuenta.getId());
            if (cuenta.getBalance().compareTo(saldoJerarquico) != 0) {
                log.error("La cuenta {} no está cuadrada. Saldo actual: {}, Saldo esperado: {}",
                        cuenta.getCode(), cuenta.getBalance(), saldoJerarquico);
                throw new RuntimeException("La cuenta " + cuenta.getCode() + " no está cuadrada.");
            }
        }
    }

    private void cerrarCuentasTemporales(String year) {
        // Obtener todas las cuentas de ingresos y gastos
        List<Account> cuentasIngresos = cuentaService.findByTipoCuenta(AccountType.INGRESO);
        List<Account> cuentasGastos = cuentaService.findByTipoCuenta(AccountType.GASTO);

        // Crear un asiento para cerrar las cuentas de ingresos
        AccountingEntry asientoIngresos = new AccountingEntry();
        asientoIngresos.setDescription("Cierre de cuentas de ingresos para el año " + year);
        asientoIngresos.setFecha(LocalDate.parse(year + "-12-31"));
        for (Account cuenta : cuentasIngresos) {
            asientoIngresos.addTransaccion(new Transaction(TipoTransaccion.CREDITO, cuenta.getBalance(), cuenta));
        }
        registrarAsiento(asientoIngresos);

        // Crear un asiento para cerrar las cuentas de gastos
        AccountingEntry asientoGastos = new AccountingEntry();
        asientoGastos.setDescription("Cierre de cuentas de gastos para el año " + year);
        asientoGastos.setFecha(LocalDate.parse(year + "-12-31"));
        for (Account cuenta : cuentasGastos) {
            asientoGastos.addTransaccion(new Transaction(TipoTransaccion.DEBITO, cuenta.getBalance(), cuenta));
        }
        registrarAsiento(asientoGastos);
    }

    private void transferirSaldosAPatrimonio(String year) {
        // Obtener la cuenta de patrimonio
        Account cuentaPatrimonio = cuentaService.findByCodigo("1000").get(); // Ejemplo de código de cuenta de patrimonio

        // Calcular el saldo neto de ingresos y gastos
        BigDecimal saldoNeto = calcularSaldoNeto(year);

        // Crear un asiento para transferir el saldo neto a la cuenta de patrimonio
        AccountingEntry asientoTransferencia = new AccountingEntry();
        asientoTransferencia.setDescription("Transferencia de saldo neto a patrimonio para el año " + year);
        asientoTransferencia.setFecha(LocalDate.parse(year + "-12-31"));
        if (saldoNeto.compareTo(BigDecimal.ZERO) > 0) {
            asientoTransferencia.addTransaccion(new Transaction(TipoTransaccion.DEBITO, saldoNeto, cuentaPatrimonio));
        } else {
            asientoTransferencia.addTransaccion(new Transaction(TipoTransaccion.CREDITO, saldoNeto.negate(), cuentaPatrimonio));
        }
        registrarAsiento(asientoTransferencia);
    }

    private BigDecimal calcularSaldoNeto(String year) {
        // Obtener todas las cuentas de ingresos y gastos
        List<Account> cuentasIngresos = cuentaService.findByTipoCuenta(AccountType.INGRESO);
        List<Account> cuentasGastos = cuentaService.findByTipoCuenta(AccountType.GASTO);

        // Calcular el saldo total de ingresos
        BigDecimal totalIngresos = cuentasIngresos.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular el saldo total de gastos
        BigDecimal totalGastos = cuentasGastos.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular el saldo neto
        return totalIngresos.subtract(totalGastos);
    }

    public int obtenerSiguienteCodigoDeAsiento(Period periodo) {
        return asientoService.obtenerSiguienteCodigo(periodo);
    }

    public List<Account> findAllCuentas() {
        return cuentaService.findAll();
    }

    public boolean validateAsiento(AccountingEntry asiento) {
        return asientoService.validateAsiento(asiento);
    }

    public void save(Model model) {
        database.save(model);
    }

    public Optional<Account> findCuentaByCodigo(String codigoCompleto) {
        return cuentaService.findByCodigo(codigoCompleto);
    }

    public Account createAccount(String code, String name, NatureOfAccount natureOfAccount, AccountType accountType, Currency currency, ChartOfAccounts chartOfAccounts) {
        Account account = new Account();
        account.setCode(code);
        account.setName(name);
        account.setNatureOfAccount(natureOfAccount);
        account.setAccountType(accountType);
        account.setTypeOfOpening(TypeOfOpening.SIN_APERTURA);
        account.setCurrency(currency);
        account.setChartOfAccounts(chartOfAccounts);
        account.setActive(true);
        account.setOpening(false);
        account.setLedger(new Ledger(account));
        database.save(account);
        return account;
    }

    public void addSubAccount(Account account, Account subAccount) {
       account.addSubCuenta(subAccount);
       database.update(account);
    }
}
