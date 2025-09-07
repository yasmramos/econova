package com.univsoftdev.econova.core.config;

import io.avaje.config.Config;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    
    public static String getDatabaseHost() {
        return Config.get("econova.database.host", "localhost");
    }
    
    public static String getDefaultDatabase() {
        return Config.get("econova.database.defaultDatabase", "postgres");
    }
    
    public static void setDatabaseHost(String host) {
        Config.setProperty("econova.database.host", host);
    }
    
    public static int getDatabasePort() {
        return Config.getInt("econova.database.port", 5432);
    }
    
    public static void setDatabaseHost(int host) {
        Config.setProperty("econova.database.host", String.valueOf(host));
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
    
    public static String getLookAndFeel() {
        return Config.get("econova.app.lookandfeel", "com.formdev.flatlaf.FlatLightLaf");
    }
    
    public static void setLookAndFeel(String lookAndFeel) {
        Config.setProperty("econova.app.lookandfeel", lookAndFeel);
    }
    
    public static void setProperty(String key, String value) {
        Config.setProperty(key, value);
    }
    
    private static void setObject(String key, Object value) {
        Config.setProperty(key, String.valueOf(value));
    }
    
    public static void setInt(String key, int value) {
        setObject(key, value);
    }
    
    public static void setBool(String key, boolean value) {
        setObject(key, value);
    }
    
    public static void setDouble(String key, double value) {
        setObject(key, value);
    }
    
    public static void setBigDecimal(String key, BigDecimal value) {
        setObject(key, value.toString());
    }
    
    public static void setLocalDateTime(String key, LocalDateTime value) {
        setObject(key, value.toString());
    }
    
    public static void setLocalDate(String key, LocalDate value) {
        setObject(key, value.toString());
    }
    
    public static void setTrial(boolean value) {
        setBool("econova.trial.period", value);
    }
    
    public static boolean isTrial() {
        return Config.getBool("econova.trial.period");
    }
    
    public static LocalDateTime trialStartDate() {
        return Config.getAs("econova.trial.period.start", (t) -> {
            return LocalDateTime.parse(t);
        });
    }
    
    public static void setTrialStartDate(LocalDateTime date) {
        setLocalDateTime("econova.trial.period.start", date);
    }
    
    public static LocalDateTime trialEndDate() {
        return Config.getAs("econova.trial.period.end", (t) -> {
            return LocalDateTime.parse(t);
        });
    }
    
    public static void setTrialEndDate(LocalDateTime date) {
        setLocalDateTime("econova.trial.period.end", date);
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
