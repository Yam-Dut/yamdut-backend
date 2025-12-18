package org.yamdut.dao;

import java.util.List;

import org.yamdut.model.User;

public interface UserDAO {
    boolean createUser(User user);
    User getUserByUsername(String username);
    User getUserById(int id);
    boolean updateUser(User user);
    boolean deleteUser(int id);
    List<User> getAllUsers();
    boolean validateCredentials(String username, String password);
}
