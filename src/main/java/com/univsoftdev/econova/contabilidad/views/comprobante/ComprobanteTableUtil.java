package com.univsoftdev.econova.contabilidad.views.comprobante;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

public class ComprobanteTableUtil {

    private static final NumberFormat formatoMoneda;

    static {
        formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "US"));
        formatoMoneda.setMinimumFractionDigits(2);
        formatoMoneda.setMaximumFractionDigits(2);
    }

    public static void configurarEventos(JTable table1, DefaultTableModel modelo, JTextField textFieldTotalDebito, JTextField textFieldTotalCredito) {
        table1.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "accionEnter");

        table1.getActionMap().put("accionEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarEnter(table1, modelo);
            }
        });

        table1.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "accionTab");

        table1.getActionMap().put("accionTab", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ComprobanteTableUtil.avanzarCelda(table1, modelo);
            }
        });

        modelo.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int fila = e.getFirstRow();
                int columna = e.getColumn();

                if (columna == 5 || columna == 6) {
                    Object valor = modelo.getValueAt(fila, columna);
                    if (valor != null && !valor.toString().isEmpty()) {
                        modelo.setValueAt("", fila, (columna == 5) ? 6 : 5);
                    }
                }

                actualizarTotales(modelo, textFieldTotalDebito, textFieldTotalCredito);
            }
        });
        table1.setShowGrid(true);
        table1.setGridColor(Color.gray);
        table1.setIntercellSpacing(new Dimension(1, 1));
    }

    public static void actualizarTotales(DefaultTableModel modelo, JTextField textFieldTotalDebito, JTextField textFieldTotalCredito) {
        double totalDebito = 0.0;
        double totalCredito = 0.0;

        for (int i = 0; i < modelo.getRowCount(); i++) {
            try {
                String debito = (String) modelo.getValueAt(i, 5);
                String credito = (String) modelo.getValueAt(i, 6);

                if (debito != null && !debito.isEmpty()) {
                    totalDebito += Double.parseDouble(debito.replace("$", "").replace(",", ""));
                }

                if (credito != null && !credito.isEmpty()) {
                    totalCredito += Double.parseDouble(credito.replace("$", "").replace(",", ""));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        textFieldTotalDebito.setText(formatoMoneda.format(totalDebito));
        textFieldTotalCredito.setText(formatoMoneda.format(totalCredito));
    }

    public static void manejarEnter(JTable table1, DefaultTableModel modelo) {
        int fila = table1.getSelectedRow();
        int columna = table1.getSelectedColumn();

        if (table1.isEditing()) {
            if (!table1.getCellEditor().stopCellEditing()) {
                return;
            }

            Object valor = table1.getValueAt(fila, columna);
            modelo.setValueAt(valor, fila, columna);
        }

        if (columna >= 0 && columna <= 4) {
            Object valorActual = modelo.getValueAt(fila, columna);
            if (valorActual == null || valorActual.toString().trim().isEmpty()) {
                copiarValorSuperior(modelo, fila, columna);
            }
        }

        if (columna == 5 || columna == 6) {
            Object valor = modelo.getValueAt(fila, columna);
            if (columna == 6 && (valor == null || valor.toString().trim().isEmpty())) {
                Component editor = table1.getEditorComponent();
                if (editor instanceof JTextField jTextField) {
                    jTextField.selectAll();
                }
                return;
            } else if (columna == 5 && (valor == null || valor.toString().trim().isEmpty())) {
                // Permitir avanzar a Crédito
            } else {
                int otraColumna = (columna == 5) ? 6 : 5;
                modelo.setValueAt("", fila, otraColumna);
                ComprobanteTableUtil.agregarNuevaFila(table1, modelo);
                return;
            }
        }

        ComprobanteTableUtil.avanzarCelda(table1, modelo);
    }

    public static void copiarValorSuperior(DefaultTableModel modelo, int fila, int columna) {
        if (fila > 0) {
            Object valorSuperior = modelo.getValueAt(fila - 1, columna);
            if (valorSuperior != null && !valorSuperior.toString().isEmpty()) {
                modelo.setValueAt(valorSuperior.toString(), fila, columna);
            }
        }
    }

    public static void agregarNuevaFila(JTable table1, DefaultTableModel modelo) {
        modelo.addRow(new Object[]{"", "", "", "", "", "", ""});
        int nuevaFila = table1.getRowCount() - 1;

        table1.setRowSelectionInterval(nuevaFila, nuevaFila);
        table1.setColumnSelectionInterval(0, 0);
        table1.editCellAt(nuevaFila, 0);

        Component editor = table1.getEditorComponent();
        if (editor != null) {
            editor.requestFocusInWindow();
            if (editor instanceof JTextField jTextField) {
                jTextField.selectAll();
            }
        }

        table1.scrollRectToVisible(table1.getCellRect(nuevaFila, 0, true));
    }

    public static void avanzarCelda(JTable table1, DefaultTableModel modelo) {
        if (table1.isEditing()) {
            table1.getCellEditor().stopCellEditing();
        }

        int filaActual = table1.getSelectedRow();
        int columnaActual = table1.getSelectedColumn();

        if (filaActual == -1 || columnaActual == -1) {
            filaActual = 0;
            columnaActual = 0;
        } else {
            columnaActual++;
            if (columnaActual >= table1.getColumnCount()) {
                columnaActual = 0;
                filaActual++;
            }
        }

        if (filaActual >= table1.getRowCount()) {
            ComprobanteTableUtil.agregarNuevaFila(table1, modelo);
            return;
        }

        table1.setRowSelectionInterval(filaActual, filaActual);
        table1.setColumnSelectionInterval(columnaActual, columnaActual);
        table1.editCellAt(filaActual, columnaActual);

        Component editor = table1.getEditorComponent();
        if (editor != null) {
            editor.requestFocusInWindow();
            if (editor instanceof JTextField jTextField) {
                jTextField.selectAll();
            }
        }

        table1.scrollRectToVisible(table1.getCellRect(filaActual, columnaActual, true));
    }
}
