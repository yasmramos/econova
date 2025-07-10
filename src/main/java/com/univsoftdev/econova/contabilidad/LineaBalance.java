package com.univsoftdev.econova.contabilidad;

import java.math.BigDecimal;

public class LineaBalance {

    /**
     * Código único de la cuenta asociada a esta línea del balance.
     */
    private String codigo;

    private String descripcion;
    private BigDecimal debitoPeriodo;
    private BigDecimal creditoPeriodo;
    private BigDecimal saldoPeriodo;
    private BigDecimal debitoAcumulado;
    private BigDecimal creditoAcumulado;
    private BigDecimal saldo;

    public LineaBalance() {
    }

    public LineaBalance(String codigo, String descripcion, BigDecimal debitoPeriodo, BigDecimal creditoPeriodo, BigDecimal debitoAcumulado, BigDecimal creditoAcumulado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.debitoPeriodo = debitoPeriodo;
        this.creditoPeriodo = creditoPeriodo;
        this.debitoAcumulado = debitoAcumulado;
        this.creditoAcumulado = creditoAcumulado;
    }

    public LineaBalance(String codigo, String descripcion, BigDecimal debitoPeriodo, BigDecimal creditoPeriodo, BigDecimal saldoPeriodo, BigDecimal debitoAcumulado, BigDecimal creditoAcumulado, BigDecimal saldo) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.debitoPeriodo = debitoPeriodo;
        this.creditoPeriodo = creditoPeriodo;
        this.saldoPeriodo = saldoPeriodo;
        this.debitoAcumulado = debitoAcumulado;
        this.creditoAcumulado = creditoAcumulado;
        this.saldo = saldo;
    }

    public BigDecimal calcularSaldo() {
        return debitoAcumulado.subtract(creditoAcumulado);
    }

    public BigDecimal getSaldoPeriodo() {
        return debitoPeriodo.subtract(creditoPeriodo);
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getDebitoPeriodo() {
        return debitoPeriodo;
    }

    public void setDebitoPeriodo(BigDecimal debitoPeriodo) {
        if (debitoPeriodo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El débito del período no puede ser negativo.");
        }
        this.debitoPeriodo = debitoPeriodo;
    }

    public BigDecimal getCreditoPeriodo() {
        return creditoPeriodo;
    }

    public void setCreditoPeriodo(BigDecimal creditoPeriodo) {
        this.creditoPeriodo = creditoPeriodo;
    }

    public void setSaldoPeriodo(BigDecimal saldoPeriodo) {
        this.saldoPeriodo = saldoPeriodo;
    }

    public BigDecimal getDebitoAcumulado() {
        return debitoAcumulado;
    }

    public void setDebitoAcumulado(BigDecimal debitoAcumulado) {
        this.debitoAcumulado = debitoAcumulado;
    }

    public BigDecimal getCreditoAcumulado() {
        return creditoAcumulado;
    }

    public void setCreditoAcumulado(BigDecimal creditoAcumulado) {
        this.creditoAcumulado = creditoAcumulado;
    }

    @Override
    public String toString() {
        return String.format("LineaBalance[codigo=%s, descripcion=%s, debitoPeriodo=%s, creditoPeriodo=%s, saldoPeriodo=%s, debitoAcumulado=%s, creditoAcumulado=%s, saldo=%s]",
                codigo, descripcion, debitoPeriodo, creditoPeriodo, saldoPeriodo, debitoAcumulado, creditoAcumulado, saldo);
    }

}
