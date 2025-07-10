package com.univsoftdev.econova.modules;

import jakarta.inject.Singleton;
import jakarta.inject.Provider;
import com.univsoftdev.econova.MyCurrentUserProvider;
import io.avaje.inject.Factory;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.ClassLoadConfig;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Factory
@Slf4j
public class DatabaseModule {

    @Singleton
    public static class EbeanDatabaseProvider implements Provider<Database> {

        private static final String DB_DRIVER = "org.postgresql.Driver";
        private static final String DB_USER_ENV = "DB_USER";
        private static final String DB_PASSWORD_ENV = "DB_PASSWORD";
        private static final String DB_URL_ENV = "DB_URL";
        private static final String DB_SCHEMA_ENV = "DB_SCHEMA";
        private static final String DB_NAME_ENV = "DB_NAME";
        private static final String DEFAULT_SCHEMA = "accounting";

        private static final String DRIVER = "org.postgresql.Driver";
        private static final String DEFAULT_USER = "postgres";
        private static final String DEFAULT_PASSWORD = "postgres";
        private static final String DEFAULT_DATABASE = "econova";
        private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/" + DEFAULT_DATABASE;

        @Override
        public Database get() {
            ensureSchemaExists();
            return DatabaseFactory.create(createDatabaseConfig());
        }

        private void ensureSchemaExists() {
            String baseUrl = Optional.ofNullable(System.getenv(DB_URL_ENV)).orElse(DEFAULT_URL);
            String url = baseUrl;
            String user = getDatabaseUser();
            String password = getDatabasePassword();
            String schema = Optional.ofNullable(System.getenv(DB_SCHEMA_ENV)).orElse(DEFAULT_SCHEMA);
            try (Connection conn = DriverManager.getConnection(url, user, password); Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schema + ";");
                log.info("Esquema '{}' verificado/creado correctamente.", schema);
                
                // Si es accounting y no es dev/test, aplicar DDL de tablas SOLO si no existe init_marker
                String appEnv = Optional.ofNullable(System.getenv("APP_ENV"))
                        .orElse(System.getProperty("app.env", "prod"));
                
                boolean isProd = !appEnv.equalsIgnoreCase("dev") && !appEnv.equalsIgnoreCase("test");
                
                if (isProd && schema.equals("accounting")) {
                    stmt.execute("SET search_path TO " + schema);
                    boolean needsInit = true;
                    try (var rs = stmt.executeQuery("SELECT to_regclass('init_marker')")) {
                        if (rs.next() && rs.getString(1) != null) {
                            needsInit = false;
                        }
                    }
                    if (needsInit) {
                        final List<String> ddlInitSql = readInitSqlListFromResource("ebean-init.sql");
                        for (final String sql : ddlInitSql) {
                            if (!sql.isBlank()) {
                                stmt.execute(sql);
                            }
                        }
                        // Crear tabla de control para marcar la inicialización
                        stmt.execute("CREATE TABLE IF NOT EXISTS init_marker (id INT PRIMARY KEY);");
                        stmt.execute("INSERT INTO init_marker (id) VALUES (1) ON CONFLICT DO NOTHING;");
                        log.info("DDL aplicado al schema base '{}' en producción (solo una vez).", schema);
                    } else {
                        log.info("El schema '{}' ya fue inicializado previamente, no se ejecuta DDL.", schema);
                    }
                }
            } catch (Exception e) {
                log.error("No se pudo crear/verificar el esquema '{}': {}", schema, e.getMessage());
            }
        }

        private DatabaseConfig createDatabaseConfig() {
            try {
                final var config = new DatabaseConfig();
                config.setName("ebean");
                config.setDefaultServer(true);
                config.setRegister(true);
                config.setDataSourceConfig(createDataSourceConfig());
                
                final ClassLoadConfig classLoadConfig = new ClassLoadConfig(Thread.currentThread().getContextClassLoader());
                config.setClassLoadConfig(classLoadConfig);
                config.setCurrentUserProvider(new MyCurrentUserProvider());
                config.setCurrentTenantProvider(new com.univsoftdev.econova.MyCurrentTenantProvider());
                config.setRunMigration(true);
                config.setDatabasePlatform(new io.ebean.platform.postgres.PostgresPlatform());
                
                // Activar DDL automático solo en desarrollo o pruebas
                final String appEnv = Optional.ofNullable(System.getenv("APP_ENV"))
                        .orElse(System.getProperty("app.env", "prod"));
                
                final boolean enableDdl = appEnv.equalsIgnoreCase("dev") || appEnv.equalsIgnoreCase("test");
                config.setDdlGenerate(enableDdl);
                config.setDdlRun(enableDdl);
                if (enableDdl) {
                    log.info("DDL automático ACTIVADO (entorno: {})", appEnv);
                } else {
                    log.info("DDL automático DESACTIVADO (entorno: {})", appEnv);
                }
                // Multi-tenant por schema
                config.setTenantMode(io.ebean.config.TenantMode.SCHEMA);
                config.setTenantSchemaProvider(new com.univsoftdev.econova.MyTenantSchemaProvider());
                return config;
            } catch (Exception e) {
                log.error("Failed to create database configuration", e);
                throw new RuntimeException("Database initialization failed", e);
            }
        }

