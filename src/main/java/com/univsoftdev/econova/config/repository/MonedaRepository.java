package com.univsoftdev.econova.config.repository;

import com.univsoftdev.econova.contabilidad.model.Currency;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class MonedaRepository extends BaseRepository<Currency> {

    @Inject
    public MonedaRepository(Database database) {
        super(database);
    }
    
    @Override
    protected Class<Currency> getEntityType() {
        return Currency.class;
    }
    
    @Override
    public List<Currency> findByCriteria(String criteria) {
        return database.find(Currency.class)
                .where()
                .ilike("displayName", "%" + criteria + "%")
                .or()
                .ilike("symbol", "%" + criteria + "%")
                .or()
                .ilike("pais", "%" + criteria + "%")
                .findList();
    }
    
    // Métodos adicionales específicos para Currency
    public Optional<Currency> findBySimbolo(String symbol) {
        return Optional.ofNullable(database.find(Currency.class)
                .where()
                .eq("symbol", symbol)
                .findOne()
        );
    }
    
    public Optional<Currency> findByDisplayName(String displayName) {
        return Optional.ofNullable(database.find(Currency.class)
                .where()
                .eq("displayName", displayName)
                .findOne()
        );
    }
    
    public Optional<Currency> findPorDefecto() {
        return Optional.ofNullable(database.find(Currency.class)
                .where()
                .eq("porDefecto", true)
                .findOne()
        );
    }
    
    public List<Currency> findConTasaCambio(BigDecimal tasaMinima) {
        return database.find(Currency.class)
                .where()
                .ge("tasaCambio", tasaMinima)
                .findList();
    }
    
    public List<Currency> obtenerMonedasOrdenadas() {
        return database.find(Currency.class)
                .orderBy("displayName asc")
                .findList();
    }
    
    public List<Currency> obtenerMonedasPorPais(String pais) {
        return database.find(Currency.class)
                .where()
                .eq("pais", pais)
                .orderBy("displayName asc")
                .findList();
    }
}