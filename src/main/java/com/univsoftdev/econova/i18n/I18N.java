package com.univsoftdev.econova.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Internationalization utility class using ResourceBundle. Thread-safe
 * singleton that supports dynamic locale and bundle changes.
 */
@Slf4j
public class I18N {

    private static final String DEFAULT_BUNDLE_NAME = "messages";
    private static final String MISSING_KEY_PREFIX = "!";
    private static final String MISSING_KEY_SUFFIX = "!";
    private static volatile I18N instance;

    @Getter
    private volatile Locale currentLocale;
    private volatile ResourceBundle bundle;
    private volatile String bundleName;

    private I18N(String bundleName, Locale locale) {
        this.bundleName = bundleName;
        this.currentLocale = locale;
        loadBundle();
    }

    public static I18N getInstance() {
        return getInstance(DEFAULT_BUNDLE_NAME, Locale.getDefault());
    }

    public static I18N getInstance(String bundleName, Locale locale) {
        I18N inst = I18N.instance;
        if (inst == null) {
            synchronized (I18N.class) {
                inst = I18N.instance;
                if (inst == null) {
                    I18N.instance = inst = new I18N(bundleName, locale);
                    return inst;
                }
            }
        }
        // Si ya existe, actualizar si es necesario
        if (!inst.currentLocale.equals(locale)) {
            inst.setLocale(locale);
        }
        if (!inst.bundleName.equals(bundleName)) {
            inst.setBundleName(bundleName);
        }
        return inst;
    }

    /**
     * Gets a translated string for the given key. Returns "!key!" if not found
     * or bundle not loaded.
     */
    public String getString(String key) {
        if (key == null || key.trim().isEmpty()) {
            log.warn("Attempted to get translation for null or empty key");
            return MISSING_KEY_PREFIX + "INVALID_KEY" + MISSING_KEY_SUFFIX;
        }

        ResourceBundle currentBundle = this.bundle; // lectura volátil segura
        if (currentBundle == null) {
            log.warn("Resource bundle is not loaded. Key: {}", key);
            return MISSING_KEY_PREFIX + key + MISSING_KEY_SUFFIX;
        }

        try {
            return currentBundle.getString(key);
        } catch (MissingResourceException e) {
            log.warn("Missing translation for key: {}", key);
            return MISSING_KEY_PREFIX + key + MISSING_KEY_SUFFIX;
        }
    }

    public void setLocale(Locale newLocale) {
        if (newLocale == null) {
            throw new IllegalArgumentException("Locale cannot be null");
        }

        if (!currentLocale.equals(newLocale)) {
            currentLocale = newLocale;
            loadBundle();
            log.info("Locale changed to: {}", newLocale);
        }
    }

    public void setBundleName(String newBundleName) {
        if (newBundleName == null || newBundleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bundle name cannot be null or empty");
        }

        if (!bundleName.equals(newBundleName)) {
            bundleName = newBundleName;
            loadBundle();
            log.info("Resource bundle changed to: {}", newBundleName);
        }
    }

    private void loadBundle() {
        ResourceBundle.Control utf8Control = new ResourceBundle.Control() {
            @Override
            public List<String> getFormats(String baseName) {
                return List.of("properties"); // o Arrays.asList("properties") en Java < 9
            }

            @Override
            public ResourceBundle newBundle(String baseName, Locale locale, String format,
                    ClassLoader loader, boolean reload)
                    throws IllegalAccessException, InstantiationException, IOException {
                // Construye el nombre del archivo: messages_es.properties
                String bundleName = toBundleName(baseName, locale);
                String resourceName = toResourceName(bundleName, "properties");

                try (InputStream stream = loader.getResourceAsStream(resourceName)) {
                    if (stream == null) {
                        return null;
                    }
                    try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                        return new PropertyResourceBundle(reader);
                    }
                }
            }
        };

        try {
            bundle = ResourceBundle.getBundle(bundleName, currentLocale, utf8Control);
            log.debug("Loaded resource bundle '{}' for locale: {}", bundleName, currentLocale);
        } catch (MissingResourceException e1) {
            log.warn("Bundle not found for locale: {}, trying default locale...", currentLocale);
            try {
                bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault(), utf8Control);
                log.debug("Loaded resource bundle '{}' for default locale: {}", bundleName, Locale.getDefault());
            } catch (MissingResourceException e2) {
                log.warn("Bundle not found for default locale, trying base name without locale...");
                try {
                    bundle = ResourceBundle.getBundle(bundleName, utf8Control);
                    log.debug("Loaded base resource bundle: {}", bundleName);
                } catch (MissingResourceException e3) {
                    log.error("No resource bundle found for name: '{}'. Using fallback keys.", bundleName);
                    bundle = null;
                }
            }
        }
    }
}
