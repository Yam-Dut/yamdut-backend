package org.yamdut.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class MySqlConfig {
    private static final String URL = "jdbc:mariadb://localhost:3306/yamdut_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Rolex123";

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Mariadb driver not found, e");
        }
    }
    public static Connection gConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}