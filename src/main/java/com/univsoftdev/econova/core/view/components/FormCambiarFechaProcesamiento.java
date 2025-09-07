package com.univsoftdev.econova.core.view.components;

import com.univsoftdev.econova.contabilidad.SubSystem;
import java.awt.event.*;
import javax.swing.*;
import com.univsoftdev.econova.core.component.*;
import io.avaje.config.Config;
import java.time.LocalDate;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;

public class FormCambiarFechaProcesamiento extends Modal {

    public FormCambiarFechaProcesamiento() {
        initComponents();
        datePickerFechaProcesamiento.setSelectedDate(Config.getAs("econova.accounting.current.date", (value) -> {
            return LocalDate.parse(value);
        }));
    }

    private void btnCancelar(ActionEvent e) {
        ModalDialog.closeModal(this.getId());
    }

    private void btnAceptar(ActionEvent e) {
        var fecha = datePickerFechaProcesamiento.getSelectedDate();
        SubSystem subsistema = Config.getEnum(SubSystem.class, "econova.current.subsistema");
        if (subsistema == SubSystem.CONTABILIDAD) {
            Config.setProperty("econova.accounting.current.date", fecha.toString());
        }
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.datePickerFechaProcesamiento = new DatePickerSwing();
	this.btnCancelar = new JButton();
	this.btnAceptar = new JButton();
	this.label1 = new JLabel();

	//======== this ========

	//---- btnCancelar ----
	this.btnCancelar.setText("Cancelar"); //NOI18N
	this.btnCancelar.addActionListener(e -> btnCancelar(e));

	//---- btnAceptar ----
	this.btnAceptar.setText("Aceptar"); //NOI18N
	this.btnAceptar.addActionListener(e -> btnAceptar(e));

	//---- label1 ----
	this.label1.setText("Cambiar fecha de procesamiento"); //NOI18N

	GroupLayout layout = new GroupLayout(this);
	setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
			.addComponent(this.datePickerFechaProcesamiento, GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
			.addGroup(layout.createSequentialGroup()
			    .addGap(0, 205, Short.MAX_VALUE)
			    .addComponent(this.btnAceptar)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			    .addComponent(this.btnCancelar)))
		    .addGap(24, 24, 24))
		.addGroup(layout.createSequentialGroup()
		    .addGap(15, 15, 15)
		    .addComponent(this.label1)
		    .addContainerGap(205, Short.MAX_VALUE))
	);
	layout.setVerticalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGap(32, 32, 32)
		    .addComponent(this.label1)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(this.datePickerFechaProcesamiento, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.btnCancelar)
			.addComponent(this.btnAceptar))
		    .addGap(14, 14, 14))
	);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private DatePickerSwing datePickerFechaProcesamiento;
    private JButton btnCancelar;
    private JButton btnAceptar;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
