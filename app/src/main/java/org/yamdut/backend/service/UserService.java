package org.yamdut.backend.service;

import org.yamdut.backend.dao.UserDAO;
import org.yamdut.backend.dao.UserDAOImpl;
import org.yamdut.backend.model.User;
import org.yamdut.backend.utils.PasswordHasher;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Registers a new user as either DRIVER or PASSENGER.
     * The same account can later log in from the single login screen
     * and the backend will decide behaviour based on this role.
     */
    public boolean registerUser(String fullName,
                                String email,
                                String rawPassword,
                                String phone,
                                boolean isDriver) {

        // Use email as the unique username/identifier for login
        User existing = userDAO.getUserByUsername(email);
        if (existing != null) {
            throw new IllegalStateException("An account with this email already exists");
        }

        String passwordHash = PasswordHasher.hashPassword(rawPassword);
        String role = isDriver ? "DRIVER" : "PASSENGER";

        User user = new User(fullName, email, phone, email, passwordHash, role, verified);
        return userDAO.createUser(user);
    }
}