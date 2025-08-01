package com.univsoftdev.econova.contabilidad;

public enum SubSistema {

    CONTABILIDAD("Contabilidad"),
    FINANZAS("Finanzas"),
    NOMINAS("Nóminas"),
    AFT("Activos Fijos"),
    INVENTARIOS("Inventarios"),
    FACTURACION("Facturación");

    private final String descripcion;

    private SubSistema(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
