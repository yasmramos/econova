package com.univsoftdev.econova.component.wizard;

import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import com.univsoftdev.econova.SwingUtils;

public class UnidadWizardStep extends WizardPage{

    public UnidadWizardStep() {
        super("Nueva Unidad", "Nueva Unidad");
        initComponents();
        modalAdicionarUnidadContable1.getButtonAceptar().setVisible(false);
        modalAdicionarUnidadContable1.getButtonCancelar().setVisible(false);
    }

    @Override
    public boolean onNext(WizardSettings settings) {
        String codigo = SwingUtils.getValue(modalAdicionarUnidadContable1.getTextFieldCodigo());
        String nombre = SwingUtils.getValue(modalAdicionarUnidadContable1.getTextFieldNombre());
        String direccion = SwingUtils.getValue(modalAdicionarUnidadContable1.getTextFieldDireccion());
        String correo = SwingUtils.getValue(modalAdicionarUnidadContable1.getTextFieldCorreo());
        return true;
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modalAdicionarUnidadContable1 = new com.univsoftdev.econova.config.view.ModalAdicionarUnidadContable();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(modalAdicionarUnidadContable1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(modalAdicionarUnidadContable1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.univsoftdev.econova.config.view.ModalAdicionarUnidadContable modalAdicionarUnidadContable1;
    // End of variables declaration//GEN-END:variables
}
