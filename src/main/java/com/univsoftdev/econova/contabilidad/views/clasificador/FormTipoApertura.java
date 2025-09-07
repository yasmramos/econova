package com.univsoftdev.econova.contabilidad.views.clasificador;

import com.univsoftdev.econova.contabilidad.OpeningTypeAnalysis;
import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.TypeOfOpening;
import com.univsoftdev.econova.contabilidad.service.PlanDeCuentasService;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.univsoftdev.econova.core.component.*;
import com.univsoftdev.econova.core.utils.DialogUtils;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;

public class FormTipoApertura extends Modal {

    private static final long serialVersionUID = 7001209952064907430L;
    private JTree tree;
    private PlanDeCuentasService planDeCuentas;
    private Account cuenta;

    public FormTipoApertura() {
        initComponents();
        init();
    }

    public FormTipoApertura(JTree tree, PlanDeCuentasService planDeCuentas, Account cuenta) {
        initComponents();
        init();
        this.tree = tree;
        this.planDeCuentas = planDeCuentas;
        this.cuenta = cuenta;
        if (cuenta.getAccountFather() != null) {
            TypeOfOpening tipoApertura = cuenta.getAccountFather().getTypeOfOpening();
            if (null != tipoApertura) {
                switch (tipoApertura) {
                    case SUBCUENTA ->
                        comboBoxNivel.removeItem(TypeOfOpening.SUBCUENTA.toString());
                    case CONTROL -> {
                        comboBoxNivel.removeItem(TypeOfOpening.SUBCUENTA.toString());
                        comboBoxNivel.removeItem(TypeOfOpening.CONTROL.toString());
                    }
                    case ANALISIS -> {
                        comboBoxNivel.removeItem(TypeOfOpening.SUBCUENTA.toString());
                        comboBoxNivel.removeItem(TypeOfOpening.CONTROL.toString());
                        comboBoxNivel.removeItem(TypeOfOpening.ANALISIS.toString());
                    }
                    default -> {
                    }
                }
            }
        }
    }

    private void init() {

        comboBoxAnalisis.removeAllItems();
        comboBoxNivel.removeAllItems();

        TypeOfOpening[] values = TypeOfOpening.values();
        for (TypeOfOpening value : values) {
            comboBoxNivel.addItem(value.toString());
        }

        OpeningTypeAnalysis[] analisiTipoApertura = OpeningTypeAnalysis.values();
        for (OpeningTypeAnalysis analisiTipoApertura1 : analisiTipoApertura) {
            comboBoxAnalisis.addItem(analisiTipoApertura1.toString());
        }
    }

    private void buttonAceptarActionPerformed(ActionEvent e) {

        String nameTipoApertura = String.valueOf(comboBoxNivel.getSelectedItem()).split(" ")[0];
        String nameAnalisisTipoApertura = String.valueOf(comboBoxAnalisis.getSelectedItem()).split(" ")[0];

        TypeOfOpening tipoApertura = TypeOfOpening.valueOf(nameTipoApertura);
        OpeningTypeAnalysis analisisTipoApertura = OpeningTypeAnalysis.valueOf(nameAnalisisTipoApertura.toUpperCase());

        cuenta.setTypeOfOpening(tipoApertura);
        cuenta.setOpeningTypeAnalysis(analisisTipoApertura);
        cuenta.setOpening(true);
        
        DialogUtils.showModalDialog(this, new FormAperturaCuenta(this.tree, this.planDeCuentas, this.cuenta), "Aperturas de la Cuenta");
        ModalDialog.closeModal(this.getId());

    }

    private void buttonCancelarActionPerformed(ActionEvent e) {
        ModalDialog.closeModal(this.getId());
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.label1 = new JLabel();
	this.label2 = new JLabel();
	this.label3 = new JLabel();
	this.buttonCancelar = new EButton();
	this.buttonAceptar = new EButton();
	this.comboBoxNivel = new JComboBox();
	this.comboBoxAnalisis = new JComboBox();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));

	//---- label1 ----
	this.label1.setText("Apertura por"); //NOI18N

	//---- label2 ----
	this.label2.setText("Nivel"); //NOI18N

	//---- label3 ----
	this.label3.setText("An\u00e1lisis"); //NOI18N

	//---- buttonCancelar ----
	this.buttonCancelar.setText("Cancelar"); //NOI18N
	this.buttonCancelar.addActionListener(e -> buttonCancelarActionPerformed(e));

	//---- buttonAceptar ----
	this.buttonAceptar.setText("Aceptar"); //NOI18N
	this.buttonAceptar.addActionListener(e -> buttonAceptarActionPerformed(e));

	GroupLayout layout = new GroupLayout(this);
	setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
			    .addGap(92, 92, 92)
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
				.addGroup(layout.createSequentialGroup()
				    .addComponent(this.buttonAceptar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(this.buttonCancelar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
				    .addGroup(layout.createParallelGroup()
					.addComponent(this.label2)
					.addComponent(this.label3))
				    .addGap(15, 15, 15)
				    .addGroup(layout.createParallelGroup()
					.addComponent(this.comboBoxAnalisis, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
					.addComponent(this.comboBoxNivel)))))
			.addGroup(layout.createSequentialGroup()
			    .addGap(22, 22, 22)
			    .addComponent(this.label1)))
		    .addContainerGap(18, Short.MAX_VALUE))
	);
	layout.setVerticalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGap(17, 17, 17)
		    .addComponent(this.label1)
		    .addGap(18, 18, 18)
		    .addGroup(layout.createParallelGroup()
			.addComponent(this.label2)
			.addComponent(this.comboBoxNivel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.label3)
			.addComponent(this.comboBoxAnalisis, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.buttonCancelar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(this.buttonAceptar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
	);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private EButton buttonCancelar;
    private EButton buttonAceptar;
    private JComboBox comboBoxNivel;
    private JComboBox comboBoxAnalisis;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
