package com.univsoftdev.econova.contabilidad.views.comprobante;

import com.univsoftdev.econova.contabilidad.model.Account;

public class ItemCombo {

    private final Account cuenta;

    public ItemCombo(Account cuenta) {
        this.cuenta = cuenta;
    }

    public String getCodigo() {
        return cuenta.getCodigoSinCuentaPadre();
    }

    public String getNombre() {
        return cuenta.getName();
    }

    public Account getCuenta() {
        return cuenta;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ItemCombo)) {
            return false;
        }
        return ((ItemCombo) obj).getCuenta().equals(cuenta);
    }

    @Override
    public int hashCode() {
        return cuenta.hashCode();
    }

    @Override
    public String toString() {
        return getCodigo() + " - " + getNombre();
    }
}
