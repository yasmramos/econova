package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.ebean.config.MyTenantSchemaProvider;
import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.config.model.Periodo;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.component.*;
import com.univsoftdev.econova.core.utils.DialogUtils;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import raven.modal.ModalDialog;
import raven.modal.component.Modal;

public class FormNuevoEjercicioEstandar extends Modal {
    
    private static final long serialVersionUID = -2855466166311660120L;
    private Ejercicio ejercicio;
    private JTable tablePeriodos;
    private JTable tableEjercicios;
    
    @Inject
    private MyTenantSchemaProvider tenantSchemaProvider;
    
    public FormNuevoEjercicioEstandar() {
        initComponents();
        init();
    }
    
    public FormNuevoEjercicioEstandar(JTable tableEjercicios,
            JTable tablePeriodos) {
        initComponents();
        init();
        this.tableEjercicios = tableEjercicios;
        this.tablePeriodos = tablePeriodos;
    }
    
    private void init() {
        panel1.setVisible(false);
        Calendar calendar = Calendar.getInstance();
        datePickerSwing1.setSelectedDate(
                LocalDate.of(calendar.get(Calendar.YEAR), 1, 1)
        );
        datePickerSwing2.setSelectedDate(
                LocalDate.of(calendar.get(Calendar.YEAR), 12, 31)
        );
        String currentYear = String.valueOf(
                Calendar.getInstance().get(Calendar.YEAR)
        );
        textFieldAnno.setText(currentYear);
        textFieldNombre.setText(currentYear);
    }
    
    private void checkBoxBasadoAnnoNaturalStateChanged(ChangeEvent e) {
        
    }
    
    private void basadoAnnoNaturalActionPerformed(ActionEvent e) {
        if (checkBoxBasadoAnnoNatural.isSelected()) {
            panel1.setVisible(false);
            labelAnno.setVisible(true);
            textFieldAnno.setVisible(true);
        } else {
            panel1.setVisible(true);
            labelAnno.setVisible(false);
            textFieldAnno.setVisible(false);
        }
    }
    
    private void adicionarActionPeformed(ActionEvent e) {
        DialogUtils.showModalDialog(
                this,
                new FormAdicionarPeriodo(ejercicio, table1),
                "Modificar Período"
        );
    }
    
    private void eliminarActionPeformed(ActionEvent e) {
        
    }
    
    private void modificarActionPeformed(ActionEvent e) {
        
    }
    
    private void datePickerSwing2PropertyChange(PropertyChangeEvent e) {
        label5.setText(datePickerSwing2.getSelectedDateAsString());
    }
    
    private void cancelarActionPerformed(ActionEvent e) {
        ModalDialog.closeModal(this.getId());
        FormEjerciciosUtil.updateView(tableEjercicios, tablePeriodos);
    }
    
