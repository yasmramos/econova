package com.univsoftdev.econova.config.dto;

import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeriodoDto {

    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Ejercicio ejercicio;
    private boolean current;
    private List<Transaccion> transacciones = new ArrayList<>();

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public boolean isCurrent() {
        return current;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }
    
    
}
