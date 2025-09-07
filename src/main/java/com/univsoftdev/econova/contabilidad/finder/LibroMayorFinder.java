package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.Ledger;
import io.ebean.Finder;
import java.util.List;

public class LibroMayorFinder extends Finder<Long, Ledger> {

    public LibroMayorFinder() {
        super(Ledger.class);
    }

    public List<Ledger> findByCuenta(Account cuenta) {
        return db().find(Ledger.class)
                .where()
                .eq("cuenta.id", cuenta.getId())
                .findList();
    }

    public Ledger findByCuentaId(Long cuentaId) {
        return db().find(Ledger.class)
                .where()
                .eq("cuenta.id", cuentaId)
                .findOne();
    }
}
