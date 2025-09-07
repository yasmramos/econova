package com.univsoftdev.econova.config.finder;

import com.univsoftdev.econova.config.model.Period;
import io.ebean.Finder;
import java.util.List;

public class PeriodoFinder extends Finder<Long, Period> {

    public PeriodoFinder() {
        super(Period.class);
    }
    
    public Period findByNombre(String nombre) {
        return db().find(Period.class).where().eq("nombre", nombre).findOne();
    }

    public List<Period> findActuales() {
        return db().find(Period.class).where().eq("current", true).findList();
    }
}
