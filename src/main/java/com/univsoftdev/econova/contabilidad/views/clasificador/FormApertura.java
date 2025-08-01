package com.univsoftdev.econova.contabilidad.views.clasificador;

import com.univsoftdev.econova.Injector;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.service.PlanDeCuentasService;
import com.univsoftdev.econova.core.utils.DialogUtils;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;

public class FormApertura extends Modal {

    private static final long serialVersionUID = 1196232226313239805L;
    private JTree tree;
    private PlanDeCuentasService planDeCuentasService;
    
    public FormApertura() {
        initComponents();
        init();
    }

    public FormApertura(JTree tree) {
        initComponents();
        init();
        this.tree = tree;
    }

    private void button1ActionPerformed(ActionEvent e) {
        DialogUtils.showModalDialog(this, new FormNuevaCuenta(table1), "Nueva cuenta");
    }

    private void btnModificarActionPerformed(ActionEvent e) {
        DialogUtils.showModalDialog(this, new FormNuevaCuenta(table1), "Modificar cuenta");
    }

    private void cerrarActionPerformed(ActionEvent e) {
        tree.setModel(planDeCuentasService.crearModelPlanDeCuentas());
        ModalDialog.closeModal(this.getId());
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	panel1 = new JPanel();
	toolBar1 = new JToolBar();
	buttonNuevo = new JButton();
	button2 = new JButton();
	btnModificar = new JButton();
	button4 = new JButton();
	button5 = new JButton();
	scrollPane1 = new JScrollPane();
	table1 = new JTable();
	panel2 = new JPanel();
	panel3 = new JPanel();
	buttonCerrar = new JButton();

	//======== this ========
	setPreferredSize(new Dimension(600, 498));
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    panel1.setLayout(new BorderLayout());

	    //======== toolBar1 ========
	    {

		//---- buttonNuevo ----
		buttonNuevo.setText("Nuevo");
		buttonNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonNuevo.addActionListener(e -> button1ActionPerformed(e));
		toolBar1.add(buttonNuevo);

		//---- button2 ----
		button2.setText("Eliminar");
		button2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		toolBar1.add(button2);

		//---- btnModificar ----
		btnModificar.setText("Modificar");
		btnModificar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnModificar.addActionListener(e -> btnModificarActionPerformed(e));
		toolBar1.add(btnModificar);

		//---- button4 ----
		button4.setText("Activar/Desactivar");
		button4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		toolBar1.add(button4);

		//---- button5 ----
		button5.setText("Imprimir");
		button5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		toolBar1.add(button5);
	    }
	    panel1.add(toolBar1, BorderLayout.EAST);
	}
	add(panel1, BorderLayout.NORTH);

	//======== scrollPane1 ========
	{

	    //---- table1 ----
	    table1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	    table1.setModel(new DefaultTableModel(
		new Object[][] {
		},
		new String[] {
		    "C\u00f3digo", "Descripci\u00f3n", "Naturaleza", "Tipo", "Estado", "Moneda"
		}
	    ));
	    scrollPane1.setViewportView(table1);
	}
	add(scrollPane1, BorderLayout.CENTER);

	//======== panel2 ========
	{
	    panel2.setMinimumSize(new Dimension(30, 35));
	    panel2.setMaximumSize(new Dimension(560, 40));
	    panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
	    panel2.setLayout(new BorderLayout());

	    //======== panel3 ========
	    {
		panel3.setLayout(new BorderLayout(5, 5));
	    }
	    panel2.add(panel3, BorderLayout.CENTER);

	    //---- buttonCerrar ----
	    buttonCerrar.setText("Cerrar");
	    buttonCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    buttonCerrar.addActionListener(e -> cerrarActionPerformed(e));
	    panel2.add(buttonCerrar, BorderLayout.EAST);
	}
	add(panel2, BorderLayout.SOUTH);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JToolBar toolBar1;
    private JButton buttonNuevo;
    private JButton button2;
    private JButton btnModificar;
    private JButton button4;
    private JButton button5;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JPanel panel2;
    private JPanel panel3;
    private JButton buttonCerrar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void init() {
        planDeCuentasService = Injector.get(PlanDeCuentasService.class);

        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        java.util.List<Cuenta> allCuentas = planDeCuentasService.findAllCuentas();

        for (Cuenta cuenta : allCuentas) {
            String activa = (cuenta.isActiva()) ? "Activa" : "Inactiva";
            model.addRow(new Object[]{
                cuenta.getCodigo(),
                cuenta.getNombre(),
                cuenta.getNaturaleza().name(),
                cuenta.getTipoCuenta().name(),
                activa,
                cuenta.getMoneda().getSymbol()});
        }
    }
}
