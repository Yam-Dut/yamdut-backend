package org.yamdut.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConfig {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    private static final String HOST = envOrDefault("DB_HOST", "localhost");
    private static final String PORT = envOrDefault("DB_PORT", "3306");
    private static final String NAME = envOrDefault("DB_NAME", "yamdut_db");
    private static final String USER = envOrDefault("DB_USER", "root");
    private static final String PASSWORD = envOrDefault("DB_PASSWORD", "");

    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            HOST, PORT, NAME
    );

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found. Ensure mysql-connector-j is on the classpath.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String envOrDefault(String key, String def) {
        String v = dotenv.get(key);
        return (v == null || v.isBlank()) ? def : v;
    }
}