package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.config.finder.UnidadFinder;
import com.univsoftdev.econova.contabilidad.model.AccountingEntry;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_units")
public class Unit extends BaseModel {

    public static final UnidadFinder find = new UnidadFinder();
    private static final long serialVersionUID = 1L;

    @NotNull(message = "El código no puede ser nulo.")
    @Column(unique = true, length = 50)
    private String code;

    @NotNull(message = "El nombre no puede ser nulo.")
    @Column(length = 200)
    private String name;

    @Column(length = 500)
    private String address;

    @Email(message = "El correo debe tener un formato válido.")
    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String nae;

    @Column(length = 20)
    private String dpa;

    @Column(length = 20)
    private String reup;

    // Relación con Transaction (bidireccional)
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    // Relación con AccountingEntry (bidireccional) - CORREGIDO
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AccountingEntry> accountingEntrys = new ArrayList<>();

    // Relación con Company
    @NotNull(message = "La empresa no puede ser nula.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    private boolean active;

    public Unit() {
    }

    public Unit(String codigo, String nombre, Company empresa) {
        this.code = codigo;
        this.name = nombre;
        this.company = empresa;
    }

    public Unit(String codigo, String nombre, String direccion, String correo,
            String nae, String dpa, String reup, Company empresa) {
        this.code = codigo;
        this.name = nombre;
        this.address = direccion;
        this.email = correo;
        this.nae = nae;
        this.dpa = dpa;
        this.reup = reup;
        this.company = empresa;
    }

    // Métodos para manejar la relación bidireccional con Transaction
    public void addTransaccion(@NotNull Transaction transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }
        if (!this.transactions.contains(transaccion)) {
            this.transactions.add(transaccion);
            transaccion.setUnidad(this);
        }
    }

    public void removeTransaccion(@NotNull Transaction transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }
        if (this.transactions.remove(transaccion)) {
            transaccion.setUnidad(null);
        }
    }

    // Métodos para manejar la relación bidireccional con AccountingEntry
    public void addAsiento(@NotNull AccountingEntry asiento) {
        if (asiento == null) {
            throw new IllegalArgumentException("El asiento no puede ser nulo.");
        }
        if (!this.accountingEntrys.contains(asiento)) {
            this.accountingEntrys.add(asiento);
            asiento.setUnidad(this);
        }
    }

    public void removeAsiento(@NotNull AccountingEntry asiento) {
        if (asiento == null) {
            throw new IllegalArgumentException("El asiento no puede ser nulo.");
        }
        if (this.accountingEntrys.remove(asiento)) {
            asiento.setUnidad(null);
        }
    }

    public boolean tieneAsientos() {
        return !this.accountingEntrys.isEmpty();
    }

    public boolean tieneTransacciones() {
        return !this.transactions.isEmpty();
    }

    public int getCantidadDeAsientos() {
        return this.accountingEntrys.size();
    }

    public int getCantidadDeTransacciones() {
        return this.transactions.size();
    }

    public BigDecimal getSaldoTotal() {
        return this.transactions.stream()
                .map(t -> t.esDebito() ? t.getBalance() : t.getBalance().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalDebitos() {
        return this.transactions.stream()
                .filter(Transaction::esDebito)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCreditos() {
        return this.transactions.stream()
                .filter(Transaction::esCredito)
                .map(Transaction::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNae() {
        return nae;
    }

    public void setNae(String nae) {
        this.nae = nae;
    }

    public String getDpa() {
        return dpa;
    }

    public void setDpa(String dpa) {
        this.dpa = dpa;
    }

    public String getReup() {
        return reup;
    }

    public void setReup(String reup) {
        this.reup = reup;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<AccountingEntry> getAccountingEntrys() {
        return accountingEntrys;
    }

    public void setAccountingEntrys(List<AccountingEntry> accountingEntrys) {
        this.accountingEntrys = accountingEntrys;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    // Método toString mejorado
    @Override
    public String toString() {
        return "Unidad{"
                + "id=" + getId()
                + ", codigo='" + code + '\''
                + ", nombre='" + name + '\''
                + ", empresa=" + (company != null ? company.getName() : "null")
                + ", tenantId='" + getTenantId() + '\''
                + '}';
    }

}
