package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.Injector;
import com.univsoftdev.econova.config.model.Empresa;
import java.awt.event.*;
import com.univsoftdev.econova.config.service.UnidadService;
import com.univsoftdev.econova.core.utils.table.TableColumnAdjuster;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;

public class FormSeleccionUnidad extends Modal {

    private final UnidadService unidadService;
    private final AppContext appContext;

    public FormSeleccionUnidad(Empresa empresa) {
        initComponents();
        this.setSize(462, 496);
        unidadService = Injector.get(UnidadService.class);
        appContext = Injector.get(AppContext.class);

        final var model = (DefaultTableModel) table1.getModel();
        final var unidades = empresa.getUnidades();

        unidades.forEach(u -> {
            model.addRow(new Object[]{u.getCodigo(), u.getNombre()});
        });

        new TableColumnAdjuster(table1).adjustColumns();
    }

    private void aceptar(ActionEvent e) {

        final int row = table1.getSelectedRow();
        final int col = 0;
        final var codigo = (String) table1.getValueAt(row, col);
        final var unidadOpt = unidadService.findByCodigo(codigo);

        if (unidadOpt.isPresent()) {
            appContext.getSession().setUnidad(unidadOpt.get());
            ModalDialog.closeModal(this.getId());
        }
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.scrollPane1 = new JScrollPane();
	this.table1 = new JTable();
	this.panel2 = new JPanel();
	this.button1 = new JButton();
	this.buttonAceptar = new JButton();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    this.panel1.setLayout(new BorderLayout());

	    //---- label1 ----
	    this.label1.setText("Unidades"); //NOI18N
	    this.label1.setBorder(new EmptyBorder(5, 5, 5, 5));
	    this.panel1.add(this.label1, BorderLayout.WEST);
	}
	add(this.panel1, BorderLayout.NORTH);

	//======== scrollPane1 ========
	{

	    //---- table1 ----
	    this.table1.setModel(new DefaultTableModel(
		new Object[][] {
		    {null, null},
		},
		new String[] {
		    "C\u00f3digo", "Nombre" //NOI18N
		}
	    ));
	    {
		TableColumnModel cm = this.table1.getColumnModel();
		cm.getColumn(0).setPreferredWidth(50);
		cm.getColumn(1).setPreferredWidth(320);
	    }
	    this.scrollPane1.setViewportView(this.table1);
	}
	add(this.scrollPane1, BorderLayout.CENTER);

	//======== panel2 ========
	{
	    this.panel2.setBorder(new EmptyBorder(5, 5, 5, 5));

	    //---- button1 ----
	    this.button1.setText("Cancelar"); //NOI18N
	    this.button1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	    //---- buttonAceptar ----
	    this.buttonAceptar.setText("Aceptar"); //NOI18N
	    this.buttonAceptar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.buttonAceptar.addActionListener(e -> aceptar(e));

	    GroupLayout panel2Layout = new GroupLayout(this.panel2);
	    panel2.setLayout(panel2Layout);
	    panel2Layout.setHorizontalGroup(
		panel2Layout.createParallelGroup()
		    .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
			.addGap(0, 176, Short.MAX_VALUE)
			.addComponent(this.buttonAceptar)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(this.button1))
	    );
	    panel2Layout.setVerticalGroup(
		panel2Layout.createParallelGroup()
		    .addGroup(panel2Layout.createSequentialGroup()
			.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(this.button1)
			    .addComponent(this.buttonAceptar))
			.addGap(0, 0, Short.MAX_VALUE))
	    );
	}
	add(this.panel2, BorderLayout.SOUTH);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JPanel panel2;
    private JButton button1;
    private JButton buttonAceptar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
