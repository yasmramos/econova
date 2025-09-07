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
@Table(name = "cont_monedas")
public class Moneda extends BaseModel {

    private String symbol;
    private String displayName;
    private String pais;
    private boolean porDefecto;
    private int fraccion;
    private BigDecimal tasaCambio;
    
    @OneToMany(mappedBy = "moneda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cuenta> cuentas = new ArrayList<>();
    
    @OneToMany(mappedBy = "moneda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Transaccion> transacciones = new ArrayList<>();

    public Moneda() {
    }

    public Moneda(String codigo, String nombre) {
        this.symbol = codigo;
        this.displayName = nombre;
    }

    public Moneda(String codigo, String nombre, BigDecimal tasaCambio) {
        this.symbol = codigo;
        this.displayName = nombre;
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

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
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

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

}
