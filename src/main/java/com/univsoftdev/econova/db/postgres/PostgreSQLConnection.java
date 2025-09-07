package com.univsoftdev.econova.security;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;

/**
 * Gestor de conexiones a bases de datos PostgreSQL con pooling de conexiones.
 * 
 * <p>Esta clase proporciona funcionalidades para:
 * <ul>
 *   <li>Conexión a PostgreSQL con HikariCP para pooling eficiente</li>
 *   <li>Gestión de múltiples bases de datos</li>
 *   <li>Creación automática de bases de datos</li>
 *   <li>Validación de conexiones</li>
 * </ul>
 * </p>
 * 
 * <p>Implementa {@link AutoCloseable} para permitir su uso en try-with-resources.</p>
 * 
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 * 
 * @see HikariDataSource
 * @see Connection
 */
@Slf4j
public class PostgreSQLConnection implements AutoCloseable {

    /**
     * Driver JDBC de PostgreSQL.
     */
    private static final String DRIVER = "org.postgresql.Driver";
    
    /** Tamaño máximo del pool de conexiones por defecto. */
    private static final int DEFAULT_MAX_POOL_SIZE = 10;
    
    /** Número mínimo de conexiones inactivas por defecto. */
    private static final int DEFAULT_MIN_IDLE = 2;
    
    /** Tiempo máximo (ms) que una conexión puede estar inactiva antes de ser cerrada. */
    private static final int DEFAULT_IDLE_TIMEOUT = 30000;
    
    /** Tiempo máximo (ms) para esperar una conexión del pool. */
    private static final int DEFAULT_CONNECTION_TIMEOUT = 10000;

    /**
     * Fuente de datos HikariCP que gestiona el pool de conexiones.
     */
    private HikariDataSource dataSource;
    
    /**
     * Nombre de la base de datos actualmente en uso.
     */
    private String currentDatabase;
    
    /** Servidor PostgreSQL (hostname o IP). */
    private final String server;
    
    /** Puerto de conexión PostgreSQL. */
    private final String port;
    
    /** Nombre de usuario para la conexión. */
    private final String userName;
    
    /** Contraseña para la conexión. */
    private final String password;
    
    /** 
     * Base de datos por defecto para operaciones administrativas.
     * Generalmente "postgres" o una base de datos de sistema.
     */
    private final String defaultDatabase;

