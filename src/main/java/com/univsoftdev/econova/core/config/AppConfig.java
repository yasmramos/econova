package com.univsoftdev.econova.core.config;

import io.avaje.config.Config;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuración centralizada de la aplicación usando Avaje Config. Delega todas
 * las responsabilidades de configuración desde AppContext y ApplicationSession.
 */
@Slf4j
public class AppConfig {

    public static String getAppName() {
        return Config.get("econova.app.name", "Econova");
    }

    public static String getAppVersion() {
        return Config.get("econova.app.version", "0.1.0");
    }

    public static String getDefaultLanguage() {
        return Config.get("econova.app.language", "es");
    }

    public static String getDefaultTheme() {
        return Config.get("econova.app.theme", "light");
    }

    public static String getDatabaseName() {
        return Config.get("econova.database.name", "econova");
    }

    public static void setDatabaseName(String name) {
        set("econova.database.name", name);
    }

    public static String getDatabaseDriver() {
        return Config.get("econova.database.driver", "org.postgresql.Driver");
    }

    public static void setDatabaseDriver(String driver) {
        set("econova.database.driver", driver);
    }

    private static void set(String key, String value) {
        Config.setProperty(key, value);
        try {
            Config.asProperties().store(new FileOutputStream("application.properties"), "Configuración Actualizada");
        } catch (FileNotFoundException ex) {
            log.error(ex.getMessage());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    public static String getDatabaseUrl() {
        return Config.get("econova.database.url", "jdbc:postgresql://localhost:5432/econova");
    }

    public static void setDatabaseUrl(String url) {
        set("econova.database.url", url);
    }

    public static String getEncryptionKey() {
        String key = Config.get("econova.encryption.key", System.getenv("ECONOVA_ENCRYPTION_KEY"));
        if (key == null || key.equals("your-32-character-encryption-key")) {
            log.warn("Usando clave de encriptación por defecto. Configura variable de entorno ECONOVA_ENCRYPTION_KEY.");
        }
        return key;
    }

    public static UserConfig getUserConfig(String userId) {
        return new UserConfig(userId != null ? userId : "default");
    }

    public static UserConfig getDefaultUserConfig() {
        return getUserConfig("default");
    }

    public static int getDefaultGridPageSize() {
        return Config.getInt("econova.ui.grid.pageSize", 25);
    }

    public static boolean isDarkModeEnabled() {
        return Config.getBool("econova.ui.darkMode", false);
    }

    public static long getCacheMaxSize() {
        return Config.getLong("econova.cache.maxSize", 1000);
    }

    public static int getCacheExpirationMinutes() {
        return Config.getInt("econova.cache.expirationMinutes", 60);
    }

    public static String getDefaultCurrency() {
        return Config.get("econova.currency.default.code", "CUP");
    }

    public static String getDefaultCurrencyName() {
        return Config.get("econova.currency.default.name", "Moneda Nacional");
    }

    public static String getDatabasePassword() {
        return Config.get("econova.database.password", "postgres");
    }

    public static void setDatabasePassword(String password) {
        set("econova.database.password", password);
    }
    
    public static void setDatabaseUser(String user) {
        set("econova.database.user", user);
    }
    
    public static String getDatabaseUser() {
        return Config.get("econova.database.user", "postgres");
    }
    
    public static String getDatabaseAdminPassword() {
        return Config.get("econova.database.admin.password", "postgres");
    }

    public static void setDatabaseAdminPassword(String password) {
        set("econova.database.admin.password", password);
    }
    
    public static void setDatabaseAdminUser(String user) {
        set("econova.database.admin.username", user);
    }
    
    public static String getDatabaseAdminUser() {
        return Config.get("econova.database.username", "postgres");
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

        public void setFormPreference(String formName, String key, String value) {
            setPreference("forms." + formName + "." + key, value);
        }

        public String getFormPreference(String formName, String key, String defaultValue) {
            return getPreference("forms." + formName + "." + key, defaultValue);
        }

        public String getUserId() {
            return userId;
        }

        public String getPrefix() {
            return prefix;
        }
    }
}
