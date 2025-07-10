package com.univsoftdev.econova.contabilidad;

public enum SubSistemas {

    CONTABILIDAD("Contabilidad"),
    FINANZAS("Finanzas"),
    NOMINAS("Nóminas"),
    AFT("Activos Fijos"),
    INVENTARIOS("Inventarios"),
    FACTURACION("Facturación");

    private final String descripcion;

    private SubSistemas(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
