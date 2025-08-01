package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.Injector;
import com.univsoftdev.econova.config.service.MonedaService;
import com.univsoftdev.econova.contabilidad.model.Moneda;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.DialogUtils;
import com.univsoftdev.econova.core.utils.table.TableColumnAdjuster;
import java.awt.*;
import javax.swing.*;

public class FormMonedas extends Form {

    private static final long serialVersionUID = -3496353075731804293L;
    MonedaService monedaService;

    public FormMonedas() {
        initComponents();
        DefaultTableModel model = (DefaultTableModel) tableMonedas.getModel();
        model.setRowCount(0);

        monedaService = Injector.get(MonedaService.class);
        java.util.List<Moneda> availableCurrencies = monedaService.findAll();
        for (Moneda moneda : availableCurrencies) {
            String displayName = moneda.getDisplayName();
            String symbol = moneda.getSymbol();
            model.addRow(new Object[]{symbol, displayName});
        }

        TableColumnAdjuster adjuster = new TableColumnAdjuster(tableMonedas);
        adjuster.adjustColumns();
    }

    private void adicionarMoneda(ActionEvent e) {
        DialogUtils.showModalDialog(this, new FormAdicionarMoneda(monedaService), "Adicionar Nueva Moneda");
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.scrollPane1 = new JScrollPane();
	this.tableMonedas = new JTable();
	this.popupMenu1 = new JPopupMenu();
	this.menuItemAdicionarMoneda = new JMenuItem();
	this.menuItem1 = new JMenuItem();
	this.menuItem2 = new JMenuItem();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    this.panel1.setLayout(new BorderLayout());

	    //---- label1 ----
	    this.label1.setText("Monedas"); //NOI18N
	    this.panel1.add(this.label1, BorderLayout.WEST);
	}
	add(this.panel1, BorderLayout.NORTH);

	//======== scrollPane1 ========
	{
	    this.scrollPane1.setComponentPopupMenu(this.popupMenu1);

	    //---- tableMonedas ----
	    this.tableMonedas.setModel(new DefaultTableModel(
		new Object[][] {
		    {null, null},
		},
		new String[] {
		    "Simbolo", "Nombre" //NOI18N
		}
	    ));
	    {
		TableColumnModel cm = this.tableMonedas.getColumnModel();
		cm.getColumn(0).setPreferredWidth(60);
		cm.getColumn(1).setPreferredWidth(340);
	    }
	    this.tableMonedas.setComponentPopupMenu(this.popupMenu1);
	    this.tableMonedas.setFillsViewportHeight(true);
	    this.tableMonedas.setAutoCreateRowSorter(true);
	    this.scrollPane1.setViewportView(this.tableMonedas);
	}
	add(this.scrollPane1, BorderLayout.CENTER);

	//======== popupMenu1 ========
	{

	    //---- menuItemAdicionarMoneda ----
	    this.menuItemAdicionarMoneda.setText("Adicionar"); //NOI18N
	    this.menuItemAdicionarMoneda.setToolTipText("Adiciona una nueva moneda"); //NOI18N
	    this.menuItemAdicionarMoneda.addActionListener(e -> adicionarMoneda(e));
	    this.popupMenu1.add(this.menuItemAdicionarMoneda);

	    //---- menuItem1 ----
	    this.menuItem1.setText("Eliminar"); //NOI18N
	    this.menuItem1.setToolTipText("Elimina la moneda seleccionada"); //NOI18N
	    this.popupMenu1.add(this.menuItem1);

	    //---- menuItem2 ----
	    this.menuItem2.setText("Modificar"); //NOI18N
	    this.menuItem2.setToolTipText("Modifica la moneda seleccionada"); //NOI18N
	    this.popupMenu1.add(this.menuItem2);
	}
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTable tableMonedas;
    private JPopupMenu popupMenu1;
    private JMenuItem menuItemAdicionarMoneda;
    private JMenuItem menuItem1;
    private JMenuItem menuItem2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
