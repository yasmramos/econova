package com.univsoftdev.econova.core.utils.table;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author UnivSoftDev Team
 */
public class TableColumnAdjuster {

    private final JTable table;
    private final int spacing;
    private final boolean isColumnHeaderIncluded;
    private final boolean isColumnDataIncluded;

    public TableColumnAdjuster(JTable table) {
        this.table = table;
        this.spacing = 6;
        this.isColumnHeaderIncluded = true;
        this.isColumnDataIncluded = true;
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    public void adjustColumns() {
        TableColumnModel tcm = table.getColumnModel();
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            adjustColumns(i);
        }
    }

    public void adjustColumns(int column) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (tableColumn.getResizable()) {
            int preferredWidth = calculateColumnWidth(column);
            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    private int calculateColumnWidth(int column) {
        int columnHeaderWidth = getColumnHeaderWidth(column);
        int columnDataWidth = getColumnDataWidth(column);
        return Math.max(columnHeaderWidth, columnDataWidth) + spacing;
    }

    private int getColumnHeaderWidth(int column) {
        if (!isColumnHeaderIncluded) {
            return 0;
        }
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        Component comp = renderer.getTableCellRendererComponent(table, value, false, false, 0, column);
        return comp.getPreferredSize().width;
    }

    private int getColumnDataWidth(int column) {
        if (!isColumnDataIncluded) {
            return 0;
        }

        int maxWidth = 0;

        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
            Component comp = table.prepareRenderer(cellRenderer, row, column);
            int width = comp.getPreferredSize().width + table.getIntercellSpacing().width;
            maxWidth = Math.max(maxWidth, width);
        }

        return maxWidth;
    }

}
