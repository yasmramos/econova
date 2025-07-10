package com.univsoftdev.econova;

public enum AperturaCuenta {

    CUENTA("Cuenta"),
    SUBCUENTA("SubCuenta"),
    CONTROL("Control"),
    SUBCONTROL("SubControl"),
    ANALISIS("Análisis");

    private final String descripcion;

    private AperturaCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
