package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.core.Injector;
import com.univsoftdev.econova.config.model.Unit;
import com.univsoftdev.econova.config.service.UnitService;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.table.TableColumnAdjuster;
import java.awt.*;

public class FormUnidades extends Form {

    private static final long serialVersionUID = 2899783794151596658L;

    public FormUnidades() {
        initComponents();
        UnitService unidadService = Injector.get(UnitService.class);
        java.util.List<Unit> findAll = unidadService.findAll();

        var model = (DefaultTableModel) table1.getModel();
        for (Unit unidad : findAll) {
            model.addRow(new Object[]{
                unidad.getCode(),
                unidad.getName(),
                unidad.getAddress(),
                unidad.getEmail(),
                unidad.getNae(),
                unidad.getDpa(),
                unidad.getReup()
            });
        }

        TableColumnAdjuster adjuster = new TableColumnAdjuster(table1);
        adjuster.adjustColumns();
    }

    private void adicionarActionPerformed(ActionEvent e) {

    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.scrollPane1 = new JScrollPane();
	this.table1 = new JTable();
	this.panel2 = new JPanel();
	this.label2 = new JLabel();
	this.popupMenu1 = new JPopupMenu();
	this.menuItemAdicionar = new JMenuItem();
	this.menuItem2 = new JMenuItem();
	this.menuItem3 = new JMenuItem();
	this.menuItem4 = new JMenuItem();
	this.menuItem5 = new JMenuItem();
	this.menuItem6 = new JMenuItem();
	this.menuItem7 = new JMenuItem();
	this.menuItem8 = new JMenuItem();
	this.menuItem9 = new JMenuItem();
	this.menuItem10 = new JMenuItem();
	this.menuItem11 = new JMenuItem();
	this.menuItem12 = new JMenuItem();

	//======== this ========
	setLayout(new BorderLayout());

	//======== panel1 ========
	{

	    //---- label1 ----
	    this.label1.setText("UNIDADES"); //NOI18N

	    GroupLayout panel1Layout = new GroupLayout(this.panel1);
	    panel1.setLayout(panel1Layout);
	    panel1Layout.setHorizontalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(this.label1)
			.addContainerGap(338, Short.MAX_VALUE))
	    );
	    panel1Layout.setVerticalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(this.label1)
			.addContainerGap())
	    );
	}
	add(this.panel1, BorderLayout.NORTH);

	//======== scrollPane1 ========
	{
	    this.scrollPane1.setComponentPopupMenu(this.popupMenu1);

	    //---- table1 ----
	    this.table1.setModel(new DefaultTableModel(
		new Object[][] {
		},
		new String[] {
		    "C\u00f3digo", "Nombre", "Direcci\u00f3n", "Correo", "NAE", "DPA", "REUUP" //NOI18N
		}
	    ));
	    this.table1.setComponentPopupMenu(this.popupMenu1);
	    this.scrollPane1.setViewportView(this.table1);
	}
	add(this.scrollPane1, BorderLayout.CENTER);

	//======== panel2 ========
	{

	    //---- label2 ----
	    this.label2.setText("0 Unidades"); //NOI18N

	    GroupLayout panel2Layout = new GroupLayout(this.panel2);
	    panel2.setLayout(panel2Layout);
	    panel2Layout.setHorizontalGroup(
		panel2Layout.createParallelGroup()
		    .addGroup(panel2Layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(this.label2)
			.addContainerGap(336, Short.MAX_VALUE))
	    );
	    panel2Layout.setVerticalGroup(
		panel2Layout.createParallelGroup()
		    .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
			.addGap(0, 0, Short.MAX_VALUE)
			.addComponent(this.label2))
	    );
	}
	add(this.panel2, BorderLayout.SOUTH);

	//======== popupMenu1 ========
	{

	    //---- menuItemAdicionar ----
	    this.menuItemAdicionar.setText("Adicionar"); //NOI18N
	    this.menuItemAdicionar.addActionListener(e -> adicionarActionPerformed(e));
	    this.popupMenu1.add(this.menuItemAdicionar);

	    //---- menuItem2 ----
	    this.menuItem2.setText("Eliminar"); //NOI18N
	    this.popupMenu1.add(this.menuItem2);

	    //---- menuItem3 ----
	    this.menuItem3.setText("Modificar"); //NOI18N
	    this.popupMenu1.add(this.menuItem3);

	    //---- menuItem4 ----
	    this.menuItem4.setText("Activar"); //NOI18N
	    this.popupMenu1.add(this.menuItem4);
	    this.popupMenu1.addSeparator();

	    //---- menuItem5 ----
	    this.menuItem5.setText("Imprimir"); //NOI18N
	    this.popupMenu1.add(this.menuItem5);

	    //---- menuItem6 ----
	    this.menuItem6.setText("Buscar"); //NOI18N
	    this.popupMenu1.add(this.menuItem6);

	    //---- menuItem7 ----
	    this.menuItem7.setText("Ver Todas"); //NOI18N
	    this.popupMenu1.add(this.menuItem7);
	    this.popupMenu1.addSeparator();

	    //---- menuItem8 ----
	    this.menuItem8.setText("Exportar"); //NOI18N
	    this.popupMenu1.add(this.menuItem8);

	    //---- menuItem9 ----
	    this.menuItem9.setText("Importar"); //NOI18N
	    this.popupMenu1.add(this.menuItem9);
	    this.popupMenu1.addSeparator();

	    //---- menuItem10 ----
	    this.menuItem10.setText("Historia"); //NOI18N
	    this.popupMenu1.add(this.menuItem10);

	    //---- menuItem11 ----
	    this.menuItem11.setText("Logotipo"); //NOI18N
	    this.popupMenu1.add(this.menuItem11);
	    this.popupMenu1.addSeparator();

	    //---- menuItem12 ----
	    this.menuItem12.setText("Grupo de Unidades"); //NOI18N
	    this.popupMenu1.add(this.menuItem12);
	}
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JPanel panel2;
    private JLabel label2;
    private JPopupMenu popupMenu1;
    private JMenuItem menuItemAdicionar;
    private JMenuItem menuItem2;
    private JMenuItem menuItem3;
    private JMenuItem menuItem4;
    private JMenuItem menuItem5;
    private JMenuItem menuItem6;
    private JMenuItem menuItem7;
    private JMenuItem menuItem8;
    private JMenuItem menuItem9;
    private JMenuItem menuItem10;
    private JMenuItem menuItem11;
    private JMenuItem menuItem12;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
