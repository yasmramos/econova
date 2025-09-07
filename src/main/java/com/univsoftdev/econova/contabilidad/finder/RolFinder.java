package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.config.model.Role;
import io.ebean.Finder;
import java.util.List;

public class RolFinder extends Finder<Long, Role> {

    public RolFinder() {
        super(Role.class);
    }

    public Role findByNombre(String nombre) {
        return db().find(Role.class).where().eq("nombre", nombre).findOne();
    }

    public List<Role> findConUsuarios() {
        return db().find(Role.class).where().isNotNull("usuarios").findList();
    }
}
