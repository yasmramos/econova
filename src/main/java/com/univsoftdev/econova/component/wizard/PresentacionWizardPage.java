package com.univsoftdev.econova.component.wizard;

import com.github.cjwizard.WizardPage;

public class PresentacionWizardPage extends WizardPage {

    public PresentacionWizardPage() {
        super("Presentación", "Presentación");
        initComponents();
        setFinishEnabled(false);
        setNextEnabled(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        startButton = new javax.swing.JButton();

        jScrollPane1.setBorder(null);

        jTextPane1.setEditable(false);
        jTextPane1.setContentType("text/html"); // NOI18N
        jTextPane1.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N
        jTextPane1.setText("<html>\r\n  <head>\r\n\r\n  </head>\r\n  <body>\r\n    <center>\n          <p style=\"margin-top: 0; text-align: justify\">\n              Bienvenido al Asistente de Configuración\n<p style=\"margin-top: 0; text-align: justify\">\n                                           de \n</p>\n<p style=\"margin-top: 0; text-align: justify\">\n                                      Econova.\n</p>\n          </p>\n   </center>\n  </body>\r\n</html>\r\n");
        jScrollPane1.setViewportView(jTextPane1);

        startButton.setBackground(new java.awt.Color(51, 102, 255));
        startButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        startButton.setForeground(new java.awt.Color(255, 255, 255));
        startButton.setText("Comenzar");
        startButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(614, Short.MAX_VALUE)
                .addComponent(startButton)
                .addGap(16, 16, 16))
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 616, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnComenzarActionPerformed(java.awt.event.ActionEvent evt) {
        // Avanza al siguiente paso del wizard
        if (getController() != null) {
            getController().next();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
