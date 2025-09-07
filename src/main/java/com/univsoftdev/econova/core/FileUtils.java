package com.univsoftdev.econova.core;

import com.univsoftdev.econova.core.config.AppConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    // Base application directory
    public static final String APP_DATA = System.getenv("APPDATA") + File.separator + AppConfig.getAppName() + File.separator;

    // Configuration directories
    public static final String CONFIG_PATH = APP_DATA + "config";
    public static final String USER_CONFIG_PATH = CONFIG_PATH + File.separator + "user";
    public static final String SYSTEM_CONFIG_PATH = CONFIG_PATH + File.separator + "system";

    // Data directories
    public static final String DATA_PATH = APP_DATA + "data";
    public static final String TEMP_PATH = APP_DATA + "temp";
    public static final String CACHE_PATH = APP_DATA + "cache";
    public static final String BACKUP_PATH = APP_DATA + "backup";

    // Resource directories
    public static final String RESOURCES_PATH = APP_DATA + "resources";
    public static final String TEMPLATES_PATH = RESOURCES_PATH + File.separator + "templates";
    public static final String IMAGES_PATH = RESOURCES_PATH + File.separator + "images";
    public static final String ICONS_PATH = RESOURCES_PATH + File.separator + "icons";

    // Logging directories
    public static final String LOGS_PATH = APP_DATA + "logs";
    public static final String DEBUG_LOGS_PATH = LOGS_PATH + File.separator + "debug";
    public static final String ERROR_LOGS_PATH = LOGS_PATH + File.separator + "error";
    public static final String AUDIT_LOGS_PATH = LOGS_PATH + File.separator + "audit";

    // Security directories
    public static final String KEYS_PATH = APP_DATA + "keys";
    public static final String CERTIFICATES_PATH = APP_DATA + "certificates";
    public static final String PRIVATE_KEYSET = KEYS_PATH + File.separator + "private-keyset.json";
    public static final String PUBLIC_KEYSET = KEYS_PATH + File.separator + "public-keyset.json";

    // Database directories
    public static final String DATABASE_PATH = APP_DATA + "database";
    public static final String MIGRATIONS_PATH = DATABASE_PATH + File.separator + "migrations";

    // Export directories
    public static final String EXPORT_PATH = APP_DATA + "export";
    public static final String REPORTS_PATH = EXPORT_PATH + File.separator + "reports";
    public static final String EXPORT_DATA_PATH = EXPORT_PATH + File.separator + "data";

    // Backup directories with timestamp
    public static final String TIMESTAMPED_BACKUP_PATH = BACKUP_PATH + File.separator + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    /**
     * Initialize all application directories
     */
    public static void initializeAppDirectories() {
        createDirectory(APP_DATA);
        createDirectory(CONFIG_PATH);
        createDirectory(USER_CONFIG_PATH);
        createDirectory(SYSTEM_CONFIG_PATH);
        createDirectory(DATA_PATH);
        createDirectory(TEMP_PATH);
        createDirectory(CACHE_PATH);
        createDirectory(BACKUP_PATH);
        createDirectory(RESOURCES_PATH);
        createDirectory(TEMPLATES_PATH);
        createDirectory(IMAGES_PATH);
        createDirectory(ICONS_PATH);
        createDirectory(LOGS_PATH);
        createDirectory(DEBUG_LOGS_PATH);
        createDirectory(ERROR_LOGS_PATH);
        createDirectory(AUDIT_LOGS_PATH);
        createDirectory(KEYS_PATH);
        createDirectory(CERTIFICATES_PATH);
        createDirectory(DATABASE_PATH);
        createDirectory(MIGRATIONS_PATH);
        createDirectory(EXPORT_PATH);
        createDirectory(REPORTS_PATH);
        createDirectory(EXPORT_DATA_PATH);
        createDirectory(TIMESTAMPED_BACKUP_PATH);
    }

    /**
     * Create a directory if it doesn't exist
     *
     * @param path The directory path to create
     * @return true if directory was created or already exists, false otherwise
     */
    public static boolean createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    /**
     * Delete a directory and all its contents
     *
     * @param path The directory path to delete
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteDirectory(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file.getAbsolutePath());
                    } else {
                        file.delete();
                    }
                }
            }
            return dir.delete();
        }
        return false;
    }

    /**
     * Get the size of a directory in bytes
     *
     * @param path The directory path
     * @return The size in bytes
     */
    public static long getDirectorySize(String path) {
        File dir = new File(path);
        long size = 0;
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else {
                        size += getDirectorySize(file.getAbsolutePath());
                    }
                }
            }
        }
        return size;
    }

    /**
     * Clean temporary files directory
     */
    public static void cleanTempDirectory() {
        deleteDirectory(TEMP_PATH);
        createDirectory(TEMP_PATH);
    }

    /**
     * Clean cache directory
     */
    public static void cleanCacheDirectory() {
        deleteDirectory(CACHE_PATH);
        createDirectory(CACHE_PATH);
    }

    /**
     * Create a backup of important directories
     */
    public static void createBackup() {
        try {
            // Backup configuration
            copyDirectory(CONFIG_PATH, TIMESTAMPED_BACKUP_PATH + File.separator + "config");

            // Backup keys and certificates
            copyDirectory(KEYS_PATH, TIMESTAMPED_BACKUP_PATH + File.separator + "keys");
            copyDirectory(CERTIFICATES_PATH, TIMESTAMPED_BACKUP_PATH + File.separator + "certificates");

            // Backup database
            copyDirectory(DATABASE_PATH, TIMESTAMPED_BACKUP_PATH + File.separator + "database");

            System.out.println("Backup created at: " + TIMESTAMPED_BACKUP_PATH);
        } catch (IOException e) {
            System.err.println("Backup failed: " + e.getMessage());
        }
    }

    /**
     * Copy directory from source to destination
     *
     * @param sourceDir Source directory path
     * @param destDir Destination directory path
     * @throws IOException If copy operation fails
     */
    public static void copyDirectory(String sourceDir, String destDir) throws IOException {
        Files.walk(Paths.get(sourceDir))
                .forEach(source -> {
                    Path destination = Paths.get(destDir, source.toString()
                            .substring(sourceDir.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                });
    }

    /**
     * Get a formatted string of directory sizes for reporting
     *
     * @return Formatted string with directory sizes
     */
    public static String getStorageUsageReport() {
        StringBuilder report = new StringBuilder();
        report.append("Storage Usage Report:\n");
        report.append(String.format("Config: %d MB%n", getDirectorySize(CONFIG_PATH) / (1024 * 1024)));
        report.append(String.format("Data: %d MB%n", getDirectorySize(DATA_PATH) / (1024 * 1024)));
        report.append(String.format("Logs: %d MB%n", getDirectorySize(LOGS_PATH) / (1024 * 1024)));
        report.append(String.format("Cache: %d MB%n", getDirectorySize(CACHE_PATH) / (1024 * 1024)));
        report.append(String.format("Backups: %d MB%n", getDirectorySize(BACKUP_PATH) / (1024 * 1024)));
        report.append(String.format("Total: %d MB%n", getDirectorySize(APP_DATA) / (1024 * 1024)));

        return report.toString();
    }
}
