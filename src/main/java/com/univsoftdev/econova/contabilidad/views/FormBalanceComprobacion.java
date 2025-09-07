package com.univsoftdev.econova.contabilidad.views;

import com.univsoftdev.econova.core.Injector;
import java.awt.event.*;
import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.config.service.EjercicioService;
import com.univsoftdev.econova.contabilidad.LineaBalance;
import com.univsoftdev.econova.contabilidad.service.BalanceGeneralService;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.univsoftdev.econova.core.component.*;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.utils.table.TableColumnAdjuster;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.Optional;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormBalanceComprobacion extends Form {

    private transient EjercicioService ejercService;
    private BalanceGeneralService balanceService;

    public FormBalanceComprobacion() {
        initComponents();
        init();
    }

    private void comboBoxEjerciciosItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
            return; // Ignorar eventos de deselección
        }

        // Obtener el nombre del ejercicio seleccionado
        var selected = String.valueOf(comboBoxEjercicios.getSelectedItem());
        if (selected == null || selected.trim().isEmpty()) {
            log.warn("No se ha seleccionado ningún ejercicio.");
            return;
        }

        try {
            // Buscar el ejercicio por nombre
            Optional<Exercise> ejercicio = ejercService.findByNombre(selected);
            if (ejercicio.isPresent()) {
                // Obtener los periodos del ejercicio
                java.util.List<Period> periodos = ejercicio.get().getPeriodos();

                // Limpiar la lista actual
                listPeriodos.removeAll();

                // Crear un nuevo modelo de lista y llenarlo con los nombres de los periodos
                DefaultListModel<String> model = new DefaultListModel<>();
                periodos.stream()
                        .map(Period::getName)
                        .forEach(model::addElement);

                // Asignar el nuevo modelo a la lista
                listPeriodos.setModel(model);
            } else {
                log.warn("No se encontró el ejercicio con el nombre: {}", selected);
                // Opcional: Mostrar un mensaje al usuario
                JOptionPane.showMessageDialog(null, "No se encontró el ejercicio seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException ex) {
            log.error("Error al procesar el cambio de selección de ejercicio: {}", ex.getMessage(), ex);
            // Opcional: Mostrar un mensaje al usuario
            JOptionPane.showMessageDialog(null, "Ocurrió un error al cargar los periodos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void formRefresh() {
        init();
    }

    private void imprimir(ActionEvent e) {

    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	this.panel1 = new JPanel();
	this.panel2 = new JPanel();
	this.label1 = new JLabel();
	this.panel8 = new JPanel();
	this.panel9 = new JPanel();
	this.panel10 = new JPanel();
	this.label6 = new JLabel();
	this.comboBoxEjercicios = new JComboBox();
	this.toolBar1 = new JToolBar();
	this.eButtonImprimir = new EButton();
	this.eButton2 = new EButton();
	this.eButton3 = new EButton();
	this.eButton4 = new EButton();
	this.eButton5 = new EButton();
	this.scrollPane2 = new JScrollPane();
	this.panel14 = new JPanel();
	this.label10 = new JLabel();
	this.scrollPane5 = new JScrollPane();
	this.listPeriodos = new JList();
	this.panel11 = new JPanel();
	this.scrollPane3 = new JScrollPane();
	this.panel12 = new JPanel();
	this.panel13 = new JPanel();
	this.label7 = new JLabel();
	this.label8 = new JLabel();
	this.label9 = new JLabel();
	this.scrollPane4 = new JScrollPane();
	this.tableBalance = new JTable();

	//======== this ========
	setLayout(new BorderLayout());

	//======== panel1 ========
	{
	    this.panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
	    this.panel1.setLayout(new BorderLayout());

	    //======== panel2 ========
	    {
		this.panel2.setLayout(new BorderLayout());

		//---- label1 ----
		this.label1.setText("Balance de Comprobaci\u00f3n"); //NOI18N
		this.panel2.add(this.label1, BorderLayout.NORTH);
	    }
	    this.panel1.add(this.panel2, BorderLayout.NORTH);

	    //======== panel8 ========
	    {
		this.panel8.setLayout(new BorderLayout());

		//======== panel9 ========
		{
		    this.panel9.setLayout(new BorderLayout());

		    //======== panel10 ========
		    {
			this.panel10.setBorder(new EmptyBorder(5, 5, 5, 5));
			this.panel10.setLayout(new BorderLayout());

			//---- label6 ----
			this.label6.setText("Ejercicios"); //NOI18N
			this.label6.setBorder(new EmptyBorder(5, 5, 5, 5));
			this.panel10.add(this.label6, BorderLayout.NORTH);

			//---- comboBoxEjercicios ----
			this.comboBoxEjercicios.addItemListener(e -> comboBoxEjerciciosItemStateChanged(e));
			this.panel10.add(this.comboBoxEjercicios, BorderLayout.SOUTH);
		    }
		    this.panel9.add(this.panel10, BorderLayout.WEST);

		    //======== toolBar1 ========
		    {

			//---- eButtonImprimir ----
			this.eButtonImprimir.setText("Imprimir"); //NOI18N
			this.eButtonImprimir.addActionListener(e -> imprimir(e));
			this.toolBar1.add(this.eButtonImprimir);

			//---- eButton2 ----
			this.eButton2.setText("text"); //NOI18N
			this.toolBar1.add(this.eButton2);

			//---- eButton3 ----
			this.eButton3.setText("text"); //NOI18N
			this.toolBar1.add(this.eButton3);

			//---- eButton4 ----
			this.eButton4.setText("text"); //NOI18N
			this.toolBar1.add(this.eButton4);

			//---- eButton5 ----
			this.eButton5.setText("text"); //NOI18N
			this.toolBar1.add(this.eButton5);
		    }
		    this.panel9.add(this.toolBar1, BorderLayout.EAST);
		}
		this.panel8.add(this.panel9, BorderLayout.NORTH);

		//======== scrollPane2 ========
		{

		    //======== panel14 ========
		    {
			this.panel14.setLayout(new BorderLayout());

			//---- label10 ----
			this.label10.setText("P\u00e9riodos"); //NOI18N
			this.panel14.add(this.label10, BorderLayout.NORTH);

			//======== scrollPane5 ========
			{
			    this.scrollPane5.setViewportView(this.listPeriodos);
			}
			this.panel14.add(this.scrollPane5, BorderLayout.CENTER);
		    }
		    this.scrollPane2.setViewportView(this.panel14);
		}
		this.panel8.add(this.scrollPane2, BorderLayout.WEST);

		//======== panel11 ========
		{
		    this.panel11.setLayout(new BorderLayout());
		}
		this.panel8.add(this.panel11, BorderLayout.SOUTH);

		//======== scrollPane3 ========
		{

		    //======== panel12 ========
		    {
			this.panel12.setBorder(new EmptyBorder(5, 5, 5, 5));
			this.panel12.setLayout(new BorderLayout());

			//======== panel13 ========
			{
			    this.panel13.setLayout(new BorderLayout());

			    //---- label7 ----
			    this.label7.setText("text"); //NOI18N
			    this.panel13.add(this.label7, BorderLayout.EAST);

			    //---- label8 ----
			    this.label8.setText("text"); //NOI18N
			    this.panel13.add(this.label8, BorderLayout.CENTER);

			    //---- label9 ----
			    this.label9.setText("text"); //NOI18N
			    this.panel13.add(this.label9, BorderLayout.WEST);
			}
			this.panel12.add(this.panel13, BorderLayout.NORTH);

			//======== scrollPane4 ========
			{

			    //---- tableBalance ----
			    this.tableBalance.setModel(new DefaultTableModel(
				new Object[][] {
				    {null, null, null, null, null, null, null},
				},
				new String[] {
				    "C\u00f3digo", "Descripci\u00f3n", "D\u00e9bitos P\u00e9riodo", "C\u00e9ditos P\u00e9riodo", "Saldo P\u00e9riodo", "D\u00e9bitos Acumulado", "C\u00e9ditos Acumulado" //NOI18N
				}
			    ));
			    this.scrollPane4.setViewportView(this.tableBalance);
			}
			this.panel12.add(this.scrollPane4, BorderLayout.CENTER);
		    }
		    this.scrollPane3.setViewportView(this.panel12);
		}
		this.panel8.add(this.scrollPane3, BorderLayout.CENTER);
	    }
	    this.panel1.add(this.panel8, BorderLayout.CENTER);
	}
	add(this.panel1, BorderLayout.CENTER);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panel1;
    private JPanel panel2;
    private JLabel label1;
    private JPanel panel8;
    private JPanel panel9;
    private JPanel panel10;
    private JLabel label6;
    private JComboBox comboBoxEjercicios;
    private JToolBar toolBar1;
    private EButton eButtonImprimir;
    private EButton eButton2;
    private EButton eButton3;
    private EButton eButton4;
    private EButton eButton5;
    private JScrollPane scrollPane2;
    private JPanel panel14;
    private JLabel label10;
    private JScrollPane scrollPane5;
    private JList listPeriodos;
    private JPanel panel11;
    private JScrollPane scrollPane3;
    private JPanel panel12;
    private JPanel panel13;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    private JScrollPane scrollPane4;
    private JTable tableBalance;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void init() {
        try {
            // Inicializar el injector y los servicios necesarios
            ejercService = Injector.get(EjercicioService.class);
            balanceService = Injector.get(BalanceGeneralService.class);

            // Obtener todos los ejercicios
            java.util.List<Exercise> ejercicios = ejercService.findAll();

            // Limpiar el comboBox de ejercicios
            comboBoxEjercicios.removeAllItems();

            // Encontrar el ejercicio actual y llenar el comboBox
            Exercise currentEjercicio = null;
            for (Exercise ejercicio : ejercicios) {
                comboBoxEjercicios.addItem(ejercicio.getName());
                if (ejercicio.isCurrent()) {
                    currentEjercicio = ejercicio;
                }
            }

            // Validar que haya un ejercicio actual
            if (currentEjercicio == null) {
                log.error("No se encontró ningún ejercicio actual.");
                JOptionPane.showMessageDialog(null, "No se encontró ningún ejercicio actual.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obtener los periodos del ejercicio actual
            java.util.List<Period> periodos = currentEjercicio.getPeriodos();

            // Encontrar el periodo actual
            Period currentPeriodo = null;
            for (Period periodo : periodos) {
                if (periodo.isCurrent()) {
                    currentPeriodo = periodo;
                }
            }

            // Validar que haya un periodo actual
            if (currentPeriodo == null) {
                log.error("No se encontró ningún periodo actual para el ejercicio: {}", currentEjercicio.getName());
                JOptionPane.showMessageDialog(null, "No se encontró ningún periodo actual para el ejercicio: " + currentEjercicio.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generar el balance general para el periodo actual
            java.util.List<LineaBalance> lineas = balanceService.generarBalanceGeneral(currentPeriodo.getStartDate(), currentPeriodo.getEndDate());

            // Obtener el modelo de la tabla de balance
            DefaultTableModel model = (DefaultTableModel) tableBalance.getModel();

            // Limpiar la tabla de balance
            model.setRowCount(0);

            // Llenar la tabla con las líneas de balance, filtrando las que tienen saldo cero en periodo y acumulado
            lineas.stream()
                    .filter(linea -> !linea.getDebitoPeriodo().equals(BigDecimal.ZERO)
                    || !linea.getCreditoPeriodo().equals(BigDecimal.ZERO)
                    || !linea.getDebitoAcumulado().equals(BigDecimal.ZERO)
                    || !linea.getCreditoAcumulado().equals(BigDecimal.ZERO))
                    .forEach(linea -> {
                        model.addRow(new Object[]{
                            linea.getCodigo(),
                            linea.getDescripcion(),
                            linea.getDebitoPeriodo(),
                            linea.getCreditoPeriodo(),
                            linea.getDebitoAcumulado(),
                            linea.getCreditoAcumulado()});
                    });

            // Ajustar las columnas de la tabla
            new TableColumnAdjuster(tableBalance).adjustColumns();
        } catch (Exception ex) {
            log.error("Error al inicializar la interfaz: {}", ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, "Ocurrió un error al inicializar la interfaz.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
