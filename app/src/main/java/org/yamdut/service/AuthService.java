package org.yamdut.service;

import org.yamdut.dao.UserDAO;
import org.yamdut.dao.UserDAOImpl;
import org.yamdut.model.User;
import org.yamdut.utils.PasswordHasher;

/**
 * Authentication service responsible for validating credentials and
 * returning the authenticated user with their role (DRIVER/PASSENGER/ADMIN).
 *
 * The UI layer (single login screen) does not need to know about roles
 * up front – it just passes identifier + password and
 * this service decides who the user is and what their role is.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Attempts to authenticate a user.
     *
     * @param identifier username/email used at signup (we use email as username)
     * @param rawPassword plain text password from the login form
     * @return the authenticated {@link User} (including role) or {@code null} if auth fails
     */
    public User login(String identifier, String rawPassword) {
        User user = userDAO.getUserByUsername(identifier);
        if (user == null) {
            return null;
        }

        boolean ok = PasswordHasher.verifyPassword(rawPassword, user.getPasswordHash());
        return ok ? user : null;
    }
}