package org.yamdut.backend.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.yamdut.backend.database.DatabaseConfig;
import org.yamdut.backend.database.MySqlConfig;
import org.yamdut.backend.model.User;
import org.yamdut.backend.utils.PasswordHasher;

public class UserDAOImpl implements UserDAO {
    private DatabaseConfig db = new MySqlConfig();

    @Override
    public boolean createUser(User user) {
        Connection conn = db.openConnection();
        if (conn == null) return false;
        
        String sql = "INSERT INTO users (full_name, email, phone, username, password_hash, role) VALUES ('" 
            + user.getFullName() + "', '" + user.getEmail() + "', '" + user.getPhone() + "', '" 
            + user.getUsername() + "', '" + user.getPasswordHash() + "', '" + user.getRole() + "')";
        
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        Connection conn = db.openConnection();
        if (conn == null) return null;
        
        String sql = "SELECT * FROM users WHERE username = '" + username + "'";
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null && rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return null;
    }

    @Override
    public User getUserById(int id) {
        Connection conn = db.openConnection();
        if (conn == null) return null;
        
        String sql = "SELECT * FROM users WHERE id = " + id;
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null && rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        Connection conn = db.openConnection();
        if (conn == null) return false;
        
        String sql = "UPDATE users SET full_name = '" + user.getFullName() + "', email = '" + user.getEmail() 
            + "', phone = '" + user.getPhone() + "', password_hash = '" + user.getPasswordHash() 
            + "', role = '" + user.getRole() + "' WHERE id = " + user.getId();
        
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public boolean deleteUser(int id) {
        Connection conn = db.openConnection();
        if (conn == null) return false;
        
        String sql = "DELETE FROM users WHERE id = " + id;
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null) return users;
        
        String sql = "SELECT * FROM users";
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return users;
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null) {
            return PasswordHasher.verifyPassword(password, user.getPasswordHash());
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
