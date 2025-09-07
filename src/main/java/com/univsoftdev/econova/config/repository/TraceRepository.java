package com.univsoftdev.econova.config.repository;

import com.univsoftdev.econova.config.model.Trace;
import com.univsoftdev.econova.contabilidad.SubSystem;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.LocalDateTime;
import java.util.List;

@Singleton
public class TraceRepository extends BaseRepository<Trace>{

    @Inject
    public TraceRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<Trace> getEntityType() {
        return Trace.class;
    }

    public List<Trace> findBySubSystem(SubSystem subSystem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Trace> findByHost(String host) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Trace> findByTimeEndIsNull() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Trace> findByTimeStartBetween(LocalDateTime start, LocalDateTime end) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
