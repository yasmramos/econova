package com.univsoftdev.econova.contabilidad;

import com.univsoftdev.econova.contabilidad.model.*;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

public class BalanceGeneral {

    private List<Cuenta> activos;
    private List<Cuenta> pasivos;
    private List<Cuenta> patrimonio;
    private BigDecimal totalActivos;
    private BigDecimal totalPasivos;
    private BigDecimal totalPatrimonio;
    private List<Cuenta> cuentas = new ArrayList<>();

    public BalanceGeneral(List<Cuenta> activos, List<Cuenta> pasivos, List<Cuenta> patrimonio) {
        this.activos = activos;
        this.pasivos = pasivos;
        this.patrimonio = patrimonio;
    }


    public List<Cuenta> getActivos() {
        return activos;
    }

    public void setActivos(List<Cuenta> activos) {
        this.activos = activos;
    }

    public List<Cuenta> getPasivos() {
        return pasivos;
    }

    public void setPasivos(List<Cuenta> pasivos) {
        this.pasivos = pasivos;
    }

    public List<Cuenta> getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(List<Cuenta> patrimonio) {
        this.patrimonio = patrimonio;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public BigDecimal getTotalActivos() {
        return totalActivos;
    }

    public void setTotalActivos(BigDecimal totalActivos) {
        this.totalActivos = totalActivos;
    }

    public BigDecimal getTotalPasivos() {
        return totalPasivos;
    }

    public void setTotalPasivos(BigDecimal totalPasivos) {
        this.totalPasivos = totalPasivos;
    }

    public BigDecimal getTotalPatrimonio() {
        return totalPatrimonio;
    }

    public void setTotalPatrimonio(BigDecimal totalPatrimonio) {
        this.totalPatrimonio = totalPatrimonio;
    }

}
