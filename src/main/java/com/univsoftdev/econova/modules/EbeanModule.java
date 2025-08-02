package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.ebean.config.*;
import io.ebean.*;
import io.ebean.annotation.Platform;
import io.ebean.config.*;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.dbmigration.DbMigration;
import jakarta.inject.Inject;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Módulo de configuración para la base de datos Ebean.
 *
 * <p>
 * Esta clase configura la conexión a la base de datos PostgreSQL, establece los
 * proveedores para multi-tenancy y configura las migraciones y DDL según el
 * entorno de ejecución.</p>
 *
 * <p>
 * Características principales:
 * <ul>
 * <li>Configuración multi-tenant basada en esquemas</li>
 * <li>Gestión de migraciones de base de datos</li>
 * <li>Configuración condicional de DDL según el entorno</li>
 * <li>Verificación/creación del esquema base</li>
 * <li>Generación automática de archivos DDL para tenants</li>
 * </ul>
 * </p>
 */
@Slf4j
public class EbeanConfig {

    // Constantes de configuración  
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_USER_ENV = "DB_USER";
    private static final String DB_PASSWORD_ENV = "DB_PASSWORD";
    private static final String DB_URL_ENV = "DB_URL";
    private static final String DB_SCHEMA_ENV = "DB_SCHEMA";
    private static final String DEFAULT_SCHEMA = "accounting";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";
    private static final String DEFAULT_DATABASE = "econova";
    
    @Inject
    FlywayMigrator flywayMigrator;

    /**
     * Crea y configura la instancia principal de la base de datos Ebean.
     *
     * <p>
     * Este método:
     * <ol>
     * <li>Asegura que el esquema base exista</li>
     * <li>Crea la configuración de la base de datos</li>
     * <li>Inicializa Ebean con la configuración</li>
     * <li>Genera archivos DDL para uso en TenantMigrationService</li>
     * </ol>
     * </p>
     *
     * @return Instancia configurada de Database
     */
    public Database configure() {

        Database db = DatabaseFactory.create(createDatabaseConfig());

        // Generar archivos DDL  
        final var dbMigration = DbMigration.create();
        dbMigration.addPlatform(Platform.POSTGRES);
        dbMigration.setStrictMode(false);

        try {
            dbMigration.generateMigration();
            log.info("DDL migration files generated successfully");
            
            flywayMigrator.migrate(DEFAULT_SCHEMA);
            
        } catch (IOException ex) {
            log.error("Failed to generate DDL migration files: {}", ex.getMessage());
        }
        return db;
    }

    /**
     * Crea la configuración principal de la base de datos Ebean.
     *
     * <p>
     * Configura todos los aspectos de la conexión y comportamiento de Ebean:
     * <ul>
     * <li>Proveedor de datos de conexión</li>
     * <li>Proveedores de contexto multi-tenant</li>
     * <li>Modo multi-tenant (basado en esquemas)</li>
     * <li>Configuración de DDL y migraciones</li>
     * <li>Plataforma de base de datos</li>
     * </ul>
     * </p>
     *
     * @param currentUserProvider Proveedor del usuario actual
     * @param currentTenantProvider Proveedor del tenant actual
     * @param tenantSchemaProvider Proveedor de esquemas para tenants
     * @return Configuración completa de DatabaseConfig
     */
    private static DatabaseConfig createDatabaseConfig() {

        DatabaseConfig config = new DatabaseConfig();
        config.setName("econova");
        config.setDefaultServer(true);
        config.setRegister(true);
        config.setDataSourceConfig(createDataSourceConfig());
        config.setClassLoadConfig(new ClassLoadConfig(Thread.currentThread().getContextClassLoader()));

        // Configuración de proveedores inyectados      
        config.setCurrentUserProvider(new MyCurrentUserProvider());
        config.setCurrentTenantProvider(new MyCurrentTenantProvider());
        config.setTenantSchemaProvider(new MyTenantSchemaProvider());

        // Configuración de tenant basado en esquemas  
        config.setTenantMode(TenantMode.SCHEMA);

        // Configuración de migraciones y DDL      
        configureDdlAndMigrations(config);

        // Configurar header para archivos DDL generados  
        config.setDdlHeader("-- Generated DDL for Econova Multi-tenant Schema");

        // Plataforma de base de datos específica  
        config.setDatabasePlatform(new io.ebean.platform.postgres.PostgresPlatform());

        return config;
    }

