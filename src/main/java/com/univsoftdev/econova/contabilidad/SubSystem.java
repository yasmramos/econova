package com.univsoftdev.econova.contabilidad;

public enum SubSystem {

    CONTABILIDAD("Contabilidad"),
    FINANZAS("Finanzas"),
    NOMINAS("Nóminas"),
    AFT("Activos Fijos"),
    INVENTARIOS("Inventarios"),
    FACTURACION("Facturación");

    private final String description;

    private SubSystem(String descripcion) {
        this.description = descripcion;
    }

    public String getDescription() {
        return description;
    }

}
