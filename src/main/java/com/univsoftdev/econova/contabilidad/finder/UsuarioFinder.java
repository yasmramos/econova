package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.config.model.User;
import io.ebean.Finder;
import java.util.List;

public class UsuarioFinder extends Finder<Long, User> {

    public UsuarioFinder() {
        super(User.class);
    }

    public User findByIdentificador(String identificador) {
        return db().find(User.class)
                .where()
                .eq("identificador", identificador)
                .findOne();
    }

    public List<User> findActivos() {
        return db().find(User.class)
                .where()
                .eq("activo", true)
                .findList();
    }
}
