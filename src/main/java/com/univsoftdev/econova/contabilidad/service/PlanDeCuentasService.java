package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.model.PlanDeCuentas;
import com.univsoftdev.econova.core.Service;
import io.ebean.Database;
import io.ebean.annotation.Transactional;
import jakarta.inject.Singleton;
import com.univsoftdev.econova.contabilidad.model.LibroMayor;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class PlanDeCuentasService extends Service<PlanDeCuentas> {

    @Inject
    private final CuentaService cuentaService;
      
    @Inject
    public PlanDeCuentasService(Database database, CuentaService cuentaService) {
        super(database, PlanDeCuentas.class);
        this.cuentaService = cuentaService;
    }

    public DefaultTreeModel crearModelPlanDeCuentas() {
        // Nodo raíz del árbol
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Plan de Cuentas");

        // Obtener todas las cuentas principales (sin cuenta padre)
        List<Cuenta> cuentasPrincipales = database.find(Cuenta.class)
                .where()
                .isNull("cuentaPadre")
                .findList();

        // Construir el árbol recursivamente
        for (Cuenta cuenta : cuentasPrincipales) {
            agregarCuentaAlArbol(rootNode, cuenta);
        }

        // Crear el modelo del árbol
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        // Crear y retornar el JTree
        return treeModel;
    }

    private void agregarCuentaAlArbol(DefaultMutableTreeNode parentNode, Cuenta cuenta) {
        // Crear un nodo para la cuenta actual
        DefaultMutableTreeNode cuentaNode = new DefaultMutableTreeNode(cuenta);

        // Agregar el nodo al nodo padre
        parentNode.add(cuentaNode);

        // Recursivamente agregar las subcuentas
        if (cuenta.getSubCuentas() != null && !cuenta.getSubCuentas().isEmpty()) {
            for (Cuenta subcuenta : cuenta.getSubCuentas()) {
                agregarCuentaAlArbol(cuentaNode, subcuenta);
            }
        }
    }

    @Transactional
    public void addCuenta(@NotNull Cuenta cuenta) {
        if (findCuentaByCodigo(cuenta.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe una cuenta con el código " + cuenta.getCodigo());
        }
        PlanDeCuentas planDeCuentas = getPlanDeCuentas();
        cuenta.setPlanDeCuenta(planDeCuentas);
        LibroMayor libroMayor  = new LibroMayor(cuenta);
        cuenta.setLibroMayor(libroMayor);
        planDeCuentas.agregarCuenta(cuenta);
        this.update(planDeCuentas);
    }

    public List<Cuenta> getCuentas() {
        return getPlanDeCuentas().getCuentas();
    }

    @Transactional
    public void deletePlanDeCuentas(Long id) {
        database.delete(id);
    }

    @Transactional
    public void createPlanDeCuentas(PlanDeCuentas planDeCuentas) {
        database.save(planDeCuentas);
    }

    @Transactional
    public PlanDeCuentas getPlanDeCuentas() {
        return database.find(PlanDeCuentas.class).findOne();
    }

    @Transactional
    @Override
    public void update(PlanDeCuentas planDeCuentas) {
        database.update(planDeCuentas);
    }

    public Cuenta findCuentaByCodigo(String codigo) {
        return Cuenta.finder.query().where().eq("codigo", codigo).findOne();
    }

    @Transactional
    public List<Cuenta> getCuentasActivas() {
        return Cuenta.finder.query().where().eq("activa", true).findList();
    }

    @Transactional
    public List<Cuenta> getCuentasInactivas() {
        return Cuenta.finder.query().where().eq("activa", false).findList();
    }

    public List<Cuenta> getCuentasPaginadas(int page, int pageSize) {
        return database.find(Cuenta.class)
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findList();
    }

    public void updateCuenta(Cuenta cuenta) {
       cuentaService.update(cuenta);
    }

    public List<Cuenta> findAllCuentas() {
        return cuentaService.findAll();
    }

}
