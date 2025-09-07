package com.univsoftdev.econova;

import com.univsoftdev.econova.core.LookAndFeelUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.Serial;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Splash extends javax.swing.JDialog {

    @Serial
    private static final long serialVersionUID = 5114217418452901907L;
    private final ScheduledExecutorService executor;
    private static final String LOGO_PATH = "/logo.png"; // Cambia por tu logo real si lo tienes
    private final ImageIcon logoIcon;

    public Splash(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setUndecorated(true);
        logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(LOGO_PATH))); // Si no tienes logo, ignora la excepción
        initComponents();
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void paint(@NotNull Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo con degradado profesional - BORDES RECTOS
        LinearGradientPaint paint = new LinearGradientPaint(0, 0, 0, getHeight(),
                new float[]{0f, 0.5f, 1f},
                new Color[]{
                    new Color(5, 38, 89),
                    new Color(24, 84, 145),
                    new Color(44, 130, 201)
                }
        );
        g2.setPaint(paint);
        // Rectángulo con bordes rectos
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Línea decorativa en la parte inferior
        g2.setColor(new Color(255, 255, 255, 100));
        g2.fillRect(0, getHeight() - 4, getWidth(), 2);

        g2.dispose();
        super.paint(g);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        curvesPanel1 = new com.univsoftdev.econova.component.CurvesPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dibuja el logo centrado arriba
                if (logoIcon != null) {
                    int x = (getWidth() - logoIcon.getIconWidth()) / 2;
                    g.drawImage(logoIcon.getImage(), x, 10, null);
                }
            }
        };
        jLabel1 = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        pro = new com.univsoftdev.econova.component.ProgressBarCustom();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        curvesPanel1.setBackground(new java.awt.Color(0, 51, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 42)); // Título más grande y bold
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Econova 1.0");
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        lbStatus.setForeground(new java.awt.Color(255, 255, 255));
        lbStatus.setText("Cargando...");
        lbStatus.setIcon(new ImageIcon(getClass().getResource("/loading.gif"))); // Icono animado de carga

        javax.swing.GroupLayout curvesPanel1Layout = new javax.swing.GroupLayout(curvesPanel1);
        curvesPanel1.setLayout(curvesPanel1Layout);
        curvesPanel1Layout.setHorizontalGroup(
                curvesPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(curvesPanel1Layout.createSequentialGroup()
                                .addGroup(curvesPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(curvesPanel1Layout.createSequentialGroup()
                                                .addGap(171, 171, 171)
                                                .addComponent(jLabel1))
                                        .addGroup(curvesPanel1Layout.createSequentialGroup()
                                                .addGap(38, 38, 38)
                                                .addGroup(curvesPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lbStatus)
                                                        .addComponent(pro, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(45, Short.MAX_VALUE))
        );
        curvesPanel1Layout.setVerticalGroup(
                curvesPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(curvesPanel1Layout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addComponent(pro, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbStatus)
                                .addContainerGap(117, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(curvesPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(curvesPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(@SuppressWarnings("unused") java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        startLoading();
    }//GEN-LAST:event_formWindowOpened

    private void doTask(String taskName, int progress) {
        SwingUtilities.invokeLater(() -> {
            lbStatus.setText(taskName);
            pro.setValue(progress); // Usar método estándar, sin animación
        });
    }

    private void startLoading() {

        executor.schedule(() -> {
            try {
                doTask("Conectando a la base de datos...", 10);
                Thread.sleep(1000);

                doTask("Cargando configuración...", 30);
                Thread.sleep(1000);

                doTask("Cargando plan de cuentas...", 60);
                Thread.sleep(1000);

                doTask("Finalizando inicio...", 100);
                Thread.sleep(1000);

                SwingUtilities.invokeLater(() -> {
                    dispose();
                    curvesPanel1.stop();
                });
            } catch (InterruptedException e) {
                log.error("Proceso de inicialización interrumpido", e);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Proceso de inicialización interrumpido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                });
            } catch (Exception e) {
                log.error("Error durante la inicialización", e);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Error durante la inicialización: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                });
            } finally {
                executor.shutdown();
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    public static void main(String args[]) {

        LookAndFeelUtils.setupLookAndFeel();

        java.awt.EventQueue.invokeLater(() -> {
            new Splash(null, true).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.univsoftdev.econova.component.CurvesPanel curvesPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbStatus;
    private com.univsoftdev.econova.component.ProgressBarCustom pro;
    // End of variables declaration//GEN-END:variables
}
