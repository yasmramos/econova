package com.univsoftdev.econova.contabilidad.views.comprobante;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import com.univsoftdev.econova.contabilidad.model.Account;

public class ComprobanteComboBoxCellEditor extends DefaultCellEditor {

    private static final long serialVersionUID = -8420066090242012622L;
    private final JComboBox<ItemCombo> comboBox;
    private JTable tabla;
    private int fila;
    private int columna;
    private boolean actualizando = false;
    private TableCellEditor editorOriginalColumnas5y6; // Almacena los editores originales

    public ComprobanteComboBoxCellEditor(ItemCombo[] items) {
        super(new JComboBox<>(items));
        this.comboBox = (JComboBox<ItemCombo>) editorComponent;
        this.comboBox.setEditable(true);
        this.comboBox.addItemListener(crearItemListener());
        this.editorOriginalColumnas5y6 = this;
        configurarEventoEnter();
    }

    public ComprobanteComboBoxCellEditor(ItemCombo[] items, TableCellEditor editorOriginal) {
        super(new JComboBox<>(items));
        this.comboBox = (JComboBox<ItemCombo>) editorComponent;
        this.comboBox.setEditable(true);
        this.comboBox.addItemListener(crearItemListener());
        this.editorOriginalColumnas5y6 = editorOriginal;
        configurarEventoEnter();
    }

    private void configurarEventoEnter() {
        comboBox.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (comboBox.getSelectedItem() instanceof ItemCombo selectedItem) {
                        Account cuenta = (selectedItem != null) ? selectedItem.getCuenta() : null;
                        List<Account> subCuentas = (cuenta != null) ? cuenta.getSubAccounts() : null;

                        // Si no hay subcuentas, salta automáticamente a la columna 5
                        if (subCuentas == null || subCuentas.isEmpty()) {
                            SwingUtilities.invokeLater(() -> {
                                if (tabla != null && tabla.getRowCount() > fila) {
                                    tabla.changeSelection(fila, 5, false, false);
                                    tabla.editCellAt(fila, 5);
                                    Component comp = tabla.getEditorComponent();
                                    if (comp != null) {
                                        comp.requestFocusInWindow();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void bloquearColumnasIntermedias(int desde, int hasta) {
        for (int col = desde; col <= hasta; col++) {
            // Limpiar valor
            tabla.getModel().setValueAt("", fila, col);

            // Asignar un editor vacío o no editable
            tabla.getColumnModel().getColumn(col).setCellEditor(new DefaultCellEditor(new JTextField()) {
                @Override
                public boolean isCellEditable(EventObject e) {
                    return false; // No editable
                }
            });
        }

        // Enfocar automáticamente la columna 5
        SwingUtilities.invokeLater(() -> {
            if (tabla != null && tabla.getRowCount() > fila) {
                tabla.changeSelection(fila, 5, false, false);
                tabla.editCellAt(fila, 5);
                Component comp = tabla.getEditorComponent();
                if (comp != null) {
                    comp.requestFocusInWindow();
                }
            }
        });
    }

    private ItemListener crearItemListener() {
        return e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !actualizando) {
                actualizando = true;
                try {
                    if (e.getItem() instanceof ItemCombo selected) {
                        SwingUtilities.invokeLater(() -> {
                            if (columna < 4) { // Solo actualizar si es columna 0-4
                                actualizarColumnasDependientes(selected);
                            }
                        });
                    }
                } finally {
                    actualizando = false;
                }
            }
        };
    }

    private void actualizarColumnasDependientes(ItemCombo itemSeleccionado) {
        if (tabla == null || columna >= 4) {
            return;
        }

        Account cuenta = itemSeleccionado.getCuenta();
        List<Account> subCuentas = (cuenta != null) ? cuenta.getSubAccounts() : null;

        if (subCuentas == null || subCuentas.isEmpty()) {
            // Si no hay subcuentas, bloquear columnas 1-4 y saltar a la 5
            bloquearColumnasIntermedias(columna + 1, 4);
        } else {
            // Si hay subcuentas, limpiar y habilitar edición
            limpiarColumnasIntermedias(columna + 1, 4);
            configurarColumnaCombo(columna + 1, subCuentas);
        }
    }

    private void limpiarColumnasIntermedias(int desdeColumna, int hastaColumna) {
        for (int col = desdeColumna; col <= hastaColumna; col++) {
            tabla.getModel().setValueAt("", fila, col);
            tabla.getColumnModel().getColumn(col).setCellEditor(new ComprobanteComboBoxCellEditor(new ItemCombo[]{new ItemCombo(new Account("", ""))}, editorOriginalColumnas5y6));
        }
    }

    private void configurarColumnaCombo(int columnaDestino, List<Account> subCuentas) {
        ItemCombo[] items = new ItemCombo[subCuentas.size() + 1];
        items[0] = new ItemCombo(new Account("", ""));
        for (int i = 1; i < subCuentas.size(); i++) {
            items[i + 1] = new ItemCombo(subCuentas.get(i));
        }

        TableCellEditor editor = new ComprobanteComboBoxCellEditor(items, editorOriginalColumnas5y6);
        tabla.getColumnModel().getColumn(columnaDestino).setCellEditor(editor);

        if (!subCuentas.isEmpty()) {
            String primerCodigo = subCuentas.get(0).getCodigoSinCuentaPadre();
            tabla.getModel().setValueAt(primerCodigo, fila, columnaDestino);
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        // Si es columna 5 o 6, usar el editor original
        if (column >= 5) {
            return editorOriginalColumnas5y6.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        this.tabla = table;
        this.fila = row;
        this.columna = column;

        try {
            actualizando = true;
            if (value instanceof String strValue) {
                String input = strValue.trim();
                boolean encontrado = false;

                for (int i = 0; i < comboBox.getItemCount(); i++) {
                    ItemCombo item = comboBox.getItemAt(i);
                    if (input.equals(item.getCodigo())) {
                        comboBox.setSelectedItem(item);
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    comboBox.getEditor().setItem(input);
                }
            } else if (value instanceof ItemCombo) {
                comboBox.setSelectedItem(value);
            } else {
                comboBox.setSelectedIndex(0);
            }

            Object selected = comboBox.getSelectedItem();
            if (selected instanceof ItemCombo itemCombo) {
                comboBox.getEditor().setItem(itemCombo.getCodigo());
            }
        } finally {
            actualizando = false;
        }

        return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        if (columna >= 5) {
            return editorOriginalColumnas5y6.getCellEditorValue();
        }

        Object selected = comboBox.getSelectedItem();

        if (selected instanceof ItemCombo itemCombo) {
            return itemCombo.getCodigo();
        }

        String text = selected != null ? selected.toString().trim() : "";

        if (!text.isEmpty()) {
            for (int i = 0; i < comboBox.getItemCount(); i++) {
                ItemCombo item = comboBox.getItemAt(i);
                if (text.equals(item.getCodigo())) {
                    return item.getCodigo();
                }
            }

           // mostrarErrorValidacion();
            return comboBox.getEditor().getItem();
        }

        return text;
    }

    private void mostrarErrorValidacion() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    "Valor inválido. Por favor, ingrese un código válido.",
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public boolean stopCellEditing() {
        if (columna >= 5) {
            return editorOriginalColumnas5y6.stopCellEditing();
        }

        Object value = getCellEditorValue();

        if (value == null || value.toString().trim().isEmpty()) {
            return super.stopCellEditing();
        }

        String codigo = value.toString().trim();
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ItemCombo item = comboBox.getItemAt(i);
            if (codigo.equals(item.getCodigo())) {
                return super.stopCellEditing();
            }
        }

        return false;
    }
}
