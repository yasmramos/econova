package com.univsoftdev.econova.ebean.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.univsoftdev.econova.Resources;

@Slf4j
public class EbeanUtils {

    private EbeanUtils() {
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
    public static void createSchemaAndTablesForTenant(
            String schema, 
            String dbUrl, 
            String dbUser, 
            String dbPassword
    ) {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword); 
                Statement stmt = conn.createStatement()) {
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
                List<String> ddlInitSql = readInitSqlListFromResource("ebean-init.sql");
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

    public static List<String> readInitSqlListFromResource(String resourceName) {
        try (final var is = Resources.class.getClassLoader().getResourceAsStream(resourceName)) {
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
}
