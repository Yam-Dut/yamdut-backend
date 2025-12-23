package org.yamdut.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class MySqlConfig {
    // MySQL connection configuration (no SSL)
    private static final String URL = "jdbc:mysql://localhost:3306/yamdut_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        }
    }
    /*
    ===========================================================================
    Get connection
    ===========================================================================
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
}