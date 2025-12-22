package org.yamdut.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class MySqlConfig {
    // for linux/mariadb (amrxtgh69)
    // private static final String URL = "jdbc:mariadb://localhost:3306/yamdut_db";
    // private static final String USER = "yamdut";
    // private static final String PASSWORD = "//your password here";

    //for windows 
    private static final String URL = "jdbc:mysql://localhost:3306/yamdut_db";
    private static final String USER = "root";
    private static final String PASSWORD = "@Abhi1004";

    static {
        try {
            // for linux
            Class.forName("org.mariadb.jdbc.Driver");
            //for windows
            //Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Mariadb driver not found", e);
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