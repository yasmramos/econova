package com.univsoftdev.econova;

import com.formdev.flatlaf.FlatLightLaf;
import com.univsoftdev.econova.core.config.AppConfig;
import com.univsoftdev.econova.modules.EconovaModule;
import com.univsoftdev.econova.security.shiro.ShiroConfig;
import io.avaje.inject.BeanScope;
import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;
import jakarta.inject.Inject;
import java.io.IOException;
import java.security.Security;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Slf4j
public class Econova {

    @Inject
    private ShiroConfig shiroConfig;

    static{
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public static void main(String[] args) {
        
        Test.main(args);
        // Configuración de clave de cifrado (usar variable de entorno en producción)
        String encryptionKey = System.getenv().getOrDefault("ECONOVA_ENCRYPTION_KEY", "your-32-character-encryption-key");
        System.setProperty("config.encryption.password", encryptionKey);

        // Ajuste multiplataforma: look and feel nativo si no es Windows
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) {
            System.setProperty("flatlaf.useWindowDecorations", "true"); // Decoraciones nativas en Linux/Mac
        }
        init();

        // Arranque seguro de la aplicación en el hilo de eventos de Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                setupLookAndFeel(os);
                setupSecurity();
                initializeJavaFxToolkit();
                showSplashScreen();
                //runDbMigration();
                startMainApplication(args);
            } catch (Exception e) {

                log.error(
                        "Error iniciando la aplicación: {}",
                        e.getMessage(),
                        e
                );

                JOptionPane.showMessageDialog(
                        null,
                        "Error iniciando la aplicación: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void init() {
        try {
            BeanScope beanScope = BeanScope.builder()
                    .modules(new EconovaModule())
                    .build();
        } catch (Exception e) {
            log.error("No se pudo crear el BeanScope", e);
        }
    }

    /**
     * Ejecuta la migración de base de datos usando Ebean.
     */
    private static void runDbMigration() {

        try {
            final var dbMigration = DbMigration.create();
            dbMigration.addPlatform(Platform.POSTGRES);
            dbMigration.setStrictMode(false);
            dbMigration.generateMigration();

            System.out.println("Migración de base de datos completada.");

        } catch (IOException ex) {

            log.error("No se pudieron generar las migraciones", ex);

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudieron generar las migraciones: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static void setupLookAndFeel(String os) {
        try {
            if (os.contains("mac")) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", AppConfig.getAppName());
            }
            FlatLightLaf.setup();
        } catch (Exception e) {
            log.error("Error al configurar la apariencia de la interfaz gráfica: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Error al configurar la apariencia de la interfaz gráfica: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void setupSecurity() {
        Security.setProperty("crypto.policy", "unlimited");
        System.setProperty("app.encryption.password", System.getenv().getOrDefault("ECONOVA_ENCRYPTION_KEY", "efghjsdfkj"));
    }

    private static void initializeJavaFxToolkit() {
        try {
            JavaFxInitializer.initializeToolkit();
        } catch (Exception e) {
            log.error("Error al inicializar el toolkit de JavaFX: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Error al inicializar el toolkit de JavaFX: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showSplashScreen() {
        try {
            new Splash(null, true).setVisible(true);
        } catch (Exception e) {
            log.error("Error al mostrar el splash screen: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Error al mostrar el splash screen: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void startMainApplication(String[] args) {
        try {
            MainFormApp.main(args);
        } catch (Exception e) {
            log.error("Error al iniciar la aplicación principal: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Error al iniciar la aplicación principal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
