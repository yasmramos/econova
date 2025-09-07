package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Period;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.univsoftdev.econova.core.component.*;
import javax.swing.table.DefaultTableModel;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;

public class FormAdicionarPeriodo extends Modal {

    private static final long serialVersionUID = -1243416098925173479L;
    private Exercise ejercicio;
    private JTable table;
    
    public FormAdicionarPeriodo() {
        initComponents();
    }

    public FormAdicionarPeriodo(Exercise ejercicio, JTable table) {
        initComponents();
        this.table = table;
        this.ejercicio = ejercicio;
    }

    private void aceptarActionPerformed(ActionEvent e) {
        try {
            Period periodo = new Period();
            periodo.setName(textFieldNombre.getText());
            periodo.setStartDate(datePickerSwing1.getSelectedDate());
            periodo.setEndDate(datePickerSwing2.getSelectedDate());
            ejercicio.addPeriodo(periodo);
            var model = (DefaultTableModel)table.getModel();
            model.addRow(new Object[]{
                periodo.getName(),
                periodo.getStartDate().toString(), 
                periodo.getEndDate().toString()
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void cancelarActionPerformed(ActionEvent e) {
	ModalDialog.closeModal(this.getId());
    }

    private void aceptaractionPerformed(ActionEvent e) {
	
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.label1 = new JLabel();
	this.textFieldNombre = new JTextField();
	this.label2 = new JLabel();
	this.datePickerSwing1 = new DatePickerSwing();
	this.datePickerSwing2 = new DatePickerSwing();
	this.label4 = new JLabel();
	this.buttonCancelar = new JButton();
	this.buttonAceptar = new JButton();

	//======== this ========
	setBorder(new EmptyBorder(20, 20, 20, 20));

	//---- label1 ----
	this.label1.setText("Nombre"); //NOI18N

	//---- label2 ----
	this.label2.setText("Inicio"); //NOI18N

	//---- datePickerSwing1 ----
	this.datePickerSwing1.setEnabled(false);

	//---- label4 ----
	this.label4.setText("Fin"); //NOI18N

	//---- buttonCancelar ----
	this.buttonCancelar.setText("Cancelar"); //NOI18N
	this.buttonCancelar.addActionListener(e -> {
			cancelarActionPerformed(e);
			cancelarActionPerformed(e);
		});

	//---- buttonAceptar ----
	this.buttonAceptar.setText("Aceptar"); //NOI18N
	this.buttonAceptar.addActionListener(e -> {
			aceptarActionPerformed(e);
			aceptaractionPerformed(e);
		});

	GroupLayout layout = new GroupLayout(this);
	setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
			    .addComponent(this.label1)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
			    .addComponent(this.textFieldNombre, GroupLayout.PREFERRED_SIZE, 316, GroupLayout.PREFERRED_SIZE)
			    .addGap(8, 8, 8))
			.addGroup(layout.createSequentialGroup()
			    .addGap(22, 22, 22)
			    .addComponent(this.label2)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(this.datePickerSwing1, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
			    .addGap(36, 36, 36)
			    .addComponent(this.label4)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
			    .addComponent(this.datePickerSwing2, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)))
		    .addContainerGap())
		.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
		    .addContainerGap(231, Short.MAX_VALUE)
		    .addComponent(this.buttonAceptar)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(this.buttonCancelar)
		    .addGap(15, 15, 15))
	);
	layout.setVerticalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGap(13, 13, 13)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.label1)
			.addComponent(this.textFieldNombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(layout.createParallelGroup()
			.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(this.datePickerSwing1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.datePickerSwing2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(this.buttonCancelar)
				.addComponent(this.buttonAceptar))
			    .addContainerGap())
			.addGroup(layout.createSequentialGroup()
			    .addGap(18, 18, 18)
			    .addGroup(layout.createParallelGroup()
				.addComponent(this.label4)
				.addComponent(this.label2))
			    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
	);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel label1;
    private JTextField textFieldNombre;
    private JLabel label2;
    private DatePickerSwing datePickerSwing1;
    private DatePickerSwing datePickerSwing2;
    private JLabel label4;
    private JButton buttonCancelar;
    private JButton buttonAceptar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
