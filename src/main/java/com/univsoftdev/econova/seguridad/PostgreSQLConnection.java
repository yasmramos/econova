package com.univsoftdev.econova.seguridad;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgreSQLConnection implements AutoCloseable {

    private static final String DRIVER = "org.postgresql.Driver";
    private static final int DEFAULT_MAX_POOL_SIZE = 10;
    private static final int DEFAULT_MIN_IDLE = 2;
    private static final int DEFAULT_IDLE_TIMEOUT = 30000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 10000;

    private HikariDataSource dataSource;
    private String currentDatabase;
    private final String server;
    private final String port;
    private final String userName;
    private final String password;
    private final String defaultDatabase; // Usualmente "postgres" para operaciones administrativas

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        }
    }

    public PostgreSQLConnection(String server, String port,
            String username, String password,
            String defaultDatabase) {
        validateConnectionParams(server, port, username, defaultDatabase);

        this.server = server;
        this.port = port;
        this.userName = username;
        this.password = password;
        this.defaultDatabase = defaultDatabase;
        this.currentDatabase = defaultDatabase;
    }

    public boolean testConnection() {
        return testConnection(this.defaultDatabase);
    }

    public boolean testConnection(String databaseName) {
        try (Connection testConn = DriverManager.getConnection(
                buildConnectionUrl(databaseName), userName, password)) {
            return testConn.isValid(5); // 5 segundos de timeout
        } catch (SQLException e) {
            log.error("Connection test failed for database: " + databaseName, e);
            return false;
        }
    }

    public void switchDatabase(String newDatabase) throws SQLException {
        if (newDatabase == null || newDatabase.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty");
        }

        if (newDatabase.equals(currentDatabase)) {
            return; // Ya estamos en la base de datos solicitada
        }

        if (!databaseExists(newDatabase)) {
            createDatabase(newDatabase);
        }

        this.currentDatabase = newDatabase;

        // Reconfigurar el pool de conexiones con la nueva base de datos
        if (dataSource != null) {
            shutdownPool();
            configureConnectionPool();
        }
    }

    // Verificar si una base de datos existe
    public boolean databaseExists(String dbName) throws SQLException {
        try (Connection conn = getAdminConnection(); ResultSet rs = conn.getMetaData().getCatalogs()) {

            while (rs.next()) {
                String existingDbName = rs.getString(1);
                if (existingDbName.equals(dbName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void createDatabase(String dbName) throws SQLException {
        if (databaseExists(dbName)) {
            log.info("Database {} already exists", dbName);
            return;
        }

        try (Connection conn = getAdminConnection(); Statement stmt = conn.createStatement()) {

            String sql = String.format("CREATE DATABASE \"%s\"", dbName);
            stmt.executeUpdate(sql);
            log.info("Database {} created successfully", dbName);
        }
    }

    private Connection getAdminConnection() throws SQLException {
        return DriverManager.getConnection(
                buildConnectionUrl(defaultDatabase),
                userName,
                password
        );
    }

    public void configureConnectionPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(buildConnectionUrl(currentDatabase));
        config.setUsername(userName);
        config.setPassword(password);
        config.setMaximumPoolSize(DEFAULT_MAX_POOL_SIZE);
        config.setMinimumIdle(DEFAULT_MIN_IDLE);
        config.setIdleTimeout(DEFAULT_IDLE_TIMEOUT);
        config.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);

        // Configuraciones adicionales
        config.setPoolName("PostgreSQL-HikariCP-" + currentDatabase);
        config.setConnectionInitSql("SELECT 1"); // Test query al obtener conexión

        dataSource = new HikariDataSource(config);
    }

    private String buildConnectionUrl(String databaseName) {
        return String.format("jdbc:postgresql://%s:%s/%s?ApplicationName=Econova&connectTimeout=5",
                server, port, databaseName);
    }

    public void closeConnection(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    log.error("Error closing resource", e);
                }
            }
        }
    }

    private void validateConnectionParams(String server, String port,
            String username, String database) {
        if (server == null || server.trim().isEmpty()) {
            throw new IllegalArgumentException("Server cannot be null or empty");
        }
        if (port == null || !port.matches("\\d+")) {
            throw new IllegalArgumentException("Port must be a valid number");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (database == null || database.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty");
        }
    }

    private String buildConnectionUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s?ApplicationName=Econova",
                server, port, currentDatabase);
    }

    public boolean isConnectionValid(int timeoutSeconds) {
        try (Connection conn = getConnection()) {
            return conn.isValid(timeoutSeconds);
        } catch (SQLException e) {
            log.error("Connection validation failed", e);
            return false;
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getServer() {
        return server;
    }

    public String getPort() {
        return port;
    }

    public Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        throw new IllegalStateException("Connection pool not configured. Call configureConnectionPool() first.");
    }

    @Override
    public void close() {
        shutdownPool();
    }

    public void shutdownPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("Connection pool shutdown successfully");
        }
    }

    public String getCurrentDatabase() {
        return currentDatabase;
    }

}
