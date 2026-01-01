package org.yamdut.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.sql.Connection;

import org.yamdut.database.MySqlConfig;
import org.yamdut.model.RideRequest;
import org.yamdut.model.Driver;

public class RideMatchingService {
    private static final RideMatchingService INSTANCE = new RideMatchingService();

    public static RideMatchingService getInstance() {
        return INSTANCE;
    }

    // Use thread-safe list
    private final List<Driver> onlineDrivers = new CopyOnWriteArrayList<>();

    private RideMatchingService() {
        System.out.println("[RideMatching] Service instance created: " + this);
    }

    public void submitRide(RideRequest request) {
        String sql = """
                    INSERT INTO rides
                    (passenger_id, passenger_name, pickup, dropoff, status)
                    VALUES (?, ?, ?, ?, 'PENDING')
                """;

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, request.getPassengerId());
            ps.setString(2, request.getPassengerName());
            ps.setString(3, request.getPickup());
            ps.setString(4, request.getDropoff());

            ps.executeUpdate();

            System.out.println("[RideMatching] Ride stored in DB for passenger "
                    + request.getPassengerName());

        } catch (SQLException e) {
            throw new RuntimeException("Failed to submit ride", e);
        }
    }

    public List<RideRequest> getPendingRequests() {
        String sql = """
                    SELECT * FROM rides
                    WHERE status = 'PENDING'
                    ORDER BY created_at ASC
                """;

        List<RideRequest> list = new ArrayList<>();

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RideRequest req = new RideRequest(
                        rs.getInt("passenger_id"),
                        rs.getString("passenger_name"),
                        rs.getString("pickup"),
                        rs.getString("dropoff"));
                req.setId(rs.getInt("id"));
                list.add(req);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch pending rides", e);
        }

        System.out.println("[RideMatching] DB pending rides: " + list.size());
        return list;
    }

    /**
     * Very naive matching for now
     * returns all online drivers
     * Later: distance, rating, ETA
     * Note: Does NOT submit ride - that should be done separately
     **/
    public List<Driver> findAvailableDrivers(RideRequest request) {
        // Don't submit here - ride should already be submitted
        return new ArrayList<>(onlineDrivers);
    }

    public void registerDriver(Driver driver) {
        // Avoid duplicates based on ID
        onlineDrivers.removeIf(d -> d.getId() == driver.getId());
        onlineDrivers.add(driver);
        System.out.println("[RideMatching] Driver registered: " + driver.getName());
    }

    public void unregisterDriver(Driver driver) {
        onlineDrivers.removeIf(d -> d.getId() == driver.getId());
        System.out.println("[RideMatching] Driver went offline: " + driver.getName());
    }

    public void assignRide(int rideId, int driverId, String driverName) {
        // Calculate random fare between $5 and $25
        double fare = 5.0 + (Math.random() * 20.0);
        fare = Math.round(fare * 100.0) / 100.0; // Round to 2 decimals

        String sql = """
                    UPDATE rides
                    SET status = 'ACCEPTED', driver_id = ?, driver_name = ?, fare = ?
                    WHERE id = ?
                """;

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, driverId);
            ps.setString(2, driverName);
            ps.setDouble(3, fare);
            ps.setInt(4, rideId);
            ps.executeUpdate();

            System.out.println("[RideMatching] Ride accepted: " + rideId + " Fare: $" + fare);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign ride", e);
        }
    }

    public void startRide(int rideId) {
        String sql = "UPDATE rides SET status = 'IN_PROGRESS' WHERE id = ?";
        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rideId);
            ps.executeUpdate();
            System.out.println("[RideMatching] Ride started: " + rideId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelRide(int rideId) {
        String sql = """
                    UPDATE rides
                    SET status = 'CANCELLED'
                    WHERE id = ?
                """;

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rideId);
            ps.executeUpdate();

            System.out.println("[RideMatching] Ride cancelled: " + rideId);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to cancel ride", e);
        }
    }

    public int getAllRequestsCount() {
        // Simple count of pending requests for now
        return getPendingRequests().size();
    }

    public RideRequest getRideStatus(int rideId) {
        String sql = "SELECT * FROM rides WHERE id = ?";
        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rideId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RideRequest req = new RideRequest(
                            rs.getInt("passenger_id"),
                            rs.getString("passenger_name"),
                            rs.getString("pickup"),
                            rs.getString("dropoff"));
                    req.setId(rs.getInt("id"));
                    req.setStatus(rs.getString("status"));
                    req.setDriverId(rs.getInt("driver_id"));
                    req.setDriverName(rs.getString("driver_name"));
                    req.setFare(rs.getDouble("fare"));
                    if ("ACCEPTED".equals(req.getStatus())) {
                        req.markAccepted();
                    }
                    return req;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
