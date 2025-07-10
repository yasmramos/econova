package com.univsoftdev.econova.component.wizard;

import com.github.cjwizard.WizardPage;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

public class PresentacionWizardStep extends WizardPage {

    public PresentacionWizardStep() {
        super("Presentación", "Presentación");
        initComponents();
        setFinishEnabled(false);
        setNextEnabled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image imagen = Toolkit.getDefaultToolkit().getImage("econova_presentacion.png");
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1024, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 486, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnComenzarActionPerformed(java.awt.event.ActionEvent evt) {
        // Avanza al siguiente paso del wizard
        if (getController() != null) {
            getController().next();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
