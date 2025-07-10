package com.univsoftdev.econova.config.dto;

public class IdiomaDto {

    private final String symbol;
    private final String nombre;
    private final String pais;

    public IdiomaDto(String symbol, String nombre, String pais) {
        this.symbol = symbol;
        this.nombre = nombre;
        this.pais = pais;
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
    
}
