package com.univsoftdev.econova.contabilidad.views.comprobante.dto;

import javax.swing.table.DefaultTableModel;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AsientoRowData {

    private String cta;
    private String sbcta;
    private String sctro;
    private String anal;
    private String epig;
    private Object debito;
    private Object credito;
    private int filaIndex;

    public static AsientoRowData fromTableModel(DefaultTableModel table, int row) {
        return AsientoRowData.builder()
                .cta(getStringValue(table, row, 0))
                .sbcta(getStringValue(table, row, 1))
                .sctro(getStringValue(table, row, 2))
                .anal(getStringValue(table, row, 3))
                .epig(getStringValue(table, row, 4))
                .debito(table.getValueAt(row, 5))
                .credito(table.getValueAt(row, 6))
                .filaIndex(row)
                .build();
    }

    private static String getStringValue(DefaultTableModel table, int row, int col) {
        Object value = table.getValueAt(row, col);
        return (value != null) ? value.toString().trim() : null;
    }
    
    
}
