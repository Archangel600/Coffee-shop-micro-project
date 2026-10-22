package com.coffeeshop;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Loads local database settings and opens JDBC connections. */
final class DatabaseConnection {
    private DatabaseConnection() {
    }

    static Connection open() throws SQLException {
        Path config = Path.of("db.properties");
        Properties properties = new Properties();
        if (!Files.isRegularFile(config)) {
            throw new SQLException("db.properties is missing. Copy db.properties.example "
                    + "to db.properties and enter your MySQL credentials.");
        }

        try (InputStream input = Files.newInputStream(config)) {
            properties.load(input);
        } catch (IOException exception) {
            throw new SQLException("Could not read db.properties.", exception);
        }

        String url = properties.getProperty("db.url", "").trim();
        String user = properties.getProperty("db.user", "").trim();
        String password = properties.getProperty("db.password", "");
        if (url.isEmpty() || user.isEmpty()) {
            throw new SQLException("Set db.url and db.user in db.properties.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("MySQL JDBC driver not found. Place Connector/J at "
                    + "lib/mysql-connector-j.jar.", exception);
        }

        return DriverManager.getConnection(url, user, password);
    }
}
