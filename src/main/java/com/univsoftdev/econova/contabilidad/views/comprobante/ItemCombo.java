package com.univsoftdev.econova.contabilidad.views.comprobante;

import com.univsoftdev.econova.contabilidad.model.Cuenta;

public class ItemCombo {

    private final Cuenta cuenta;

    public ItemCombo(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public String getCodigo() {
        return cuenta.getCodigoSinCuentaPadre();
    }

    public String getNombre() {
        return cuenta.getNombre();
    }

    public Cuenta getCuenta() {
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
