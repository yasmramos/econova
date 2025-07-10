package com.univsoftdev.econova.contabilidad;

public class Filter {

    private String property; // Nombre de la propiedad
    private String operator; // Operador (eq, lt, gt, contains, etc.)
    private Object value; // Valor del filtro

    public Filter(String property, String operator, Object value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public String getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }
}
