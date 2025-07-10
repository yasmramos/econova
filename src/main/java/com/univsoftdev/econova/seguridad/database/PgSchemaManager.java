package com.univsoftdev.econova.seguridad.database;

import lombok.extern.slf4j.Slf4j;
import javax.sql.DataSource;

import java.sql.*;

@Slf4j
public class PgSchemaManager {

    private final DataSource dataSource;

    public PgSchemaManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Crea un nuevo esquema con propietario (similar a SQL Server)
     */
    public boolean createSchema(String schemaName, String owner) {
        String sql = "CREATE SCHEMA ? AUTHORIZATION ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, schemaName);
            stmt.setString(2, owner);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            log.error("Error al crear esquema: " + schemaName, e);
            return false;
        }
    }

    /**
     * Asigna permisos de esquema a un rol de aplicación
     */
    public boolean grantSchemaPermissions(String schemaName, String roleName,
            String[] permissions) {
        String sqlTemplate = "GRANT %s ON SCHEMA ? TO ?";

        try (Connection conn = dataSource.getConnection()) {
            for (String permission : permissions) {
                String sql = String.format(sqlTemplate, permission);
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, schemaName);
                    stmt.setString(2, roleName);
                    stmt.execute();
                }
            }
            return true;
        } catch (SQLException e) {
            log.error("Error al asignar permisos al esquema: " + schemaName, e);
            return false;
        }
    }

    /**
     * Verifica si un usuario tiene permisos sobre un esquema
     */
    public boolean hasSchemaPermission(String username, String schemaName,
            String permission) {
        String sql = """
            SELECT has_schema_privilege(
                (SELECT oid FROM pg_roles WHERE rolname = ?),
                (SELECT oid FROM pg_namespace WHERE nspname = ?),
                ?
            )""";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, schemaName);
            stmt.setString(3, permission);

            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getBoolean(1);
        } catch (SQLException e) {
            log.error("Error verificando permisos de esquema", e);
            return false;
        }
    }
}
