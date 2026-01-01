package org.yamdut.dao;

import org.yamdut.model.User;

public interface UserDAO {

    void save(User user);

    void markVerified(String email);

    User getUserByEmail(String email);

    boolean existsByEmail(String email);

    // Admin dashboard methods
    java.util.List<User> getAllUsers();

    boolean createUser(User user);

    User getUserById(int userId);

    boolean updateUser(User user);

    boolean deleteUser(int userId);

    void updatePassword(String email, String passwordHash);
}
