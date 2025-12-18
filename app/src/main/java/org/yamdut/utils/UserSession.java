package org.yamdut.utils;

import org.yamdut.backend.model.*;

/**
 * Simple singleton to keep track of the currently logged-in user
 * on the desktop client side.
 */
public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isDriver() {
        return currentUser != null && Role.DRIVER.equals(currentUser.getRole());
    }

    public boolean isPassenger() {
        return currentUser != null && Role.PASSENGER.equals(currentUser.getRole());
    }

    public boolean isAdmin() {
        return currentUser != null && Role.ADMIN.equals(currentUser.getRole());
    }

    public String getUserEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }

    public String getUserName() {
        return currentUser != null ? currentUser.getFullName() : null;
    }
}


