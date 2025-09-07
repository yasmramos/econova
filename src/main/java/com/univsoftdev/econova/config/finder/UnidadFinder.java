package com.univsoftdev.econova.config.finder;

import com.univsoftdev.econova.config.model.Unit;
import io.ebean.Finder;
import java.util.List;
import java.util.Optional;

public class UnidadFinder extends Finder<Long, Unit> {

    public UnidadFinder() {
        super(Unit.class);
    }

    public Optional<Unit> byCodigo(String codigo) {
        return query().where().eq("codigo", codigo).findOneOrEmpty();
    }

    public List<Unit> byEmpresa(Long empresaId) {
        return query().where().eq("empresa.id", empresaId).findList();
    }

    public List<Unit> activas() {
        return query().where().eq("deleted", false).findList();
    }
}
