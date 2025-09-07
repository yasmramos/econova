package com.univsoftdev.econova.component.wizard;

import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import com.univsoftdev.econova.config.service.ConfigService;
import com.univsoftdev.econova.core.Injector;
import java.util.List;
import javax.swing.JOptionPane;

public class UsuariosWizardPage extends WizardPage {

    private final ConfigService configService;

    public UsuariosWizardPage() {
        super("Gestión de Usuarios", "Almost done");
        initComponents();
        setNextEnabled(true);
        setFinishEnabled(false);
        configService = Injector.get(ConfigService.class);
    }

    @Override
    public void rendering(List<WizardPage> path, WizardSettings settings) {
        setNextEnabled(false);
        setFinishEnabled(true);
        super.rendering(path, settings);
    }

    @Override
    public boolean onNext(WizardSettings settings) {
        try {
            configService.createUserPrincipal(
                    "admin",
                    "admin",
                    "admin@jwehgj.ref",
                    "admin123".toCharArray()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel1.setText("<html>Step 4<br>You are almost there</html>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(379, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
