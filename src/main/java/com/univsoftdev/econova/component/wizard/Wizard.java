package com.univsoftdev.econova.component.wizard;

import com.github.cjwizard.StackWizardSettings;
import com.github.cjwizard.WizardContainer;
import com.github.cjwizard.WizardListener;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import com.github.cjwizard.pagetemplates.TitledPageTemplate;
import com.univsoftdev.econova.MainFormApp;
import com.univsoftdev.econova.config.model.Company;
import com.univsoftdev.econova.core.Injector;
import com.univsoftdev.econova.core.UserContext;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import lombok.extern.slf4j.Slf4j;
import raven.modal.Toast;
import raven.modal.toast.ToastPromise;

@Slf4j
public class Wizard extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private javax.swing.JList<String> jListNavigation;
    private javax.swing.JScrollPane jScrollPane1;
    private final WizardFactory factory = new WizardFactory();
    private JPanel mainPanel; // Panel principal con CardLayout
    private JPanel wizardContent; // Panel del contenido del wizard
    private SplashPanel splashPanel; // Panel de presentación
    private WizardContainer wizardContainer; // Referencia al contenedor del wizard

    public Wizard(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        initializeWizard();
    }

    private void initializeWizard() {
        setSize(new Dimension(983, 704));
        setMinimumSize(new Dimension(900, 600));
        setResizable(true);
        setLocationRelativeTo(getParent());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    private void handleWindowClosing() {
        log.info("Intento de cierre del wizard");

        // Si estamos en el wizard (no en splash), preguntar confirmación
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        // Aquí podrías verificar en qué panel estás para decidir si mostrar confirmación

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir del asistente de configuración?\nSe perderán todos los datos no guardados.",
                "Confirmar salida",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE
        );

        if (option == javax.swing.JOptionPane.YES_OPTION) {
            log.info("Wizard cerrado por el usuario");
            System.exit(0);
        }
    }

    @SuppressWarnings("unchecked")
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
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pack();
    }

    private void setupWizardContent() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jListNavigation = new javax.swing.JList<>();

        wizardContainer = new WizardContainer(
                factory,
                new TitledPageTemplate(),
                new StackWizardSettings()
        );

        wizardContainer.setForgetTraversedPath(true);

        wizardContainer.addWizardListener(new WizardListener() {
            @Override
            public void onCanceled(List<WizardPage> path, WizardSettings settings) {
                handleWizardCanceled(settings);
            }

            @Override
            public void onFinished(List<WizardPage> path, WizardSettings settings) {
                handleWizardFinished(settings);
            }

            @Override
            public void onPageChanged(WizardPage newPage, List<WizardPage> path) {
                handlePageChanged(newPage, path);
            }

            @Override
            public void onPageChanging(WizardPage newPage, List<WizardPage> path) {
                // Puedes agregar validaciones aquí antes de cambiar de página
            }
        });

        setupNavigationList();
        setupLayout();
    }

    private void setupNavigationList() {
        jListNavigation.setModel(new AbstractListModel<String>() {
            @Override
            public int getSize() {
                return factory.getPages().size();
            }

            @Override
            public String getElementAt(int index) {
                return factory.getPages().get(index).getTitle();
            }
        });

        jListNavigation.setSelectedIndex(0);
        jListNavigation.setEnabled(false);
        jScrollPane1.setViewportView(jListNavigation);
    }

    private void setupLayout() {
        GroupLayout layout = new GroupLayout(wizardContent);
        wizardContent.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wizardContainer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(wizardContainer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                        .addContainerGap())
        );
    }

    private void handleWizardCanceled(WizardSettings settings) {
        log.info("Wizard cancelado por el usuario");

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea cancelar la configuración?\nLa aplicación se cerrará.",
                "Confirmar cancelación",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

        if (option == javax.swing.JOptionPane.YES_OPTION) {
            log.info("Aplicación terminada por cancelación del wizard");
            System.exit(0);
        }
    }

    private void handleWizardFinished(WizardSettings settings) {
        log.info("Wizard finalizado exitosamente - Guardando configuración");

        try {
            // Guardar configuración
            saveConfiguration(settings);

            // Procesar datos
            procesarDatos();

            // Limpiar recursos y cerrar
            cleanupAndClose();

        } catch (IOException e) {
            log.error("Error al finalizar el wizard", e);
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar la configuración: " + e.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }

    }

    private void saveConfiguration(WizardSettings settings) throws IOException {
        if (settings != null && !settings.isEmpty()) {
            log.debug("Guardando {} propiedades de configuración", settings.size());
            List<Company> companys = (List<Company>) settings.get("empresas");
            companys.stream().forEach(c -> c.save());
        } else {
            log.warn("No hay configuración para guardar");
        }
    }

    private void handlePageChanged(WizardPage newPage, List<WizardPage> path) {
        if (newPage != null) {
            this.setTitle(newPage.getName());
            if (jListNavigation != null && newPage.getTitle() != null) {
                jListNavigation.setSelectedValue(newPage.getTitle(), true);
            }
            log.debug("Página cambiada a: {} - {}", newPage.getName(), newPage.getTitle());
        }
    }

    private void procesarDatos() {
        log.info("Iniciando procesamiento de datos post-configuración");

        Toast.showPromise(this, "Conectando a la base de datos...", new ToastPromise(UUID.randomUUID().toString()) {
            @Override
            public void execute(ToastPromise.PromiseCallback pc) {
                try {
                    pc.update("Conectando a la base de datos...");

                    pc.done(Toast.Type.SUCCESS, "Configuración completada exitosamente.");
                    log.info("Procesamiento de datos finalizado");

                } catch (Exception e) {
                    log.error("Error en procesamiento de datos", e);
                    pc.done(Toast.Type.ERROR, "Error en el proceso: " + e.getMessage());
                }
            }

            @Override
            public boolean useThread() {
                return true; // Usar thread para no bloquear UI
            }
        });
    }

    private void cleanupAndClose() {
        log.info("Limpiando recursos y cerrando wizard");

        // Limpiar contexto si es necesario
        try {
            UserContext.reset();
        } catch (Exception e) {
            log.warn("Error al limpiar contexto de usuario", e);
        }

        // Cerrar el diálogo
        SwingUtilities.invokeLater(() -> {
            try {
                this.dispose();
                log.info("Wizard cerrado correctamente");
            } catch (Exception e) {
                log.error("Error al cerrar wizard", e);
            }
        });

        try {
            MainFormApp mainFormApp = Injector.get(MainFormApp.class);
            mainFormApp.start();
        } catch (Exception e) {
            log.error("Error starting main application: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Error starting main application: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método público para mostrar el wizard desde el splash
    public void showWizard() {
        if (mainPanel != null && mainPanel.getLayout() instanceof CardLayout) {
            CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
            cardLayout.show(mainPanel, "wizard");
            log.info("Mostrando panel del wizard");
        }
    }

    // Método para obtener el contenedor del wizard (útil para testing)
    public WizardContainer getWizardContainer() {
        return wizardContainer;
    }
}
