package com.univsoftdev.econova.core;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.util.FontUtils;
import com.univsoftdev.econova.core.config.AppConfig;
import java.awt.Component;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LookAndFeelUtils {

    public static void setupLookAndFeel() {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("com.univsoftdev.econova.themes");
        UIManager.put("defaultFont", FontUtils.getCompositeFont(FlatRobotoFont.FAMILY, Font.PLAIN, 13));

        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) {
            System.setProperty("flatlaf.useWindowDecorations", "true"); // Decoraciones nativas en Linux/Mac
        }

        if (os.contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", AppConfig.getAppName());
        }
        String lafClassName = AppConfig.getLookAndFeel();
        log.debug("Cargando Look and Feel: " + lafClassName);
        setupLookAndFeelWithReflection(lafClassName);
    }

    public static void setupLookAndFeel(Component c) {
        setupLookAndFeel();
        SwingUtilities.updateComponentTreeUI(c);
    }

    private static boolean setupLookAndFeelWithReflection(String lafClassName) {
        try {
            Class<?> lafClass = Class.forName(lafClassName);
            Method setupMethod = lafClass.getDeclaredMethod("setup");
            Boolean result = (Boolean) setupMethod.invoke(null);
            return result != null ? result : false;
        } catch (ClassNotFoundException ex) {
            log.warn("Look and Feel class not found: " + lafClassName);
            return false;
        } catch (NoSuchMethodException ex) {
            log.warn("setup() method not found in: " + lafClassName);
            return false;
        } catch (IllegalAccessException | SecurityException | InvocationTargetException ex) {
            log.error("Error setting up Look and Feel: " + lafClassName, ex);
            return false;
        }
    }
}
