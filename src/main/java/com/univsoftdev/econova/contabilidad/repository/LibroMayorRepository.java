package com.univsoftdev.econova.contabilidad.repository;

import com.univsoftdev.econova.contabilidad.model.Ledger;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class LibroMayorRepository extends BaseRepository<Ledger> {

    public LibroMayorRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<Ledger> getEntityType() {
        return Ledger.class;
    }

}
