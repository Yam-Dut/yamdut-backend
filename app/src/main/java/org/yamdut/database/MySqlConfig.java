package org.yamdut.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
@author preeti
 */
public class MySqlConfig {
   
    // for linux/mariadb (amrxtgh69)

    /*
    i have add mariadb for myself u do yourself 
    */

    private static final String URL = "jdbc:mariadb://localhost:3306/yamdut_db";
    private static final String USER = "your user name";
    private static final String PASSWORD = "your password here boss";

    static {
        try {
            // for linux
           
            //Class.forName("org.mariadb.jdbc.Driver");
           
            /*  for windows uncomment the line below */
           
            Class.forName("com.mysql.cj.jdbc.Driver");
        
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
}