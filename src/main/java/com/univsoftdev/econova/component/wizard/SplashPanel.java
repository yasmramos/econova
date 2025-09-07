package com.univsoftdev.econova.component.wizard;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SplashPanel extends javax.swing.JPanel {

    private final JPanel mainPanel;

    public SplashPanel(JPanel mainPanel) {
        initComponents();
        textPane.setFocusable(false);
        textPane.setCaret(new DefaultCaret() {
            @Override
            public void setVisible(boolean visible) {
            }

            @Override
            public void paint(Graphics g) {
            }
        });

        this.mainPanel = mainPanel;
        startButton.addActionListener((ActionEvent e) -> {
            showWizard(); // Cambiar al wizard
        });

        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule("body {font-family: Arial, sans-serif; font-size: 12pt; color: #333333; margin: 10px;}");
        styleSheet.addRule("p {margin: 0 0 10px 0; text-align: justify; line-height: 1.5;}");
        styleSheet.addRule(".highlight {color: #2E86C1; font-weight: bold;}");

        HTMLEditorKit htmlKit = (HTMLEditorKit) textPane.getEditorKit();
        htmlKit.setStyleSheet(styleSheet);
    }

    private void showWizard() {
        final CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "wizard");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();

        startButton.setBackground(new java.awt.Color(51, 102, 255));
        startButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        startButton.setForeground(new java.awt.Color(255, 255, 255));
        startButton.setText("Comenzar");
        startButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jScrollPane1.setBorder(null);

        textPane.setBackground(new java.awt.Color(242, 242, 242));
        textPane.setContentType("text/html"); // NOI18N
        textPane.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N
        textPane.setText("<html>\n  <head>\n\n  </head>\n  <body>\n    <center>\n          <p style=\"margin-top: 0; text-align: justify\">\n              Bienvenido al Asistente de Configuración\n<p style=\"margin-top: 0; text-align: justify\">\n                                           de \n</p>\n<p style=\"margin-top: 0; text-align: justify\">\n                                      Econova.\n</p>\n          </p>\n   </center>\n  </body>\n</html>\n");
        jScrollPane1.setViewportView(textPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 708, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton startButton;
    private javax.swing.JTextPane textPane;
    // End of variables declaration//GEN-END:variables
}
