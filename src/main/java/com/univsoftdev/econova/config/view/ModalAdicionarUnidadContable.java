package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.Injector;
import com.univsoftdev.econova.ebean.config.MyTenantSchemaProvider;
import com.univsoftdev.econova.config.model.Empresa;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.config.service.EmpresaService;
import com.univsoftdev.econova.core.simple.SimpleMessageModal;
import jakarta.inject.Inject;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;
import raven.modal.component.SimpleModalBorder;
import raven.modal.option.Option;

public class ModalAdicionarUnidadContable extends Modal {

    private static final long serialVersionUID = 2095217302656241995L;
    private JTable table;
    private Empresa empresa;
    
    @Inject
    MyTenantSchemaProvider tenantSchemaProvider;

    public ModalAdicionarUnidadContable() {
        initComponents();
    }

    public ModalAdicionarUnidadContable(JTable table1, Empresa empresa) {
        initComponents();
        this.table = table1;
        this.empresa = empresa;
    }

    private void cancelarActionPerformed(ActionEvent e) {
        ModalDialog.closeModal(this.getId());
    }

    private void aceptarActionPerformed(ActionEvent e) {
        try {
            Unidad unidad = new Unidad();
            unidad.setCodigo(textFieldCodigo.getText());
            unidad.setNombre(textFieldNombre.getText());
            unidad.setDireccion(textFieldDireccion.getText());
            unidad.setCorreo(textFieldCorreo.getText());
            unidad.setNae(String.valueOf(comboBoxNae.getSelectedItem()).trim());
            unidad.setDpa(String.valueOf(comboBoxDpa.getSelectedItem()).trim());
            unidad.setReup(String.valueOf(comboBoxReuup.getSelectedItem()).trim());
            empresa.addUnidad(unidad);
            Injector.get(EmpresaService.class).update(empresa);
            ModalDialog.closeModal(this.getId());
            
            var model = (DefaultTableModel)table.getModel();
            
            model.addRow(new Object[]{
                unidad.getCodigo(),
                unidad.getNombre(),
                unidad.getDireccion(),
                unidad.getCorreo(),
                unidad.getNae(),
                unidad.getDpa(),
                unidad.getReup()
            });
            ModalDialog.showModal(
                    this, 
                    new SimpleMessageModal(
                            SimpleMessageModal.Type.ERROR, 
                            "Se ha añadido la Unidad correctamente.", 
                            "Información", 
                            SimpleModalBorder.OK_OPTION, 
                            null), 
                    Option.getDefault()
            );
        } catch (Exception ex) {
            ModalDialog.showModal(
                    this, 
                    new SimpleMessageModal(
                            SimpleMessageModal.Type.ERROR, 
                            ex.getMessage(), 
                            "ERROR", 
                            SimpleModalBorder.YES_NO_OPTION, 
                            null), 
                    Option.getDefault()
            );
        }
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.label1 = new JLabel();
	this.label2 = new JLabel();
	this.label3 = new JLabel();
	this.label4 = new JLabel();
	this.label5 = new JLabel();
	this.label6 = new JLabel();
	this.label7 = new JLabel();
	this.comboBoxNae = new JComboBox<>();
	this.comboBoxDpa = new JComboBox<>();
	this.comboBoxReuup = new JComboBox<>();
	this.textFieldCodigo = new JTextField();
	this.textFieldNombre = new JTextField();
	this.textFieldDireccion = new JTextField();
	this.textFieldCorreo = new JTextField();
	this.buttonCancelar = new JButton();
	this.buttonAceptar = new JButton();

	//======== this ========
	setBorder(new EmptyBorder(20, 20, 20, 20));

	//---- label1 ----
	this.label1.setText("C\u00f3digo"); //NOI18N

	//---- label2 ----
	this.label2.setText("Nombre"); //NOI18N

	//---- label3 ----
	this.label3.setText("Direcci\u00f3n"); //NOI18N

	//---- label4 ----
	this.label4.setText("Correo"); //NOI18N

	//---- label5 ----
	this.label5.setText("NAE"); //NOI18N

	//---- label6 ----
	this.label6.setText("DPA"); //NOI18N

	//---- label7 ----
	this.label7.setText("REUP"); //NOI18N

	//---- comboBoxNae ----
	this.comboBoxNae.setModel(new DefaultComboBoxModel<>(new String[] {
	    "  " //NOI18N
	}));
	this.comboBoxNae.setEditable(true);

	//---- comboBoxDpa ----
	this.comboBoxDpa.setModel(new DefaultComboBoxModel<>(new String[] {
	    " " //NOI18N
	}));
	this.comboBoxDpa.setEditable(true);

	//---- comboBoxReuup ----
	this.comboBoxReuup.setModel(new DefaultComboBoxModel<>(new String[] {
	    " " //NOI18N
	}));
	this.comboBoxReuup.setEditable(true);

	//---- buttonCancelar ----
	this.buttonCancelar.setText("Cancelar"); //NOI18N
	this.buttonCancelar.addActionListener(e -> cancelarActionPerformed(e));

	//---- buttonAceptar ----
	this.buttonAceptar.setText("Aceptar"); //NOI18N
	this.buttonAceptar.addActionListener(e -> aceptarActionPerformed(e));

	GroupLayout layout = new GroupLayout(this);
	setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
			.addGroup(layout.createSequentialGroup()
			    .addComponent(this.buttonAceptar)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(this.buttonCancelar))
			.addGroup(layout.createSequentialGroup()
			    .addGroup(layout.createParallelGroup()
				.addComponent(this.label7)
				.addComponent(this.label1)
				.addComponent(this.label6)
				.addComponent(this.label5)
				.addComponent(this.label4)
				.addComponent(this.label3)
				.addComponent(this.label2))
			    .addGap(295, 295, 295))
			.addComponent(this.textFieldCodigo, GroupLayout.Alignment.LEADING)
			.addComponent(this.textFieldNombre, GroupLayout.Alignment.LEADING)
			.addComponent(this.textFieldDireccion, GroupLayout.Alignment.LEADING)
			.addComponent(this.textFieldCorreo, GroupLayout.Alignment.LEADING)
			.addComponent(this.comboBoxNae, GroupLayout.Alignment.LEADING)
			.addComponent(this.comboBoxDpa, GroupLayout.Alignment.LEADING)
			.addComponent(this.comboBoxReuup, GroupLayout.Alignment.LEADING))
		    .addGap(0, 0, Short.MAX_VALUE))
	);
	layout.setVerticalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addComponent(this.label1)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addComponent(this.textFieldCodigo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
		    .addComponent(this.label2)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addComponent(this.textFieldNombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addComponent(this.label3)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addComponent(this.textFieldDireccion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addComponent(this.label4)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addComponent(this.textFieldCorreo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addGap(18, 18, 18)
		    .addComponent(this.label5)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(this.comboBoxNae, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addGap(18, 18, 18)
		    .addComponent(this.label6)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(this.comboBoxDpa, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addComponent(this.label7)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(this.comboBoxReuup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.buttonCancelar)
			.addComponent(this.buttonAceptar))
		    .addContainerGap())
	);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    public JButton getButtonCancelar() {
        return buttonCancelar;
    }

    public void setButtonCancelar(JButton buttonCancelar) {
        this.buttonCancelar = buttonCancelar;
    }

    public JButton getButtonAceptar() {
        return buttonAceptar;
    }

    public void setButtonAceptar(JButton buttonAceptar) {
        this.buttonAceptar = buttonAceptar;
    }

    public JComboBox<String> getComboBoxNae() {
        return comboBoxNae;
    }

    public void setComboBoxNae(JComboBox<String> comboBoxNae) {
        this.comboBoxNae = comboBoxNae;
    }

    public JComboBox<String> getComboBoxDpa() {
        return comboBoxDpa;
    }

    public void setComboBoxDpa(JComboBox<String> comboBoxDpa) {
        this.comboBoxDpa = comboBoxDpa;
    }

    public JComboBox<String> getComboBoxReuup() {
        return comboBoxReuup;
    }

    public void setComboBoxReuup(JComboBox<String> comboBoxReuup) {
        this.comboBoxReuup = comboBoxReuup;
    }

    public JTextField getTextFieldCodigo() {
        return textFieldCodigo;
    }

    public void setTextFieldCodigo(JTextField textFieldCodigo) {
        this.textFieldCodigo = textFieldCodigo;
    }

    public JTextField getTextFieldNombre() {
        return textFieldNombre;
    }

    public void setTextFieldNombre(JTextField textFieldNombre) {
        this.textFieldNombre = textFieldNombre;
    }

    public JTextField getTextFieldDireccion() {
        return textFieldDireccion;
    }

    public void setTextFieldDireccion(JTextField textFieldDireccion) {
        this.textFieldDireccion = textFieldDireccion;
    }

    public JTextField getTextFieldCorreo() {
        return textFieldCorreo;
    }

    public void setTextFieldCorreo(JTextField textFieldCorreo) {
        this.textFieldCorreo = textFieldCorreo;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JComboBox<String> comboBoxNae;
    private JComboBox<String> comboBoxDpa;
    private JComboBox<String> comboBoxReuup;
    private JTextField textFieldCodigo;
    private JTextField textFieldNombre;
    private JTextField textFieldDireccion;
    private JTextField textFieldCorreo;
    private JButton buttonCancelar;
    private JButton buttonAceptar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
