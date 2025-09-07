package com.univsoftdev.econova.config.repository;

import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class ExerciseRepository extends BaseRepository<Exercise> {

    @Inject
    public ExerciseRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<Exercise> getEntityType() {
        return Exercise.class;
    }

    @Transactional
    @Override
    public List<Exercise> findByCriteria(String criteria) {
        return database.find(Exercise.class)
                .where()
                .ilike("nombre", "%" + criteria + "%")
                .findList();
    }

    @Transactional
    public Optional<Exercise> findByYear(int year) {
        return Optional.ofNullable(database.find(Exercise.class)
                        .where()
                        .eq("year", year)
                        .findOne()
        );
    }
    
    @Transactional
    public Optional<Exercise> findByName(String name) {
        return Optional.ofNullable(database.find(Exercise.class)
                        .where()
                        .eq("nombre", name)
                        .findOne()
        );
    }

    @Transactional
    public List<Exercise> findActive() {
        return database.find(Exercise.class)
                .where()
                .eq("current", true)
                .findList();
    }

    @Transactional
    public List<Exercise> findCurrentAndActive() {
        return database.find(Exercise.class)
                .where()
                .eq("current", true)
                .eq("iniciado", true)
                .findList();
    }
}
