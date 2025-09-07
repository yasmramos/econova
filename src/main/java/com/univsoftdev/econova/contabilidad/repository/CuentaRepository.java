package com.univsoftdev.econova.contabilidad.repository;

import com.univsoftdev.econova.contabilidad.AccountType;
import com.univsoftdev.econova.contabilidad.model.Account;
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
public class CuentaRepository extends BaseRepository<Account> {

    @Inject
    public CuentaRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<Account> getEntityType() {
        return Account.class;
    }

    @Override
    public List<Account> findByCriteria(String criteria) {
        return database.find(Account.class)
                .where()
                .ilike("codigo", "%" + criteria + "%")
                .or()
                .ilike("nombre", "%" + criteria + "%")
                .findList();
    }

    public Optional<Account> findByCodigo(String codigo) {
        return Optional.ofNullable(database.find(Account.class)
                        .where()
                        .eq("codigo", codigo)
                        .findOne()
        );
    }

    public Optional<Account> findByNombre(String nombre) {
        return Optional.ofNullable(database.find(Account.class)
                        .where()
                        .eq("nombre", nombre)
                        .findOne()
        );
    }

    public List<Account> findByTipoCuenta(AccountType tipoCuenta) {
        return database.find(Account.class)
                .where()
                .eq("tipoCuenta", tipoCuenta)
                .orderBy("codigo asc")
                .findList();
    }

    public List<Account> findByNaturaleza(String naturaleza) {
        return database.find(Account.class)
                .where()
                .eq("naturaleza", naturaleza)
                .orderBy("codigo asc")
                .findList();
    }

    public List<Account> findByEstadoCuenta(String estado) {
        return database.find(Account.class)
                .where()
                .eq("estadoCuenta", estado)
                .orderBy("codigo asc")
                .findList();
    }

    public List<Account> findByPlanDeCuenta(Long planId) {
        return database.find(Account.class)
                .where()
                .eq("planDeCuenta.id", planId)
                .orderBy("codigo asc")
                .findList();
    }

    public List<Account> findByCuentaPadre(Long padreId) {
        return database.find(Account.class)
                .where()
                .eq("cuentaPadre.id", padreId)
                .orderBy("codigo asc")
                .findList();
    }

    public List<Account> obtenerCuentasActivas() {
        return database.find(Account.class)
                .where()
                .eq("activa", true)
                .orderBy("codigo asc")
                .findList();
    }

    public List<Account> obtenerCuentasPorTipoYEstado(String tipoCuenta, String estado) {
        return database.find(Account.class)
                .where()
                .eq("tipoCuenta", tipoCuenta)
                .eq("estadoCuenta", estado)
                .orderBy("codigo asc")
                .findList();
    }

    public List<Account> obtenerCuentasConSaldo() {
        return database.find(Account.class)
                .where()
                .gt("saldo", BigDecimal.ZERO)
                .orderBy("codigo asc")
                .findList();
    }

    public List<Account> obtenerSubCuentas(Long cuentaId) {
        return database.find(Account.class)
                .where()
                .eq("cuentaPadre.id", cuentaId)
                .orderBy("codigo asc")
                .findList();
    }

    public List<Account> obtenerCuentasRaiz() {
        return database.find(Account.class)
                .where()
                .isNull("cuentaPadre")
                .orderBy("codigo asc")
                .findList();
    }

    public boolean existeCuentaConCodigo(String codigo) {
        return database.find(Account.class)
                .where()
                .eq("codigo", codigo)
                .exists();
    }

    public boolean existeCuentaConNombre(String nombre) {
        return database.find(Account.class)
                .where()
                .eq("nombre", nombre)
                .exists();
    }

    public List<Account> obtenerCuentasOrdenadasPorCodigo() {
        return database.find(Account.class)
                .orderBy("codigo asc")
                .findList();
    }

    public List<Account> obtenerCuentasPorTipoApertura(String tipoApertura) {
        return database.find(Account.class)
                .where()
                .eq("tipoApertura", tipoApertura)
                .orderBy("codigo asc")
                .findList();
    }

    public BigDecimal getTotalSaldoCuentas() {
        // Para calcular el total, necesitarías una consulta específica
        // Esta es una aproximación simple
        return BigDecimal.ZERO; // Placeholder
    }
}
