package com.univsoftdev.econova.db.postgres;

import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Singleton
public class PgSchemaManager {

    private final Database database;

    @Inject
    public PgSchemaManager(Database database) {
        this.database = database;
    }

    /**
     * Crea un nuevo esquema con propietario (similar a SQL Server)
     */
    public boolean createSchema(String schemaName, String owner) {
        String sql = "CREATE SCHEMA %s AUTHORIZATION %s";

        try {
            database.sqlUpdate(String.format(sql, schemaName, owner));
            return true;
        } catch (Exception e) {
            log.error("Error al crear esquema: " + schemaName, e);
            return false;
        }
    }

    /**
     * Asigna permisos de esquema a un rol de aplicación
     *
     * @param schemaName
     * @param roleName
     * @param permissions
     */
    public boolean grantSchemaPermissions(String schemaName, String roleName,
            String[] permissions) {
        String sqlTemplate = "GRANT %s ON SCHEMA %s TO %s";

        try {
            for (String permission : permissions) {
                String sql = String.format(sqlTemplate, permission, schemaName, roleName);
                database.sqlUpdate(sql);
            }
            return true;
        } catch (Exception e) {
            log.error("Error al asignar permisos al esquema: " + schemaName, e);
            return false;
        }
    }

}
