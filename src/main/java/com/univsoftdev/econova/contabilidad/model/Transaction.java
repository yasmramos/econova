package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.config.model.Unit;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.contabilidad.finder.TransaccionFinder;
import com.univsoftdev.econova.core.model.AuditBaseModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cont_transactions")
public class Transaction extends AuditBaseModel {

    private static final long serialVersionUID = 1L;

    transient static TransaccionFinder finder = new TransaccionFinder();

    private String description;

    @NotNull(message = "The type cannot be null.")
    private TipoTransaccion tipo;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    @NotNull(message = "The date cannot be null.")
    @Column(name ="transaction_date")
    private LocalDate date;

    @NotNull(message = "The currency cannot be null.")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_entry_id")
    private AccountingEntry asiento;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;

    public Transaction() {
    }

    public Transaction(@NotNull String descripcion,
            @NotNull TipoTransaccion tipo,
            @NotNull BigDecimal monto,
            @NotNull LocalDate fecha,
            @NotNull Currency currency,
            @NotNull AccountingEntry accountingEntry,
            @NotNull Account account,
            @NotNull Ledger libroMayor) {
        this.description = descripcion;
        this.tipo = tipo;
        this.balance = monto;
        this.date = fecha;
        this.currency = currency;
        this.asiento = accountingEntry;
        this.account = account;
        this.ledger = libroMayor;
    }

    public Transaction(
            @NotNull TipoTransaccion tipo,
            @NotNull BigDecimal monto,
            @NotNull LocalDate fecha,
            @NotNull Currency moneda,
            @NotNull User usuario,
            @NotNull Account cuenta) {
        this.tipo = tipo;
        this.balance = monto;
        this.date = fecha;
        this.currency = moneda;
        this.user = usuario;
        this.account = cuenta;
    }

    public Transaction(@NotNull TipoTransaccion tipoTransaccion,
            @NotNull BigDecimal saldo,
            @NotNull Account cuenta) {
        this.tipo = tipoTransaccion;
        this.balance = saldo;
        this.account = cuenta;
    }

    public void setLedger(@NotNull Ledger ledger) {
        if (this.ledger != null) {
            this.ledger.getTransactions().remove(this);
        }
        this.ledger = ledger;
        ledger.getTransactions().add(this);
    }

    public boolean esMonedaPrincipal(String codigo) {
        return currency.getSymbol().equals(codigo);
    }

    public AccountingEntry getAsiento() {
        return asiento;
    }

    public void setAsiento(AccountingEntry asiento) {
        if (this.asiento != null) {
            this.asiento.getTransactions().remove(this);
        }
        this.asiento = asiento;
        if (asiento != null) {
            asiento.getTransactions().add(this);
        }
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(@NotNull Account account) {
        this.account = account;
    }

    @Override
    public void setUnidad(@NotNull Unit unidad) {
        this.getUnidad().getTransactions().remove(this);
        this.unit = unidad;
        unidad.getTransactions().add(this);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean esDebito() {
        return tipo == TipoTransaccion.DEBITO;
    }

    public boolean esCredito() {
        return tipo == TipoTransaccion.CREDITO;
    }

    public BigDecimal getSaldo() {
        return esDebito() ? balance : balance.negate();
    }

    public String getDescription() {
        return String.format("%s - %s %s",
                tipo,
                balance.stripTrailingZeros().toPlainString(),
                currency.getSymbol()
        );
    }

    public void setBalance(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo.");
        }
        this.balance = balance;
    }

    public Ledger getLedger() {
        return ledger;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
