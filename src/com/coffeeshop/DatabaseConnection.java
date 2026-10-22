package com.coffeeshop;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/** JDBC connections and first-run MySQL schema initialization. */
final class DatabaseConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/coffee_shop?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static boolean initialized;

    private DatabaseConnection() {
    }

    private record Settings(String url, String user, String password, String serverUrl) {
    }

    private static Settings loadSettings() throws SQLException {
        Properties properties = new Properties();
        Path file = Path.of("db.properties");
        if (Files.isRegularFile(file)) {
            try (InputStream input = Files.newInputStream(file)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new SQLException("Could not read db.properties.", exception);
            }
        }

        String envPassword = System.getenv("COFFEE_DB_PASSWORD");
        if (envPassword == null && !Files.isRegularFile(file)) {
            throw new SQLException("Database login is not configured. Start with run.bat "
                    + "to configure it automatically, or create db.properties.");
        }

        String url = System.getenv().getOrDefault(
                "COFFEE_DB_URL", properties.getProperty("db.url", DEFAULT_URL));
        String user = System.getenv().getOrDefault(
                "COFFEE_DB_USER", properties.getProperty("db.user", "root"));
        String password = envPassword == null
                ? properties.getProperty("db.password", "") : envPassword;

        if (url.isBlank() || user.isBlank()) {
            throw new SQLException("MySQL JDBC URL and username cannot be blank.");
        }

        String defaultServerUrl = url.replaceFirst(
                "(?i)(jdbc:mysql://[^/]+/)[^?]*", "$1");
        if (defaultServerUrl.equals(url)) {
            throw new SQLException("Invalid MySQL database JDBC URL: " + url);
        }
        String serverUrl = System.getenv().getOrDefault(
                "COFFEE_DB_SERVER_URL", defaultServerUrl);

        return new Settings(url, user, password, serverUrl);
    }

    private static void loadDriver() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("MySQL JDBC driver missing. Run run.bat to "
                    + "download Connector/J automatically.", exception);
        }
    }

    static Connection open() throws SQLException {
        loadDriver();
        Settings settings = loadSettings();
        return DriverManager.getConnection(
                settings.url(), settings.user(), settings.password());
    }

    /** Safe to call on every launch: tables and menu seed are idempotent. */
    static synchronized void initializeDatabase() throws SQLException {
        if (initialized) {
            return;
        }

        loadDriver();
        Settings settings = loadSettings();

        try (Connection connection = DriverManager.getConnection(
                settings.serverUrl(), settings.user(), settings.password());
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS coffee_shop "
                    + "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }

        String schema;
        try {
            schema = Files.readString(Path.of("database", "schema.sql"));
        } catch (IOException exception) {
            throw new SQLException("Cannot load database/schema.sql from the project root.", exception);
        }

        StringBuilder commands = new StringBuilder();
        for (String line : schema.split("\\R")) {
            if (!line.stripLeading().startsWith("--")) {
                commands.append(line).append('\n');
            }
        }

        try (Connection connection = open();
             Statement statement = connection.createStatement()) {
            for (String command : commands.toString().split(";")) {
                String sql = command.trim();
                if (sql.isEmpty() || sql.regionMatches(true, 0, "CREATE DATABASE", 0, 15)
                        || sql.regionMatches(true, 0, "USE ", 0, 4)) {
                    continue;
                }
                statement.execute(sql);
            }
        }
        initialized = true;
    }
}
