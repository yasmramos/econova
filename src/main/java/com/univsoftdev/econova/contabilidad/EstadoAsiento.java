package com.univsoftdev.econova.contabilidad;

public enum EstadoAsiento {

    OK("Ok"),
    TERMINADO("Terminado"),
    CONFIRMADO("Confirmado"),
    EDICION("En edición"),
    ERROR("Con Error"), 
    VALIDADO("Validado");

    private final String descripcion;

    private EstadoAsiento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
