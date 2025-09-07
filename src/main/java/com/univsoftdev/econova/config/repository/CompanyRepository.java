package com.univsoftdev.econova.config.repository;

import com.univsoftdev.econova.config.model.Company;
import com.univsoftdev.econova.core.repository.BaseRepository;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class CompanyRepository extends BaseRepository<Company> {

    @Inject
    public CompanyRepository(Database database) {
        super(database);
    }
    
    @Override
    protected Class<Company> getEntityType() {
        return Company.class;
    }
    
    @Override
    public List<Company> findByCriteria(String criteria) {
        return database.find(Company.class)
                .where()
                .ilike("name", "%" + criteria + "%")
                .or()
                .ilike("nif", "%" + criteria + "%")
                .or()
                .ilike("cif", "%" + criteria + "%")
                .findList();
    }
    
    // Métodos adicionales específicos para Company
    public Optional<Company> findByNif(String nif) {
        return Optional.ofNullable(database.find(Company.class)
                .where()
                .eq("nif", nif)
                .findOne()
        );
    }
    
    public Optional<Company> findByCif(String cif) {
        return Optional.ofNullable(database.find(Company.class)
                .where()
                .eq("cif", cif)
                .findOne()
        );
    }
    
    public Optional<Company> findByCode(String code) {
        return Optional.ofNullable(database.find(Company.class)
                .where()
                .eq("code", code)
                .findOne()
        );
    }
    
    public List<Company> findByNombreContaining(String nombre) {
        return database.find(Company.class)
                .where()
                .ilike("name", "%" + nombre + "%")
                .orderBy("name asc")
                .findList();
    }
}