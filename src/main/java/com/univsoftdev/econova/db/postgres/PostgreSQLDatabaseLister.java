package com.univsoftdev.econova.db.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgreSQLDatabaseLister {

    private final String url;
    private final String username;
    private final String password;
    private Connection connection;

    public PostgreSQLDatabaseLister(String host, int port, String username, String password) {
        this.url = "jdbc:postgresql://" + host + ":" + port + "/postgres";
        this.username = username;
        this.password = password;
    }

    public List<String> getAvailableDatabases() {
        final List<String> databases = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            log.error(ex.getMessage());
        }
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false")) {
            while (resultSet.next()) {
                databases.add(resultSet.getString("datname"));
            }
        } catch (SQLException ex) {
            log.error(ex.getMessage());
        }
        return databases;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
