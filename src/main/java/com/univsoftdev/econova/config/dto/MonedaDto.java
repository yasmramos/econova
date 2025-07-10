package com.univsoftdev.econova.config.dto;

import java.math.BigDecimal;

public class MonedaDto {

    private final String symbol;
    private final String nombre;
    private final String pais;
    private final int fraccion;
    private final BigDecimal tazaCambio;
    private final boolean porDefecto;

    public MonedaDto(String symbol, String nombre, String pais, int fraccion, BigDecimal tazaCambio, boolean porDefecto) {
        this.symbol = symbol;
        this.nombre = nombre;
        this.pais = pais;
        this.fraccion = fraccion;
        this.tazaCambio = tazaCambio;
        this.porDefecto = porDefecto;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPais() {
        return pais;
    }

    public int getFraccion() {
        return fraccion;
    }

    public BigDecimal getTazaCambio() {
        return tazaCambio;
    }

    public boolean isPorDefecto() {
        return porDefecto;
    }
    
}
