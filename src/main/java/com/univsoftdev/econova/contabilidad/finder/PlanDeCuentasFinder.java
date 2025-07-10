package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.contabilidad.model.PlanDeCuentas;
import io.ebean.Finder;
import java.util.List;

public class PlanDeCuentasFinder extends Finder<Long, PlanDeCuentas> {

    public PlanDeCuentasFinder() {
        super(PlanDeCuentas.class);
    }

    public PlanDeCuentas findByNombre(String nombre) {
        return db().find(PlanDeCuentas.class)
                .where()
                .eq("nombre", nombre)
                .findOne();
    }

    public List<PlanDeCuentas> findActivos() {
        return db().find(PlanDeCuentas.class)
                .where()
                .eq("activo", true)
                .findList();
    }
}