    /**
     * Configura las opciones de DDL y migraciones según el entorno.
     *
     * <p>
     * Política de configuración (OPCIÓN 3):
     * <ul>
     * <li><strong>DDL Generate:</strong> Siempre habilitado para generar
     * archivos</li>
     * <li><strong>DDL Run:</strong> Deshabilitado para evitar ejecución
     * automática</li>
     * <li><strong>DDL Create Only:</strong> Solo crear, no drops</li>
     * <li><strong>Migraciones:</strong> Habilitadas para todos los
     * entornos</li>
     * </ul>
     * </p>
     *
     * @param config Configuración de la base de datos a modificar
     */
    private static void configureDdlAndMigrations(DatabaseConfig config) {
        // Generar DDL pero no ejecutar automáticamente  
        config.setDdlGenerate(true);  // Siempre generar para crear archivos  
        config.setDdlRun(false);      // No ejecutar automáticamente  
        config.setDdlCreateOnly(true); // Solo crear, no drops  

        // Migraciones habilitadas para todos los entornos  
        config.setRunMigration(false);

        log.info("DDL Generation is ENABLED, DDL Run is DISABLED");
        log.info("Migrations are ENABLED for all environments");
        log.debug("Database URL configured with schema: {}", getBaseSchema());
    }

    /**
     * Crea la configuración del pool de conexiones de datos.
     *
     * <p>
     * Configura parámetros importantes como:
     * <ul>
     * <li>Controlador JDBC</li>
     * <li>URL de conexión (incluyendo el esquema por defecto)</li>
     * <li>Credenciales de acceso</li>
     * <li>Parámetros del pool de conexiones</li>
     * </ul>
     * </p>
     *
     * @return Configuración completa de DataSourceConfig
     */
    private static DataSourceConfig createDataSourceConfig() {
        DataSourceConfig dsConfig = new DataSourceConfig();
        dsConfig.setDriver(DB_DRIVER);
        dsConfig.setUrl(getDatabaseUrl());
        dsConfig.setUsername(getDatabaseUser());
        dsConfig.setPassword(getDatabasePassword());

        // Configuración del pool de conexiones      
        dsConfig.setMinConnections(5);
        dsConfig.setMaxConnections(50);
        dsConfig.setHeartbeatSql("SELECT 1");
        dsConfig.setMaxInactiveTimeSecs((int) TimeUnit.MINUTES.toSeconds(30));
        dsConfig.setCaptureStackTrace(true);
        dsConfig.setMaxAgeMinutes((int) TimeUnit.HOURS.toMinutes(2));
        return dsConfig;
    }

    /**
     * Obtiene la URL completa de la base de datos incluyendo el esquema por
     * defecto.
     *
     * <p>
     * Esta URL incluye el parámetro currentSchema para asegurar que todas las
     * operaciones de Ebean ocurran en el esquema correcto.</p>
     *
     * @return URL de base de datos con currentSchema
     */
    private static String getDatabaseUrl() {
        String baseUrl = Optional.ofNullable(System.getenv(DB_URL_ENV))
                .orElse("jdbc:postgresql://localhost:5432/" + DEFAULT_DATABASE);
        String schema = getBaseSchema();

        // Añadir o modificar el parámetro currentSchema en la URL  
        if (baseUrl.contains("?")) {
            // Ya tiene parámetros, añadir o reemplazar currentSchema  
            if (baseUrl.contains("currentSchema=")) {
                // Reemplazar el valor existente  
                return baseUrl.replaceAll("currentSchema=[^&]*", "currentSchema=" + schema);
            } else {
                // Añadir currentSchema al final  
                return baseUrl + "&currentSchema=" + schema;
            }
        } else {
            // No tiene parámetros, añadir el parámetro  
            return baseUrl + "?currentSchema=" + schema;
        }
    }

    /**
     * Obtiene el nombre de usuario de la base de datos.
     *
     * @return Nombre de usuario desde variable de entorno o valor por defecto
     */
    private static String getDatabaseUser() {
        return Optional.ofNullable(System.getenv(DB_USER_ENV)).orElse(DEFAULT_USER);
    }

    /**
     * Obtiene la contraseña de la base de datos.
     *
     * @return Contraseña desde variable de entorno o valor por defecto
     */
    private static String getDatabasePassword() {
        return Optional.ofNullable(System.getenv(DB_PASSWORD_ENV)).orElse(DEFAULT_PASSWORD);
    }

    /**
     * Obtiene el nombre del esquema base.
     *
     * @return Nombre del esquema base desde variable de entorno o valor por
     * defecto
     */
    private static String getBaseSchema() {
        return Optional.ofNullable(System.getenv(DB_SCHEMA_ENV)).orElse(DEFAULT_SCHEMA);
    }

}
