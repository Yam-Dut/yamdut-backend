package org.yamdut.backend.service;

import org.yamdut.backend.dao.UserDAO;
import org.yamdut.backend.dao.UserDAOImpl;
import org.yamdut.backend.model.User;
import org.yamdut.backend.utils.PasswordHasher;
import org.yamdut.backend.model.Role;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    /*
     checks if the user with the given email exists
     */

    public boolean exists(String email) {
        return userDAO.getUserByUsername(email) != null;
    }

    public void createUnverifiedUser(String email, String passwordHash, Role role) {
        String defaultFullName = email.split("@")[0];
        String defaultPhone = "";

        User user = new User(
            null,
            email,
            null,
            email,
            passwordHash,
            role, 
            false
        );
        userDAO.save(user);
    }

    public void activateUser(String email) {
        userDAO.markVerified(email);
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
    /*
     helper method to register a user without full details(simpler version)
     */

     public boolean registerBasicUser(String email, String rawPassword, boolean isDriver) {
        if (exists(email)) {
            return false;
        }

        String passwordHash = PasswordHasher.hashPassword(rawPassword);
        String role = isDriver ? "DRIVER" : "PASSENGER";

        createUnverifiedUser(email, passwordHash, role);
        return true;
     }
}