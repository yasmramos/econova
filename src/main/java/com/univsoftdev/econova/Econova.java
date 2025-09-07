package com.univsoftdev.econova;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.config.TinkConfig;
import com.univsoftdev.econova.component.wizard.Wizard;
import com.univsoftdev.econova.config.service.CompanyService;
import com.univsoftdev.econova.core.AppContext;
import com.univsoftdev.econova.core.FileUtils;
import com.univsoftdev.econova.core.Injector;
import com.univsoftdev.econova.core.JavaFxInitializer;
import com.univsoftdev.econova.core.LookAndFeelUtils;
import com.univsoftdev.econova.core.module.ModuleInitializationException;
import com.univsoftdev.econova.ebean.config.EbeanMigrator;
import com.univsoftdev.econova.security.SecurityContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.Security;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Slf4j
public class Econova {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {

        System.setProperty("config.encryption.password", "HWEUIE4684685GE445878678567866574$&%*");

        LookAndFeelUtils.setupLookAndFeel();

        Injector.init();

        EbeanMigrator migrator = Injector.get(EbeanMigrator.class);
        migrator.runInitMigration();

        AppContext appContext = Injector.get(AppContext.class);
        appContext.addModule(new ContabilidadModule());
        try {
            appContext.initializeAllModules();
        } catch (ModuleInitializationException ex) {
            log.error(ex.getMessage());
        }

        // Arranque seguro de la aplicación en el hilo de eventos de Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                setupSecurity();
                initializeJavaFxToolkit();
                showSplashScreen();
                init(args);
            } catch (Exception e) {

                log.error(
                        "Error starting the application: {}",
                        e.getMessage(),
                        e
                );

                JOptionPane.showMessageDialog(
                        null,
                        "Error starting the application:" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void init(String[] args) {
        // Initialize all application directories
        FileUtils.initializeAppDirectories();

        // Display storage usage
        log.info(FileUtils.getStorageUsageReport());

        // Create a backup
        FileUtils.createBackup();

        // Clean temporary files
        FileUtils.cleanTempDirectory();

        boolean valid = LicenseUtils.loadAndValidateTrialLicense();

        SwingUtilities.invokeLater(() -> {

            FormLicense.main(args);

            try {
                CompanyService empresaService = Injector.get(CompanyService.class);
                if (empresaService != null && empresaService.findAll().isEmpty()) {
                    log.info("Iniciando wizard de configuración inicial");
                    Wizard wizard = new Wizard(null);
                    wizard.setLocationRelativeTo(null);
                    wizard.setVisible(true);

                } else {
                    log.info("Aplicación iniciada con datos existentes");
                    startMainApplication(null);
                }
            } catch (Exception e) {
                log.error("Error al verificar datos iniciales", e);
                JOptionPane.showMessageDialog(
                        null,
                        "Error al verificar configuración inicial: " + e.getMessage(),
                        "Error de inicialización",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private static void setupSecurity() {
        Security.setProperty("crypto.policy", "unlimited");
        try {
            
            TinkConfig.register();
            
            KeyStorage.initializeTink();
            
            if (!Files.exists(Path.of(FileUtils.PRIVATE_KEYSET))) {
                log.info("Guardando claves en archivos...");
                KeysetHandle privateKeysetHandle = KeyStorage.generateHybridKeys();
                KeyStorage.saveKeysetToFile(privateKeysetHandle, FileUtils.PRIVATE_KEYSET);
                KeysetHandle publicKeysetHandle = privateKeysetHandle.getPublicKeysetHandle();
                KeyStorage.saveKeysetToFile(publicKeysetHandle, FileUtils.PUBLIC_KEYSET);
                log.info("Claves guardadas exitosamente en directorio: " + FileUtils.APP_DATA + "keys/");
            }
            KeysetHandle privateKeyset = KeyStorage.loadKeysetFromFile(FileUtils.PRIVATE_KEYSET);
            KeysetHandle publicKeyset = KeyStorage.loadKeysetFromFile(FileUtils.PUBLIC_KEYSET);
            log.info("Se han cargado el par de claves.");
            SecurityContext.setPrivateKeysetHandle(privateKeyset);
            SecurityContext.setPublicKeysetHandle(publicKeyset);
        } catch (GeneralSecurityException ex) {
            log.error("Error configuring Tink. ", ex);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    private static void initializeJavaFxToolkit() {
        try {
            JavaFxInitializer.initializeToolkit();
        } catch (Exception e) {
            log.error("Error initializing JavaFX toolkit: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Error initializing JavaFX toolkit: {}" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showSplashScreen() {
        try {
            new Splash(null, true).setVisible(true);
        } catch (Exception e) {
            log.error("Error displaying splash screen: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Error displaying splash screen: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void startMainApplication(String[] args) {
        try {
            MainFormApp mainFormApp = Injector.get(MainFormApp.class);
            mainFormApp.start();
        } catch (Exception e) {
            log.error("Error starting main application: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Error starting main application: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
