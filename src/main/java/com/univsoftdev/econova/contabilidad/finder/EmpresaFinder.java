package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.config.model.Company;
import io.ebean.Finder;
import java.util.List;

public class EmpresaFinder extends Finder<Long, Company> {

    public EmpresaFinder() {
        super(Company.class);
    }

    public Company findByNif(String nif) {
        return db().find(Company.class).where().eq("nif", nif).findOne();
    }

    public Company findByCif(String cif) {
        return db().find(Company.class).where().eq("cif", cif).findOne();
    }

    public List<Company> findActivas() {
        return db().find(Company.class).where().eq("activo", true).findList();
    }
}
