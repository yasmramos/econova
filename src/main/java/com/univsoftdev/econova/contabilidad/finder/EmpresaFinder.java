package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.config.model.Empresa;
import io.ebean.Finder;
import java.util.List;

public class EmpresaFinder extends Finder<Long, Empresa> {

    public EmpresaFinder() {
        super(Empresa.class);
    }

    public Empresa findByNif(String nif) {
        return db().find(Empresa.class).where().eq("nif", nif).findOne();
    }

    public Empresa findByCif(String cif) {
        return db().find(Empresa.class).where().eq("cif", cif).findOne();
    }

    public List<Empresa> findActivas() {
        return db().find(Empresa.class).where().eq("activo", true).findList();
    }
}
