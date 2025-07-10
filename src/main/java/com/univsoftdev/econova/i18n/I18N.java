package com.univsoftdev.econova.i18n;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Slf4j
public class I18N {

    private static final String DEFAULT_BUNDLE_NAME = "messages";
    private static final String MISSING_KEY_PREFIX = "!";
    private static final String MISSING_KEY_SUFFIX = "!";
    private static volatile I18N instance;

    @Getter
    private Locale currentLocale;
    private ResourceBundle bundle;
    private String bundleName;

    private I18N(String bundleName, Locale locale) {
        this.bundleName = bundleName;
        this.currentLocale = locale;
        loadBundle();
    }

    public static I18N getInstance() {
        return getInstance(DEFAULT_BUNDLE_NAME, Locale.getDefault());
    }

    public static I18N getInstance(String bundleName, Locale locale) {
        if (instance == null) {
            synchronized (I18N.class) {
                if (instance == null) {
                    instance = new I18N(bundleName, locale);
                }
            }
        }
        return instance;
    }

    public String getString(String key) {
        if (key == null || key.trim().isEmpty()) {
            log.warn("Attempted to get translation for null or empty key");
            return MISSING_KEY_PREFIX + "INVALID_KEY" + MISSING_KEY_SUFFIX;
        }

        try {
            return bundle.getString(key);
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
        try {
            bundle = ResourceBundle.getBundle(bundleName, currentLocale);
            log.debug("Loaded resource bundle '{}' for locale: {}", bundleName, currentLocale);
        } catch (MissingResourceException e) {
            log.error("Failed to load resource bundle '{}' for locale: {}", bundleName, currentLocale, e);
            bundle = ResourceBundle.getBundle(bundleName);
        }
    }
}