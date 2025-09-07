package com.univsoftdev.econova.contabilidad.repository;

import com.univsoftdev.econova.contabilidad.model.AccountingEntry;
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
public class AsientoRepository extends BaseRepository<AccountingEntry> {

    @Inject
    public AsientoRepository(Database database) {
        super(database);
    }
    
    @Override
    protected Class<AccountingEntry> getEntityType() {
        return AccountingEntry.class;
    }
    
    @Override
    public List<AccountingEntry> findByCriteria(String criteria) {
        return database.find(AccountingEntry.class)
                .where()
                .ilike("descripcion", "%" + criteria + "%")
                .or()
                .eq("nro", Integer.valueOf(criteria))
                .findList();
    }
    
    public Optional<AccountingEntry> findByNumero(int numero) {
        return Optional.ofNullable(database.find(AccountingEntry.class)
                .where()
                .eq("nro", numero)
                .findOne()
        );
    }
    
    public List<AccountingEntry> findByFecha(LocalDate fecha) {
        return database.find(AccountingEntry.class)
                .where()
                .eq("fecha", fecha)
                .orderBy("nro asc")
                .findList();
    }
    
    public List<AccountingEntry> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return database.find(AccountingEntry.class)
                .where()
                .ge("fecha", fechaInicio)
                .le("fecha", fechaFin)
                .orderBy("fecha asc, nro asc")
                .findList();
    }
    
    public List<AccountingEntry> findByPeriodo(Long periodoId) {
        return database.find(AccountingEntry.class)
                .where()
                .eq("periodo.id", periodoId)
                .orderBy("nro asc")
                .findList();
    }
    
    public List<AccountingEntry> findByEstado(String estado) {
        return database.find(AccountingEntry.class)
                .where()
                .eq("estadoAsiento", estado)
                .orderBy("fecha desc, nro desc")
                .findList();
    }
    
    public List<AccountingEntry> findBySubSistema(String subSistema) {
        return database.find(AccountingEntry.class)
                .where()
                .eq("subSistema", subSistema)
                .orderBy("fecha desc, nro desc")
                .findList();
    }
    
    public List<AccountingEntry> findByPeriodoYEstado(Long periodoId, String estado) {
        return database.find(AccountingEntry.class)
                .where()
                .eq("periodo.id", periodoId)
                .eq("estadoAsiento", estado)
                .orderBy("nro asc")
                .findList();
    }
    
    public List<AccountingEntry> obtenerAsientosConfirmados() {
        return database.find(AccountingEntry.class)
                .where()
                .eq("estadoAsiento", "CONFIRMADO")
                .orderBy("fecha desc, nro desc")
                .findList();
    }
    
    public List<AccountingEntry> obtenerAsientosEdicion() {
        return database.find(AccountingEntry.class)
                .where()
                .eq("estadoAsiento", "EDICION")
                .orderBy("fecha desc, nro desc")
                .findList();
    }
    
    public boolean existeAsientoConNumero(int numero) {
        return database.find(AccountingEntry.class)
                .where()
                .eq("nro", numero)
                .exists();
    }
    
    public List<AccountingEntry> obtenerAsientosOrdenadosPorFecha() {
        return database.find(AccountingEntry.class)
                .orderBy("fecha desc, nro desc")
                .findList();
    }
    
    public List<AccountingEntry> obtenerAsientosPorSubSistemaYSistema(String subSistema, String sistema) {
        return database.find(AccountingEntry.class)
                .where()
                .eq("subSistema", subSistema)
                .eq("sistema", sistema)
                .orderBy("fecha desc, nro desc")
                .findList();
    }
}