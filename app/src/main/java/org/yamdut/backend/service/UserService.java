package org.yamdut.backend.service;

import org.yamdut.backend.dao.UserDAO;
import org.yamdut.backend.dao.UserDAOImpl;
import org.yamdut.backend.model.User;
import org.yamdut.backend.utilities.PasswordHasher;
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

    public void createUnverifiedUser(String fullname, String email, String phone, String passwordHash, Role role) {
        String defaultFullName = email.split("@")[0];
        String defaultPhone = "";

        User user = new User(
            fullname,
            email,
            phone,
            email, //userrname
            passwordHash,
            role, 
            false
        );
        userDAO.save(user);i
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

    public boolean registerBasicUser(String fullName, String email, String rawPassword, String phone, boolean isDriver) {
        if (exists(email)) {
            return false;
        }

        String passwordHash = PasswordHasher.hashPassword(rawPassword);
        Role role = isDriver ? Role.DRIVER : Role.PASSENGER;

        createUnverifiedUser(fullName, email, phone, passwordHash, role);
        return true;
    }

    public User getUserByEmail(String email) {
        return UserDAO.getUserByUsername(email);
    }
}