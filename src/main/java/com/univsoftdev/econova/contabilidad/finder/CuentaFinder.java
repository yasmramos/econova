package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.contabilidad.EstadoCuenta;
import com.univsoftdev.econova.contabilidad.TipoCuenta;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import io.ebean.Finder;
import java.util.List;

public class CuentaFinder extends Finder<Long, Cuenta> {

    public CuentaFinder() {
        super(Cuenta.class);
    }

    public List<Cuenta> findByTipo(TipoCuenta tipo) {
        return db().find(Cuenta.class).where().eq("tipoCuenta", tipo).findList();
    }

    public List<Cuenta> findActivas() {
        return db().find(Cuenta.class).where().eq("estadoCuenta", EstadoCuenta.ACTIVA).findList();
    }
}