    /**
     * Bloque estático de inicialización para cargar el driver JDBC de PostgreSQL.
     * 
     * @throws RuntimeException si el driver no se encuentra en el classpath
     */
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        }
    }

    /**
     * Constructor para crear una nueva conexión PostgreSQL.
     * 
     * @param server Servidor PostgreSQL (no null ni vacío)
     * @param port Puerto de conexión (debe ser un número válido)
     * @param username Nombre de usuario (no null ni vacío)
     * @param password Contraseña del usuario
     * @param defaultDatabase Base de datos por defecto para operaciones administrativas
     * 
     * @throws IllegalArgumentException si alguno de los parámetros es inválido
     * @throws RuntimeException si el driver JDBC no se puede cargar
     */
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

    /**
     * Prueba la conexión a la base de datos por defecto.
     * 
     * @return {@code true} si la conexión es exitosa, {@code false} en caso contrario
     * 
     * @see #testConnection(String)
     */
    public boolean testConnection() {
        return testConnection(this.defaultDatabase);
    }

    /**
     * Prueba la conexión a una base de datos específica.
     * 
     * <p>Realiza una conexión directa usando DriverManager y verifica
     * su validez con un timeout de 5 segundos.</p>
     * 
     * @param databaseName Nombre de la base de datos a probar
     * @return {@code true} si la conexión es exitosa, {@code false} en caso contrario
     * 
     * @throws IllegalArgumentException si databaseName es null o vacío
     */
    public boolean testConnection(String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty");
        }
        
        try (Connection testConn = DriverManager.getConnection(
                buildConnectionUrl(databaseName), userName, password)) {
            return testConn.isValid(5); // 5 segundos de timeout
        } catch (SQLException e) {
            log.error("Connection test failed for database: " + databaseName, e);
            return false;
        }
    }

    /**
     * Cambia la base de datos actual y reconfigura el pool de conexiones.
     * 
     * <p>Si la base de datos no existe, se crea automáticamente.
     * El pool de conexiones se reconfigura para usar la nueva base de datos.</p>
     * 
     * @param newDatabase Nombre de la nueva base de datos
     * @throws SQLException si ocurre un error durante la operación
     * @throws IllegalArgumentException si newDatabase es null o vacío
     * 
     * @see #databaseExists(String)
     * @see #createDatabase(String)
     * @see #configureConnectionPool()
     */
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

    /**
     * Verifica si una base de datos existe en el servidor.
     * 
     * <p>Utiliza una conexión administrativa para consultar los catálogos
     * disponibles en el servidor PostgreSQL.</p>
     * 
     * @param dbName Nombre de la base de datos a verificar
     * @return {@code true} si la base de datos existe, {@code false} en caso contrario
     * @throws SQLException si ocurre un error durante la consulta
     * 
     * @see #getAdminConnection()
     */
    public boolean databaseExists(String dbName) throws SQLException {
        try (Connection conn = getAdminConnection(); 
             ResultSet rs = conn.getMetaData().getCatalogs()) {

            while (rs.next()) {
                String existingDbName = rs.getString(1);
                if (existingDbName.equals(dbName)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Crea una nueva base de datos en el servidor PostgreSQL.
     * 
     * <p>Primero verifica si la base de datos ya existe para evitar errores.
     * Utiliza una sentencia SQL {@code CREATE DATABASE} para la creación.</p>
     * 
     * @param dbName Nombre de la base de datos a crear
     * @throws SQLException si ocurre un error durante la creación
     * 
     * @see #databaseExists(String)
     */
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

    /**
     * Obtiene una conexión administrativa a la base de datos por defecto.
     * 
     * <p>Esta conexión se utiliza para operaciones administrativas como
     * crear bases de datos o verificar su existencia.</p>
     * 
     * @return Una nueva conexión JDBC a la base de datos por defecto
     * @throws SQLException si ocurre un error durante la conexión
     * 
     * @see #defaultDatabase
     */
    private Connection getAdminConnection() throws SQLException {
        return DriverManager.getConnection(
                buildConnectionUrl(defaultDatabase),
                userName,
                password
        );
    }

    /**
     * Configura el pool de conexiones HikariCP.
     * 
     * <p>Establece las configuraciones por defecto y crea una nueva
     * instancia de {@link HikariDataSource}.</p>
     * 
     * @see HikariConfig
     * @see HikariDataSource
     */
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

    /**
     * Construye la URL de conexión JDBC para una base de datos específica.
     * 
     * <p>Formato: {@code jdbc:postgresql://server:port/database?ApplicationName=Econova&connectTimeout=5}</p>
     * 
     * @param databaseName Nombre de la base de datos
     * @return URL de conexión JDBC completa
     */
    private String buildConnectionUrl(String databaseName) {
        return String.format("jdbc:postgresql://%s:%s/%s?ApplicationName=Econova&connectTimeout=5",
                server, port, databaseName);
    }

    /**
     * Cierra recursos AutoCloseable de forma segura.
     * 
     * <p>Método de utilidad para cerrar múltiples recursos sin preocuparse
     * por excepciones durante el cierre.</p>
     * 
     * @param resources Array de recursos a cerrar (pueden ser null)
     */
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

    /**
     * Valida los parámetros de conexión proporcionados.
     * 
     * @param server Servidor (no null ni vacío)
     * @param port Puerto (debe ser numérico)
     * @param username Usuario (no null ni vacío)
     * @param database Base de datos (no null ni vacío)
     * 
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
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

    /**
     * Construye la URL de conexión JDBC para la base de datos actual.
     * 
     * <p>Formato: {@code jdbc:postgresql://server:port/database?ApplicationName=Econova}</p>
     * 
     * @return URL de conexión JDBC completa
     * @deprecated Usar {@link #buildConnectionUrl(String)} en su lugar
     */
    @Deprecated
    private String buildConnectionUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s?ApplicationName=Econova",
                server, port, currentDatabase);
    }

    /**
     * Valida si la conexión actual es válida.
     * 
     * @param timeoutSeconds Tiempo máximo de espera para la validación (en segundos)
     * @return {@code true} si la conexión es válida, {@code false} en caso contrario
     * 
     * @see Connection#isValid(int)
     */
    public boolean isConnectionValid(int timeoutSeconds) {
        try (Connection conn = getConnection()) {
            return conn.isValid(timeoutSeconds);
        } catch (SQLException e) {
            log.error("Connection validation failed", e);
            return false;
        }
    }

    /**
     * Obtiene la fuente de datos HikariCP configurada.
     * 
     * @return La fuente de datos o {@code null} si no está configurada
     */
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Establece la fuente de datos HikariCP.
     * 
     * @param dataSource La nueva fuente de datos
     */
    public void setDataSource(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Obtiene el servidor PostgreSQL configurado.
     * 
     * @return El servidor (hostname o IP)
     */
    public String getServer() {
        return server;
    }

    /**
     * Obtiene el puerto de conexión configurado.
     * 
     * @return El puerto como string
     */
    public String getPort() {
        return port;
    }

    /**
     * Obtiene una conexión del pool de conexiones.
     * 
     * @return Una conexión JDBC del pool
     * @throws SQLException si ocurre un error al obtener la conexión
     * @throws IllegalStateException si el pool no está configurado
     * 
     * @see HikariDataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        throw new IllegalStateException("Connection pool not configured. Call configureConnectionPool() first.");
    }

    /**
     * Cierra el pool de conexiones cuando se termina de usar esta instancia.
     * 
     * <p>Este método se llama automáticamente cuando se usa la instancia
     * en un bloque try-with-resources.</p>
     * 
     * @see #shutdownPool()
     */
    @Override
    public void close() {
        shutdownPool();
    }

    /**
     * Cierra el pool de conexiones HikariCP de forma segura.
     * 
     * <p>Verifica que el dataSource exista y no esté ya cerrado antes de cerrarlo.</p>
     * 
     * @see HikariDataSource#close()
     * @see HikariDataSource#isClosed()
     */
    public void shutdownPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("Connection pool shutdown successfully");
        }
    }

    /**
     * Obtiene el nombre de la base de datos actualmente en uso.
     * 
     * @return El nombre de la base de datos actual
     */
    public String getCurrentDatabase() {
        return currentDatabase;
    }
}