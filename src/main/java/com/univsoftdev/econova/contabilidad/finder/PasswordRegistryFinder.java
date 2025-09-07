package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.contabilidad.model.PasswordHistory;
import com.univsoftdev.econova.config.model.User;
import io.ebean.Finder;

public class PasswordRegistryFinder extends Finder<Long, PasswordHistory> {

    public PasswordRegistryFinder() {
        super(PasswordHistory.class);
    }

    public PasswordHistory findByUsuario(User usuario) {
        return db().find(PasswordHistory.class)
                .where()
                .eq("usuario.id", usuario.getId())
                .findOne();
    }
}
