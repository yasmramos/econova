package com.univsoftdev.econova.contabilidad;

public enum OpeningTypeAnalysis {

    NINGUNO("Ninguno", 0),
    ENTIDADES("Entidades", 11),
    TRABAJADORES("Trabajadores", 6);

    private final String descripction;
    private final int size;

    private OpeningTypeAnalysis(String descripccion, int size) {
        this.descripction = descripccion;
        this.size = size;
    }

    public String getDescripction() {
        return descripction;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return descripction + " (" + size + ")";
    }

}
