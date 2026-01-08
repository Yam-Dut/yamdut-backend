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
                    (passenger_id, passenger_name, pickup, dropoff,
                     pickup_lat, pickup_lon, destination_lat, destination_lon, status, fare)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'REQUESTING', ?)
                """;

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, request.getPassengerId());
            ps.setString(2, request.getPassengerName());
            ps.setString(3, request.getPickup());
            ps.setString(4, request.getDropoff());
            ps.setDouble(5, request.getPickupLat());
            ps.setDouble(6, request.getPickupLon());
            ps.setDouble(7, request.getDestLat());
            ps.setDouble(8, request.getDestLon());
            ps.setDouble(9, request.getFare()); // User-offered initial fare

            ps.executeUpdate();

            // Retrieve generated ID
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setId(generatedKeys.getLong(1));
                    System.out.println("[RideMatching] Ride stored in DB with ID: " + request.getId());
                }
            }

            System.out.println("[RideMatching] Ride stored in DB for passenger "
                    + request.getPassengerName());

        } catch (SQLException e) {
            throw new RuntimeException("Failed to submit ride", e);
        }
    }

    public List<RideRequest> getPendingRequests() {
        String sql = """
                    SELECT * FROM rides
                    WHERE status = 'REQUESTING'
                    AND created_at >= NOW() - INTERVAL 1 MINUTE
                    ORDER BY created_at ASC
                """;

        List<RideRequest> list = new ArrayList<>();

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RideRequest req = new RideRequest(
                        rs.getString("pickup"),
                        rs.getString("dropoff"),
                        rs.getDouble("pickup_lat"),
                        rs.getDouble("pickup_lon"),
                        rs.getDouble("destination_lat"),
                        rs.getDouble("destination_lon"),
                        String.valueOf(rs.getLong("passenger_id")),
                        rs.getString("passenger_name"));
                req.setId(rs.getLong("id"));
                req.setDriverId(rs.getLong("driver_id"));
                req.setDriverName(rs.getString("driver_name"));
                req.setDriverLat(rs.getDouble("driver_lat"));
                req.setDriverLon(rs.getDouble("driver_lon"));
                req.setStatus(rs.getString("status"));
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

    public void assignRide(long rideId, long driverId, String driverName, double driverLat, double driverLon) {
        String fetchSql = "SELECT pickup_lat, pickup_lon, destination_lat, destination_lon, fare FROM rides WHERE id = ?";
        double fare = 75.0; // Default fallback
        double tripDist = 0.0;
        double approachDist = 0.0;

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement psFetch = conn.prepareStatement(fetchSql)) {
            psFetch.setLong(1, rideId);
            try (ResultSet rs = psFetch.executeQuery()) {
                if (rs.next()) {
                    double pLat = rs.getDouble("pickup_lat");
                    double pLon = rs.getDouble("pickup_lon");
                    double dLat = rs.getDouble("destination_lat");
                    double dLon = rs.getDouble("destination_lon");
                    double offeredFare = rs.getDouble("fare");

                    tripDist = calculateDistance(pLat, pLon, dLat, dLon);
                    approachDist = calculateDistance(driverLat, driverLon, pLat, pLon);

                    // Final Fare = OfferedFare + (ApproachDist * 6.25)
                    fare = offeredFare + (approachDist * 6.25);
                    fare = Math.round(fare); // Round to nearest Rupee
                }
            }

            String updateSql = """
                        UPDATE rides
                        SET status = 'ACCEPTED', driver_id = ?, driver_name = ?, fare = ?,
                            driver_lat = ?, driver_lon = ?
                        WHERE id = ?
                    """;

            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setLong(1, driverId);
                psUpdate.setString(2, driverName);
                psUpdate.setDouble(3, fare);
                psUpdate.setDouble(4, driverLat);
                psUpdate.setDouble(5, driverLon);
                psUpdate.setLong(6, rideId);
                psUpdate.executeUpdate();

                System.out.println("[RideMatching] Ride accepted: " + rideId + " Fare: Rs. " + fare
                        + " (Trip: " + String.format("%.1f", tripDist) + "km, Approach: "
                        + String.format("%.1f", approachDist) + "km)");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign ride", e);
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        return dist;
    }

    public void updateRideLocation(long rideId, double lat, double lon) {
        String sql = "UPDATE rides SET driver_lat = ?, driver_lon = ? WHERE id = ?";
        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, lat);
            ps.setDouble(2, lon);
            ps.setLong(3, rideId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startRide(long rideId) {
        String sql = "UPDATE rides SET status = 'IN_PROGRESS' WHERE id = ?";
        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, rideId);
            ps.executeUpdate();
            System.out.println("[RideMatching] Ride started: " + rideId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void completeRide(long rideId) {
        String sql = "UPDATE rides SET status = 'COMPLETED' WHERE id = ?";
        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, rideId);
            ps.executeUpdate();
            System.out.println("[RideMatching] Ride completed in DB: " + rideId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelRide(long rideId) {
        String sql = """
                    UPDATE rides
                    SET status = 'CANCELLED'
                    WHERE id = ?
                """;

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, rideId);
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

    public RideRequest getRideStatus(long rideId) {
        String sql = "SELECT * FROM rides WHERE id = ?";
        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, rideId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RideRequest req = new RideRequest(
                            rs.getString("pickup"),
                            rs.getString("dropoff"),
                            rs.getDouble("pickup_lat"),
                            rs.getDouble("pickup_lon"),
                            rs.getDouble("destination_lat"),
                            rs.getDouble("destination_lon"),
                            String.valueOf(rs.getLong("passenger_id")),
                            rs.getString("passenger_name"));
                    req.setId(rs.getLong("id"));
                    req.setStatus(rs.getString("status"));
                    req.setDriverId(rs.getLong("driver_id"));
                    req.setDriverName(rs.getString("driver_name"));
                    req.setDriverLat(rs.getDouble("driver_lat"));
                    req.setDriverLon(rs.getDouble("driver_lon"));
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
