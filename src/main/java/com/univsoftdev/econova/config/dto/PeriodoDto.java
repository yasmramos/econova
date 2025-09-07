package com.univsoftdev.econova.config.dto;

import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeriodoDto {

    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Exercise ejercicio;
    private boolean current;
    private List<Transaction> transacciones = new ArrayList<>();

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public Exercise getEjercicio() {
        return ejercicio;
    }

    public boolean isCurrent() {
        return current;
    }

    public List<Transaction> getTransacciones() {
        return transacciones;
    }
    
    
}
