package com.univsoftdev.econova.config.model;

import jakarta.validation.constraints.NotNull;

public enum RolPredefinido {

    ADMIN_SISTEMA("ADMIN_SISTEMA"),
    ADMIN_ECONOMICO("ADMIN_ECONOMICO"),
    CONTADOR("CONTADOR"),
    USUARIO_BASICO("USUARIO_BASICO");

    private final String nombre;

    RolPredefinido(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static Role toEntity(@NotNull RolPredefinido rolEnum) {
        Role rol = new Role(rolEnum.nombre);
        return rol;
    }
}
