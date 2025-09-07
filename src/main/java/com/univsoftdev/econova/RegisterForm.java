package com.univsoftdev.econova;

import com.licify.Licify;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterForm extends JPanel {

    public RegisterForm() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        btnLicenseFile = new javax.swing.JButton();
        btnPaste = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jLabel1.setText("Usted necesita una licencia válida para usar Econova Descktock");

        jLabel2.setText("Llave de Licencia");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel3.setText("Copia y pega (o arrastra y suelta) su código de licencia aqui.");

        btnLicenseFile.setText("License File ...");
        btnLicenseFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLicenseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLicenseFileActionPerformed(evt);
            }
        });

        btnPaste.setText("Paste");
        btnPaste.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasteActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnOk.setText("Ok");
        btnOk.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOk.setEnabled(false);

        jLabel4.setText("Gracias por escoger Econova.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabel4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnOk)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnCancel))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabel3)
                    .addGap(18, 18, 18)
                    .addComponent(btnPaste)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnLicenseFile)))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel2)
            .addComponent(jLabel1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(btnLicenseFile)
                    .addComponent(btnPaste))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk)
                    .addComponent(jLabel4))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasteActionPerformed
        jTextArea1.setText("");
        jTextArea1.paste();
    }//GEN-LAST:event_btnPasteActionPerformed

    private void btnLicenseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLicenseFileActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "License File", "lic", "bin", "txt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            var selectedLicenseFile = chooser.getSelectedFile().toString();
            var extencion = selectedLicenseFile.substring(selectedLicenseFile.lastIndexOf(".") + 1);
            System.out.println(extencion);
            try {
                Licify.License license = Licify.load(selectedLicenseFile);
                if (Licify.isValidLicense(license)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Licensee Name: ").append(license.getLicenseeName()).append("\n");
                    sb.append("Licensee Email: ").append(license.getLicenseeEmail()).append("\n");
                    sb.append("Expiration Date: ").append(license.getExpirationDate()).append("\n");
                    sb.append("--------").append("\n");
                    sb.append(license.getSignature()).append("\n");
                    jTextArea1.setText(sb.toString());
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid License Key", "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }

    }//GEN-LAST:event_btnLicenseFileActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnLicenseFile;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnPaste;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
