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
        if (conn == null)
            return false;

        String sql = "INSERT INTO drivers (name, phone, rating, total_rides, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, driver.getName());
            ps.setString(2, driver.getPhone());
            ps.setDouble(3, driver.getRating());
            ps.setInt(4, driver.getTotalRides());
            ps.setString(5, driver.getStatus());

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public Driver getDriverById(int id) {
        Connection conn = db.openConnection();
        if (conn == null)
            return null;

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
        if (conn == null)
            return false;

        String sql = "UPDATE drivers SET name = ?, phone = ?, rating = ?, total_rides = ?, status = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, driver.getName());
            ps.setString(2, driver.getPhone());
            ps.setDouble(3, driver.getRating());
            ps.setInt(4, driver.getTotalRides());
            ps.setString(5, driver.getStatus());
            ps.setInt(6, driver.getId());

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public boolean deleteDriver(int id) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "DELETE FROM drivers WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null)
            return drivers;

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
