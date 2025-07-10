package com.univsoftdev.econova.contabilidad.views.clasificador;

import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.contabilidad.service.PlanDeCuentasService;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.component.*;
import com.univsoftdev.econova.core.utils.DialogUtils;
import com.univsoftdev.econova.core.utils.table.TableColumnAdjuster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;

public class FormAperturaCuenta extends Modal {

    private static final long serialVersionUID = 9099945688940617884L;
    private Cuenta cuenta;
    private final PlanDeCuentasService planDeCuentasService;
    private static final Logger LOGGER = LoggerFactory.getLogger(FormAperturaCuenta.class);

    public FormAperturaCuenta() {
        initComponents();
        this.planDeCuentasService = AppContext.getInstance().getInjector().get(PlanDeCuentasService.class);
    }

    public FormAperturaCuenta(JTree tree, PlanDeCuentasService planDeCuentasService, Cuenta cuenta) {
        initComponents();
        this.planDeCuentasService = planDeCuentasService;
        this.cuenta = cuenta;

        textFieldCuenta.setText(cuenta.getCodigo());
        textFieldAperturaPor.setText(cuenta.getTipoApertura().name());

        updateTable();
    }

    private void adicionar(ActionEvent e) {
        DialogUtils.showModalDialog(this, new FormNuevaCuentaApertura(tableCuentasApertura, cuenta), "Aperturas de la Cuenta");
        Thread thread = new Thread(() -> {
            updateTable();
            new TableColumnAdjuster(tableCuentasApertura).adjustColumns();
        });
        thread.start();
    }

    private void updateTable() {
        java.util.List<Cuenta> subCuentas = cuenta.getSubCuentas();
        var model = (DefaultTableModel) tableCuentasApertura.getModel();
        subCuentas.forEach(c -> {
            model.addRow(new Object[]{
                cuenta.getCodigo(),
                cuenta.getNombre(),
                cuenta.getNaturaleza(),
                cuenta.getEstadoCuenta().getDescripcion(),
                cuenta.getMoneda().getSymbol()
            });
        });
        new TableColumnAdjuster(tableCuentasApertura).adjustColumns();
    }

    private void cancelar(ActionEvent e) {
        ModalDialog.closeModal(this.getId());
    }

