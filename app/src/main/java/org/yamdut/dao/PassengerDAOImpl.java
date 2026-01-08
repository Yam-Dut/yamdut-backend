package org.yamdut.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.yamdut.database.MySqlConfig;
import org.yamdut.database.DatabaseConfig;
import org.yamdut.model.Passenger;

public class PassengerDAOImpl implements PassengerDAO {
    private final DatabaseConfig db = new MySqlConfig();

    @Override
    public boolean createPassenger(Passenger passenger) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "INSERT INTO passengers (user_id, name, phone, rating, total_rides, total_spent) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, passenger.getUserId());
            ps.setString(2, passenger.getName());
            ps.setString(3, passenger.getPhone());
            ps.setDouble(4, passenger.getRating());
            ps.setInt(5, passenger.getTotalRides());
            ps.setDouble(6, passenger.getTotalSpent());

            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        passenger.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return false;
    }

    @Override
    public Passenger getPassengerByUserId(long userId) {
        Connection conn = db.openConnection();
        if (conn == null)
            return null;

        String sql = "SELECT * FROM passengers WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPassenger(rs);
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
    public Passenger getPassengerById(long id) {
        Connection conn = db.openConnection();
        if (conn == null)
            return null;

        String sql = "SELECT * FROM passengers WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPassenger(rs);
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
    public boolean updatePassenger(Passenger passenger) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "UPDATE passengers SET name = ?, phone = ?, rating = ?, total_rides = ?, total_spent = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passenger.getName());
            ps.setString(2, passenger.getPhone());
            ps.setDouble(3, passenger.getRating());
            ps.setInt(4, passenger.getTotalRides());
            ps.setDouble(5, passenger.getTotalSpent());
            ps.setLong(6, passenger.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return false;
    }

    @Override
    public boolean deletePassenger(long id) {
        Connection conn = db.openConnection();
        if (conn == null)
            return false;

        String sql = "DELETE FROM passengers WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return false;
    }

    @Override
    public List<Passenger> getAllPassengers() {
        List<Passenger> passengers = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null)
            return passengers;

        String sql = "SELECT * FROM passengers";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                passengers.add(mapResultSetToPassenger(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return passengers;
    }

    private Passenger mapResultSetToPassenger(ResultSet rs) throws SQLException {
        Passenger passenger = new Passenger();
        passenger.setId(rs.getLong("id"));
        passenger.setUserId(rs.getLong("user_id"));
        passenger.setName(rs.getString("name"));
        passenger.setPhone(rs.getString("phone"));
        passenger.setRating(rs.getDouble("rating"));
        passenger.setTotalRides(rs.getInt("total_rides"));
        passenger.setTotalSpent(rs.getDouble("total_spent"));
        passenger.setCreatedAt(rs.getTimestamp("created_at"));
        return passenger;
    }
}
