package com.univsoftdev.econova.contabilidad.views.clasificador;

import com.univsoftdev.econova.contabilidad.AccountType;
import com.univsoftdev.econova.contabilidad.NatureOfAccount;
import com.univsoftdev.econova.contabilidad.OpeningTypeAnalysis;
import com.univsoftdev.econova.contabilidad.TypeOfOpening;
import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.Currency;
import com.univsoftdev.econova.contabilidad.service.PlanDeCuentasService;
import com.univsoftdev.econova.core.Injector;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;

public class FormNuevaCuenta extends Modal {

    private static final long serialVersionUID = 4148928387045554238L;
    private static final Logger LOGGER = LoggerFactory.getLogger(FormNuevaCuenta.class);
    private JTable table;
    private Account cuenta;

    public FormNuevaCuenta() {
        initComponents();
        init();
    }

    public FormNuevaCuenta(JTable table) {
        initComponents();
        init();
        this.table = table;
    }

    private void btnAceptarActionPerformed(ActionEvent e) {
        try {
            String codigo = txtCodigo.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            NatureOfAccount naturaleza = NatureOfAccount.valueOf(String.valueOf(cboxNaturaleza.getSelectedItem()));
            AccountType categoria = AccountType.valueOf(String.valueOf(comboBoxTipoCuenta.getSelectedItem()));
            Currency monedaC = new Currency("CUP", "Moneda Cubana");

            var planDeCuentasService = Injector.get(PlanDeCuentasService.class);
            
            cuenta = new Account(codigo, descripcion, naturaleza, categoria, monedaC);
            cuenta.setTypeOfOpening(TypeOfOpening.SIN_APERTURA);
            cuenta.setOpeningTypeAnalysis(OpeningTypeAnalysis.NINGUNO);
            
            planDeCuentasService.addCuenta(cuenta);
            
            this.putClientProperty("cuenta", cuenta);
            
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.addRow(new Object[]{codigo, descripcion, naturaleza, categoria, "Activa", monedaC.getSymbol()});

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        ModalDialog.closeModal(this.getId());
    }

    private void btnCancelarActionPerformed(ActionEvent e) {
        ModalDialog.closeModal(this.getId());
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	label1 = new JLabel();
	label2 = new JLabel();
	label3 = new JLabel();
	label4 = new JLabel();
	txtCodigo = new JTextField();
	txtDescripcion = new JTextField();
	label5 = new JLabel();
	cboxNaturaleza = new JComboBox();
	cboxMoneda = new JComboBox();
	btnCancelar = new JButton();
	btnAceptar = new JButton();
	label6 = new JLabel();
	comboBoxTipoCuenta = new JComboBox();

	//======== this ========
	setBorder(new EmptyBorder(10, 10, 10, 10));

	//---- label1 ----
	label1.setText("Clave");

	//---- label2 ----
	label2.setText("Descripci\u00f3n");

	//---- label3 ----
	label3.setText("Naturaleza");

	//---- label4 ----
	label4.setText("Moneda");

	//---- label5 ----
	label5.setText("3 d\u00edgitos");

	//---- cboxNaturaleza ----
	cboxNaturaleza.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	//---- cboxMoneda ----
	cboxMoneda.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	//---- btnCancelar ----
	btnCancelar.setText("Cancelar");
	btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	btnCancelar.addActionListener(e -> btnCancelarActionPerformed(e));

	//---- btnAceptar ----
	btnAceptar.setText("Aceptar");
	btnAceptar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	btnAceptar.addActionListener(e -> btnAceptarActionPerformed(e));

	//---- label6 ----
	label6.setText("Tipo");

	GroupLayout layout = new GroupLayout(this);
	setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGap(19, 19, 19)
		    .addGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
			    .addGap(0, 226, Short.MAX_VALUE)
			    .addComponent(btnAceptar)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(btnCancelar)
			    .addGap(37, 37, 37))
			.addGroup(layout.createSequentialGroup()
			    .addGroup(layout.createParallelGroup()
				.addComponent(label1)
				.addComponent(label2)
				.addComponent(label4)
				.addComponent(label6)
				.addComponent(label3))
			    .addGap(26, 26, 26)
			    .addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
				    .addComponent(txtCodigo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addComponent(label5))
				.addComponent(txtDescripcion, GroupLayout.PREFERRED_SIZE, 305, GroupLayout.PREFERRED_SIZE)
				.addComponent(cboxMoneda, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
				.addComponent(comboBoxTipoCuenta, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
				.addComponent(cboxNaturaleza, GroupLayout.Alignment.TRAILING))
			    .addContainerGap(26, Short.MAX_VALUE))))
	);
	layout.setVerticalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGap(18, 18, 18)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(label1)
			.addComponent(txtCodigo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(label5))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(label2)
			.addComponent(txtDescripcion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(label3)
			.addComponent(cboxNaturaleza, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGap(18, 18, 18)
		    .addGroup(layout.createParallelGroup()
			.addComponent(label6, GroupLayout.Alignment.TRAILING)
			.addComponent(comboBoxTipoCuenta, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addGroup(layout.createParallelGroup()
			.addComponent(label4)
			.addComponent(cboxMoneda, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGap(26, 26, 26)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(btnCancelar)
			.addComponent(btnAceptar)))
	);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JTextField txtCodigo;
    private JTextField txtDescripcion;
    private JLabel label5;
    private JComboBox cboxNaturaleza;
    private JComboBox cboxMoneda;
    private JButton btnCancelar;
    private JButton btnAceptar;
    private JLabel label6;
    private JComboBox comboBoxTipoCuenta;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void init() {
        Locale[] locales = {Locale.forLanguageTag("es-CU"), Locale.US};
        for (Locale locale : locales) {
            java.util.Currency currency = java.util.Currency.getInstance(locale);
            cboxMoneda.addItem(locale.getDisplayCountry() + ": " + currency.getDisplayName() + " (" + currency.getCurrencyCode() + ")");
        }
        AccountType[] tiposDeCuenta = AccountType.values();
        for (AccountType tipoCuenta : tiposDeCuenta) {
            comboBoxTipoCuenta.addItem(tipoCuenta.name());
        }

        NatureOfAccount[] naturalezaCuenta = NatureOfAccount.values();
        for (NatureOfAccount naturaleza : naturalezaCuenta) {
            cboxNaturaleza.addItem(naturaleza.name());
        }
    }

    public Account getCuenta() {
        return cuenta;
    }

    public void setCuenta(Account cuenta) {
        this.cuenta = cuenta;
    }

}
