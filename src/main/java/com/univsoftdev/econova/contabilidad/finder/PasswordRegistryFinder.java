package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.contabilidad.model.PasswordRegistry;
import com.univsoftdev.econova.config.model.User;
import io.ebean.Finder;

public class PasswordRegistryFinder extends Finder<Long, PasswordRegistry> {

    public PasswordRegistryFinder() {
        super(PasswordRegistry.class);
    }

    public PasswordRegistry findByUsuario(User usuario) {
        return db().find(PasswordRegistry.class)
                .where()
                .eq("usuario.id", usuario.getId())
                .findOne();
    }
}
