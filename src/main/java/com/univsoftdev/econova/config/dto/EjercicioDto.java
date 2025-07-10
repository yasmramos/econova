package com.univsoftdev.econova.config.dto;

import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EjercicioDto {

    private String nombre;
    private int year;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean iniciado;
    private List<Periodo> periodos = new ArrayList<>();
    private List<Transaccion> transacciones = new ArrayList<>();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isIniciado() {
        return iniciado;
    }

    public void setIniciado(boolean iniciado) {
        this.iniciado = iniciado;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }

    public List<Periodo> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<Periodo> periodos) {
        this.periodos = periodos;
    }
    
}
