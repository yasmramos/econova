package com.univsoftdev.econova.contabilidad.repository;

import com.univsoftdev.econova.contabilidad.model.Audit;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class AuditoriaRepository extends BaseRepository<Audit> {

    @Inject
    public AuditoriaRepository(Database database) {
        super(database);
    }
    
    @Override
    protected Class<Audit> getEntityType() {
        return Audit.class;
    }
    
    @Override
    public List<Audit> findByCriteria(String criteria) {
        return database.find(Audit.class)
                .where()
                .ilike("accion", "%" + criteria + "%")
                .or()
                .ilike("entidad", "%" + criteria + "%")
                .or()
                .ilike("usuario", "%" + criteria + "%")
                .findList();
    }
    
    public List<Audit> findByUsuario(String usuario) {
        return database.find(Audit.class)
                .where()
                .eq("usuario", usuario)
                .orderBy("fecha desc, id desc")
                .findList();
    }
    
    public List<Audit> findByEntidad(String entidad) {
        return database.find(Audit.class)
                .where()
                .eq("entidad", entidad)
                .orderBy("fecha desc, id desc")
                .findList();
    }
    
    public List<Audit> findByAccion(String accion) {
        return database.find(Audit.class)
                .where()
                .eq("accion", accion)
                .orderBy("fecha desc, id desc")
                .findList();
    }
    
    public List<Audit> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return database.find(Audit.class)
                .where()
                .ge("fecha", fechaInicio)
                .le("fecha", fechaFin)
                .orderBy("fecha desc, id desc")
                .findList();
    }
    
    public List<Audit> findByUsuarioYRangoFechas(String usuario, LocalDate fechaInicio, LocalDate fechaFin) {
        return database.find(Audit.class)
                .where()
                .eq("usuario", usuario)
                .ge("fecha", fechaInicio)
                .le("fecha", fechaFin)
                .orderBy("fecha desc, id desc")
                .findList();
    }
    
    public List<Audit> findByEntidadYRangoFechas(String entidad, LocalDate fechaInicio, LocalDate fechaFin) {
        return database.find(Audit.class)
                .where()
                .eq("entidad", entidad)
                .ge("fecha", fechaInicio)
                .le("fecha", fechaFin)
                .orderBy("fecha desc, id desc")
                .findList();
    }
    
    public List<Audit> obtenerAuditoriasOrdenadasPorFecha() {
        return database.find(Audit.class)
                .orderBy("fecha desc, id desc")
                .findList();
    }
    
    public List<Audit> obtenerAuditoriasPorUsuarioYEntidad(String usuario, String entidad) {
        return database.find(Audit.class)
                .where()
                .eq("usuario", usuario)
                .eq("entidad", entidad)
                .orderBy("fecha desc, id desc")
                .findList();
    }
    
    public long contarAuditoriasPorUsuario(String usuario) {
        return database.find(Audit.class)
                .where()
                .eq("usuario", usuario)
                .findCount();
    }
    
    public long contarAuditoriasPorEntidad(String entidad) {
        return database.find(Audit.class)
                .where()
                .eq("entidad", entidad)
                .findCount();
    }
    
    public List<Audit> obtenerUltimasAuditorias(int limite) {
        return database.find(Audit.class)
                .orderBy("fecha desc, id desc")
                .setMaxRows(limite)
                .findList();
    }
    
    public Optional<Audit> encontrarAuditoriaMasReciente() {
        return Optional.ofNullable(database.find(Audit.class)
                .orderBy("fecha desc, id desc")
                .setMaxRows(1)
                .findOne()
        );
    }
}