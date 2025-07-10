package com.univsoftdev.econova.core.utils;

import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.model.PlanDeCuentas;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasificadorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasificadorUtil.class);

    private ClasificadorUtil() {
    }

    public static void update(JTree tree, PlanDeCuentas planDeCuentas) {
        try {
            tree.removeAll();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(planDeCuentas.getNombre());
            List<Cuenta> cuentas = planDeCuentas.getCuentas();
            for (Cuenta cuenta : cuentas) {
                DefaultMutableTreeNode treeNodeCuenta = new DefaultMutableTreeNode(cuenta);
                if (!cuenta.getSubCuentas().isEmpty()) {
                    addSubCuentas(cuenta, treeNodeCuenta);
                }
                root.add(treeNodeCuenta);
            }
            DefaultTreeModel treeModel = new DefaultTreeModel(root);

            tree.setModel(treeModel);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    private static void addSubCuentas(Cuenta cuenta, DefaultMutableTreeNode treeNodeCuenta) {
        for (Cuenta subCuenta : cuenta.getSubCuentas()) {
            DefaultMutableTreeNode treeNodeSubCuenta = new DefaultMutableTreeNode(subCuenta.getCodigo() + " " + subCuenta.getNombre());
            if (!subCuenta.getSubCuentas().isEmpty()) {
                for (var control : subCuenta.getSubCuentas()) {
                    DefaultMutableTreeNode treeNodeControl = new DefaultMutableTreeNode(control.getCodigo() + " " + control.getNombre());
                    addControl(control, treeNodeControl);
                    treeNodeCuenta.add(treeNodeSubCuenta);
                }
            }
            treeNodeCuenta.add(treeNodeSubCuenta);
        }
    }

    private static void addControl(Cuenta control, DefaultMutableTreeNode treeNodeControl) {
        treeNodeControl.add(treeNodeControl);
    }

}
