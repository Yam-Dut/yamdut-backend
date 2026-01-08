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

        String sql = "INSERT INTO drivers (user_id, name, phone, vehicle_type, license_number, rating, total_rides, total_earnings, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, driver.getUserId());
            ps.setString(2, driver.getName());
            ps.setString(3, driver.getPhone());
            ps.setString(4, driver.getVehicleType());
            ps.setString(5, driver.getLicenseNumber());
            ps.setDouble(6, driver.getRating());
            ps.setInt(7, driver.getTotalRides());
            ps.setDouble(8, driver.getTotalEarnings());
            ps.setString(9, driver.getStatus());

            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        driver.setId(generatedKeys.getLong(1));
                        System.out.println("[DriverDAO] Created driver record with ID: " + driver.getId());
                    }
                }
                return true;
            }
            System.err.println("[DriverDAO] Insert failed, no rows affected.");
            return false;
        } catch (SQLException e) {
            System.err.println("[DriverDAO] SQL Error creating driver: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public Driver getDriverByUserId(long userId) {
        Connection conn = db.openConnection();
        if (conn == null)
            return null;

        String sql = "SELECT * FROM drivers WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDriver(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return null;
    }

    @Override
    public Driver getDriverById(long id) {
        Connection conn = db.openConnection();
        if (conn == null)
            return null;

        String sql = "SELECT * FROM drivers WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDriver(rs);
                }
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

        String sql = "UPDATE drivers SET user_id = ?, name = ?, phone = ?, vehicle_type = ?, license_number = ?, rating = ?, total_rides = ?, total_earnings = ?, status = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, driver.getUserId());
            ps.setString(2, driver.getName());
            ps.setString(3, driver.getPhone());
            ps.setString(4, driver.getVehicleType());
            ps.setString(5, driver.getLicenseNumber());
            ps.setDouble(6, driver.getRating());
            ps.setInt(7, driver.getTotalRides());
            ps.setDouble(8, driver.getTotalEarnings());
            ps.setString(9, driver.getStatus());
            ps.setLong(10, driver.getId());

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
    public boolean deleteDriver(long id) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "DELETE FROM drivers WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
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
        driver.setId(rs.getLong("id"));
        driver.setUserId(rs.getLong("user_id"));
        driver.setName(rs.getString("name"));
        driver.setPhone(rs.getString("phone"));
        driver.setVehicleType(rs.getString("vehicle_type"));
        driver.setLicenseNumber(rs.getString("license_number"));
        driver.setRating(rs.getDouble("rating"));
        driver.setTotalRides(rs.getInt("total_rides"));
        driver.setTotalEarnings(rs.getDouble("total_earnings"));
        driver.setStatus(rs.getString("status"));
        return driver;
    }
}
