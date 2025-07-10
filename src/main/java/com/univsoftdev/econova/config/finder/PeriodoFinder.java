package com.univsoftdev.econova.config.finder;

import com.univsoftdev.econova.config.model.Periodo;
import io.ebean.Finder;
import java.util.List;

public class PeriodoFinder extends Finder<Long, Periodo> {

    public PeriodoFinder() {
        super(Periodo.class);
    }
    
    public Periodo findByNombre(String nombre) {
        return db().find(Periodo.class).where().eq("nombre", nombre).findOne();
    }

    public List<Periodo> findActuales() {
        return db().find(Periodo.class).where().eq("current", true).findList();
    }
}
