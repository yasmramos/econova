package com.univsoftdev.econova.core.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.univsoftdev.econova.core.utils.table.TableHeaderAlignment;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.*;

/**
 * @author UnivSoftDev
 */
public class BasicTable extends JPanel {

    private static final long serialVersionUID = -8078318041547988159L;
    private transient Object[] columnsName;
    private final JTable table;
    private final JScrollPane scrollPane;
    private final JLabel title;
    private final DefaultTableModel model;

    public BasicTable() {
        initComponents();
        this.setLayout(new MigLayout("fillx,wrap,insets 10 0 10 0", "[fill]", "[]0[fill,grow]"));
        this.columnsName = new Object[]{};
        this.model = new DefaultTableModel() {
            private static final long serialVersionUID = -6493520122392879229L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(model);
        this.scrollPane = new JScrollPane(this.table);
        this.scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // alignment table header
        table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table) {
            @Override
            protected int getAlignment(int column) {
                if (column == 0) {
                    return SwingConstants.CENTER;
                }
                return SwingConstants.LEADING;
            }
        });
        
        // style
        this.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "background:$Table.background;");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:20;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;");
        table.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:30;"
                + "showHorizontalLines:true;"
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionInactiveBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:$ScrollBar.thumbArc;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");
        title = new JLabel("");
        title.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:bold +2");
        //this.add(title, "gapx 20");
        this.add(scrollPane);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public Object[] getColumnsName() {
        return columnsName;
    }

    public void setColumnsName(Object[] columnsName) {
        this.columnsName = columnsName;
        for (Object object : columnsName) {
            this.model.addColumn(object);
        }
    }

    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off

	//======== this ========
	setLayout(new MigLayout(
	    "insets 0,hidemode 3", //NOI18N
	    // columns
	    "[fill]", //NOI18N
	    // rows
	    "[fill]")); //NOI18N
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
