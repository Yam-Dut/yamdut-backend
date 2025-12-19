package org.yamdut.service;

import org.yamdut.dao.UserDAO;
import org.yamdut.dao.UserDAOImpl;
import org.yamdut.helpers.PasswordHasher;
import org.yamdut.model.Role;
import org.yamdut.model.User;

public class UserService {

    private final UserDAO userDAO;
    //private final User user;

    public UserService() {
        this.userDAO = new UserDAOImpl();
      //  this.user = new User();
    }

    /*
     checks if the user with the given email exists
     */

    public boolean exists(String email) {
        return userDAO.getUserByUsername(email) != null;
    }

    public void createUnverifiedUser(String fullname, String email, String phone, String passwordHash, Role role) {
        User user = new User(
            fullname,
            email,
            phone,
            email, //userrname
            passwordHash,
            role, 
            false
        );
        userDAO.save(user);
    }

    public void activateUser(String email) {
        User user = userDAO.getUserByUsername(email);
        if (user != null) {
            user.setVerified(true);
            userDAO.update(user);
        }
    }
    /**
     * Registers a new user as either DRIVER or PASSENGER.
     * The same account can later log in from the single login screen
     * and the backend will decide behaviour based on this role.
     */
    public User registerUser(String fullName,
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
        Role role = isDriver ? Role.DRIVER : Role.PASSENGER;

        boolean verified = false;
        User user = new User(fullName, email, phone, email, passwordHash, role, verified);
        userDAO.save(user);
        return user;
    }
    /*
     helper method to register a user without full details(simpler version)
     */

    public User registerBasicUser(String fullName, String email, String rawPassword, String phone, boolean isDriver) {
        if (exists(email)) {
            return null;
        }

        String passwordHash = PasswordHasher.hashPassword(rawPassword);
        Role role = isDriver ? Role.DRIVER : Role.PASSENGER;

        String username = email.split("@")[0];

        User user = new User(
            fullName,
            email,
            phone,
            username,
            passwordHash,
            role,
            false
        );

        userDAO.save(user);
        return user;
    }

    public User authenticate(String email, String rawPassword) {
        User user = userDAO.getUserByUsername(email);
        if (user == null) return null;
        if (!PasswordHasher.verifyPassword(rawPassword, user.getPasswordHash())) {
            return null;
        }
        if (!user.getVerified()) {
            throw new IllegalStateException("Please verify your account first");
        }
        return user;
    }

    public User findByEmail(String email) {
        return userDAO.getUserByUsername(email);
    }
}