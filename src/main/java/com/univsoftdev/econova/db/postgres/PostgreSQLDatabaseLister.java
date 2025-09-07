package com.univsoftdev.econova.db.postgres;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgreSQLDatabaseLister {

    private final String url;
    private final String username;
    private final String password;

    @Inject
    public PostgreSQLDatabaseLister() {
        this.url = null;
        this.username = null;
        this.password = null;
    }

    /**
     * Constructs a lister targeting the 'postgres' system database.
     *
     * @param host PostgreSQL host
     * @param port PostgreSQL port
     * @param username Username
     * @param password Password
     */
    public PostgreSQLDatabaseLister(String host, int port, String username, String password) {
        this.url = String.format("jdbc:postgresql://%s:%d/postgres",
                Objects.requireNonNull(host), port);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
    }

    /**
     * Retrieves names of all non-template databases.
     *
     * @return list of database names (may be empty, never null)
     */
    public List<String> getAvailableDatabases() {
        List<String> databases = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, username, password); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false")) {

            while (rs.next()) {
                databases.add(rs.getString("datname"));
            }

        } catch (SQLException ex) {
            log.error("Failed to retrieve database list from PostgreSQL at {}", url, ex);
            throw new RuntimeException("Database listing failed", ex); // Or handle differently
        }

        return databases;
    }
}
