package com.univsoftdev.econova.ebean.config;

import com.univsoftdev.econova.core.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

@Slf4j
public class FlywayMigrator {

    public void migrateAllSchemas(String... schemas) {

        try {
            for (String schema : schemas) {
                migrate(schema);
            }
        } catch (FlywayException flywayException) {
            log.error("No se pudo ejecutar la migracion en el esquema" + flywayException.getMessage());
        }
    }

    public void migrate(String schema) {

        try {
            log.info("Iniciando migración para el esquema: " + schema);

            Flyway flyway = Flyway.configure()
                    .driver(AppConfig.getDatabaseDriver())
                    .createSchemas(true)
                    .schemas(schema)
                    .defaultSchema(schema)
                    .dataSource(
                            AppConfig.getDatabaseUrl(),
                            AppConfig.getDatabaseAdminUser(),
                            AppConfig.getDatabaseAdminPassword()
                    )
                    .baselineOnMigrate(true)
                    .locations("classpath:dbmigration/postgres")
                    .validateMigrationNaming(false)
                    .sqlMigrationPrefix("")
                    .sqlMigrationSeparator("__")
                    .sqlMigrationSuffixes(".sql")
                    .table("flyway_schema_history_" + schema)
                    .load();

            flyway.migrate();

            log.info("Migración ejecutada correctamente para el esquema: " + schema);

        } catch (FlywayException flywayException) {
            log.error("No se pudo ejecutar la migracion en el esquema" + flywayException.getMessage());
        }
    }

}
