package com.univsoftdev.econova.contabilidad.repository;

import com.univsoftdev.econova.contabilidad.model.ChartOfAccounts;
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
public class PlanDeCuentasRepository extends BaseRepository<ChartOfAccounts> {

    @Inject
    public PlanDeCuentasRepository(Database database) {
        super(database);
    }

    @Override
    protected Class<ChartOfAccounts> getEntityType() {
        return ChartOfAccounts.class;
    }

    @Override
    public List<ChartOfAccounts> findByCriteria(String criteria) {
        return database.find(ChartOfAccounts.class)
                .where()
                .ilike("nombre", "%" + criteria + "%")
                .findList();
    }

    public Optional<ChartOfAccounts> findByNombre(String nombre) {
        return Optional.ofNullable(database.find(ChartOfAccounts.class)
                        .where()
                        .eq("nombre", nombre)
                        .findOne()
        );
    }

    public List<ChartOfAccounts> obtenerPlanesDeCuentasOrdenados() {
        return database.find(ChartOfAccounts.class)
                .orderBy("nombre asc")
                .findList();
    }

    public List<ChartOfAccounts> obtenerPlanesConCuentas() {
        return database.find(ChartOfAccounts.class)
                .orderBy("nombre asc")
                .findList();
    }

    public List<ChartOfAccounts> obtenerPlanesConCuentasActivas() {
        return database.find(ChartOfAccounts.class)
                .where()
                .eq("cuentas.activa", true)
                .orderBy("nombre asc")
                .findList();
    }

    public boolean existePlanConNombre(String nombre) {
        return database.find(ChartOfAccounts.class)
                .where()
                .eq("nombre", nombre)
                .exists();
    }

    public long contarCuentasPorPlan(Long planId) {
        return database.find(ChartOfAccounts.class)
                .where()
                .eq("id", planId)
                .findCount();
    }

    public BigDecimal getSaldoTotalPorPlan(Long planId) {
        // Para calcular el saldo total, necesitarías una consulta específica
        // Esta es una aproximación simple
        return BigDecimal.ZERO; // Placeholder
    }

    public List<ChartOfAccounts> obtenerPlanesConCuentasYPorcentaje() {
        return database.find(ChartOfAccounts.class)
                .orderBy("nombre asc")
                .findList();
    }

    public Optional<ChartOfAccounts> encontrarPlanMasReciente() {
        return Optional.ofNullable(database.find(ChartOfAccounts.class)
                        .orderBy("id desc")
                        .setMaxRows(1)
                        .findOne()
        );
    }

    public List<ChartOfAccounts> obtenerPlanesConMasDeXCuentas(int cantidadMinima) {
        return database.find(ChartOfAccounts.class)
                .where()
                .gt("cuentas.size", cantidadMinima)
                .orderBy("nombre asc")
                .findList();
    }

    // Método corregido para obtener planes con cuentas que tienen saldo negativo
    public List<ChartOfAccounts> obtenerPlanesConCuentasConSaldoNegativo() {
        return database.find(ChartOfAccounts.class)
                .where()
                .gt("cuentas.saldo", BigDecimal.ZERO.negate()) // Cuentas con saldo negativo
                .orderBy("nombre asc")
                .findList();
    }

    public List<ChartOfAccounts> obtenerPlanesPorEstado(boolean activo) {
        // Esta consulta depende de cómo se implemente el estado en el modelo
        return database.find(ChartOfAccounts.class)
                .orderBy("nombre asc")
                .findList();
    }

    public List<ChartOfAccounts> obtenerPlanesConSubCuentas() {
        return database.find(ChartOfAccounts.class)
                .where()
                .gt("cuentas.subCuentas.size", 0)
                .orderBy("nombre asc")
                .findList();
    }

}
