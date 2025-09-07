package com.univsoftdev.econova;

import com.licify.Licify;
import com.univsoftdev.econova.core.FileUtils;
import com.univsoftdev.econova.core.config.AppConfig;
import com.univsoftdev.econova.core.utils.DialogUtils;
import java.io.File;
import java.security.KeyPair;
import java.time.LocalDateTime;
import javax.swing.JDialog;
import raven.modal.component.SimpleModalBorder;

public class FormLicense extends javax.swing.JFrame {
    
    private String trialLicenseFile = FileUtils.APP_DATA + "trial.lic";
    Licify.License license;
    
    public FormLicense() {
        initComponents();
        try {
            if (new File(trialLicenseFile).exists()) {
                license = Licify.load(trialLicenseFile);
                if (license.isExpired()) {
                    btnTrialPeriod.setEnabled(false);
                }
            }
        } catch (Exception ex) {
            System.getLogger(FormLicense.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnRegister = new javax.swing.JButton();
        btnTrialPeriod = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnRegister.setText("Register");
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        btnTrialPeriod.setText("Trial Period");
        btnTrialPeriod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTrialPeriodActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(227, Short.MAX_VALUE)
                .addComponent(btnRegister)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTrialPeriod)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(271, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTrialPeriod)
                    .addComponent(btnRegister))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
        JDialog registerDialog = DialogUtils.createDialog(new RegisterForm(), "Register " + AppConfig.getAppName() + " License Key", true);
        registerDialog.setVisible(true);
    }//GEN-LAST:event_btnRegisterActionPerformed

    private void btnTrialPeriodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTrialPeriodActionPerformed
        try {
            
            Licify.License trialLicense = Licify.trialLicense()
                    .licenseeName("Usuario de Prueba")
                    .licenseeEmail("prueba@test.com")
                    .productId("ECONOVA-001")
                    .productVersion("1.0")
                    .expirationDate(Licify.createExpirationDate(30, 0, 0))
                    .maxUsers(1)
                    .feature("BASIC_FEATURES")
                    .feature("LIMITED_ACCESS")
                    .build();
            
            KeyPair keyPair = Licify.generateKeyPair();
            Licify.savePrivateKeyToFile(keyPair.getPrivate(), trialLicenseFile);
            Licify.savePublicKeyToFile(keyPair.getPublic(), trialLicenseFile);
            Licify.sign(trialLicense, keyPair);
            Licify.save(trialLicense, trialLicenseFile);
            
            btnTrialPeriod.setEnabled(false);
            AppConfig.setTrial(true);
            AppConfig.setTrialStartDate(LocalDateTime.now());
            AppConfig.setTrialEndDate(LocalDateTime.now().plusDays(30L));
            DialogUtils.showInfoDialog(this, "Se ha iniciado correctamente el período de Prueba por 30 días", "Info", SimpleModalBorder.CLOSE_OPTION);
            this.dispose();
        } catch (Exception ex) {
            DialogUtils.showErrorDialog(this, trialLicenseFile, trialLicenseFile, SimpleModalBorder.CLOSE_OPTION);
        }
    }//GEN-LAST:event_btnTrialPeriodActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FormLicense().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRegister;
    private javax.swing.JButton btnTrialPeriod;
    // End of variables declaration//GEN-END:variables
}
