package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import io.ebean.Finder;
import java.util.List;

public class TransaccionFinder extends Finder<Long, Transaccion> {

    public TransaccionFinder() {
        super(Transaccion.class);
    }

    public List<Transaccion> findByCuenta(Cuenta cuenta) {
        return db().find(Transaccion.class)
                .where()
                .eq("cuenta.id", cuenta.getId())
                .findList();
    }

    public List<Transaccion> findByPeriodo(Periodo periodo) {
        return db().find(Transaccion.class)
                .where()
                .eq("periodo.id", periodo.getId())
                .findList();
    }
}
