package org.example.dao;

import org.example.model.User;
import java.util.List;

public interface UserDAO {
    boolean createUser(User user);
    User getUserByUsername(String username);
    User getUserById(int id);
    boolean updateUser(User user);
    boolean deleteUser(int id);
    List<User> getAllUsers();
    boolean validateCredentials(String username, String password);
}
