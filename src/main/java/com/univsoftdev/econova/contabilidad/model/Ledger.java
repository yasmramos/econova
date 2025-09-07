package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.contabilidad.TipoTransaccion;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cont_ledgers")
public class Ledger extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Account cuenta;

    @OneToMany(mappedBy = "ledger", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    public Ledger() {
    }

    public Ledger(Account cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula.");
        }
        this.cuenta = cuenta;
    }

    public Account getCuenta() {
        return cuenta;
    }

    public void setCuenta(Account cuenta) {
        this.cuenta = cuenta;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }

    // --- Métodos de negocio ---
    /**
     * Calcula el saldo actual del libro mayor basándose en sus transacciones.
     * Débito incrementa el saldo; Crédito lo reduce.
     *
     * @return
     */
    public BigDecimal calcularSaldo() {
        return transactions.stream()
                .filter(t -> t.getBalance() != null && t.getTipo() != null)
                .map(t -> {
                    return switch (t.getTipo()) {
                        case DEBITO ->
                            t.getBalance();
                        case CREDITO ->
                            t.getBalance().negate();
                        default ->
                            BigDecimal.ZERO;
                    };
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalDebitos() {
        return transactions.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.DEBITO)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCreditos() {
        return transactions.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.CREDITO)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCantidadTransacciones() {
        return transactions.size();
    }

    public boolean tieneTransacciones() {
        return transactions != null && !transactions.isEmpty();
    }

    public boolean estaBalanceado() {
        return BigDecimal.ZERO.compareTo(calcularSaldo()) == 0;
    }

    /**
     * Agrega una transacción al libro mayor y establece la relación inversa.
     *
     * @param transaccion
     */
    public void agregarTransaccion(Transaction transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }

        transaccion.setLedger(this);
        this.transactions.add(transaccion);
    }

    /**
     * Elimina una transacción del libro mayor y rompe la relación inversa.
     *
     * @param transaccion
     */
    public void eliminarTransaccion(Transaction transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }

        transaccion.setLedger(null);
        this.transactions.remove(transaccion);
    }

}