    private void aceptar(ActionEvent e) {
        try {
            planDeCuentasService.updateCuenta(cuenta);
            ModalDialog.closeModal(this.getId());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.label1 = new JLabel();
	this.textFieldCuenta = new JTextField();
	this.label2 = new JLabel();
	this.textFieldAperturaPor = new JTextField();
	this.panel2 = new JPanel();
	this.panel3 = new JPanel();
	this.toolBar1 = new JToolBar();
	this.eButtonAdicionar = new EButton();
	this.eButtonEliminar = new EButton();
	this.eButtonModificar = new EButton();
	this.eButtonactivarDesactivar = new EButton();
	this.eButton5 = new EButton();
	this.eButton4 = new EButton();
	this.label3 = new JLabel();
	this.scrollPane1 = new JScrollPane();
	this.tableCuentasApertura = new JTable();
	this.panel4 = new JPanel();
	this.eButtonCancelar = new EButton();
	this.eButtonAceptar = new EButton();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    this.panel1.setBorder(new EmptyBorder(5, 5, 5, 5));

	    //---- label1 ----
	    this.label1.setText("Cuenta"); //NOI18N

	    //---- textFieldCuenta ----
	    this.textFieldCuenta.setEditable(false);

	    //---- label2 ----
	    this.label2.setText("Apertura por"); //NOI18N

	    //---- textFieldAperturaPor ----
	    this.textFieldAperturaPor.setEditable(false);

	    GroupLayout panel1Layout = new GroupLayout(this.panel1);
	    panel1.setLayout(panel1Layout);
	    panel1Layout.setHorizontalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addGap(15, 15, 15)
			.addGroup(panel1Layout.createParallelGroup()
			    .addComponent(this.label2)
			    .addComponent(this.label1))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
			    .addComponent(this.textFieldCuenta, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
			    .addComponent(this.textFieldAperturaPor, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
			.addContainerGap(315, Short.MAX_VALUE))
	    );
	    panel1Layout.setVerticalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(this.label1)
			    .addComponent(this.textFieldCuenta, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(this.label2)
			    .addComponent(this.textFieldAperturaPor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap())
	    );
	}
	add(this.panel1, BorderLayout.NORTH);

	//======== panel2 ========
	{
	    this.panel2.setLayout(new BorderLayout());

	    //======== panel3 ========
	    {
		this.panel3.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.panel3.setLayout(new BorderLayout());

		//======== toolBar1 ========
		{

		    //---- eButtonAdicionar ----
		    this.eButtonAdicionar.setText("Adicionar"); //NOI18N
		    this.eButtonAdicionar.addActionListener(e -> adicionar(e));
		    this.toolBar1.add(this.eButtonAdicionar);

		    //---- eButtonEliminar ----
		    this.eButtonEliminar.setText("Eliminar"); //NOI18N
		    this.toolBar1.add(this.eButtonEliminar);

		    //---- eButtonModificar ----
		    this.eButtonModificar.setText("Modificar"); //NOI18N
		    this.toolBar1.add(this.eButtonModificar);

		    //---- eButtonactivarDesactivar ----
		    this.eButtonactivarDesactivar.setText("Activar/Desactivar"); //NOI18N
		    this.toolBar1.add(this.eButtonactivarDesactivar);

		    //---- eButton5 ----
		    this.eButton5.setText("text"); //NOI18N
		    this.toolBar1.add(this.eButton5);

		    //---- eButton4 ----
		    this.eButton4.setText("text"); //NOI18N
		    this.toolBar1.add(this.eButton4);
		}
		this.panel3.add(this.toolBar1, BorderLayout.EAST);

		//---- label3 ----
		this.label3.setText("Cuentas de la apertura"); //NOI18N
		this.panel3.add(this.label3, BorderLayout.WEST);
	    }
	    this.panel2.add(this.panel3, BorderLayout.NORTH);

	    //======== scrollPane1 ========
	    {

		//---- tableCuentasApertura ----
		this.tableCuentasApertura.setModel(new DefaultTableModel(
		    new Object[][] {
		    },
		    new String[] {
			"C\u00f3digo", "Descripci\u00f3n", "Naturaleza", "Estado", "Moneda" //NOI18N
		    }
		));
		this.scrollPane1.setViewportView(this.tableCuentasApertura);
	    }
	    this.panel2.add(this.scrollPane1, BorderLayout.CENTER);

	    //======== panel4 ========
	    {
		this.panel4.setBorder(new EmptyBorder(5, 5, 5, 5));

		//---- eButtonCancelar ----
		this.eButtonCancelar.setText("Cancelar"); //NOI18N
		this.eButtonCancelar.addActionListener(e -> cancelar(e));

		//---- eButtonAceptar ----
		this.eButtonAceptar.setText("Aceptar"); //NOI18N
		this.eButtonAceptar.addActionListener(e -> aceptar(e));

		GroupLayout panel4Layout = new GroupLayout(this.panel4);
		panel4.setLayout(panel4Layout);
		panel4Layout.setHorizontalGroup(
		    panel4Layout.createParallelGroup()
			.addGroup(GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
			    .addGap(0, 416, Short.MAX_VALUE)
			    .addComponent(this.eButtonAceptar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(this.eButtonCancelar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		panel4Layout.setVerticalGroup(
		    panel4Layout.createParallelGroup()
			.addGroup(panel4Layout.createSequentialGroup()
			    .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(this.eButtonCancelar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.eButtonAceptar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addGap(0, 0, Short.MAX_VALUE))
		);
	    }
	    this.panel2.add(this.panel4, BorderLayout.SOUTH);
	}
	add(this.panel2, BorderLayout.CENTER);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JLabel label1;
    private JTextField textFieldCuenta;
    private JLabel label2;
    private JTextField textFieldAperturaPor;
    private JPanel panel2;
    private JPanel panel3;
    private JToolBar toolBar1;
    private EButton eButtonAdicionar;
    private EButton eButtonEliminar;
    private EButton eButtonModificar;
    private EButton eButtonactivarDesactivar;
    private EButton eButton5;
    private EButton eButton4;
    private JLabel label3;
    private JScrollPane scrollPane1;
    private JTable tableCuentasApertura;
    private JPanel panel4;
    private EButton eButtonCancelar;
    private EButton eButtonAceptar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
