package org.yamdut.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.yamdut.database.DatabaseConfig;
import org.yamdut.database.MySqlConfig;
import org.yamdut.model.Driver;

public class DriverDAOImpl implements DriverDAO {
    private DatabaseConfig db = (DatabaseConfig) new MySqlConfig();

    @Override
    public boolean createDriver(Driver driver) {
        Connection conn = db.openConnection();
        if (conn == null) return false;
        
        String sql = "INSERT INTO drivers (name, phone, rating, total_rides, status) VALUES ('" 
            + driver.getName() + "', '" + driver.getPhone() + "', " + driver.getRating() 
            + ", " + driver.getTotalRides() + ", '" + driver.getStatus() + "')";
        
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public Driver getDriverById(int id) {
        Connection conn = db.openConnection();
        if (conn == null) return null;
        
        String sql = "SELECT * FROM drivers WHERE id = " + id;
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null && rs.next()) {
                return mapResultSetToDriver(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return null;
    }

    @Override
    public boolean updateDriver(Driver driver) {
        Connection conn = db.openConnection();
        if (conn == null) return false;
        
        String sql = "UPDATE drivers SET name = '" + driver.getName() + "', phone = '" + driver.getPhone() 
            + "', rating = " + driver.getRating() + ", total_rides = " + driver.getTotalRides() 
            + ", status = '" + driver.getStatus() + "' WHERE id = " + driver.getId();
        
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public boolean deleteDriver(int id) {
        Connection conn = db.openConnection();
        if (conn == null) return false;
        
        String sql = "DELETE FROM drivers WHERE id = " + id;
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null) return drivers;
        
        String sql = "SELECT * FROM drivers";
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null) {
                while (rs.next()) {
                    drivers.add(mapResultSetToDriver(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return drivers;
    }

    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        Driver driver = new Driver();
        driver.setId(rs.getInt("id"));
        driver.setName(rs.getString("name"));
        driver.setPhone(rs.getString("phone"));
        driver.setRating(rs.getDouble("rating"));
        driver.setTotalRides(rs.getInt("total_rides"));
        driver.setStatus(rs.getString("status"));
        return driver;
    }
}
