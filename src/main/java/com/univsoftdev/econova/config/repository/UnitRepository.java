package com.univsoftdev.econova.config.repository;

import com.univsoftdev.econova.config.model.Unit;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class UnitRepository extends BaseRepository<Unit> {

    @Inject
    public UnitRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<Unit> getEntityType() {
        return Unit.class;
    }

    @Override
    public List<Unit> findByCriteria(String criteria) {
        return database.find(Unit.class)
                .where()
                .ilike("nombre", "%" + criteria + "%")
                .or()
                .ilike("codigo", "%" + criteria + "%")
                .or()
                .ilike("empresa.nombre", "%" + criteria + "%")
                .findList();
    }

    public Optional<Unit> findByCodigo(String codigo) {
        return Optional.ofNullable(database.find(Unit.class)
                        .where()
                        .eq("codigo", codigo)
                        .findOne()
        );
    }

    public Optional<Unit> findByNombre(String nombre) {
        return Optional.ofNullable(database.find(Unit.class)
                        .where()
                        .eq("nombre", nombre)
                        .findOne()
        );
    }

    public List<Unit> findByEmpresa(Long empresaId) {
        return database.find(Unit.class)
                .where()
                .eq("empresa.id", empresaId)
                .orderBy("nombre asc")
                .findList();
    }

    public List<Unit> obtenerUnidadesPorEmpresaYNombre(Long empresaId, String nombre) {
        return database.find(Unit.class)
                .where()
                .eq("empresa.id", empresaId)
                .ilike("nombre", "%" + nombre + "%")
                .orderBy("nombre asc")
                .findList();
    }

    public boolean existeUnidadConCodigo(String codigo) {
        return database.find(Unit.class)
                .where()
                .eq("codigo", codigo)
                .exists();
    }

    public List<Unit> obtenerUnidadesOrdenadasPorNombre() {
        return database.find(Unit.class)
                .orderBy("nombre asc")
                .findList();
    }

    public List<Unit> obtenerUnidadesConAsientos() {
        return database.find(Unit.class)
                .where()
                .gt("asientos.size", 0)
                .orderBy("nombre asc")
                .findList();
    }
}
