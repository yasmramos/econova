package com.univsoftdev.econova.core.utils;

import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.ChartOfAccounts;
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

    public static void update(JTree tree, ChartOfAccounts planDeCuentas) {
        try {
            tree.removeAll();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(planDeCuentas.getName());
            List<Account> cuentas = planDeCuentas.getAccounts();
            for (Account cuenta : cuentas) {
                DefaultMutableTreeNode treeNodeCuenta = new DefaultMutableTreeNode(cuenta);
                if (!cuenta.getSubAccounts().isEmpty()) {
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

    private static void addSubCuentas(Account cuenta, DefaultMutableTreeNode treeNodeCuenta) {
        for (Account subCuenta : cuenta.getSubAccounts()) {
            DefaultMutableTreeNode treeNodeSubCuenta = new DefaultMutableTreeNode(subCuenta.getCode() + " " + subCuenta.getName());
            if (!subCuenta.getSubAccounts().isEmpty()) {
                for (var control : subCuenta.getSubAccounts()) {
                    DefaultMutableTreeNode treeNodeControl = new DefaultMutableTreeNode(control.getCode() + " " + control.getName());
                    addControl(control, treeNodeControl);
                    treeNodeCuenta.add(treeNodeSubCuenta);
                }
            }
            treeNodeCuenta.add(treeNodeSubCuenta);
        }
    }

    private static void addControl(Account control, DefaultMutableTreeNode treeNodeControl) {
        treeNodeControl.add(treeNodeControl);
    }

}
