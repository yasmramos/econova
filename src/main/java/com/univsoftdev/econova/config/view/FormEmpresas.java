package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.core.Injector;
import com.univsoftdev.econova.config.model.Company;
import com.univsoftdev.econova.config.model.Unit;
import com.univsoftdev.econova.config.service.CompanyService;
import com.univsoftdev.econova.config.service.UnitService;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.DialogUtils;
import com.univsoftdev.econova.core.utils.table.TableColumnAdjuster;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.Optional;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class FormEmpresas extends Form {
    
    private final CompanyService empService;
    
    public FormEmpresas() {
	initComponents();
        empService = Injector.get(CompanyService.class);

        java.util.List<Company> empresas = empService.findAll();

        var modelEmps = (DefaultTableModel) tableEmpresas.getModel();
        for (Company emp : empresas) {
            modelEmps.addRow(new Object[]{
                emp.getCode(),
                emp.getName(),
                emp.getAddress(),
                emp.getEmail(),
                emp.getNae(),
                emp.getDpa(),
                emp.getReuup()
            });
        }

        TableColumnAdjuster adjusterEmps = new TableColumnAdjuster(tableEmpresas);
        adjusterEmps.adjustColumns();
        
         UnitService unidadService = Injector.get(UnitService.class);
        java.util.List<Unit> findAll = unidadService.findAll();

        var model = (DefaultTableModel) tableUnidades.getModel();
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

        TableColumnAdjuster adjusterUnids = new TableColumnAdjuster(tableUnidades);
        adjusterUnids.adjustColumns();
    }
   
    private Optional<Company> getSelectedEmpresa() {
        int selectedRow = tableEmpresas.getSelectedRow();
        int column = 0;
        Object valueAt = tableEmpresas.getValueAt(selectedRow, column);
        return empService.findByCode(String.valueOf(valueAt));
    }

    private void adicionar(ActionEvent e) {
	DialogUtils.showModalDialog(this, new ModalAdicionarUnidadContable(tableEmpresas, getSelectedEmpresa().get()), "Adicionar Unidad Contable");
        TableColumnAdjuster adjuster = new TableColumnAdjuster(tableEmpresas);
        adjuster.adjustColumns();
    }

    private void tableEmpresasPropertyChange(PropertyChangeEvent e) {
	labelEmpCount.setText(String.format("{0} EMPRESAS", tableEmpresas.getRowCount()));
    }

    private void tableUnidadesPropertyChange(PropertyChangeEvent e) {
	labelUnidadesCount.setText(String.format("{0} UNIDADES", tableEmpresas.getRowCount()));
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.scrollPane1 = new JScrollPane();
	this.tableEmpresas = new JTable();
	this.panel2 = new JPanel();
	this.scrollPane2 = new JScrollPane();
	this.tableUnidades = new JTable();
	this.panel7 = new JPanel();
	this.label2 = new JLabel();
	this.panel8 = new JPanel();
	this.labelEmpCount = new JLabel();
	this.panel9 = new JPanel();
	this.labelUnidadesCount = new JLabel();
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
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    this.panel1.setPreferredSize(new Dimension(400, 25));

	    //---- label1 ----
	    this.label1.setText("EMPRESAS"); //NOI18N
	    this.label1.setBorder(new EmptyBorder(5, 5, 5, 5));
	    this.label1.setFont(this.label1.getFont().deriveFont(this.label1.getFont().getStyle() | Font.BOLD));

	    GroupLayout panel1Layout = new GroupLayout(this.panel1);
	    panel1.setLayout(panel1Layout);
	    panel1Layout.setHorizontalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addComponent(this.label1)
			.addGap(0, 750, Short.MAX_VALUE))
	    );
	    panel1Layout.setVerticalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
			.addGap(0, 0, Short.MAX_VALUE)
			.addComponent(this.label1))
	    );
	}
	add(this.panel1, BorderLayout.NORTH);

	//======== scrollPane1 ========
	{
	    this.scrollPane1.setComponentPopupMenu(this.popupMenu1);

	    //---- tableEmpresas ----
	    this.tableEmpresas.setModel(new DefaultTableModel(
		new Object[][] {
		    {null, null, null, null, null, null, null},
		    {null, null, null, null, null, null, null},
		},
		new String[] {
		    "C\u00f3digo", "Nombre", "Direcci\u00f3n", "Correo", "NAE", "DPA", "REUUP" //NOI18N
		}
	    ));
	    this.tableEmpresas.setComponentPopupMenu(this.popupMenu1);
	    this.tableEmpresas.setAutoCreateRowSorter(true);
	    this.tableEmpresas.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	    this.tableEmpresas.setFillsViewportHeight(true);
	    this.tableEmpresas.addPropertyChangeListener("rowCount", e -> tableEmpresasPropertyChange(e)); //NOI18N
	    this.scrollPane1.setViewportView(this.tableEmpresas);
	}
	add(this.scrollPane1, BorderLayout.CENTER);

	//======== panel2 ========
	{
	    this.panel2.setLayout(new BorderLayout());

	    //======== scrollPane2 ========
	    {

		//---- tableUnidades ----
		this.tableUnidades.setModel(new DefaultTableModel(
		    new Object[][] {
			{null, null, null, null, null, null, null},
			{null, null, null, null, null, null, null},
		    },
		    new String[] {
			"C\u00f3digo", "Nombre", "Direcci\u00f3n", "Correo", "NAE", "DPA", "REUUP" //NOI18N
		    }
		));
		this.tableUnidades.setFillsViewportHeight(true);
		this.tableUnidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tableUnidades.addPropertyChangeListener("rowCount", e -> tableUnidadesPropertyChange(e)); //NOI18N
		this.scrollPane2.setViewportView(this.tableUnidades);
	    }
	    this.panel2.add(this.scrollPane2, BorderLayout.CENTER);

	    //======== panel7 ========
	    {
		this.panel7.setLayout(new BorderLayout());

		//---- label2 ----
		this.label2.setText("UNIDADES"); //NOI18N
		this.label2.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.label2.setFont(this.label2.getFont().deriveFont(this.label2.getFont().getStyle() | Font.BOLD));
		this.panel7.add(this.label2, BorderLayout.WEST);

		//======== panel8 ========
		{
		    this.panel8.setLayout(new BorderLayout());

		    //---- labelEmpCount ----
		    this.labelEmpCount.setText("0 EMPRESAS"); //NOI18N
		    this.labelEmpCount.setBorder(new EmptyBorder(5, 5, 5, 5));
		    this.panel8.add(this.labelEmpCount, BorderLayout.WEST);
		}
		this.panel7.add(this.panel8, BorderLayout.NORTH);
	    }
	    this.panel2.add(this.panel7, BorderLayout.NORTH);

	    //======== panel9 ========
	    {
		this.panel9.setLayout(new BorderLayout());

		//---- labelUnidadesCount ----
		this.labelUnidadesCount.setText("0 UNIDADES"); //NOI18N
		this.labelUnidadesCount.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.panel9.add(this.labelUnidadesCount, BorderLayout.WEST);
	    }
	    this.panel2.add(this.panel9, BorderLayout.SOUTH);
	}
	add(this.panel2, BorderLayout.SOUTH);

	//======== popupMenu1 ========
	{

	    //---- menuItemAdicionar ----
	    this.menuItemAdicionar.setText("Adicionar"); //NOI18N
	    this.menuItemAdicionar.addActionListener(e -> {
			adicionarActionPerformed(e);
			adicionar(e);
		});
	    this.popupMenu1.add(this.menuItemAdicionar);

	    //---- menuItem2 ----
	    this.menuItem2.setText("Eliminar"); //NOI18N
	    this.popupMenu1.add(this.menuItem2);

	    //---- menuItem3 ----
	    this.menuItem3.setText("Modificar"); //NOI18N
	    this.popupMenu1.add(this.menuItem3);
	    this.popupMenu1.addSeparator();

	    //---- menuItem4 ----
	    this.menuItem4.setText("Activar"); //NOI18N
	    this.popupMenu1.add(this.menuItem4);

	    //---- menuItem5 ----
	    this.menuItem5.setText("Imprimir"); //NOI18N
	    this.popupMenu1.add(this.menuItem5);

	    //---- menuItem6 ----
	    this.menuItem6.setText("Buscar"); //NOI18N
	    this.popupMenu1.add(this.menuItem6);
	    this.popupMenu1.addSeparator();

	    //---- menuItem7 ----
	    this.menuItem7.setText("Ver Todas"); //NOI18N
	    this.popupMenu1.add(this.menuItem7);

	    //---- menuItem8 ----
	    this.menuItem8.setText("Exportar"); //NOI18N
	    this.popupMenu1.add(this.menuItem8);
	    this.popupMenu1.addSeparator();

	    //---- menuItem9 ----
	    this.menuItem9.setText("Importar"); //NOI18N
	    this.popupMenu1.add(this.menuItem9);

	    //---- menuItem10 ----
	    this.menuItem10.setText("Historia"); //NOI18N
	    this.popupMenu1.add(this.menuItem10);
	    this.popupMenu1.addSeparator();

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
    private JTable tableEmpresas;
    private JPanel panel2;
    private JScrollPane scrollPane2;
    private JTable tableUnidades;
    private JPanel panel7;
    private JLabel label2;
    private JPanel panel8;
    private JLabel labelEmpCount;
    private JPanel panel9;
    private JLabel labelUnidadesCount;
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

    private void adicionarActionPerformed(ActionEvent e) {
        adicionar(e);
    }
}
