package org.yamdut.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yamdut.dao.DriverDAOImpl;
import org.yamdut.dao.TripDAOImpl;
import org.yamdut.database.DatabaseConfig;
import org.yamdut.database.MySqlConfig;
import org.yamdut.model.Driver;
import org.yamdut.model.Trip;

public class AdminStatsService {
    private final DatabaseConfig db = (DatabaseConfig) new MySqlConfig();
    private final TripDAOImpl tripDAO = new TripDAOImpl();
    private final DriverDAOImpl driverDAO = new DriverDAOImpl();

    public int getTotalUsers() {
        return countQuery("SELECT COUNT(*) AS total FROM users");
    }

    public int getActiveDrivers() {
        return countQuery("SELECT COUNT(*) AS total FROM drivers WHERE status = 'online' OR status = 'active'");
    }

    public int getTodaysTrips() {
        long startOfDay = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        return countQuery("SELECT COUNT(*) AS total FROM trips WHERE request_time >= " + startOfDay);
    }

    public int getPendingTrips() {
        return countQuery("SELECT COUNT(*) AS total FROM trips WHERE status = 'PENDING'");
    }

    public double getTodaysRevenue() {
        long startOfDay = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        String sql = "SELECT COALESCE(SUM(adjusted_fare),0) AS revenue FROM trips WHERE status IN ('COMPLETED','IN_PROGRESS','ACCEPTED') AND request_time >= "
                + startOfDay;
        return sumQuery(sql);
    }

    public List<Trip> getRecentTrips(int limit) {
        String sql = "SELECT * FROM trips ORDER BY request_time DESC LIMIT " + limit;
        return runTripListQuery(sql);
    }

    public List<Driver> getTopDrivers(int limit) {
        String sql = "SELECT * FROM drivers ORDER BY rating DESC, total_rides DESC LIMIT " + limit;
        return runDriverListQuery(sql);
    }

    

    private int countQuery(String sql) {
        Connection conn = db.openConnection();
        if (conn == null) return 0;
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return 0;
    }

    private double sumQuery(String sql) {
        Connection conn = db.openConnection();
        if (conn == null) return 0;
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null && rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return 0;
    }

    private List<Trip> runTripListQuery(String sql) {
        List<Trip> trips = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null) return trips;

        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null) {
                while (rs.next()) {
                    trips.add(mapTrip(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return trips;
    }

    private List<Driver> runDriverListQuery(String sql) {
        List<Driver> drivers = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null) return drivers;

        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null) {
                while (rs.next()) {
                    drivers.add(mapDriver(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return drivers;
    }

    private Trip mapTrip(ResultSet rs) throws SQLException {
        Trip trip = new Trip();
        trip.setId(rs.getInt("id"));
        trip.setDriverId(rs.getInt("driver_id"));
        trip.setRiderId(rs.getInt("rider_id"));
        trip.setRiderName(rs.getString("rider_name"));
        trip.setRiderPhone(rs.getString("rider_phone"));
        trip.setPickupLocation(rs.getString("pickup_location"));
        trip.setDropLocation(rs.getString("drop_location"));
        trip.setAdjustedFare(rs.getInt("adjusted_fare"));
        trip.setStatus(Trip.TripStatus.valueOf(rs.getString("status")));
        trip.setRequestTime(rs.getLong("request_time"));
        return trip;
    }

    private Driver mapDriver(ResultSet rs) throws SQLException {
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
