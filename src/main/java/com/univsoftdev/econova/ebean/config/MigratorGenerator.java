package com.univsoftdev.econova.ebean.config;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MigratorGenerator {

    private static final String MIGRATION_BASE_PATH = "dbmigration";

    public static synchronized void generateNextMigration() {
        try {
            DbMigration dbMigration = DbMigration.create();
            dbMigration.addPlatform(Platform.POSTGRES);
            dbMigration.setPathToResources("src/main/resources");
            dbMigration.setMigrationPath(MIGRATION_BASE_PATH);
            dbMigration.setLockTimeout(30);
            dbMigration.setStrictMode(false);

            List<String> pendingDrops = dbMigration.getPendingDrops();
            if (!pendingDrops.isEmpty()) {
                String pendingVersion = pendingDrops.get(0);
                dbMigration.setGeneratePendingDrop(pendingVersion);
                log.info("Generando pending drops para versión: " + pendingVersion);
            } else {
                log.info("Generando migración normal (sin pending drops)");
            }

            String version = dbMigration.generateMigration();
            log.info("Migración generada: " + version);

            // Verificar que los archivos se hayan creado completamente  
            if (version != null) {
                waitForMigrationFiles(version);
            }

        } catch (IOException e) {
            log.error("Error generando migración", e);
        }
    }

    public static synchronized void generateInitMigration() {
        try {

            // Verificar si ya existe una migración inicial  
            if (hasInitialMigration()) {
                log.info("Migración inicial ya existe, omitiendo generación");
                return;
            }

            DbMigration dbMigration = DbMigration.create();
            dbMigration.addPlatform(Platform.POSTGRES);
            dbMigration.setPathToResources("src/main/resources");
            dbMigration.setMigrationPath(MIGRATION_BASE_PATH);
            dbMigration.setLockTimeout(30);
            dbMigration.setName("initial");
            dbMigration.setVersion("1.0");
            dbMigration.setStrictMode(false);

            // Usar generateInitMigration() para migración inicial  
            String version = dbMigration.generateInitMigration();
            log.info("Migración inicial generada: " + version);

        } catch (IOException e) {
            log.error("Error generando migración inicial", e);
        }
    }

    public static boolean hasInitialMigration() {
        File migrationDir = new File("src/main/resources/dbinit");

        if (!migrationDir.exists()) {
            return false;
        }

        // Verificar si existe archivo SQL inicial (en subdirectorio postgres)  
        boolean hasSqlFile = checkForSqlFile(migrationDir, "1.0");

        if (hasSqlFile) {
            log.debug("Migración inicial encontrada - SQL: {}", hasSqlFile);
            return true;
        }

        return false;
    }

    private static void waitForMigrationFiles(String version) {
        waitForMigrationFiles(version, 30, 1000); // 30 segundos timeout, 1 segundo entre reintentos  
    }

    private static void waitForMigrationFiles(String version, int timeoutSeconds, int retryIntervalMs) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;

        File migrationDir = new File("src/main/resources/" + MIGRATION_BASE_PATH);

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                // Verificar que el directorio de migración existe  
                if (!migrationDir.exists()) {
                    log.debug("Directorio de migración no existe aún: {}", migrationDir.getAbsolutePath());
                    Thread.sleep(retryIntervalMs);
                    continue;
                }

                // Buscar archivos de migración específicos para esta versión  
                boolean sqlFileExists = checkForSqlFile(migrationDir, version);
                boolean modelFileExists = checkForModelFile(migrationDir, version);

                if (sqlFileExists && modelFileExists) {
                    log.info("Archivos de migración verificados exitosamente para versión: {}", version);
                    return;
                }

                log.debug("Esperando archivos de migración - SQL: {}, Model: {}", sqlFileExists, modelFileExists);

            } catch (InterruptedException e) {
                log.debug("Error verificando archivos de migración, reintentando...", e);
            }

            try {
                Thread.sleep(retryIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrumpido esperando archivos de migración", e);
            }
        }

        throw new IllegalStateException("Timeout esperando la generación de archivos de migración para versión: " + version);
    }

    private static boolean checkForSqlFile(File migrationDir, String version) {
        // Buscar primero en el directorio base  
        boolean foundInBase = checkSqlInDirectory(migrationDir, version);
        if (foundInBase) {
            return true;
        }

        // Buscar en subdirectorios de plataforma (postgres, mysql, etc.)  
        File[] platformDirs = migrationDir.listFiles(File::isDirectory);
        if (platformDirs != null) {
            for (File platformDir : platformDirs) {
                if (checkSqlInDirectory(platformDir, version)) {
                    log.debug("Archivo SQL encontrado en plataforma: {}", platformDir.getName());
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean checkSqlInDirectory(File directory, String version) {
        File[] sqlFiles = directory.listFiles((dir, name)
                -> name.toLowerCase().startsWith(version.toLowerCase())
                && name.toLowerCase().endsWith(".sql")
                && !name.toLowerCase().startsWith("r")
                && // Excluir rollback files  
                !name.toLowerCase().startsWith("i") // Excluir init files  
        );

        if (sqlFiles != null && sqlFiles.length > 0) {
            for (File sqlFile : sqlFiles) {
                if (sqlFile.length() > 0) { // Verificar que el archivo no esté vacío  
                    log.debug("Archivo SQL encontrado: {}", sqlFile.getAbsolutePath());
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkForModelFile(File migrationDir, String version) {
        // Buscar en subdirectorio model si existe  
        File modelDir = new File(migrationDir, "model");
        File searchDir = modelDir.exists() ? modelDir : migrationDir;

        // Buscar archivo model XML con el patrón: version__name.model.xml (ej: 1.0__initial.model.xml)  
        File[] modelFiles = searchDir.listFiles((dir, name)
                -> name.toLowerCase().startsWith(version.toLowerCase())
                && name.toLowerCase().endsWith(".model.xml")
        );

        if (modelFiles != null && modelFiles.length > 0) {
            for (File modelFile : modelFiles) {
                if (modelFile.length() > 0) { // Verificar que el archivo no esté vacío  
                    log.debug("Archivo model encontrado: {}", modelFile.getName());
                    return true;
                }
            }
        }
        return false;
    }
}
