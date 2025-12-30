package org.yamdut.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConfig {

    private static final String URL = "jdbc:mariadb://localhost:3306/yamdut_db";
    private static final String USER = "yamdut";
    private static final String PASSWORD = "Rolex123";

    static {
        try {
            // for linux
           
            Class.forName("org.mariadb.jdbc.Driver");
           
            /*  for windows uncomment the line below */
           
            // Class.forName("com.mysql.cj.jdbc.Driver");
        
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found. Ensure mysql-connector-j is on the classpath.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}