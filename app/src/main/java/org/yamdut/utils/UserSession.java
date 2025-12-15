package org.yamdut.utils;

import org.yamdut.backend.model.User;

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
        return currentUser != null && "DRIVER".equalsIgnoreCase(currentUser.getRole());
    }

    public boolean isPassenger() {
        if (currentUser == null || currentUser.getRole() == null) return false;
        String role = currentUser.getRole().toUpperCase();
        return "PASSENGER".equals(role) || "USER".equals(role);
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    public String getUserEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }

    public String getUserName() {
        return currentUser != null ? currentUser.getFullName() : null;
    }
}


