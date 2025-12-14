package org.example.controller;

import org.example.view.SignUpView;
import org.example.view.LoginView;
import org.example.dao.UserDAOImpl;
import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.util.PasswordHasher;
import java.util.regex.Pattern;

public class SignUpController {
    private SignUpView view;
    private UserDAO userDAO;

    public SignUpController(SignUpView view) {
        this.view = view;
        this.userDAO = new UserDAOImpl();

        view.addSignUpListener(e -> handleSignUp());
        view.addBackToLoginListener(e -> navigateToLogin());
    }

    private void handleSignUp() {
        String fullName = view.getFullName();
        String email = view.getEmail();
        String phone = view.getPhone();
        String username = view.getUsername();
        String password = view.getPassword();
        String confirmPassword = view.getConfirmPassword();
        String role = view.getSelectedRole();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            view.showError("Please fill all fields");
            return;
        }

        if (!isValidEmail(email)) {
            view.showError("Please enter a valid email address");
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.showError("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            view.showError("Password must be at least 6 characters long");
            return;
        }

        User existingUser = userDAO.getUserByUsername(username);
        if (existingUser != null) {
            view.showError("Username already exists");
            return;
        }

        String passwordHash = PasswordHasher.hashPassword(password);
        User newUser = new User(fullName, email, phone, username, passwordHash, role);

        if (userDAO.createUser(newUser)) {
            view.showSuccess("Account created successfully! Please login.");
            view.clearFields();
            navigateToLogin();
        } else {
            view.showError("Failed to create account. Please try again.");
        }
    }

    private void navigateToLogin() {
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
