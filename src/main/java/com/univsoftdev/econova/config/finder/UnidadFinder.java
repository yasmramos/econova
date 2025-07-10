package com.univsoftdev.econova.config.finder;

import com.univsoftdev.econova.config.model.Unidad;
import io.ebean.Finder;
import java.util.List;

public class UnidadFinder extends Finder<Long, Unidad> {

    public UnidadFinder() {
        super(Unidad.class);
    }

    public Unidad findByCodigo(String codigo) {
        return db().find(Unidad.class)
                .where()
                .eq("codigo", codigo)
                .findOne();
    }

    public List<Unidad> findActivas() {
        return db().find(Unidad.class)
                .where()
                .eq("activo", true)
                .findList();
    }
}
