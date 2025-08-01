package com.univsoftdev.econova.config.view;

import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.DialogUtils;
import com.univsoftdev.econova.core.utils.table.TableColumnAdjuster;
import java.awt.*;
import java.util.Locale;
import javax.swing.*;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import raven.modal.listener.ModalController;

public class FormIdiomas extends Form {

    private static final long serialVersionUID = 6346716612720387414L;

    public FormIdiomas() {
        initComponents();
        Locale[] availableLocales = Locale.getAvailableLocales();
        TableColumnAdjuster adjuster = new TableColumnAdjuster(tableIdiomas);
        adjuster.adjustColumns();
    }

    private void menuItemAdicionar(ActionEvent e) {
        DialogUtils.showModalDialog(panel1, new FormNuevoIdioma(), "Adicionar Nuevo Idioma", (ModalController mc, int i) -> {
            
        });
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.scrollPane1 = new JScrollPane();
	this.tableIdiomas = new JTable();
	this.panel2 = new JPanel();
	this.label2 = new JLabel();
	this.popupMenu1 = new JPopupMenu();
	this.menuItem1 = new JMenuItem();
	this.menuItem3 = new JMenuItem();
	this.menuItem2 = new JMenuItem();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    this.panel1.setLayout(new BorderLayout());

	    //---- label1 ----
	    this.label1.setText("Idiomas"); //NOI18N
	    this.panel1.add(this.label1, BorderLayout.WEST);
	}
	add(this.panel1, BorderLayout.NORTH);

	//======== scrollPane1 ========
	{

	    //---- tableIdiomas ----
	    this.tableIdiomas.setModel(new DefaultTableModel(
		new Object[][] {
		},
		new String[] {
		    "Idioma", "Nativo" //NOI18N
		}
	    ));
	    {
		TableColumnModel cm = this.tableIdiomas.getColumnModel();
		cm.getColumn(1).setPreferredWidth(50);
	    }
	    this.tableIdiomas.setAutoCreateRowSorter(true);
	    this.scrollPane1.setViewportView(this.tableIdiomas);
	}
	add(this.scrollPane1, BorderLayout.CENTER);

	//======== panel2 ========
	{
	    this.panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
	    this.panel2.setLayout(new BorderLayout());

	    //---- label2 ----
	    this.label2.setText("0"); //NOI18N
	    this.panel2.add(this.label2, BorderLayout.WEST);
	}
	add(this.panel2, BorderLayout.SOUTH);

	//======== popupMenu1 ========
	{

	    //---- menuItem1 ----
	    this.menuItem1.setText("Adicionar"); //NOI18N
	    this.menuItem1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.menuItem1.addActionListener(e -> menuItemAdicionar(e));
	    this.popupMenu1.add(this.menuItem1);

	    //---- menuItem3 ----
	    this.menuItem3.setText("Modificar"); //NOI18N
	    this.menuItem3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.popupMenu1.add(this.menuItem3);

	    //---- menuItem2 ----
	    this.menuItem2.setText("Eliminar"); //NOI18N
	    this.menuItem2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.popupMenu1.add(this.menuItem2);
	}

	//---- bindings ----
	this.bindingGroup = new BindingGroup();
	this.bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ,
	    this.tableIdiomas, BeanProperty.create("rowCount"), //NOI18N
	    this.label2, BeanProperty.create("text"))); //NOI18N
	this.bindingGroup.bind();
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTable tableIdiomas;
    private JPanel panel2;
    private JLabel label2;
    private JPopupMenu popupMenu1;
    private JMenuItem menuItem1;
    private JMenuItem menuItem3;
    private JMenuItem menuItem2;
    private BindingGroup bindingGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
