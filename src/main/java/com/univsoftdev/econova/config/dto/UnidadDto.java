package com.univsoftdev.econova.config.dto;

import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import java.util.ArrayList;
import java.util.List;

public class UnidadDto {

    private String codigo;
    private String nombre;
    private String direccion;
    private String correo;
    private String nae;
    private String dpa;
    private String reup;
    private List<Transaccion> transacciones = new ArrayList<>();
    private List<Asiento> asientos = new ArrayList<>();

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNae() {
        return nae;
    }

    public String getDpa() {
        return dpa;
    }

    public String getReup() {
        return reup;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public List<Asiento> getAsientos() {
        return asientos;
    }
    
    
}
