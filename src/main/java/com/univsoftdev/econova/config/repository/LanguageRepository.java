package com.univsoftdev.econova.config.repository;

import com.univsoftdev.econova.config.model.Language;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class LanguageRepository extends BaseRepository<Language> {

    @Inject
    public LanguageRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<Language> getEntityType() {
        return Language.class;
    }

    @Override
    public List<Language> findByCriteria(String criteria) {
        return database.find(Language.class)
                .where()
                .ilike("nombre", "%" + criteria + "%")
                .or()
                .ilike("symbol", "%" + criteria + "%")
                .or()
                .ilike("pais", "%" + criteria + "%")
                .findList();
    }

    // Métodos adicionales específicos para Language
    public Optional<Language> findByIdiomaPorSimbolo(String symbol) {
        return Optional.ofNullable(database.find(Language.class)
                        .where()
                        .eq("symbol", symbol)
                        .findOne()
        );
    }

    public Optional<Language> findByIdiomaPorNombre(String nombre) {
        return Optional.ofNullable(database.find(Language.class)
                        .where()
                        .eq("nombre", nombre)
                        .findOne()
        );
    }

    public List<Language> obtenerIdiomasOrdenados() {
        return database.find(Language.class)
                .orderBy("nombre asc")
                .findList();
    }
}
