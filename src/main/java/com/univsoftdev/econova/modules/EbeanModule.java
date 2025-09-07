package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.core.config.AppConfig;
import com.univsoftdev.econova.ebean.config.*;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.ebean.*;
import io.ebean.config.*;
import io.ebean.datasource.DataSourceConfig;
import java.sql.Connection;
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
@Factory
public class EbeanModule {

    // Constantes de configuración  
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_SCHEMA_ENV = "DB_SCHEMA";
    private static final String DEFAULT_SCHEMA = "public";

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
    @Bean
    public Database database() {

        var db = DatabaseFactory.create(createDatabaseConfig());

        // Generar migraciones después de que la base de datos esté inicializada  
        // y el contexto de esquema esté establecido 
        MigratorGenerator.generateInitMigration();
        MigratorGenerator.generateNextMigration();
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
     * @return Configuración completa de DatabaseConfig
     */
    @Bean
    public DatabaseConfig createDatabaseConfig() {
        DatabaseConfig config = new DatabaseConfig();
        config.setName("econova");
        config.setDefaultServer(true);
        config.setRegister(true);
        config.setDataSourceConfig(createDataSourceConfig());
        config.setClassLoadConfig(new ClassLoadConfig(Thread.currentThread().getContextClassLoader()));

        // Configurar esquema para migraciones  
        config.setDbSchema(getBaseSchema());

        // Configuración de proveedores inyectados        
        config.setCurrentUserProvider(new MyCurrentUserProvider());
        config.setCurrentTenantProvider(new MyCurrentTenantProvider());
        config.setTenantSchemaProvider(new MyTenantSchemaProvider());

        // Configuración de tenant basado en esquemas    
        config.setTenantMode(TenantMode.SCHEMA);

        // Configuración de migraciones y DDL - CORREGIDO
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
     * @param config Configuración de la base de datos a modificar
     */
    public void configureDdlAndMigrations(DatabaseConfig config) {
        config.setDdlRun(true);          // No ejecutar DDL automáticamente
        config.setDdlGenerate(false);     // No generar DDL
        config.setRunMigration(true);     // Sí ejecutar migraciones
        config.setDdlExtra(true);         // Permitir DDL extra si es necesario
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
    @Bean
    public DataSourceConfig createDataSourceConfig() {
        DataSourceConfig dsConfig = new DataSourceConfig();
        dsConfig.setDriver(DB_DRIVER);
        dsConfig.setUrl(AppConfig.getDatabaseUrl()); 
        dsConfig.setUsername(AppConfig.getDatabaseUser());
        dsConfig.setPassword(AppConfig.getDatabasePassword());
        dsConfig.setSchema(getBaseSchema());

        // Configuración mejorada del pool de conexiones  
        dsConfig.setMinConnections(1);
        dsConfig.setMaxConnections(50);
        dsConfig.setHeartbeatSql("SELECT 1");
        dsConfig.setMaxInactiveTimeSecs((int) TimeUnit.MINUTES.toSeconds(30));
        dsConfig.setCaptureStackTrace(true);
        dsConfig.setMaxAgeMinutes((int) TimeUnit.HOURS.toMinutes(2));
        dsConfig.setIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);

        // IMPORTANTE: AutoCommit debe ser false para migraciones
        dsConfig.setAutoCommit(false);

        return dsConfig;
    }

    /**
     * Obtiene el nombre del esquema base.
     *
     * @return Nombre del esquema base desde variable de entorno o valor por
     * defecto
     */
    private String getBaseSchema() {
        return Optional.ofNullable(System.getenv(DB_SCHEMA_ENV)).orElse(DEFAULT_SCHEMA);
    }

}
