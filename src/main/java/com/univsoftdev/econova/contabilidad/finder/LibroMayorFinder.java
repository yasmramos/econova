package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.model.LibroMayor;
import io.ebean.Finder;
import java.util.List;

public class LibroMayorFinder extends Finder<Long, LibroMayor> {

    public LibroMayorFinder() {
        super(LibroMayor.class);
    }

    public List<LibroMayor> findByCuenta(Cuenta cuenta) {
        return db().find(LibroMayor.class)
                .where()
                .eq("cuenta.id", cuenta.getId())
                .findList();
    }

    public LibroMayor findByCuentaId(Long cuentaId) {
        return db().find(LibroMayor.class)
                .where()
                .eq("cuenta.id", cuentaId)
                .findOne();
    }
}
