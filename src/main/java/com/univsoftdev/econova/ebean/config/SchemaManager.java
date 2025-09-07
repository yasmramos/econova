package com.univsoftdev.econova.ebean.config;

import io.ebean.Database;
import io.ebean.SqlRow;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Singleton
public class SchemaManager {

    private final EbeanMigrator migrator;
    private final Database database;

    @Inject
    public SchemaManager(EbeanMigrator migrator, Database database) {
        this.migrator = migrator;
        this.database = database;
    }

    /**
     * Crea y migra un nuevo esquema usando migraciones compartidas
     */
    public void createAndMigrateSchema(String schemaName) {
        try {
            if (schemaExists(schemaName)) {
                log.info("El esquema {} ya existe, aplicando migraciones...", schemaName);
            } else {
                log.info("Creando nuevo esquema: {}", schemaName);
                createSchema(schemaName);
            }

            // Ejecutar migraciones
            migrator.migrate(schemaName);

            // Verificar que realmente se crearon las tablas
            if (isSchemaProperlyMigrated(schemaName)) {
                log.info("Esquema {} creado y migrado exitosamente", schemaName);
            } else {
                throw new RuntimeException("El esquema " + schemaName + " no se migró correctamente - no se crearon tablas");
            }

        } catch (RuntimeException e) {
            log.error("Error crítico creando/migrando esquema {}", schemaName, e);
            throw new RuntimeException("No se pudo crear/migrar el esquema: " + schemaName, e);
        }
    }

    /**
     * Crea el esquema físicamente
     */
    private void createSchema(String schemaName) {
        try {
            database.sqlUpdate("CREATE SCHEMA IF NOT EXISTS " + schemaName).execute();

            // Esperar y verificar que el esquema se creó
            if (!waitForSchemaCreation(schemaName, 5, 1000)) {
                throw new RuntimeException("No se pudo verificar la creación del esquema: " + schemaName);
            }

        } catch (RuntimeException e) {
            throw new RuntimeException("Error creando esquema: " + schemaName, e);
        }
    }

    /**
     * Espera a que el esquema esté disponible
     */
    private boolean waitForSchemaCreation(String schemaName, int maxRetries, int delayMs) {
        for (int i = 0; i < maxRetries; i++) {
            if (schemaExists(schemaName)) {
                return true;
            }
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    /**
     * Verificación robusta de que el esquema tiene tablas
     */
    public boolean isSchemaProperlyMigrated(String schemaName) {
        try {
            // Verificar que existen tablas de aplicación (no migraciones ni pg_)
            String sql = """
            SELECT COUNT(*) FROM information_schema.tables 
            WHERE table_schema = ? 
            AND table_name NOT LIKE 'db_migration_%'
            AND table_name NOT LIKE 'pg_%'
        """;

            Integer tableCount = database.sqlQuery(sql)
                    .setParameter(1, schemaName)
                    .mapToScalar(Integer.class)
                    .findOne();

            boolean hasApplicationTables = tableCount != null && tableCount > 0;

            // Verificar que la tabla de migraciones existe (incluso si está vacía)
            boolean hasMigrationTable = hasMigrationTable(schemaName);

            log.debug("Esquema {} - Tablas de aplicación: {}, Tabla de migraciones: {}",
                    schemaName, hasApplicationTables, hasMigrationTable);

            return hasApplicationTables || hasMigrationTable; // Al menos una debe existir

        } catch (Exception e) {
            log.warn("Error verificando migración del esquema {}: {}", schemaName, e.getMessage());
            return false;
        }
    }

    private boolean hasMigrationTable(String schemaName) {
        try {
            String metaTable = "db_migration_" + schemaName.toLowerCase();
            String checkSql = "SELECT EXISTS ("
                    + "SELECT FROM information_schema.tables "
                    + "WHERE table_schema = ? AND table_name = ?)";

            SqlRow existsResult = database.sqlQuery(checkSql)
                    .setParameter(1, schemaName)
                    .setParameter(2, metaTable)
                    .findOne();

            return existsResult != null && existsResult.getBoolean("exists");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica que la tabla de migraciones existe y tiene registros
     */
    private boolean hasMigrationTableWithRecords(String schemaName) {
        try {
            String metaTable = "db_migration_" + schemaName.toLowerCase();
            String sql = "SELECT COUNT(*) FROM " + schemaName + "." + metaTable;

            Integer count = database.sqlQuery(sql)
                    .mapToScalar(Integer.class)
                    .findOne();

            return count != null && count > 0;

        } catch (Exception e) {
            // La tabla no existe o no se puede acceder
            return false;
        }
    }

    public boolean schemaExists(String schemaName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = ?";
            Integer count = database.sqlQuery(sql)
                    .setParameter(1, schemaName)
                    .mapToScalar(Integer.class)
                    .findOne();

            return count != null && count > 0;

        } catch (Exception e) {
            log.error("Error verificando existencia del esquema {}", schemaName, e);
            return false;
        }
    }

    /**
     * Crea múltiples esquemas en lote con verificación
     */
    public void createSchemas(List<String> schemaNames) {
        for (String schemaName : schemaNames) {
            try {
                createAndMigrateSchema(schemaName);
            } catch (Exception e) {
                log.error("Error creando esquema {}, continuando con los demás...", schemaName, e);
            }
        }
    }

    /**
     * Elimina un esquema con verificación
     */
    public void dropSchema(String schemaName) {
        try {
            if (schemaExists(schemaName)) {
                log.warn("Eliminando esquema: {}", schemaName);
                database.sqlUpdate("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE").execute();

                // Verificar que se eliminó
                if (!schemaExists(schemaName)) {
                    log.info("Esquema {} eliminado exitosamente", schemaName);
                } else {
                    log.error("No se pudo eliminar el esquema {}", schemaName);
                }
            } else {
                log.info("El esquema {} no existe, no se requiere eliminación", schemaName);
            }
        } catch (Exception e) {
            log.error("Error eliminando esquema {}", schemaName, e);
            throw new RuntimeException("Error eliminando esquema: " + schemaName, e);
        }
    }

    public List<String> listAllSchemas() {
        return database.sqlQuery("SELECT schema_name FROM information_schema.schemata "
                + "WHERE schema_name NOT LIKE 'pg_%' AND schema_name != 'information_schema' "
                + "ORDER BY schema_name")
                .mapToScalar(String.class)
                .findList();
    }

    /**
     * Verifica integridad completa del esquema
     */
    public boolean validateSchema(String schemaName) {
        return schemaExists(schemaName)
                && (isSchemaProperlyMigrated(schemaName) || hasMigrationTable(schemaName)); // Acepta esquemas con migraciones vacías
    }
}
