package org.yamdut.dao;

import java.util.HashMap;
import java.util.Map;

import org.yamdut.model.User;

public class UserDAOImpl implements UserDAO {

    // In-memory storage (temporary)
    private static final Map<String, User> USERS = new HashMap<>();

    @Override
    public void save(User user) {
        // store user using email as key
        USERS.put(user.getEmail(), user);
    }

    @Override
    public void markVerified(String email) {
        User user = USERS.get(email);
        if (user != null) {
            user.setVerified(true);
        }
    }

    @Override
    public User getUserByUsername(String email) {
        // return user or null if not found
        return USERS.get(email);
    }
}
