package com.univsoftdev.econova.contabilidad.views.comprobante;

import java.awt.Component;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class EditorMoneda extends DefaultCellEditor {

    private final NumberFormat formatoMoneda;

    public EditorMoneda() {
        super(new JTextField());
        formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "US"));
        formatoMoneda.setMinimumFractionDigits(2);
        formatoMoneda.setMaximumFractionDigits(2);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        JTextField field = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (value != null && value.toString().startsWith("$")) {
            field.setText(value.toString().replace("$", "").replace(",", ""));
        } else {
            field.setText("");
        }
        return field;
    }

    @Override
    public boolean stopCellEditing() {
        final JTextField field = (JTextField) getComponent();
        final String texto = field.getText().trim();

        if (!texto.isEmpty()) {
            try {
                final double valor = Double.parseDouble(texto);
                if (valor < 0) {
                    JOptionPane.showMessageDialog(null, "Solo se permiten números positivos.");
                    return false;
                }
                field.setText(formatoMoneda.format(valor));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Ingrese un número válido.");
                return false;
            }
        }
        return super.stopCellEditing();
    }
}
