package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.config.model.Idioma;
import com.univsoftdev.econova.config.service.IdiomaService;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import com.univsoftdev.econova.core.component.*;
import raven.modal.component.Modal;

public class FormAdicionarIdioma extends Modal {

    private static final long serialVersionUID = 6032278052868283579L;
    private final IdiomaService idiomaService;

    public FormAdicionarIdioma() {
        initComponents();
        this.idiomaService = AppContext.getInstance().getInjector().get(IdiomaService.class);
        final Locale[] availableLocales = Locale.getAvailableLocales();
        for (final Locale availableLocale : availableLocales) {
            comboBoxIdiomas.addItem(availableLocale.getCountry());
        }
    }

    private void buttonAceptar(ActionEvent e) {
        final Idioma idioma = new Idioma();
        idioma.setPais(String.valueOf(comboBoxIdiomas.getSelectedItem()));
        idiomaService.save(idioma);
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.label2 = new JLabel();
	this.comboBoxIdiomas = new JComboBox();
	this.buttonCancelar = new EButton();
	this.buttonAceptar = new EButton();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));

	//======== panel1 ========
	{
	    this.panel1.setLayout(new BorderLayout());

	    //---- label1 ----
	    this.label1.setText("Idiomas"); //NOI18N
	    this.panel1.add(this.label1, BorderLayout.WEST);
	}

	//---- label2 ----
	this.label2.setText("Selecciona un Idioma"); //NOI18N

	//---- buttonCancelar ----
	this.buttonCancelar.setText("Cancelar"); //NOI18N

	//---- buttonAceptar ----
	this.buttonAceptar.setText("Aceptar"); //NOI18N
	this.buttonAceptar.addActionListener(e -> buttonAceptar(e));

	GroupLayout layout = new GroupLayout(this);
	setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createParallelGroup()
		    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(this.buttonAceptar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(this.buttonCancelar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addGap(95, 95, 95))
		    .addGroup(layout.createSequentialGroup()
			.addGap(24, 24, 24)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
			    .addGroup(layout.createSequentialGroup()
				.addComponent(this.label2)
				.addGap(253, 253, 253))
			    .addGroup(layout.createSequentialGroup()
				.addComponent(this.comboBoxIdiomas)
				.addGap(95, 95, 95)))))
		.addGroup(layout.createSequentialGroup()
		    .addComponent(this.panel1, GroupLayout.PREFERRED_SIZE, 390, GroupLayout.PREFERRED_SIZE)
		    .addContainerGap())
	);
	layout.setVerticalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addComponent(this.panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addGap(18, 18, 18)
		    .addComponent(this.label2)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
		    .addComponent(this.comboBoxIdiomas, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.buttonAceptar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(this.buttonCancelar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGap(14, 14, 14))
	);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JLabel label2;
    private JComboBox comboBoxIdiomas;
    private EButton buttonCancelar;
    private EButton buttonAceptar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
