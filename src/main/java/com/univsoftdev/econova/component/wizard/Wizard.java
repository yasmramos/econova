package com.univsoftdev.econova.component.wizard;

import com.univsoftdev.econova.bsondb.BsonDb;
import com.github.cjwizard.StackWizardSettings;
import com.github.cjwizard.WizardContainer;
import com.github.cjwizard.WizardListener;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import com.github.cjwizard.pagetemplates.TitledPageTemplate;
import io.avaje.config.Config;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import lombok.extern.slf4j.Slf4j;
import raven.modal.Toast;
import raven.modal.toast.ToastPromise;

@Slf4j
public class Wizard extends javax.swing.JDialog {

    private javax.swing.JList<String> jListNavigation;
    private javax.swing.JScrollPane jScrollPane1;
    private final WizardFactory factory = new WizardFactory();
    private JPanel mainPanel; // Panel principal con CardLayout
    private JPanel wizardContent; // Panel del contenido del wizard
    private SplashPanel splashPanel; // Panel de presentación

    public Wizard(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(new Dimension(883, 604));
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        // Crear panel principal con CardLayout
        mainPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // 1. Crear y agregar panel de presentación
        splashPanel = new SplashPanel(mainPanel);
        mainPanel.add(splashPanel, "splash");

        // 2. Crear panel del wizard
        wizardContent = new JPanel();
        setupWizardContent();
        mainPanel.add(wizardContent, "wizard");

        // Mostrar inicialmente el splash
        cardLayout.show(mainPanel, "splash");

        // Configurar contenido principal
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void setupWizardContent() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jListNavigation = new javax.swing.JList<>();

        final WizardContainer wc = new WizardContainer(
                factory,
                new TitledPageTemplate(),
                new StackWizardSettings()
        );

        wc.setForgetTraversedPath(true);

        wc.addWizardListener(new WizardListener() {
            @Override
            public void onCanceled(List<WizardPage> path, WizardSettings settings) {
                System.exit(0);
            }

            @Override
            public void onFinished(List<WizardPage> path, WizardSettings settings) {
                List<String> iterator = settings.keySet().stream().toList();
                for (String key : iterator) {
                    Config.setProperty(key, (String) settings.get(key));
                }
                try {
                    Config.asProperties().store(new FileOutputStream("application.properties"), null);
                } catch (IOException ex) {
                    log.error("Error al guardar la configuración: ", ex);
                }

                procesarDatos();
                
                Wizard.this.dispose();
            }

            @Override
            public void onPageChanged(WizardPage newPage, List<WizardPage> path) {
                Wizard.this.setTitle(newPage.getName());
                jListNavigation.setSelectedValue(newPage.getTitle(), true);
            }

            @Override
            public void onPageChanging(WizardPage newPage, List<WizardPage> path) {
            }
        });

        jListNavigation.setModel(new AbstractListModel<String>() {
            @Override
            public int getSize() {
                return factory.pages.length;
            }

            @Override
            public String getElementAt(int index) {
                return factory.pages[index].getTitle();
            }
        });

        jListNavigation.setSelectedIndex(0);
        jListNavigation.setEnabled(false);
        jScrollPane1.setViewportView(jListNavigation);

        GroupLayout layout = new GroupLayout(wizardContent);
        wizardContent.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wc, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(wc, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                        .addContainerGap())
        );
    }

    private void procesarDatos() {
        Toast.showPromise(this, "Conectando a la base de datos...", new ToastPromise(UUID.randomUUID().toString()) {
            @Override
            public void execute(ToastPromise.PromiseCallback pc) {
                try {
                    Thread.sleep(500);
                    pc.update("Creando estructura de la base de datos...");
                  
                    Thread.sleep(1000); // Simular trabajo  
                    pc.done(Toast.Type.SUCCESS, "Proceso finalizado.");
                } catch (InterruptedException e) {
                    // Manejar interrupción  
                }
            }

            @Override
            public boolean useThread() {
                return false;
            }

        });
    }

}