    private void aceptarActionPerformed(ActionEvent e) {
        if (checkBoxBasadoAnnoNatural.isSelected()) {

            // Validar textField1
            if (textFieldNombre == null
                    || textFieldNombre.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "El nombre no puede estar vacío.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Validar checkBoxBasadoAnnoNatural
            if (checkBoxBasadoAnnoNatural == null
                    || !checkBoxBasadoAnnoNatural.isSelected()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debe seleccionar la opción basada en año natural.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Validar textFieldAnno
            String annoText = textFieldAnno.getText();
            
            if (annoText == null
                    || annoText.isEmpty()
                    || !annoText.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(
                        this,
                        "El año debe ser un número válido de 4 dígitos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Crear periodos
            java.util.List<Periodo> periodos = new ArrayList<>();
            
            String[] meses = {
                "Enero",
                "Febrero",
                "Marzo",
                "Abril",
                "Mayo",
                "Junio",
                "Julio",
                "Agosto",
                "Septiembre",
                "Octubre",
                "Noviembre",
                "Diciembre"
            };
            
            int[] intmeses = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            
            for (int intmese : intmeses) {
                YearMonth yearMonth = YearMonth.of(Integer.parseInt(annoText), intmese);
                LocalDate startDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
                LocalDate endDate = yearMonth.atEndOfMonth();
                periodos.add(new Periodo(meses[intmese - 1], startDate, endDate));
            }
            
            var inicio = LocalDate.of(Integer.parseInt(annoText), 1, 1);
            var fin = LocalDate.of(Integer.parseInt(annoText), 12, 31);
            
            this.ejercicio = new Ejercicio(
                    textFieldNombre.getText(),
                    Integer.parseInt(textFieldAnno.getText()),
                    inicio,
                    fin,
                    periodos
            );

            // Guardar ejercicio
            try {
                ejercicio.save();
                JOptionPane.showMessageDialog(this, "Ejercicio guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el ejercicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            
        }
        
        FormEjerciciosUtil.updateView(tableEjercicios, tablePeriodos);
    }
    
    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.label1 = new JLabel();
	this.textFieldNombre = new JTextField();
	this.panel1 = new JPanel();
	this.panel2 = new JPanel();
	this.scrollPane1 = new JScrollPane();
	this.table1 = new JTable();
	this.panel3 = new JPanel();
	this.label4 = new JLabel();
	this.label5 = new JLabel();
	this.toolBar1 = new JToolBar();
	this.buttonAdicionar = new JButton();
	this.buttonEliminar = new JButton();
	this.buttonModificar = new JButton();
	this.label2 = new JLabel();
	this.datePickerSwing1 = new DatePickerSwing();
	this.label3 = new JLabel();
	this.datePickerSwing2 = new DatePickerSwing();
	this.checkBoxBasadoAnnoNatural = new JCheckBox();
	this.buttonCancelarActionPerformed = new JButton();
	this.buttonAceptarActionPerformed = new JButton();
	this.labelAnno = new JLabel();
	this.textFieldAnno = new JTextField();

	//======== this ========
	setBorder(new EmptyBorder(20, 20, 20, 20));

	//---- label1 ----
	this.label1.setText("Nombre"); //NOI18N

	//======== panel1 ========
	{

	    //======== panel2 ========
	    {
		this.panel2.setBorder(new TitledBorder("Per\u00edodos")); //NOI18N
		this.panel2.setLayout(new BorderLayout());

		//======== scrollPane1 ========
		{

		    //---- table1 ----
		    this.table1.setModel(new DefaultTableModel(
			new Object[][] {
			    {null, null, null},
			    {null, null, null},
			},
			new String[] {
			    "Nombre", "Inicio", "Fin" //NOI18N
			}
		    ));
		    this.scrollPane1.setViewportView(this.table1);
		}
		this.panel2.add(this.scrollPane1, BorderLayout.CENTER);

		//======== panel3 ========
		{
		    this.panel3.setLayout(new BorderLayout());

		    //---- label4 ----
		    this.label4.setText("Per\u00edodo - Cierre de Ejercicio"); //NOI18N
		    this.panel3.add(this.label4, BorderLayout.WEST);

		    //---- label5 ----
		    this.label5.setText(" "); //NOI18N
		    this.panel3.add(this.label5, BorderLayout.EAST);
		}
		this.panel2.add(this.panel3, BorderLayout.SOUTH);

		//======== toolBar1 ========
		{

		    //---- buttonAdicionar ----
		    this.buttonAdicionar.setText("Adicionar"); //NOI18N
		    this.buttonAdicionar.addActionListener(e -> adicionarActionPeformed(e));
		    this.toolBar1.add(this.buttonAdicionar);

		    //---- buttonEliminar ----
		    this.buttonEliminar.setText("Eliminar"); //NOI18N
		    this.buttonEliminar.addActionListener(e -> eliminarActionPeformed(e));
		    this.toolBar1.add(this.buttonEliminar);

		    //---- buttonModificar ----
		    this.buttonModificar.setText("Modificar"); //NOI18N
		    this.buttonModificar.addActionListener(e -> modificarActionPeformed(e));
		    this.toolBar1.add(this.buttonModificar);
		}
		this.panel2.add(this.toolBar1, BorderLayout.NORTH);
	    }

	    //---- label2 ----
	    this.label2.setText("Inicio"); //NOI18N

	    //---- label3 ----
	    this.label3.setText("Fin"); //NOI18N

	    //---- datePickerSwing2 ----
	    this.datePickerSwing2.addPropertyChangeListener("selectedDate", e -> datePickerSwing2PropertyChange(e)); //NOI18N

	    GroupLayout panel1Layout = new GroupLayout(this.panel1);
	    panel1.setLayout(panel1Layout);
	    panel1Layout.setHorizontalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addGap(17, 17, 17)
			.addGroup(panel1Layout.createParallelGroup()
			    .addComponent(this.panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addGroup(panel1Layout.createSequentialGroup()
				.addComponent(this.label2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.datePickerSwing1, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.label3)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.datePickerSwing2, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)))
			.addContainerGap(13, Short.MAX_VALUE))
	    );
	    panel1Layout.setVerticalGroup(
		panel1Layout.createParallelGroup()
		    .addGroup(panel1Layout.createSequentialGroup()
			.addGroup(panel1Layout.createParallelGroup()
			    .addGroup(panel1Layout.createSequentialGroup()
				.addGap(17, 17, 17)
				.addComponent(this.label2))
			    .addGroup(panel1Layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(panel1Layout.createParallelGroup()
				    .addComponent(this.datePickerSwing1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				    .addGroup(panel1Layout.createSequentialGroup()
					.addGap(12, 12, 12)
					.addComponent(this.label3))
				    .addComponent(this.datePickerSwing2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addComponent(this.panel2, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE)
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	    );
	}

	//---- checkBoxBasadoAnnoNatural ----
	this.checkBoxBasadoAnnoNatural.setText("Per\u00edodo contable basado en a\u00f1o natural."); //NOI18N
	this.checkBoxBasadoAnnoNatural.setSelected(true);
	this.checkBoxBasadoAnnoNatural.addChangeListener(e -> checkBoxBasadoAnnoNaturalStateChanged(e));
	this.checkBoxBasadoAnnoNatural.addActionListener(e -> basadoAnnoNaturalActionPerformed(e));

	//---- buttonCancelarActionPerformed ----
	this.buttonCancelarActionPerformed.setText("Cancelar"); //NOI18N
	this.buttonCancelarActionPerformed.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	this.buttonCancelarActionPerformed.addActionListener(e -> cancelarActionPerformed(e));

	//---- buttonAceptarActionPerformed ----
	this.buttonAceptarActionPerformed.setText("Aceptar"); //NOI18N
	this.buttonAceptarActionPerformed.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	this.buttonAceptarActionPerformed.addActionListener(e -> aceptarActionPerformed(e));

	//---- labelAnno ----
	this.labelAnno.setText("A\u00f1o"); //NOI18N

	GroupLayout layout = new GroupLayout(this);
	setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup()
		.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
		    .addContainerGap(339, Short.MAX_VALUE)
		    .addComponent(this.buttonAceptarActionPerformed)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(this.buttonCancelarActionPerformed)
		    .addGap(27, 27, 27))
		.addGroup(layout.createSequentialGroup()
		    .addGap(17, 17, 17)
		    .addGroup(layout.createParallelGroup()
			.addComponent(this.panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addGroup(layout.createSequentialGroup()
			    .addGroup(layout.createParallelGroup()
				.addComponent(this.label1)
				.addComponent(this.labelAnno))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			    .addGroup(layout.createParallelGroup()
				.addComponent(this.textFieldAnno, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.textFieldNombre, GroupLayout.PREFERRED_SIZE, 291, GroupLayout.PREFERRED_SIZE)))
			.addComponent(this.checkBoxBasadoAnnoNatural))
		    .addContainerGap(11, Short.MAX_VALUE))
	);
	layout.setVerticalGroup(
	    layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
		    .addGap(14, 14, 14)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.label1)
			.addComponent(this.textFieldNombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.labelAnno)
			.addComponent(this.textFieldAnno, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGap(12, 12, 12)
		    .addComponent(this.checkBoxBasadoAnnoNatural)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addComponent(this.panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addGap(18, 18, 18)
		    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(this.buttonCancelarActionPerformed)
			.addComponent(this.buttonAceptarActionPerformed)))
	);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel label1;
    private JTextField textFieldNombre;
    private JPanel panel1;
    private JPanel panel2;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JPanel panel3;
    private JLabel label4;
    private JLabel label5;
    private JToolBar toolBar1;
    private JButton buttonAdicionar;
    private JButton buttonEliminar;
    private JButton buttonModificar;
    private JLabel label2;
    private DatePickerSwing datePickerSwing1;
    private JLabel label3;
    private DatePickerSwing datePickerSwing2;
    private JCheckBox checkBoxBasadoAnnoNatural;
    private JButton buttonCancelarActionPerformed;
    private JButton buttonAceptarActionPerformed;
    private JLabel labelAnno;
    private JTextField textFieldAnno;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
