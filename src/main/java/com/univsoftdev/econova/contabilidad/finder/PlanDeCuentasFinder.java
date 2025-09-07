package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.contabilidad.model.ChartOfAccounts;
import io.ebean.Finder;
import java.util.List;

public class PlanDeCuentasFinder extends Finder<Long, ChartOfAccounts> {

    public PlanDeCuentasFinder() {
        super(ChartOfAccounts.class);
    }

    public ChartOfAccounts findByNombre(String nombre) {
        return db().find(ChartOfAccounts.class)
                .where()
                .eq("nombre", nombre)
                .findOne();
    }

    public List<ChartOfAccounts> findActivos() {
        return db().find(ChartOfAccounts.class)
                .where()
                .eq("activo", true)
                .findList();
    }
}
