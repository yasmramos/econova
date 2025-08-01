package com.univsoftdev.econova.contabilidad.dto;

import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AsientoDto {

    private int nro;
    private String descripcion;
    private LocalDate fecha;
    private EstadoAsiento estadoAsiento;
    private List<Transaccion> transacciones = new ArrayList<>();
    private Unidad unidad;
    private Periodo periodo;
    private List<Asiento> asientos = new ArrayList<>();

    private boolean confirmado;
    private boolean validado;
    private boolean guardado;
    private boolean terminado;

    public AsientoDto(int nro, String descripcion, LocalDate fecha, EstadoAsiento estadoAsiento, Unidad unidad, Periodo periodo, boolean confirmado, boolean validado, boolean guardado, boolean terminado) {
        this.nro = nro;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estadoAsiento = estadoAsiento;
        this.unidad = unidad;
        this.periodo = periodo;
        this.confirmado = confirmado;
        this.validado = validado;
        this.guardado = guardado;
        this.terminado = terminado;
    }

    public int getNro() {
        return nro;
    }

    public void setNro(int nro) {
        this.nro = nro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoAsiento getEstadoAsiento() {
        return estadoAsiento;
    }

    public void setEstadoAsiento(EstadoAsiento estadoAsiento) {
        this.estadoAsiento = estadoAsiento;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidad unidad) {
        this.unidad = unidad;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public List<Asiento> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<Asiento> asientos) {
        this.asientos = asientos;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public boolean isValidado() {
        return validado;
    }

    public void setValidado(boolean validado) {
        this.validado = validado;
    }

    public boolean isGuardado() {
        return guardado;
    }

    public void setGuardado(boolean guardado) {
        this.guardado = guardado;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }
    
    
}