        private DataSourceConfig createDataSourceConfig() {
            final var dsConfig = new DataSourceConfig();
            dsConfig.setDriver(DRIVER);
            dsConfig.setSchema("accounting");
            dsConfig.setUrl(buildDatabaseUrl());
            dsConfig.setUsername(getDatabaseUser());
            dsConfig.setPassword(getDatabasePassword());

            dsConfig.setMinConnections(5);
            dsConfig.setMaxConnections(50);
            dsConfig.setHeartbeatSql("SELECT 1");
            dsConfig.setMaxInactiveTimeSecs((int) TimeUnit.MINUTES.toSeconds(30));
            dsConfig.setCaptureStackTrace(true);
            dsConfig.setMaxAgeMinutes((int) TimeUnit.HOURS.toMinutes(2));

            // Eliminado: bloque InitSql. La inicialización automática y segura solo se realiza en ensureSchemaExists(), nunca aquí.
            return dsConfig;
        }

        private java.util.List<String> readInitSqlListFromResource(String resourceName) {
            try (final var is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
                if (is == null) {
                    return null;
                }
                final String sql = new String(is.readAllBytes());
                // Divide por punto y coma, elimina líneas vacías y recorta espacios
                return java.util.Arrays.stream(sql.split(";"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
            } catch (Exception e) {
                log.error("Error leyendo el archivo de init SQL: {}", resourceName, e);
                return null;
            }
        }

        private String buildDatabaseUrl() {
            final String baseUrl = Optional.ofNullable(System.getenv(DB_URL_ENV)).orElse(DEFAULT_URL);
            final String schema = Optional.ofNullable(System.getenv(DB_SCHEMA_ENV)).orElse(DEFAULT_SCHEMA);
            return baseUrl + "?currentSchema=" + schema;
        }

        private String getDatabaseUser() {
            return Optional.ofNullable(System.getenv(DB_USER_ENV))
                    .orElseGet(() -> {
                        log.warn("Usando usuario por defecto para la base de datos contable");
                        return DEFAULT_USER;
                    });
        }

        private String getDatabasePassword() {
            return Optional.ofNullable(System.getenv(DB_PASSWORD_ENV))
                    .orElseGet(() -> {
                        log.warn("Usando contraseña por defecto para la base de datos contable");
                        return DEFAULT_PASSWORD;
                    });
        }

        /**
         * Crea un nuevo schema y ejecuta el DDL de inicialización en ese schema
         * (multi-tenant).
         *
         * @param schema Nombre del schema a crear
         * @param dbUrl URL de conexión JDBC
         * @param dbUser Usuario de la base de datos
         * @param dbPassword Contraseña de la base de datos
         */
        public static void createSchemaAndTablesForTenant(String schema, String dbUrl, String dbUser, String dbPassword) {
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword); Statement stmt = conn.createStatement()) {
                // 1. Crear el schema
                stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
                // 2. Cambiar el search_path al schema del tenant
                stmt.execute("SET search_path TO " + schema);
                // 3. Verificar si ya existe la tabla de control
                boolean needsInit = true;
                try (var rs = stmt.executeQuery("SELECT to_regclass('init_marker')")) {
                    if (rs.next() && rs.getString(1) != null) {
                        needsInit = false;
                    }
                }
                if (needsInit) {
                    List<String> ddlInitSql = new EbeanDatabaseProvider().readInitSqlListFromResource("ebean-init.sql");
                    for (String sql : ddlInitSql) {
                        if (!sql.isBlank()) {
                            stmt.execute(sql); // Ya se ejecuta en el schema correcto
                        }
                    }
                    // Crear tabla de control para marcar la inicialización
                    stmt.execute("CREATE TABLE IF NOT EXISTS init_marker (id INT PRIMARY KEY);");
                    stmt.execute("INSERT INTO init_marker (id) VALUES (1) ON CONFLICT DO NOTHING;");
                    log.info("Schema y tablas creadas para el tenant: {} (solo una vez)", schema);
                } else {
                    log.info("El schema '{}' del tenant ya fue inicializado previamente, no se ejecuta DDL.", schema);
                }
            } catch (Exception e) {
                log.error("Error creando schema y tablas para tenant '{}': {}", schema, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
