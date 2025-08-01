package com.univsoftdev.econova.contabilidad.views.clasificador;

import com.univsoftdev.econova.Injector;
import com.univsoftdev.econova.contabilidad.AnalisisTipoApertura;
import com.univsoftdev.econova.contabilidad.NaturalezaCuenta;
import com.univsoftdev.econova.contabilidad.TipoApertura;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.model.Moneda;
import com.univsoftdev.econova.contabilidad.service.PlanDeCuentasService;
import java.awt.*;
import java.awt.event.*;
import java.util.Currency;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import lombok.extern.slf4j.Slf4j;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;

@Slf4j
public class FormNuevaCuentaApertura extends Modal {

    private JTable table;
    private Cuenta cuenta;

    public FormNuevaCuentaApertura() {
        initComponents();
        init();
    }

    public FormNuevaCuentaApertura(JTable table, Cuenta cuenta) {
        initComponents();
        init();
        this.cuenta = cuenta;
        this.table = table;
    }

    private void btnCancelarActionPerformed(ActionEvent e) {
        ModalDialog.closeModal(this.getId());
    }

    private void btnAceptarActionPerformed(ActionEvent e) {
        try {
            String codigo = txtCodigo.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            NaturalezaCuenta naturaleza = NaturalezaCuenta.valueOf(String.valueOf(cboxNaturaleza.getSelectedItem()));

            var planDeCuentasService = Injector.get(PlanDeCuentasService.class);
            var monedaC = new Moneda("CUP", "Moneda Nacional");
            var subCuenta = new Cuenta(codigo, descripcion, naturaleza, null, monedaC);
            subCuenta.setTipoApertura(TipoApertura.SIN_APERTURA);
            subCuenta.setTipoAnalisisApertura(AnalisisTipoApertura.NINGUNO);
            cuenta.addSubCuenta(subCuenta);

            planDeCuentasService.addCuenta(cuenta);

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.addRow(new Object[]{codigo, descripcion, naturaleza, "", "Activa", monedaC.getSymbol()});

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        ModalDialog.closeModal(this.getId());
    }

    private void init() {

        Locale[] locales = {
            Locale.forLanguageTag("es-CU"),
            Locale.US
        };

        for (Locale locale : locales) {
            Currency currency = Currency.getInstance(locale);
            cboxMoneda.addItem(locale.getDisplayCountry() + ": " + currency.getDisplayName() + " (" + currency.getCurrencyCode() + ")");
        }

        NaturalezaCuenta[] naturalezaCuenta = NaturalezaCuenta.values();
        for (NaturalezaCuenta naturaleza : naturalezaCuenta) {
            cboxNaturaleza.addItem(naturaleza.name());
        }
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.this2 = new JPanel();
	this.label1 = new JLabel();
	this.label2 = new JLabel();
	this.label3 = new JLabel();
	this.label4 = new JLabel();
	this.txtCodigo = new JTextField();
	this.txtDescripcion = new JTextField();
	this.label5 = new JLabel();
	this.cboxNaturaleza = new JComboBox();
	this.cboxMoneda = new JComboBox();
	this.btnCancelar = new JButton();
	this.btnAceptar = new JButton();

	//======== this ========
	setLayout(new BorderLayout());

	//======== this2 ========
	{
	    this.this2.setBorder(new EmptyBorder(10, 10, 10, 10));

	    //---- label1 ----
	    this.label1.setText("Clave"); //NOI18N

	    //---- label2 ----
	    this.label2.setText("Descripci\u00f3n"); //NOI18N

	    //---- label3 ----
	    this.label3.setText("Naturaleza"); //NOI18N

	    //---- label4 ----
	    this.label4.setText("Moneda"); //NOI18N

	    //---- label5 ----
	    this.label5.setText("3 d\u00edgitos"); //NOI18N

	    //---- cboxNaturaleza ----
	    this.cboxNaturaleza.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	    //---- cboxMoneda ----
	    this.cboxMoneda.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	    //---- btnCancelar ----
	    this.btnCancelar.setText("Cancelar"); //NOI18N
	    this.btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.btnCancelar.addActionListener(e -> btnCancelarActionPerformed(e));

	    //---- btnAceptar ----
	    this.btnAceptar.setText("Aceptar"); //NOI18N
	    this.btnAceptar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    this.btnAceptar.addActionListener(e -> btnAceptarActionPerformed(e));

	    GroupLayout this2Layout = new GroupLayout(this.this2);
	    this2.setLayout(this2Layout);
	    this2Layout.setHorizontalGroup(
		this2Layout.createParallelGroup()
		    .addGroup(this2Layout.createSequentialGroup()
			.addGap(19, 19, 19)
			.addGroup(this2Layout.createParallelGroup()
			    .addComponent(this.label1)
			    .addComponent(this.label2)
			    .addComponent(this.label3)
			    .addComponent(this.label4))
			.addGap(26, 26, 26)
			.addGroup(this2Layout.createParallelGroup()
			    .addComponent(this.cboxNaturaleza, GroupLayout.Alignment.TRAILING)
			    .addGroup(this2Layout.createSequentialGroup()
				.addGroup(this2Layout.createParallelGroup()
				    .addGroup(this2Layout.createSequentialGroup()
					.addComponent(this.txtCodigo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(this.label5))
				    .addComponent(this.txtDescripcion, GroupLayout.PREFERRED_SIZE, 305, GroupLayout.PREFERRED_SIZE)
				    .addComponent(this.cboxMoneda, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE))
				.addGap(0, 0, Short.MAX_VALUE)))
			.addContainerGap(68, Short.MAX_VALUE))
		    .addGroup(GroupLayout.Alignment.TRAILING, this2Layout.createSequentialGroup()
			.addContainerGap(293, Short.MAX_VALUE)
			.addComponent(this.btnAceptar)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(this.btnCancelar)
			.addGap(33, 33, 33))
	    );
	    this2Layout.setVerticalGroup(
		this2Layout.createParallelGroup()
		    .addGroup(this2Layout.createSequentialGroup()
			.addGap(18, 18, 18)
			.addGroup(this2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(this.label1)
			    .addComponent(this.txtCodigo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(this.label5))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addGroup(this2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(this.label2)
			    .addComponent(this.txtDescripcion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
			.addGroup(this2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(this.label3)
			    .addComponent(this.cboxNaturaleza, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addGap(18, 18, 18)
			.addGroup(this2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(this.label4)
			    .addComponent(this.cboxMoneda, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addGap(32, 32, 32)
			.addGroup(this2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(this.btnAceptar)
			    .addComponent(this.btnCancelar))
			.addGap(31, 31, 31))
	    );
	}
	add(this.this2, BorderLayout.CENTER);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel this2;
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
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
