package com.univsoftdev.econova.contabilidad.views.comprobante;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CodigoSoloRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (value instanceof ItemCombo itemCombo) {
            value = itemCombo.getCodigo();
        } else if (value != null && value.toString().contains(" ")) {
            value = value.toString().split(" ")[0];
        }

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(SwingConstants.LEFT);
        return this;
    }
}
