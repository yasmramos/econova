package com.univsoftdev.econova.config.repository;

import com.univsoftdev.econova.config.model.Period;
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
public class PeriodoRepository extends BaseRepository<Period> {

    @Inject
    public PeriodoRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<Period> getEntityType() {
        return Period.class;
    }

    @Override
    public List<Period> findByCriteria(String criteria) {
        return database.find(Period.class)
                .where()
                .ilike("nombre", "%" + criteria + "%")
                .or()
                .ilike("exercise.nombre", "%" + criteria + "%")
                .findList();
    }

    // Métodos adicionales específicos para Period
    public Optional<Period> findByNombre(String nombre) {
        return Optional.ofNullable(database.find(Period.class)
                        .where()
                        .eq("nombre", nombre)
                        .findOne()
        );
    }

    public List<Period> findByEjercicio(Long ejercicioId) {
        return database.find(Period.class)
                .where()
                .eq("exercise.id", ejercicioId)
                .orderBy("fechaInicio asc")
                .findList();
    }

    public List<Period> findByFechaRango(LocalDate inicio, LocalDate fin) {
        return database.find(Period.class)
                .where()
                .le("fechaInicio", fin)
                .ge("fechaFin", inicio)
                .findList();
    }

    public Optional<Period> findActualPorEjercicio(Long ejercicioId) {
        return Optional.ofNullable(database.find(Period.class)
                        .where()
                        .eq("exercise.id", ejercicioId)
                        .eq("current", true)
                        .findOne()
        );
    }

    public Optional<Period> findActivoPorEjercicio(Long ejercicioId) {
        return Optional.ofNullable(database.find(Period.class)
                        .where()
                        .eq("exercise.id", ejercicioId)
                        .eq("active", true)
                        .findOne()
        );
    }

    public List<Period> findPeriodosActivos() {
        return database.find(Period.class)
                .where()
                .eq("active", true)
                .orderBy("fechaInicio asc")
                .findList();
    }

    public List<Period> findPeriodosPorFecha(LocalDate fecha) {
        return database.find(Period.class)
                .where()
                .le("fechaInicio", fecha)
                .ge("fechaFin", fecha)
                .orderBy("fechaInicio asc")
                .findList();
    }

    public List<Period> obtenerPeriodosOrdenadosPorFecha() {
        return database.find(Period.class)
                .orderBy("fechaInicio asc")
                .findList();
    }

    public boolean existePeriodoEnRango(Long ejercicioId, LocalDate inicio, LocalDate fin) {
        return database.find(Period.class)
                .where()
                .eq("exercise.id", ejercicioId)
                .le("fechaInicio", fin)
                .ge("fechaFin", inicio)
                .exists();
    }
}
