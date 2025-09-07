package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.contabilidad.AccountType;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "acc_chart_of_accounts")
public class ChartOfAccounts extends BaseModel {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "The chart of accounts name cannot be empty.")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    public ChartOfAccounts() {
    }

    public ChartOfAccounts(String nombre) {
        Objects.requireNonNull(nombre, "The name cannot be null");
        this.name = nombre;
    }

    /**
     * Obtiene todas las subcuentas del plan de accounts.
     *
     * @return Lista de subcuentas.
     */
    public List<Account> obtenerSubCuentas() {
        return this.accounts.stream()
                .flatMap(cuenta -> cuenta.getSubAccounts().stream())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los controles del plan de accounts.
     *
     * @return Lista de controles.
     */
    public List<Account> obtenerControles() {
        return this.obtenerSubCuentas().stream()
                .flatMap(subCuenta -> subCuenta.getSubAccounts().stream())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los subcontroles del plan de accounts.
     *
     * @return Lista de subcontroles.
     */
    public List<Account> obtenerSubControles() {
        return this.obtenerControles().stream()
                .flatMap(control -> control.getSubAccounts().stream())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los análisis del plan de accounts.
     *
     * @return Lista de análisis.
     */
    public List<Account> obtenerAnalisis() {
        return this.obtenerSubControles().stream()
                .flatMap(subControl -> subControl.getSubAccounts().stream())
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * Obtiene todas las accounts del plan de accounts.
     *
     * @return Lista de accounts.
     */
    public List<Account> obtenerCuentas() {
        return this.accounts;
    }

    /**
     * Agrega una cuenta al plan de accounts.
     *
     * @param cuenta La cuenta a agregar.
     */
    public void agregarCuenta(Account cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("The account cannot be null.");
        }
        cuenta.setChartOfAccounts(this);
        this.accounts.add(cuenta);
    }

    /**
     * Elimina una cuenta del plan de accounts.
     *
     * @param cuenta La cuenta a eliminar.
     */
    public void eliminarCuenta(Account cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("The account cannot be null.");
        }
        cuenta.setChartOfAccounts(null);
        this.accounts.remove(cuenta);
    }

    /**
     * Busca una cuenta por su código.
     *
     * @param codigo El código de la cuenta a buscar.
     * @return La cuenta encontrada, o null si no existe.
     */
    public Optional<Account> buscarCuentaPorCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new IllegalArgumentException("The code cannot be null or empty.");
        }
        return this.accounts.stream()
                .filter(cuenta -> cuenta.getCode().equals(codigo))
                .findFirst();
    }

    /**
     * Calcula el saldo total de todas las accounts en el plan.
     *
     * @return El saldo total.
     */
    public BigDecimal calcularSaldoTotal() {
        return this.accounts.stream()
                .map(this::calcularSaldoCuenta).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el saldo total de una cuenta, incluyendo sus subcuentas,
     * controles, etc.
     *
     * @param cuenta La cuenta cuyo saldo se desea calcular.
     * @return El saldo total de la cuenta.
     */
    public BigDecimal calcularSaldoCuenta(Account cuenta) {
        BigDecimal saldo = cuenta.getBalance();

        for (Account hijo : cuenta.getSubAccounts()) {
            saldo = saldo.add(calcularSaldoCuenta(hijo));
        }

        return saldo;
    }

    /**
     * Calcula el saldo total de una subcuenta, incluyendo sus controles y
     * subcontroles.
     *
     * @param subCuenta La subcuenta cuyo saldo se desea calcular.
     * @return El saldo total de la subcuenta.
     */
    private BigDecimal calcularSaldoSubCuenta(Account subCuenta) {
        BigDecimal saldo = BigDecimal.ZERO;

        // Sumar saldos de controles
        for (Account control : subCuenta.getSubAccounts()) {
            saldo.add(calcularSaldoControl(control));
        }

        return saldo;
    }

    /**
     * Calcula el saldo total de un control, incluyendo sus subcontroles y
     * análisis.
     *
     * @param control El control cuyo saldo se desea calcular.
     * @return El saldo total del control.
     */
    private BigDecimal calcularSaldoControl(Account control) {
        BigDecimal saldo = BigDecimal.ZERO;

        // Sumar saldos de subcontroles
        for (Account subControl : control.getSubAccounts()) {
            saldo.add(calcularSaldoSubControl(subControl));
        }

        return saldo;
    }

    /**
     * Calcula el saldo total de un subcontrol, incluyendo sus análisis.
     *
     * @param subControl El subcontrol cuyo saldo se desea calcular.
     * @return El saldo total del subcontrol.
     */
    private BigDecimal calcularSaldoSubControl(Account subControl) {
        BigDecimal saldo = BigDecimal.ZERO;

        // Sumar saldos de análisis
        for (Account anal : subControl.getSubAccounts()) {
            saldo.add(anal.getBalance());
        }

        return saldo;
    }

    public boolean tieneSaldoNegativo() {
        return this.accounts.stream().anyMatch(Account::tieneSaldoNegativo);
    }

    public BigDecimal obtenerSaldoNegativoTotal() {
        return this.accounts.stream()
                .filter(Account::tieneSaldoNegativo)
                .map(Account::obtenerSaldoNegativoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addApertura(Account cuentaRaiz) {
        if (cuentaRaiz == null || cuentaRaiz.getSubAccounts().isEmpty()) {
            throw new IllegalArgumentException("The root account must have subaccounts.");
        }
        cuentaRaiz.setChartOfAccounts(this);
        this.accounts.add(cuentaRaiz);
    }

    public boolean contieneCuenta(Account cuenta) {
        return this.accounts.contains(cuenta);
    }

    public int getCantidadTotalCuentas() {
        return this.accounts.stream()
                .mapToInt(c -> obtenerTodasLasCuentasHijas(c).size())
                .sum();
    }

    public List<Account> getCuentasPorTipo(AccountType tipo) {
        return this.accounts.stream()
                .flatMap(c -> obtenerTodasLasCuentasHijas(c).stream())
                .filter(c -> c.getAccountType() == tipo)
                .toList();
    }

    private List<Account> obtenerTodasLasCuentasHijas(Account account) {
        List<Account> todas = new ArrayList<>(List.of(account));
        for (Account hijo : account.getSubAccounts()) {
            todas.addAll(obtenerTodasLasCuentasHijas(hijo));
        }
        return todas;
    }
}
