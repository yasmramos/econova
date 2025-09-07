package com.univsoftdev.econova.config.dto;

import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EjercicioDto {

    private String nombre;
    private int year;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean iniciado;
    private List<Period> periodos = new ArrayList<>();
    private List<Transaction> transacciones = new ArrayList<>();

    public Exercise toEntity(){
        return new Exercise(nombre, year, fechaInicio, fechaFin, periodos, transacciones);
    }
    
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

    public List<Transaction> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<Transaction> transacciones) {
        this.transacciones = transacciones;
    }

    public List<Period> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<Period> periodos) {
        this.periodos = periodos;
    }
    
}
