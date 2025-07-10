package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.config.service.MonedaService;
import com.univsoftdev.econova.contabilidad.model.Moneda;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.DialogUtils;
import io.avaje.inject.BeanScope;
import java.awt.*;
import javax.swing.*;

public class FormMonedas extends Form {

    private static final long serialVersionUID = -3496353075731804293L;
    BeanScope injector = AppContext.getInstance().getInjector();
    MonedaService monedaService;

    public FormMonedas() {
        initComponents();
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setRowCount(0);
        
        monedaService = injector.get(MonedaService.class);
        java.util.List<Moneda> availableCurrencies = monedaService.findAll();
        for (Moneda currency : availableCurrencies) {
            String displayName = currency.getDisplayName();
            String symbol = currency.getSymbol();
            model.addRow(new Object[]{symbol, displayName});
        }

    }

    private void adicionarMoneda(ActionEvent e) {
        DialogUtils.showModalDialog(this, new FormAdicionarMoneda(monedaService), "Adicionar Nueva Moneda");
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.scrollPane1 = new JScrollPane();
	this.table1 = new JTable();
	this.popupMenu1 = new JPopupMenu();
	this.menuItemAdicionarMoneda = new JMenuItem();

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

	    //---- table1 ----
	    this.table1.setModel(new DefaultTableModel(
		new Object[][] {
		    {null, null},
		},
		new String[] {
		    "Simbolo", "Nombre" //NOI18N
		}
	    ));
	    {
		TableColumnModel cm = this.table1.getColumnModel();
		cm.getColumn(0).setPreferredWidth(60);
		cm.getColumn(1).setPreferredWidth(340);
	    }
	    this.table1.setComponentPopupMenu(this.popupMenu1);
	    this.table1.setFillsViewportHeight(true);
	    this.table1.setAutoCreateRowSorter(true);
	    this.scrollPane1.setViewportView(this.table1);
	}
	add(this.scrollPane1, BorderLayout.CENTER);

	//======== popupMenu1 ========
	{

	    //---- menuItemAdicionarMoneda ----
	    this.menuItemAdicionarMoneda.setText("Adicionar Moneda"); //NOI18N
	    this.menuItemAdicionarMoneda.addActionListener(e -> adicionarMoneda(e));
	    this.popupMenu1.add(this.menuItemAdicionarMoneda);
	}
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JPopupMenu popupMenu1;
    private JMenuItem menuItemAdicionarMoneda;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
