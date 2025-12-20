package org.yamdut.dao;

import java.sql.*;

import org.yamdut.database.MySqlConfig;
import org.yamdut.model.User;
import org.yamdut.model.Role;

public class UserDAOImpl implements UserDAO {

    @Override
    public void save(User user) {
        String sql = """
            INSERT INTO users
            (full_name, email, username, role, password_hash, verified)
            VALUES (?, ?, ?, ?, ?, ?)  
        """;

        try (Connection conn = MySqlConfig.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
        {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getPasswordHash());
            ps.setBoolean(6, user.getVerified());
            
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public void markVerified(String email) {
        String sql = "UPDATE users SET verified = TRUE WHERE email = ?";

        try (Connection conn = MySqlConfig.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify user", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND VERIFIED = TRUE";

        try (Connection conn = MySqlConfig.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();        
        } catch (Exception e) {
            throw new RuntimeException("Failed to check email existence", e);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = MySqlConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    return null;
                }
                return mapUser(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch by username", e);
        }
    }
    public User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setVerified(rs.getBoolean("verified"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
} 
