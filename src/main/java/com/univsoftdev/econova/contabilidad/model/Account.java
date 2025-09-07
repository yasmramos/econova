package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.contabilidad.AccountStatus;
import com.univsoftdev.econova.contabilidad.AccountType;
import com.univsoftdev.econova.contabilidad.NatureOfAccount;
import com.univsoftdev.econova.contabilidad.OpeningTypeAnalysis;
import com.univsoftdev.econova.contabilidad.TypeOfOpening;
import com.univsoftdev.econova.contabilidad.finder.AccountFinder;
import com.univsoftdev.econova.core.model.BaseModel;
import io.ebean.annotation.Index;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "acc_accounts")
public class Account extends BaseModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public static AccountFinder finder = new AccountFinder();

    @Column(nullable = false, unique = true)
    @Size(min = 3)
    @Index(
            name = "idx_account_code",
            unique = true,
            columnNames = "code"
    )
    private String code; // Código único de la cuenta

    @Column(nullable = false)
    private String name; // Nombre de la cuenta

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOfOpening typeOfOpening; // Tipo de opening (SIN APERTURA, SUBCUENTA, etc.)

    @Enumerated(EnumType.STRING)
    private OpeningTypeAnalysis openingTypeAnalysis; // Tipo de opening (SIN APERTURA, SUBCUENTA, etc.)

    @Enumerated(EnumType.STRING)
    private AccountType accountType; // Tipo de cuenta (INGRESO, GASTO, ACTIVO, PASIVO, PATRIMONIO)

    @ManyToOne
    private Account accountFather; // Relación jerárquica: cuenta padre

    @OneToMany(mappedBy = "accountFather", cascade = CascadeType.ALL)
    private List<Account> subAccounts = new ArrayList<>(); // Lista de subAccounts

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    private BigDecimal balance = BigDecimal.ZERO;

    private boolean opening = false;

    private boolean active = false;

    @ManyToOne
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;

    @ManyToOne
    @JoinColumn(name = "chart_of_accounts_id")
    private ChartOfAccounts chartOfAccounts;

    @Enumerated(EnumType.STRING)
    private NatureOfAccount natureOfAccount;

    public Account() {
    }

    public Account(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Account(String codigo, String nombre, BigDecimal saldo, Currency currency) {
        this.code = codigo;
        this.name = nombre;
        this.currency = currency;
        this.balance = saldo;
    }

    public Account(String code, String nombre, NatureOfAccount natureOfAccount, AccountType accountType, Currency moneda) {
        this.code = code;
        this.name = nombre;
        this.currency = moneda;
        this.accountType = accountType;
        this.natureOfAccount = natureOfAccount;
    }

    public void addSubCuentas(List<Account> newAccounts) {
        for (Account c : newAccounts) {
            c.setAccountFather(this);
            c.setAccountStatus(AccountStatus.ACTIVE);
            subAccounts.add(c);
        }
    }

    public boolean isDaughter() {
        return subAccounts.isEmpty();
    }

    public boolean esActivo() {
        return accountType == AccountType.ACTIVO;
    }

    public boolean esGasto() {
        return accountType == AccountType.GASTO;
    }

    public boolean esIngreso() {
        return accountType == AccountType.INGRESO;
    }

    public boolean esBalanceable() {
        return esActivo() || accountType == AccountType.PASIVO || accountType == AccountType.PATRIMONIO;
    }

    public boolean tieneSaldoNegativo() {
        return this.subAccounts.stream().anyMatch(Account::tieneSaldoNegativo);
    }

    public BigDecimal obtenerSaldoNegativoTotal() {
        return this.subAccounts.stream()
                .filter(Account::tieneSaldoNegativo)
                .map(Account::obtenerSaldoNegativoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addSubCuenta(Account subCuenta) {
        subCuenta.setAccountFather(this);
        subCuenta.setAccountStatus(AccountStatus.ACTIVE);
        subAccounts.add(subCuenta);
    }

    public Ledger getLedger() {
        return ledger;
    }

    public void setLedger(Ledger ledger) {
        this.ledger = ledger;
    }

    public boolean esActivoOPasivo() {
        return accountType == AccountType.ACTIVO || accountType == AccountType.PASIVO;
    }

    public boolean requiereSubcuentas() {
        return typeOfOpening == TypeOfOpening.SUBCUENTA;
    }

    public static AccountFinder getFinder() {
        return finder;
    }

    public OpeningTypeAnalysis getOpeningTypeAnalysis() {
        return openingTypeAnalysis;
    }

    public void setOpeningTypeAnalysis(OpeningTypeAnalysis openingTypeAnalysis) {
        this.openingTypeAnalysis = openingTypeAnalysis;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public boolean isOpening() {
        return opening;
    }

    public void setOpening(boolean opening) {
        this.opening = opening;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public NatureOfAccount getNatureOfAccount() {
        return natureOfAccount;
    }

    public void setNatureOfAccount(NatureOfAccount natureOfAccount) {
        this.natureOfAccount = natureOfAccount;
    }

    public ChartOfAccounts getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(ChartOfAccounts chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCode() {
        return code;
    }

    public String getCodigoSinCuentaPadre() {
        String[] partes = code.split("\\.");
        return partes.length > 0 ? partes[partes.length - 1] : code;
    }

    public void setCode(String code) {
        if (accountFather == null) {
            this.code = code;
        } else {
            this.code = accountFather.getCode() + "." + code;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeOfOpening getTypeOfOpening() {
        return typeOfOpening;
    }

    public void setTypeOfOpening(TypeOfOpening typeOfOpening) {
        this.typeOfOpening = typeOfOpening;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Account getAccountFather() {
        return accountFather;
    }

    public void setAccountFather(Account accountFather) {
        this.accountFather = accountFather;
    }

    public List<Account> getSubAccounts() {
        return subAccounts;
    }

    public void setSubAccounts(List<Account> subAccounts) {
        this.subAccounts = subAccounts;
    }

    @Override
    public String toString() {
        if (accountFather != null) {
            return getCodigoSinCuentaPadre() + " - " + name;
        }
        return code + " - " + name;
    }

}
