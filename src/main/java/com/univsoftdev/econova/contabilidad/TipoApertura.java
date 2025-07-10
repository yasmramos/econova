package com.univsoftdev.econova.contabilidad;

public enum TipoApertura {

    SIN_APERTURA("SIN_APERTURA", 0),
    SUBCUENTA("SUBCUENTA", 4),
    CONTROL("CONTROL", 4),
    SUB_CONTROL("SUB_CONTROL", 4),
    ANALISIS("ANÁLISIS", 4),
    SUBANALISIS("SUB_ANÁLISIS", 4),
    EPIGRAFE("EPIGRAFE", 6);

    private final String descripcion;
    private final int longitud;

    private TipoApertura(String descripcion, int longitud) {
        this.descripcion = descripcion;
        this.longitud = longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getLongitud() {
        return longitud;
    }

    @Override
    public String toString() {
        return descripcion + " (" + longitud + ")";
    }

}
