package com.univsoftdev.econova.contabilidad;

public enum TypeOfOpening {

    SIN_APERTURA("SIN_APERTURA", 0),
    SUBCUENTA("SUBCUENTA", 4),
    CONTROL("CONTROL", 4),
    SUB_CONTROL("SUB_CONTROL", 4),
    ANALISIS("ANÁLISIS", 4),
    SUBANALISIS("SUB_ANÁLISIS", 4),
    EPIGRAFE("EPIGRAFE", 6);

    private final String description;
    private final int length;

    private TypeOfOpening(String description, int length) {
        this.description = description;
        this.length = length;
    }

    public String getDescription() {
        return description;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return description + " (" + length + ")";
    }

}
