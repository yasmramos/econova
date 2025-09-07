package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.ChartOfAccounts;
import com.univsoftdev.econova.contabilidad.model.Ledger;
import com.univsoftdev.econova.contabilidad.repository.PlanDeCuentasRepository;
import com.univsoftdev.econova.core.service.BaseService;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class PlanDeCuentasService extends BaseService<ChartOfAccounts, PlanDeCuentasRepository> {

    private final CuentaService cuentaService;

    @Inject
    public PlanDeCuentasService(PlanDeCuentasRepository database, CuentaService cuentaService) {
        super(database);
        this.cuentaService = cuentaService;
    }

    public DefaultTreeModel crearModelPlanDeCuentas() {
        // Nodo raíz del árbol
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Plan de Cuentas");

        // Obtener todas las cuentas principales (sin cuenta padre)
        List<Account> cuentasPrincipales = cuentaService.findCuentasPadres();

        // Construir el árbol recursivamente
        for (Account cuenta : cuentasPrincipales) {
            agregarCuentaAlArbol(rootNode, cuenta);
        }

        // Crear el modelo del árbol
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        // Crear y retornar el JTree
        return treeModel;
    }

    private void agregarCuentaAlArbol(DefaultMutableTreeNode parentNode, Account cuenta) {
        // Crear un nodo para la cuenta actual
        DefaultMutableTreeNode cuentaNode = new DefaultMutableTreeNode(cuenta);

        // Agregar el nodo al nodo padre
        parentNode.add(cuentaNode);

        // Recursivamente agregar las subcuentas
        if (cuenta.getSubAccounts() != null && !cuenta.getSubAccounts().isEmpty()) {
            for (Account subcuenta : cuenta.getSubAccounts()) {
                agregarCuentaAlArbol(cuentaNode, subcuenta);
            }
        }
    }

    @Transactional
    public void addCuenta(@NotNull Account cuenta) {
        if (findCuentaByCodigo(cuenta.getCode()) != null) {
            throw new IllegalArgumentException("Ya existe una cuenta con el código " + cuenta.getCode());
        }
        ChartOfAccounts planDeCuentas = getChartOfAccounts();
        cuenta.setChartOfAccounts(planDeCuentas);
        Ledger libroMayor = new Ledger(cuenta);
        cuenta.setLedger(libroMayor);
        planDeCuentas.agregarCuenta(cuenta);
        this.update(planDeCuentas);
    }

    public List<Account> getAccounts() {
        return getChartOfAccounts().getAccounts();
    }

    @Transactional
    public ChartOfAccounts createChartOfAccounts(Long id, String name) {
        ChartOfAccounts chartOfAccounts = getChartOfAccounts();
        if (chartOfAccounts == null) {
            chartOfAccounts = new ChartOfAccounts(name);
            chartOfAccounts.setId(id);
            repository.save(chartOfAccounts);
        }
        return chartOfAccounts;
    }

    @Transactional
    public ChartOfAccounts getChartOfAccounts() {
        return repository.find(ChartOfAccounts.class).findOne();
    }

    @Transactional
    @Override
    public void update(ChartOfAccounts planDeCuentas) {
        repository.update(planDeCuentas);
    }

    public Optional<Account> findCuentaByCodigo(String codigo) {
        return cuentaService.findByCodigo(codigo);
    }

    @Transactional
    public List<Account> getCuentasActivas() {
        return Account.finder.query().where().eq("active", true).findList();
    }

    @Transactional
    public List<Account> getCuentasInactivas() {
        return Account.finder.query().where().eq("active", false).findList();
    }

    public List<Account> getCuentasPaginadas(int page, int pageSize) {
        return repository.find(Account.class)
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findList();
    }

    public void updateCuenta(Account cuenta) {
        cuentaService.update(cuenta);
    }

    public List<Account> findAllCuentas() {
        return cuentaService.findAll();
    }

}
