package com.univsoftdev.econova.contabilidad.views;

import java.awt.*;
import javax.swing.border.*;
import com.univsoftdev.econova.core.component.*;
import com.univsoftdev.econova.core.system.Form;

public class FormContabilidad extends Form{

    private static final long serialVersionUID = -8563591281118017341L;

    public FormContabilidad() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
	this.basicTable1 = new BasicTable();

	//======== this ========
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());
	add(this.basicTable1, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BasicTable basicTable1;
    // End of variables declaration//GEN-END:variables
}
