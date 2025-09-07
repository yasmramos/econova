package com.univsoftdev.econova.contabilidad;

import com.univsoftdev.econova.contabilidad.model.Transaction;
import java.util.List;

public class EstadoRendimiento {

    private List<Transaction> ingresos;
    private List<Transaction> gastos;
    private double totalIngresos;
    private double totalGastos;
    private double utilidadNeta;

    public EstadoRendimiento(List<Transaction> ingresos, List<Transaction> gastos) {
        this.ingresos = ingresos;
        this.gastos = gastos;
        calcularTotales();
    }

    private void calcularTotales() {

    }

    public List<Transaction> getIngresos() {
        return ingresos;
    }

    public void setIngresos(List<Transaction> ingresos) {
        this.ingresos = ingresos;
    }

    public List<Transaction> getGastos() {
        return gastos;
    }

    public void setGastos(List<Transaction> gastos) {
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
