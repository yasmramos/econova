package com.univsoftdev.econova.ebean.config;

import io.ebean.Database;
import io.ebean.SqlRow;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class EbeanMigrator {
    
    private final DataSourceConfig config;
    private final Database database;
    private static final String MIGRATION_BASE_PATH = "dbmigration";
    
    @Inject
    public EbeanMigrator(DataSourceConfig config, Database database) {
        this.config = config;
        this.database = database;
    }

    /**
     * Ejecuta migraciones para un esquema específico usando migraciones
     * compartidas
     *
     * @param schema
     */
    public void migrate(String schema) {
        try {
            log.info("Iniciando migración para esquema: {}", schema);

            // Primero asegurar que las migraciones estén generadas
            MigratorGenerator.generateNextMigration();

            // Verificar si ya hay migraciones aplicadas
            boolean hasAppliedMigrations = checkIfMigrationsApplied(schema);
            
            if (!hasAppliedMigrations) {
                log.info("No se encontraron migraciones aplicadas previamente. Forzando migración inicial.");
                runInitMigration(); // FORZAR ejecución de migración inicial
            }
            
            MigrationConfig migrationConfig = createMigrationConfig(schema);
            database.sqlUpdate("SET search_path TO " + schema).execute();
            
            runMigrations(migrationConfig);
            log.info("Migración completada exitosamente para esquema: {}", schema);
        } catch (Exception e) {
            String errorMsg = String.format("Error en migración para esquema %s: %s", schema, e.getMessage());
            log.error(errorMsg, e);
            throw new MigrationException(errorMsg, e);
        }
    }
    
    private boolean checkIfMigrationsApplied(String schema) {
        try {
            String metaTable = "db_migration_" + schema.toLowerCase();
            String checkTableExistsSql = "SELECT EXISTS ("
                    + "SELECT FROM information_schema.tables "
                    + "WHERE table_schema = ? AND table_name = ?)";
            
            SqlRow existsResult = database.sqlQuery(checkTableExistsSql)
                    .setParameter(1, schema)
                    .setParameter(2, metaTable)
                    .findOne();
            
            if (existsResult != null && existsResult.getBoolean("exists")) {
                String checkSql = "SELECT COUNT(*) as count FROM " + schema + "." + metaTable;
                SqlRow result = database.sqlQuery(checkSql).findOne();
                return result != null && result.getInteger("count") > 0;
            }
            return false;
        } catch (Exception e) {
            log.warn("Error verificando migraciones aplicadas: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Configura los parámetros de migración para el esquema específico Usa las
     * mismas migraciones base pero diferente tabla de metadatos
     */
    private MigrationConfig createMigrationConfig(String schema) {
        MigrationConfig migrationConfig = new MigrationConfig();
        migrationConfig.setDbSchema(schema);
        migrationConfig.setDbUsername(config.getUsername());
        migrationConfig.setDbPassword(config.getPassword());
        migrationConfig.setPlatform("postgres");

        // CORREGIR: Usar la URL base SIN currentSchema para migraciones
        String baseUrl = config.getUrl();
        if (baseUrl.contains("currentSchema=")) {
            baseUrl = baseUrl.replaceAll("currentSchema=[^&]*", "");
            // Limpiar parámetros vacíos
            baseUrl = baseUrl.replace("?&", "?").replace("&&", "&");
            if (baseUrl.endsWith("?") || baseUrl.endsWith("&")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
        }
        migrationConfig.setDbUrl(baseUrl + "?currentSchema=" + schema);
        
        migrationConfig.setMigrationPath(MIGRATION_BASE_PATH);
        migrationConfig.setEarlyChecksumMode(true);
        migrationConfig.setCreateSchemaIfNotExists(true);
        migrationConfig.setMigrationInitPath("dbinit");
        migrationConfig.setMetaTable("db_migration_" + schema.toLowerCase());
        migrationConfig.setRunPlaceholders("true");
        migrationConfig.setFastMode(false);
        
        return migrationConfig;
    }

    /**
     * Ejecuta las migraciones configuradas
     */
    private void runMigrations(MigrationConfig migrationConfig) {
        try {
            log.info("Ejecutando migraciones desde: {}", migrationConfig.getMigrationPath());
            log.info("URL de base de datos: {}", migrationConfig.getDbUrl());
            log.info("Esquema: {}", migrationConfig.getDbSchema());
            
            MigrationRunner runner = new MigrationRunner(migrationConfig);

            // Forzar la verificación y ejecución de migraciones
            runner.run();

            // Verificar que las migraciones se aplicaron
            verifyMigrationsApplied(migrationConfig);
            
        } catch (Exception e) {
            log.error("Error detallado en migraciones: {}", e.getMessage(), e);
            throw new MigrationException("Error al ejecutar migraciones", e);
        }
    }
    
    private void verifyMigrationsApplied(MigrationConfig migrationConfig) {
        try {
            String metaTable = migrationConfig.getMetaTable();
            String schema = migrationConfig.getDbSchema();

            // Verificar si la tabla de migración existe antes de usarla
            String checkTableExistsSql = String.format(
                    "SELECT EXISTS (SELECT FROM information_schema.tables "
                    + "WHERE table_schema = ? AND table_name = ?)",
                    schema, metaTable
            );
            
            SqlRow existsResult = database.sqlQuery(checkTableExistsSql).setParameter(1, schema).setParameter(2, metaTable).findOne();
            if (existsResult != null && existsResult.getBoolean("exists")) {
                String checkSql = String.format(
                        "SELECT COUNT(*) as count FROM %s.%s",
                        schema, metaTable
                );
                
                SqlRow result = database.sqlQuery(checkSql).findOne();
                if (result != null) {
                    Integer appliedCount = result.getInteger("count");
                    log.info("Migraciones aplicadas exitosamente: {}", appliedCount);
                } else {
                    log.info("No se encontraron registros en la tabla de migraciones");
                }
            } else {
                log.warn("La tabla de migraciones {} aún no existe.", metaTable);
            }
            
        } catch (Exception e) {
            log.warn("No se pudo verificar migraciones aplicadas: {}", e.getMessage(), e);
        }
    }
    
    private MigrationConfig createInitMigrationConfig() {
        MigrationConfig migrationConfig = new MigrationConfig();
        migrationConfig.setDbSchema("public");
        migrationConfig.setDbUsername(config.getUsername());
        migrationConfig.setDbPassword(config.getPassword());
        migrationConfig.setPlatform("postgres");
        migrationConfig.setDbUrl(config.getUrl() + "?currentSchema=public");
        migrationConfig.setMigrationPath("dbinit");
        migrationConfig.setMigrationInitPath("dbinit");
        migrationConfig.setEarlyChecksumMode(true);
        migrationConfig.setCreateSchemaIfNotExists(true);
        migrationConfig.setFastMode(false);
        // Diferente tabla de metadatos por esquema para tracking independiente
        migrationConfig.setMetaTable("db_migration_public");
        return migrationConfig;
    }
    
    public void runInitMigration() {
        if (MigratorGenerator.hasInitialMigration()) {
            log.info("Migración inicial ya existe, omitiendo generación");
            return;
        }
        try {
            MigrationConfig initMigrationConfig = createInitMigrationConfig();
            new MigrationRunner(initMigrationConfig).run();
            log.info("Migraciones ejecutadas correctamente para el esquema: " + initMigrationConfig.getDbSchema());
        } catch (Exception e) {
            log.error("Error ejecutando migración inicial", e);
        }
    }
    
    public static class MigrationException extends RuntimeException {
        
        public MigrationException(String message) {
            super(message);
        }
        
        public MigrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
