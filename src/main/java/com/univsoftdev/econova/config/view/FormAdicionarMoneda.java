package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.config.service.MonedaService;
import com.univsoftdev.econova.contabilidad.model.Moneda;
import java.awt.event.*;
import java.util.Currency;
import javax.swing.*;
import javax.swing.border.*;
import com.univsoftdev.econova.core.component.*;
import java.util.Optional;
import java.util.Set;
import raven.modal.component.Modal;

public class FormAdicionarMoneda extends Modal {

    private static final long serialVersionUID = 1816596630027194555L;
    MonedaService monedaService;

    public FormAdicionarMoneda(MonedaService monedaService) {
        initComponents();
        this.monedaService = monedaService;
        comboBoxNombre.removeAllItems();

        Set<Currency> availableCurrencies = Currency.getAvailableCurrencies();
        for (Currency currency : availableCurrencies) {
            String displayName = currency.getDisplayName();
            comboBoxNombre.addItem(displayName);
        }
    }

    private void comboBoxNombreItemStateChanged(ItemEvent e) {
        var nombre = String.valueOf(comboBoxNombre.getSelectedItem());
        Optional<Moneda> findBy = monedaService.findBy("displayName", nombre);
        if (findBy.isPresent()) {
            formattedTextFieldSigla.setText(findBy.get().getSymbol());
        }
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.label1 = new JLabel();
	this.label2 = new JLabel();
	this.formattedTextFieldSigla = new JFormattedTextField();
	this.comboBoxNombre = new JComboBox();
	this.eButton1 = new EButton();
	this.eButton2 = new EButton();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));

	//---- label1 ----
	this.label1.setText("Nombre"); //NOI18N

	//---- label2 ----
	this.label2.setText("Sigla"); //NOI18N

	//---- comboBoxNombre ----
	this.comboBoxNombre.addItemListener(e -> comboBoxNombreItemStateChanged(e));

	//---- eButton1 ----
	this.eButton1.setText("Cancelar"); //NOI18N

	//---- eButton2 ----
	this.eButton2.setText("Aceptar"); //NOI18N

	GroupLayout layout = new GroupLayout(this);
	setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGap(33, 33, 33)
		    .addGroup(layout.createParallelGroup()
			.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
			    .addGap(0, 0, Short.MAX_VALUE)
			    .addComponent(this.eButton2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(this.eButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addComponent(this.formattedTextFieldSigla)
			.addGroup(layout.createSequentialGroup()
			    .addGroup(layout.createParallelGroup()
				.addComponent(this.label1)
				.addComponent(this.label2)
				.addComponent(this.comboBoxNombre, GroupLayout.PREFERRED_SIZE, 374, GroupLayout.PREFERRED_SIZE))
			    .addGap(0, 0, Short.MAX_VALUE)))
		    .addContainerGap(33, Short.MAX_VALUE))
	);
	layout.setVerticalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGap(39, 39, 39)
		    .addComponent(this.label1)
		    .addGap(13, 13, 13)
		    .addComponent(this.comboBoxNombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addComponent(this.label2)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addComponent(this.formattedTextFieldSigla, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.eButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(this.eButton2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addContainerGap())
	);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel label1;
    private JLabel label2;
    private JFormattedTextField formattedTextFieldSigla;
    private JComboBox comboBoxNombre;
    private EButton eButton1;
    private EButton eButton2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
