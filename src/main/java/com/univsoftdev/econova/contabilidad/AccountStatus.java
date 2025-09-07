package com.univsoftdev.econova.contabilidad;

public enum EstadoCuenta {
    
    ACTIVA("Activa"),
    INACTIVA("Inactiva");

    private final String descripcion;

    private EstadoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
