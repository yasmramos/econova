package com.univsoftdev.econova.contabilidad;

import com.univsoftdev.econova.contabilidad.model.*;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

public class BalanceGeneral {

    private List<Account> activos;
    private List<Account> pasivos;
    private List<Account> patrimonio;
    private BigDecimal totalActivos;
    private BigDecimal totalPasivos;
    private BigDecimal totalPatrimonio;
    private List<Account> cuentas = new ArrayList<>();

    public BalanceGeneral(List<Account> activos, List<Account> pasivos, List<Account> patrimonio) {
        this.activos = activos;
        this.pasivos = pasivos;
        this.patrimonio = patrimonio;
    }


    public List<Account> getActivos() {
        return activos;
    }

    public void setActivos(List<Account> activos) {
        this.activos = activos;
    }

    public List<Account> getPasivos() {
        return pasivos;
    }

    public void setPasivos(List<Account> pasivos) {
        this.pasivos = pasivos;
    }

    public List<Account> getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(List<Account> patrimonio) {
        this.patrimonio = patrimonio;
    }

    public List<Account> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Account> cuentas) {
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
