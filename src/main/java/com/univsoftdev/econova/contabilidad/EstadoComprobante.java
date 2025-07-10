package com.univsoftdev.econova.contabilidad;

/**
 *
 * @author UnivSoftDev
 */
public enum EstadoComprobante {
    
    CON_ERRORES("Con errores"),
    CON_ADVERTENCIAS("Con advertencias"),
    OK("Ok"),
    TERMINADO("Terminado"),
    ASENTADO("Asentado");
    
    private final String descripcion;

    private EstadoComprobante(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
