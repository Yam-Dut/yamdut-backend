package org.example.controller;

import org.example.view.LoginView;
import org.example.view.SignUpView;
import org.example.view.AdminDashboardView;
import org.example.dao.UserDAOImpl;
import org.example.dao.UserDAO;
import org.example.model.User;

public class LoginController {
    private LoginView view;
    private UserDAO userDAO;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDAOImpl();

        view.addLoginListener(e -> handleLogin());
        view.addSignUpNavigationListener(e -> navigateToSignUp());
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Please fill all fields");
            return;
        }

        if (userDAO.validateCredentials(username, password)) {
            User user = userDAO.getUserByUsername(username);

            if ("ADMIN".equals(user.getRole())) {
                view.dispose();
                AdminDashboardView adminView = new AdminDashboardView();
                new AdminDashboardController(adminView);
                adminView.setVisible(true);
            } else {
                view.showError("User dashboard not implemented yet");
            }
        } else {
            view.showError("Invalid username or password");
        }
    }

    private void navigateToSignUp() {
        view.dispose();
        SignUpView signUpView = new SignUpView();
        new SignUpController(signUpView);
        signUpView.setVisible(true);
    }
}
