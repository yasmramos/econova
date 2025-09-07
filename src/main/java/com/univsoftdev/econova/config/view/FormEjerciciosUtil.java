package com.univsoftdev.econova.config.view;

import com.univsoftdev.econova.core.AppContext;
import com.univsoftdev.econova.core.Injector;
import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.config.service.EjercicioService;
import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormEjerciciosUtil {

    private static ListSelectionListener currentListener;

    public static void setupTableSelectionListener(JTable tableEjercicios, JTable tablePeriodos) {
        // Remover listener anterior si existe
        if (currentListener != null) {
            tableEjercicios.getSelectionModel().removeListSelectionListener(currentListener);
        }

        // Crear nuevo listener
        currentListener = (ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {

                final int selectedRow = tableEjercicios.getSelectedRow();

                if (selectedRow != -1) {
                    
                    final Object yearValue = tableEjercicios.getValueAt(selectedRow, 0);

                    if (yearValue != null) {
                        updatePeriodosTable(tablePeriodos, yearValue.toString());
                    } else {
                        clearTable(tablePeriodos);
                        showMessage("El ejercicio seleccionado no tiene año definido");
                    }
                }
            }
        };

        // Agregar listener a la tabla
        tableEjercicios.getSelectionModel().addListSelectionListener(currentListener);

        // Seleccionar primera fila al iniciar (si hay datos)
        if (tableEjercicios.getRowCount() > 0) {
            tableEjercicios.setRowSelectionInterval(0, 0);
        }
    }

    private static void updatePeriodosTable(JTable tablePeriodos, String yearStr) {
        clearTable(tablePeriodos);

        final var ejercicioService = Injector.get(EjercicioService.class);

        try {
            int year = Integer.parseInt(yearStr);
            final Optional<Exercise> ejercicioOpt = ejercicioService.findByYear(year);

            if (ejercicioOpt.isPresent()) {
                var periodos = ejercicioOpt.get().getPeriodos();

                if (periodos.isEmpty()) {
                    showMessage("Este ejercicio no tiene períodos registrados");
                } else {
                    final var model = (DefaultTableModel) tablePeriodos.getModel();
                    for (final Period periodo : periodos) {
                        model.addRow(new Object[]{
                            periodo.getName(),
                            periodo.getStartDate(),
                            periodo.getEndDate()
                        });
                    }
                }
            } else {
                showMessage("No se encontró el ejercicio seleccionado");
            }
        } catch (NumberFormatException e) {
            showMessage("Formato de año inválido: " + yearStr);
        }
    }

    private static void clearTable(JTable table) {
        final var model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
    }

    private static void showMessage(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Información",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Actualiza la vista de Ejercicios y Periodos
     *
     * @param tableEjercicios Tabla de Ejercicios a Actualizar
     * @param tablePeriodos Tabla de Periodos a Actualizar
     */
    public static void updateView(JTable tableEjercicios, JTable tablePeriodos) {
        // Clear tables if they contain data
        final var modelEjercicios = (DefaultTableModel) tableEjercicios.getModel();
        modelEjercicios.setRowCount(0); // Better way to clear the table

        final var modelPeriodos = (DefaultTableModel) tablePeriodos.getModel();
        modelPeriodos.setRowCount(0);

        // Load exercises if any are registered
        final var ejercService = Injector.get(EjercicioService.class);
        final var ejercicios = ejercService.findAll();

        for (final var ejercicio : ejercicios) {
            modelEjercicios.addRow(new Object[]{
                ejercicio.getName(),
                ejercicio.getStartDate(),
                ejercicio.getEndDate()
            });
        }

        // Load Periods corresponding to the Exercise
        if (tableEjercicios.getRowCount() > 0) {
            tableEjercicios.setRowSelectionInterval(0, 0);

            final int selectedRow = tableEjercicios.getSelectedRow();
            final Object yearValue = tableEjercicios.getValueAt(selectedRow, 0);

            if (yearValue != null) {
                try {
                    final var yearStr = yearValue.toString();

                    final var ejercicio = ejercService.findByYear(Integer.parseInt(yearStr));

                    if (ejercicio.isPresent()) {

                        final var periodos = ejercicio.get().getPeriodos();

                        for (final var periodo : periodos) {
                            modelPeriodos.addRow(new Object[]{
                                periodo.getName(),
                                periodo.getStartDate(),
                                periodo.getEndDate()
                            });
                        }
                    }
                } catch (NumberFormatException e) {
                    log.error("Invalid year format: " + yearValue);
                }
            }
        }
    }
}
