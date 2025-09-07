package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cont_currency")
public class Currency extends BaseModel {

    private String symbol;
    private String displayName;
    private String country;
    private boolean porDefecto;
    private int fraccion;
    private BigDecimal tasaCambio;
    
    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> cuentas = new ArrayList<>();
    
    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Transaction> transacciones = new ArrayList<>();

    public Currency() {
    }

    public Currency(String code, String name) {
        this.symbol = code;
        this.displayName = name;
    }

    public Currency(String code, String name, BigDecimal tasaCambio) {
        this.symbol = code;
        this.displayName = name;
        this.tasaCambio = tasaCambio;
    }

    public boolean isPorDefecto() {
        return porDefecto;
    }

    public void setPorDefecto(boolean porDefecto) {
        this.porDefecto = porDefecto;
    }

    public int getFraccion() {
        return fraccion;
    }

    public void setFraccion(int fraccion) {
        this.fraccion = fraccion;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BigDecimal getTasaCambio() {
        return tasaCambio;
    }

    public void setTasaCambio(BigDecimal tasaCambio) {
        this.tasaCambio = tasaCambio;
    }

    public List<Account> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Account> cuentas) {
        this.cuentas = cuentas;
    }

    public List<Transaction> getTransacciones() {
        return transacciones;
    }

}
