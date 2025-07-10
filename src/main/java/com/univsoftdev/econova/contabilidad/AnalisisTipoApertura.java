package com.univsoftdev.econova.contabilidad;

public enum AnalisisTipoApertura {

    NINGUNO("Ninguno", 0),
    ENTIDADES("Entidades", 11),
    TRABAJADORES("Trabajadores", 6);

    private final String descripccion;
    private final int size;

    private AnalisisTipoApertura(String descripccion, int size) {
        this.descripccion = descripccion;
        this.size = size;
    }

    public String getDescripccion() {
        return descripccion;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return descripccion + " (" + size + ")";
    }

}
