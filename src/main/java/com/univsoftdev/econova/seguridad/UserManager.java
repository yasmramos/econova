package com.univsoftdev.econova.seguridad;

import lombok.extern.slf4j.Slf4j;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class UserManager {

    private final DataSource dataSource;
    private final PasswordHasher passwordHasher;
    private final String adminRole;
    private Connection connection;

    public UserManager(DataSource dataSource, PasswordHasher passwordHasher, String adminRole) {
        this.dataSource = dataSource;
        this.passwordHasher = passwordHasher;
        this.adminRole = adminRole;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException ex) {
            System.getLogger(UserManager.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public boolean authenticate(String username, String password) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT 1 FROM pg_roles WHERE rolname = ? AND pg_has_role(?, rolname, 'member')";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, adminRole);
                stmt.setString(2, username);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            log.error("Authentication failed for user: {}", username, e);
            return false;
        }
    }

    public void createSystemUser(String adminUser, String adminPassword,
            String newUser, String newPassword, String database)
            throws SQLException {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();

            String createUserSQL = String.format(
                    "CREATE USER %s WITH PASSWORD '%s'",
                    newUser, newPassword.replace("'", "''"));

            String grantSQL = String.format("GRANT ALL PRIVILEGES ON DATABASE %s TO %s",
                    database, newUser);

            stmt.executeUpdate(createUserSQL);
            stmt.executeUpdate(grantSQL);

            log.info("Created new user: {}", newUser);

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public boolean createUser(String username, String password, String email, String role) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            // Crear usuario en PostgreSQL
            try (PreparedStatement createUser = conn.prepareStatement(
                    "CREATE ROLE ? WITH LOGIN PASSWORD ? NOSUPERUSER")) {
                createUser.setString(1, username);
                createUser.setString(2, passwordHasher.hash(password));
                createUser.executeUpdate();
            }

            // Asignar rol
            try (PreparedStatement grantRole = conn.prepareStatement(
                    "GRANT ? TO ?")) {
                grantRole.setString(1, role);
                grantRole.setString(2, username);
                grantRole.executeUpdate();
            }

            // Registrar en aplicación
            try (PreparedStatement insertUser = conn.prepareStatement(
                    "INSERT INTO app_users (username, password_hash, email, pg_role) VALUES (?, ?, ?, ?)")) {
                insertUser.setString(1, username);
                insertUser.setString(2, passwordHasher.hash(password));
                insertUser.setString(3, email);
                insertUser.setString(4, role);
                insertUser.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    log.error("Error during rollback", ex);
                }
            }
            log.error("User creation failed for: {}", username, e);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    log.error("Error closing connection", e);
                }
            }
        }
    }

    public boolean hasPermission(String username, String table, String permission) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT has_table_privilege(?, ?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, table);
            stmt.setString(3, permission);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            log.error("Permission check failed for user {} on table {}", username, table, e);
            return false;
        }
    }

    /**
     * Verifica si un usuario ya existe en PostgreSQL
     */
    public boolean pgUserExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM pg_roles WHERE rolname = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Verifica si un usuario ya existe en la tabla de la aplicación
     */
    public boolean appUserExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM app_users WHERE username = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Crea un nuevo usuario solo si no existe previamente
     */
    public boolean createUserIfNotExists(String username, String password, String email, String role) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            // Verificar existencia en PostgreSQL
            if (pgUserExists(username)) {
                log.warn("El usuario {} ya existe en PostgreSQL", username);
                return false;
            }

            // Verificar existencia en la aplicación
            if (appUserExists(username)) {
                log.warn("El usuario {} ya existe en la aplicación", username);
                return false;
            }

            // Crear usuario en PostgreSQL
            try (PreparedStatement stmt = conn.prepareStatement(
                    "CREATE ROLE ? WITH LOGIN PASSWORD ? NOSUPERUSER")) {
                stmt.setString(1, username);
                stmt.setString(2, passwordHasher.hash(password));
                stmt.executeUpdate();
            }

            // Asignar rol
            try (PreparedStatement stmt = conn.prepareStatement(
                    "GRANT ? TO ?")) {
                stmt.setString(1, role);
                stmt.setString(2, username);
                stmt.executeUpdate();
            }

            // Registrar en aplicación
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO app_users (username, password_hash, email, pg_role) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, username);
                stmt.setString(2, passwordHasher.hash(password));
                stmt.setString(3, email);
                stmt.setString(4, role);
                stmt.executeUpdate();
            }

            conn.commit();
            log.info("Usuario {} creado exitosamente", username);
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    log.error("Error durante rollback", ex);
                }
            }
            log.error("Error al crear usuario {}", username, e);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    log.error("Error cerrando conexión", e);
                }
            }
        }
    }

    /**
     * Método seguro para autenticar verificando existencia primero
     */
    public boolean safeAuthenticate(String username, String password) {
        try {
            if (!pgUserExists(username)) {
                log.warn("Intento de autenticación con usuario inexistente: {}", username);
                return false;
            }

            // Resto de la lógica de autenticación...
            return authenticate(username, password);
        } catch (SQLException e) {
            log.error("Error verificando usuario", e);
            return false;
        }
    }
}
