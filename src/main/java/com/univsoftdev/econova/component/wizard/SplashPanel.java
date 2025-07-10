package com.univsoftdev.econova.component.wizard;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SplashPanel extends javax.swing.JPanel {
    
    private final JPanel mainPanel;
    
    public SplashPanel(JPanel mainPanel) {
        initComponents();
        this.mainPanel = mainPanel;
        startButton.addActionListener((ActionEvent e) -> {
            showWizard(); // Cambiar al wizard
        });
    }
    
    private void showWizard() {
        final CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "wizard");
    }
    
    @Override
    public void paint(Graphics g) {
        try {
            super.paint(g);
            final Image imagen = ImageIO.read(getClass().getClassLoader().getResourceAsStream("econova_presentacion.png"));
            if (imagen != null) {
                final Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        welcomeLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        startButton.setText("Comenzar");

        buttonPanel.add(startButton);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);

        welcomeLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        welcomeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        welcomeLabel.setText("Bienvenido al Asistente");
        add(welcomeLabel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JLabel welcomeLabel;
    // End of variables declaration//GEN-END:variables
}
