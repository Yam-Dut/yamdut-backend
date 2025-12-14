/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.*;

/**
 *
 * @author dibij
 */
public class mySQL implements database {

    @Override
    public Connection openConnection() {
       try{
           String username = "root";
           String password = "password";
           String database = "yamdut_db";
           Connection connection;
           connection = DriverManager.getConnection(
           "jdbc:mysql://localhost:3306/" +database, username, password);
           if (connection == null){
               System.out.print("Connection unsuccessfull");
           }else{
               System.out.println("Connection successful");
           }
           
           return connection;        
       } catch (Exception e){
           System.out.println(e);
           return null;
       } 
    }

    @Override
    public void closeConnection(Connection conn) {
        try{
            if(conn != null && !conn.isClosed()){
                conn.close();
                System.out.println("Connection close");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Override
    public ResultSet runQuery(Connection conn, String query) {
        try{
        Statement stmp = conn.createStatement();
        ResultSet result = stmp.executeQuery(query);
        return result;
        
        }catch(Exception e){
        System.out.print(e);
        return null;
        }
    }

    @Override
    public int executeUpdate(Connection conn, String query) {
       try{
           Statement stamp = conn.createStatement();
           int result = stamp.executeUpdate(query);
           return result;
           
       }catch(Exception e){
           System.out.print(e);
           return -1;
       }
    }
    
}
