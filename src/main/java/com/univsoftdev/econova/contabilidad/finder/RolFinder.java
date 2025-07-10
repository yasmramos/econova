package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.config.model.Rol;
import io.ebean.Finder;
import java.util.List;

public class RolFinder extends Finder<Long, Rol> {

    public RolFinder() {
        super(Rol.class);
    }

    public Rol findByNombre(String nombre) {
        return db().find(Rol.class).where().eq("nombre", nombre).findOne();
    }

    public List<Rol> findConUsuarios() {
        return db().find(Rol.class).where().isNotNull("usuarios").findList();
    }
}
