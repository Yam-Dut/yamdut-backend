package org.yamdut.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.yamdut.database.DatabaseConfig;
import org.yamdut.database.MySqlConfig;
import org.yamdut.model.Ride;

public class RideDAOImpl implements RideDAO {
    private DatabaseConfig db = (DatabaseConfig) new MySqlConfig();

    @Override
    public boolean createRide(Ride ride) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "INSERT INTO rides (driver_id, passenger_id, pickup, dropoff, status, duration) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ride.getDriverId());
            ps.setInt(2, ride.getPassengerId());
            ps.setString(3, ride.getFromLocation());
            ps.setString(4, ride.getToLocation());
            ps.setString(5, ride.getStatus());
            ps.setInt(6, ride.getDuration());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public Ride getRideById(int id) {
        Connection conn = db.openConnection();
        if (conn == null)
            return null;

        String sql = "SELECT * FROM rides WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRide(rs);
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
    public boolean updateRide(Ride ride) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "UPDATE rides SET driver_id = ?, passenger_id = ?, pickup = ?, dropoff = ?, status = ?, duration = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ride.getDriverId());
            ps.setInt(2, ride.getPassengerId());
            ps.setString(3, ride.getFromLocation());
            ps.setString(4, ride.getToLocation());
            ps.setString(5, ride.getStatus());
            ps.setInt(6, ride.getDuration());
            ps.setInt(7, ride.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public boolean deleteRide(int id) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "DELETE FROM rides WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
        ride.setFromLocation(rs.getString("pickup"));
        ride.setToLocation(rs.getString("dropoff"));
        ride.setStatus(rs.getString("status"));
        ride.setDuration(rs.getInt("duration"));
        ride.setStartTime(rs.getTimestamp("start_time"));
        return ride;
    }
}
