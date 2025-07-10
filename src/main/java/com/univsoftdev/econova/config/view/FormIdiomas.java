package com.univsoftdev.econova.config.view;

import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.DialogUtils;
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
    }

    private void menuItemAdicionar(ActionEvent e) {
        DialogUtils.showModalDialog(panel1, new FormNuevoIdioma(), "Adicionar Nuevo Idioma", (ModalController mc, int i) -> {
            
        });
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	panel1 = new JPanel();
	label1 = new JLabel();
	scrollPane1 = new JScrollPane();
	table1 = new JTable();
	panel2 = new JPanel();
	label2 = new JLabel();
	popupMenu1 = new JPopupMenu();
	menuItem1 = new JMenuItem();
	menuItem3 = new JMenuItem();
	menuItem2 = new JMenuItem();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    panel1.setLayout(new BorderLayout());

	    //---- label1 ----
	    label1.setText("Idiomas");
	    panel1.add(label1, BorderLayout.WEST);
	}
	add(panel1, BorderLayout.NORTH);

	//======== scrollPane1 ========
	{

	    //---- table1 ----
	    table1.setModel(new DefaultTableModel(
		new Object[][] {
		},
		new String[] {
		    "Idioma", "Nativo"
		}
	    ));
	    {
		TableColumnModel cm = table1.getColumnModel();
		cm.getColumn(1).setPreferredWidth(50);
	    }
	    table1.setAutoCreateRowSorter(true);
	    scrollPane1.setViewportView(table1);
	}
	add(scrollPane1, BorderLayout.CENTER);

	//======== panel2 ========
	{
	    panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
	    panel2.setLayout(new BorderLayout());

	    //---- label2 ----
	    label2.setText("0");
	    panel2.add(label2, BorderLayout.WEST);
	}
	add(panel2, BorderLayout.SOUTH);

	//======== popupMenu1 ========
	{

	    //---- menuItem1 ----
	    menuItem1.setText("Adicionar");
	    menuItem1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    menuItem1.addActionListener(e -> menuItemAdicionar(e));
	    popupMenu1.add(menuItem1);

	    //---- menuItem3 ----
	    menuItem3.setText("Modificar");
	    menuItem3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    popupMenu1.add(menuItem3);

	    //---- menuItem2 ----
	    menuItem2.setText("Eliminar");
	    menuItem2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    popupMenu1.add(menuItem2);
	}

	//---- bindings ----
	bindingGroup = new BindingGroup();
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ,
	    table1, BeanProperty.create("rowCount"),
	    label2, BeanProperty.create("text")));
	bindingGroup.bind();
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
    private JMenuItem menuItem1;
    private JMenuItem menuItem3;
    private JMenuItem menuItem2;
    private BindingGroup bindingGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
