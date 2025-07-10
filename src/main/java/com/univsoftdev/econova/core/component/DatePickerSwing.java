package com.univsoftdev.econova.core.component;

import java.time.LocalDate;
import javax.swing.JFormattedTextField;
import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;

public class DatePickerSwing extends javax.swing.JPanel {

    private static final long serialVersionUID = 4720808086799582359L;
    private final DatePicker datePicker;

    public DatePickerSwing() {
        initComponents();
        setLayout(new MigLayout("wrap,fillx", "[fill]"));
        this.datePicker = new DatePicker();
        this.datePicker.now();
        this.datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);
        JFormattedTextField dateEditor = new JFormattedTextField();
        datePicker.setEditor(dateEditor);
        datePicker.setCloseAfterSelected(true);
        add(dateEditor, "width 200");
    }
   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    public void setSelectedDate(LocalDate date) {
        datePicker.setSelectedDate(date);
    }

    public LocalDate getSelectedDate() {
        return datePicker.getSelectedDate();
    }
    
    public String getSelectedDateAsString() {
        return datePicker.getSelectedDateAsString();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
