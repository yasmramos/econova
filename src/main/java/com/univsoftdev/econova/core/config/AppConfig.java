package com.univsoftdev.econova.core.config;

import io.avaje.config.Config;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuración centralizada de la aplicación usando Avaje Config. Delega todas
 * las responsabilidades de configuración desde AppContext y ApplicationSession.
 */
@Singleton
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    // === CONFIGURACIÓN DE APLICACIÓN ===  
    public String getAppName() {
        return Config.get("econova.app.name", "Econova");
    }

    public String getAppVersion() {
        return Config.get("econova.app.version", "0.1.0");
    }

    public String getDefaultLanguage() {
        return Config.get("econova.app.language", "es");
    }

    public String getDefaultTheme() {
        return Config.get("econova.app.theme", "light");
    }

    // === CONFIGURACIÓN DE BASE DE DATOS ===  
    public String getDatabaseName() {
        return Config.get("econova.database.name", "econova");
    }

    public String getDatabaseUrl() {
        return Config.get("econova.database.url", "jdbc:postgresql://localhost:5432/econova");
    }

    // === CONFIGURACIÓN DE SEGURIDAD ===  
    public String getEncryptionKey() {
        String key = Config.get("econova.encryption.key", System.getenv("ECONOVA_ENCRYPTION_KEY"));
        if (key == null || key.equals("your-32-character-encryption-key")) {
            logger.warn("Usando clave de encriptación por defecto. Configura ECONOVA_ENCRYPTION_KEY en producción.");
        }
        return key;
    }

    // === CONFIGURACIÓN DE USUARIO ===  
    public UserConfig getUserConfig(String userId) {
        return new UserConfig(userId != null ? userId : "default");
    }

    public UserConfig getDefaultUserConfig() {
        return getUserConfig("default");
    }

    // === CONFIGURACIÓN DE UI ===  
    public int getDefaultGridPageSize() {
        return Config.getInt("econova.ui.grid.pageSize", 25);
    }

    public boolean isDarkModeEnabled() {
        return Config.getBool("econova.ui.darkMode", false);
    }

    // === CONFIGURACIÓN DE CACHÉ ===  
    public long getCacheMaxSize() {
        return Config.getLong("econova.cache.maxSize", 1000);
    }

    public int getCacheExpirationMinutes() {
        return Config.getInt("econova.cache.expirationMinutes", 60);
    }

    /**
     * Clase interna para configuración específica de usuario
     */
    public static class UserConfig {

        private final String userId;
        private final String prefix;

        public UserConfig(String userId) {
            this.userId = userId;
            this.prefix = "econova.users." + userId;
        }

        public void setPreference(String key, String value) {
            System.setProperty(prefix + "." + key, value);
        }

        public String getPreference(String key, String defaultValue) {
            return Config.get(prefix + "." + key, defaultValue);
        }

        public void setPreference(String key, boolean value) {
            setPreference(key, String.valueOf(value));
        }

        public boolean getPreference(String key, boolean defaultValue) {
            return Config.getBool(prefix + "." + key, defaultValue);
        }

        public void setPreference(String key, int value) {
            setPreference(key, String.valueOf(value));
        }

        public int getPreference(String key, int defaultValue) {
            return Config.getInt(prefix + "." + key, defaultValue);
        }

        // === PREFERENCIAS ESPECÍFICAS ===  
        public String getPreferredLanguage() {
            return getPreference("ui.language", "es");
        }

        public void setPreferredLanguage(String language) {
            setPreference("ui.language", language);
        }

        public String getPreferredTheme() {
            return getPreference("ui.theme", "light");
        }

        public void setPreferredTheme(String theme) {
            setPreference("ui.theme", theme);
        }

        public int getGridPageSize() {
            return getPreference("ui.grid.pageSize", 25);
        }

        public void setGridPageSize(int pageSize) {
            setPreference("ui.grid.pageSize", pageSize);
        }

        // === PREFERENCIAS DE FORMULARIOS ===  
        public void setFormPreference(String formName, String key, String value) {
            setPreference("forms." + formName + "." + key, value);
        }

        public String getFormPreference(String formName, String key, String defaultValue) {
            return getPreference("forms." + formName + "." + key, defaultValue);
        }
    }
}
