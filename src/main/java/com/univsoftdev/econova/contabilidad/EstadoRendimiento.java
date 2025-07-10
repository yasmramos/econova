package com.univsoftdev.econova.contabilidad;

import com.univsoftdev.econova.contabilidad.model.Transaccion;
import java.util.List;

public class EstadoRendimiento {

    private List<Transaccion> ingresos;
    private List<Transaccion> gastos;
    private double totalIngresos;
    private double totalGastos;
    private double utilidadNeta;

    public EstadoRendimiento(List<Transaccion> ingresos, List<Transaccion> gastos) {
        this.ingresos = ingresos;
        this.gastos = gastos;
        calcularTotales();
    }

    private void calcularTotales() {

    }

    public List<Transaccion> getIngresos() {
        return ingresos;
    }

    public void setIngresos(List<Transaccion> ingresos) {
        this.ingresos = ingresos;
    }

    public List<Transaccion> getGastos() {
        return gastos;
    }

    public void setGastos(List<Transaccion> gastos) {
        this.gastos = gastos;
    }

    public double getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public double getTotalGastos() {
        return totalGastos;
    }

    public void setTotalGastos(double totalGastos) {
        this.totalGastos = totalGastos;
    }

    public double getUtilidadNeta() {
        return utilidadNeta;
    }

    public void setUtilidadNeta(double utilidadNeta) {
        this.utilidadNeta = utilidadNeta;
    }

}
