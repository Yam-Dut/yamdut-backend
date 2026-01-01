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
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new IllegalStateException("Account with this email already exists.", dup);
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
        // Check regardless of verification status to avoid duplicates
        String sql = "SELECT 1 FROM users WHERE email = ?";

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

        // Handle verified column if it exists, otherwise default to false
        try {
            user.setVerified(rs.getBoolean("verified"));
        } catch (SQLException e) {
            // Column doesn't exist, default to false
            user.setVerified(false);
        }

        // Handle created_at column if it exists
        try {
            user.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (SQLException e) {
            // Column doesn't exist, leave as null
            user.setCreatedAt(null);
        }

        return user;
    }

    @Override
    public java.util.List<User> getAllUsers() {
        java.util.List<User> users = new java.util.ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all users", e);
        }
        return users;
    }

    @Override
    public boolean createUser(User user) {
        try {
            save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by ID", e);
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, username = ?, role = ? WHERE id = ?";

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getRole().name());
            ps.setLong(6, user.getId());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public void updatePassword(String email, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";

        try (Connection conn = MySqlConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update password", e);
        }
    }
}
