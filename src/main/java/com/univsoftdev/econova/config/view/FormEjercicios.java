package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.core.Injector;
import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.service.EjercicioService;
import java.beans.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.DialogUtils;
import com.univsoftdev.econova.core.utils.table.TableColumnAdjuster;
import java.awt.*;
import java.util.Optional;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormEjercicios extends Form {

    private static final long serialVersionUID = -618088926103956911L;
    private final transient EjercicioService ejercicioService;

    public FormEjercicios() {
        initComponents();
        FormEjerciciosUtil.setupTableSelectionListener(tableEjercicios, tablePeriodos);
        updateView();
        ejercicioService = Injector.get(EjercicioService.class);
    }

    private void adicionarActionPerformed(ActionEvent e) {
        DialogUtils.showModalDialog(this, new FormNuevoEjercicioEstandar(tableEjercicios, tablePeriodos), "Nuevo ejercicio");
    }

    private void eliminarActionPerformed(ActionEvent e) {
        try {
            Optional<Exercise> findByNombre = getSelectedEjercicio();
            if (findByNombre.isPresent()) {
                ejercicioService.delete(findByNombre.get());
            }
        } catch (Exception ex) {
            log.error("No se pudo eliminar el ejercicio: " + ex.getMessage());
        }
    }

    private Optional<Exercise> getSelectedEjercicio() {
        int selectedRow = tableEjercicios.getSelectedRow();
        int column = 0;
        Object valueAt = tableEjercicios.getValueAt(selectedRow, column);
        return ejercicioService.findByNombre(String.valueOf(valueAt));
    }

    private void modificarActionPerformed(ActionEvent e) {
        Optional<Exercise> selectedEjercicio = getSelectedEjercicio();
        if (selectedEjercicio.isPresent()) {

        }
    }

    private void table1PropertyChange(PropertyChangeEvent e) {
        updateView();
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.scrollPane1 = new JScrollPane();
	this.tableEjercicios = new JTable();
	this.panel2 = new JPanel();
	this.scrollPane2 = new JScrollPane();
	this.tablePeriodos = new JTable();
	this.popupMenu1 = new JPopupMenu();
	this.menuItemAdicionar = new JMenuItem();
	this.menuItemEliminar = new JMenuItem();
	this.menuItemModificar = new JMenuItem();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    this.panel1.setPreferredSize(new Dimension(400, 25));

	    //---- label1 ----
	    this.label1.setText("EJERCICIOS"); //NOI18N

	    GroupLayout panel1Layout = new GroupLayout(this.panel1);
	    panel1.setLayout(panel1Layout);
	    panel1Layout.setHorizontalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(this.label1)
			.addContainerGap(754, Short.MAX_VALUE))
	    );
	    panel1Layout.setVerticalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
			.addGap(0, 9, Short.MAX_VALUE)
			.addComponent(this.label1))
	    );
	}
	add(this.panel1, BorderLayout.NORTH);

	//======== scrollPane1 ========
	{
	    this.scrollPane1.setComponentPopupMenu(this.popupMenu1);

	    //---- tableEjercicios ----
	    this.tableEjercicios.setModel(new DefaultTableModel(
		new Object[][] {
		},
		new String[] {
		    "Nombre", "Inicio", "Fin" //NOI18N
		}
	    ));
	    this.tableEjercicios.setComponentPopupMenu(this.popupMenu1);
	    this.tableEjercicios.setAutoCreateRowSorter(true);
	    this.tableEjercicios.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	    this.tableEjercicios.setFillsViewportHeight(true);
	    this.tableEjercicios.addPropertyChangeListener("selectedRow", e -> table1PropertyChange(e)); //NOI18N
	    this.scrollPane1.setViewportView(this.tableEjercicios);
	}
	add(this.scrollPane1, BorderLayout.CENTER);

	//======== panel2 ========
	{
	    this.panel2.setBorder(new TitledBorder("Per\u00edodos")); //NOI18N
	    this.panel2.setLayout(new BorderLayout());

	    //======== scrollPane2 ========
	    {

		//---- tablePeriodos ----
		this.tablePeriodos.setModel(new DefaultTableModel(
		    new Object[][] {
			{null, null, null},
			{null, null, null},
		    },
		    new String[] {
			"Nombre", "Inicio", "Fin" //NOI18N
		    }
		));
		this.tablePeriodos.setFillsViewportHeight(true);
		this.scrollPane2.setViewportView(this.tablePeriodos);
	    }
	    this.panel2.add(this.scrollPane2, BorderLayout.CENTER);
	}
	add(this.panel2, BorderLayout.SOUTH);

	//======== popupMenu1 ========
	{

	    //---- menuItemAdicionar ----
	    this.menuItemAdicionar.setText("Adicionar"); //NOI18N
	    this.menuItemAdicionar.addActionListener(e -> adicionarActionPerformed(e));
	    this.popupMenu1.add(this.menuItemAdicionar);

	    //---- menuItemEliminar ----
	    this.menuItemEliminar.setText("Eliminar"); //NOI18N
	    this.menuItemEliminar.addActionListener(e -> eliminarActionPerformed(e));
	    this.popupMenu1.add(this.menuItemEliminar);

	    //---- menuItemModificar ----
	    this.menuItemModificar.setText("Modificar"); //NOI18N
	    this.menuItemModificar.addActionListener(e -> modificarActionPerformed(e));
	    this.popupMenu1.add(this.menuItemModificar);
	}
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTable tableEjercicios;
    private JPanel panel2;
    private JScrollPane scrollPane2;
    private JTable tablePeriodos;
    private JPopupMenu popupMenu1;
    private JMenuItem menuItemAdicionar;
    private JMenuItem menuItemEliminar;
    private JMenuItem menuItemModificar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void updateView() {
        SwingUtilities.invokeLater(() -> {
            FormEjerciciosUtil.updateView(tableEjercicios, tablePeriodos);
            new TableColumnAdjuster(tablePeriodos).adjustColumns();
            new TableColumnAdjuster(tableEjercicios).adjustColumns();
        });
    }
}
