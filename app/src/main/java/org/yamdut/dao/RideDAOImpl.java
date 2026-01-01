package org.yamdut.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.yamdut.database.DatabaseConfig;
import org.yamdut.database.MySqlConfig;
import org.yamdut.model.Ride;

public class RideDAOImpl implements RideDAO {
    private final DatabaseConfig db = new MySqlConfig();

    @Override
    public boolean createRide(Ride ride) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "INSERT INTO rides (driver_id, passenger_id, from_location, to_location, status, duration) VALUES ("
                + ride.getDriverId() + ", " + ride.getPassengerId() + ", '" + ride.getFromLocation()
                + "', '" + ride.getToLocation() + "', '" + ride.getStatus() + "', " + ride.getDuration() + ")";

        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public Ride getRideById(int id) {
        Connection conn = db.openConnection();
        if (conn == null)
            return null;

        String sql = "SELECT * FROM rides WHERE id = " + id;
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null && rs.next()) {
                return mapResultSetToRide(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return null;
    }

    @Override
    public boolean updateRide(Ride ride) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "UPDATE rides SET driver_id = " + ride.getDriverId() + ", passenger_id = " + ride.getPassengerId()
                + ", from_location = '" + ride.getFromLocation() + "', to_location = '" + ride.getToLocation()
                + "', status = '" + ride.getStatus() + "', duration = " + ride.getDuration() + " WHERE id = "
                + ride.getId();

        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public boolean deleteRide(int id) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "DELETE FROM rides WHERE id = " + id;
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public List<Ride> getAllRides() {
        List<Ride> rides = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null)
            return rides;

        String sql = "SELECT * FROM rides";
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null) {
                while (rs.next()) {
                    rides.add(mapResultSetToRide(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return rides;
    }

    @Override
    public List<Ride> getRidesByStatus(String status) {
        List<Ride> rides = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null)
            return rides;

        String sql = "SELECT * FROM rides WHERE status = '" + status + "'";
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null) {
                while (rs.next()) {
                    rides.add(mapResultSetToRide(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return rides;
    }

    private Ride mapResultSetToRide(ResultSet rs) throws SQLException {
        Ride ride = new Ride();
        ride.setId(rs.getInt("id"));
        ride.setDriverId(rs.getInt("driver_id"));
        ride.setPassengerId(rs.getInt("passenger_id"));
        ride.setFromLocation(rs.getString("from_location"));
        ride.setToLocation(rs.getString("to_location"));
        ride.setStatus(rs.getString("status"));
        ride.setDuration(rs.getInt("duration"));
        ride.setStartTime(rs.getTimestamp("start_time"));
        return ride;
    }
}
