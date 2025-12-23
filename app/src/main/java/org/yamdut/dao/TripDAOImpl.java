package org.yamdut.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yamdut.database.DatabaseConfig;
import org.yamdut.database.MySqlConfig;
import org.yamdut.model.Trip;
import org.yamdut.model.Trip.TripStatus;

public class TripDAOImpl implements TripDAO {
    private final DatabaseConfig db = (DatabaseConfig) new MySqlConfig();

    @Override
    public boolean createTrip(Trip trip) {
        Connection conn = db.openConnection();
        if (conn == null) return false;

        String sql = "INSERT INTO trips (driver_id, rider_id, rider_name, rider_phone, pickup_location, pickup_lat, pickup_lng, "
                + "drop_location, dropoff_lat, dropoff_lng, base_fare, adjusted_fare, status, request_time) VALUES ("
                + trip.getDriverId() + ", "
                + trip.getRiderId() + ", '"
                + trip.getRiderName() + "', '"
                + trip.getRiderPhone() + "', '"
                + trip.getPickupLocation() + "', "
                + trip.getPickupLat() + ", "
                + trip.getPickupLng() + ", '"
                + trip.getDropLocation() + "', "
                + trip.getDropoffLat() + ", "
                + trip.getDropoffLng() + ", "
                + trip.getBaseFare() + ", "
                + trip.getAdjustedFare() + ", '"
                + trip.getStatus().name() + "', "
                + trip.getRequestTime() + ")";
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public List<Trip> getAllTrips() {
        String sql = "SELECT * FROM trips ORDER BY request_time DESC";
        return runListQuery(sql);
    }

    @Override
    public List<Trip> getTripsByDriver(int driverId) {
        String sql = "SELECT * FROM trips WHERE driver_id = " + driverId + " ORDER BY request_time DESC";
        return runListQuery(sql);
    }

    @Override
    public List<Trip> getPendingTripsByDriver(int driverId) {
        String sql = "SELECT * FROM trips WHERE driver_id = " + driverId + " AND status = 'PENDING' ORDER BY request_time DESC";
        return runListQuery(sql);
    }

    @Override
    public List<Trip> getTripsSince(long requestTimeMillis) {
        String sql = "SELECT * FROM trips WHERE request_time >= " + requestTimeMillis + " ORDER BY request_time DESC";
        return runListQuery(sql);
    }

    private List<Trip> runListQuery(String sql) {
        List<Trip> trips = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null) return trips;

        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null) {
                while (rs.next()) {
                    trips.add(mapResultSetToTrip(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return trips;
    }

    private Trip mapResultSetToTrip(ResultSet rs) throws SQLException {
        Trip trip = new Trip();
        trip.setId(rs.getInt("id"));
        trip.setDriverId(rs.getInt("driver_id"));
        trip.setRiderId(rs.getInt("rider_id"));
        trip.setRiderName(rs.getString("rider_name"));
        trip.setRiderPhone(rs.getString("rider_phone"));
        trip.setPickupLocation(rs.getString("pickup_location"));
        trip.setPickupLat(rs.getDouble("pickup_lat"));
        trip.setPickupLng(rs.getDouble("pickup_lng"));
        trip.setDropLocation(rs.getString("drop_location"));
        trip.setDropoffLat(rs.getDouble("dropoff_lat"));
        trip.setDropoffLng(rs.getDouble("dropoff_lng"));
        trip.setBaseFare(rs.getInt("base_fare"));
        trip.setAdjustedFare(rs.getInt("adjusted_fare"));
        trip.setStatus(TripStatus.valueOf(rs.getString("status")));
        trip.setRequestTime(rs.getLong("request_time"));
        trip.setCreatedAt(rs.getTimestamp("created_at"));
        return trip;
    }
}
