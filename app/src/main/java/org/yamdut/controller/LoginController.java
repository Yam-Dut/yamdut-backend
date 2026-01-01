package org.yamdut.controller;

import javax.swing.SwingWorker;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.yamdut.core.ScreenManager;
import org.yamdut.model.User;
import org.yamdut.service.AuthService;
import org.yamdut.utils.UserSession;
import org.yamdut.view.login.LoginScreen;

public class LoginController {
    private final AuthService authService;
    private final LoginScreen view;
    private final ScreenManager screenManager;

    private SwingWorker<User, Void> currentWorker;

    public LoginController(LoginScreen view, ScreenManager screenManager) {
        this.view = view;
        this.screenManager = screenManager;
        this.authService = new AuthService();
    }

    /**
     * Handles login from the single LoginScreen.
     * No role is selected in the UI – we simply pass credentials to the backend
     * and decide what this user is (DRIVER/PASSENGER/ADMIN) from the database.
     */
    public void login(String email, String password, boolean rememberMe) {
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
        }
        currentWorker = new SwingWorker<User, Void>() {
            private String error;

            @Override
            protected User doInBackground() {
                try {
                    User user = authService.login(email, password);

                    if (user == null) {
                        throw new IllegalStateException("User does not exists. Please sign up and verify.");
                    }
                    return user;
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    view.setLoading(false);
                    if (isCancelled() || !UserSession.getInstance().isLoggedOut()) {
                        return;
                    }
                });

                try {
                    User user = get();

                    if (user != null) {
                        UserSession.getInstance().login(user);
                        // SwingUtilities.invokeLater(() ->
                        // screenManager.showDashBoardForRole(user.getRole()));
                        screenManager.showDashBoardForRole(user.getRole());
                    } else if (error != null) {
                        view.showError(error);
                    } else {
                        view.showError("Invalid email or password.");
                    }
                    // This is where we branch behaviour based on the role.
                    // For now we just show a message; you can later wire this
                    // to different dashboards using ScreenManager.
                    // role already computed above
                } catch (CancellationException | InterruptedException ignored) {
                    // ignore cancelled login
                } catch (ExecutionException e) {
                    view.showError("Unexpected error during login: " + e.getCause().getMessage());
                }
            }
        };

        view.setLoading(true);
        currentWorker.execute();
    }

    public void logout() {
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
        }
        UserSession.getInstance().logout();
        SwingUtilities.invokeLater(() -> {
            view.reset();
            screenManager.show("LOGIN");
        });
    }

    public void navigateToSignup() {
        screenManager.show("SIGNUP");
    }

    public void requestPasswordReset(String email) {
        // 1. Request OTP
        try {
            authService.requestPasswordReset(email);
        } catch (Exception e) {
            view.showError("Failed to request password reset: " + e.getMessage());
            return;
        }

        // 2. Ask for OTP
        String otp = JOptionPane.showInputDialog(
                view,
                "An OTP has been sent to " + email + ".\nPlease enter it below:",
                "Enter OTP",
                JOptionPane.QUESTION_MESSAGE);

        if (otp == null || otp.trim().isEmpty())
            return; // User cancelled

        // 3. Ask for New Password
        javax.swing.JPasswordField pf = new javax.swing.JPasswordField();
        int okCfm = JOptionPane.showConfirmDialog(
                view,
                pf,
                "Enter New Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (okCfm != JOptionPane.OK_OPTION)
            return;

        String newPassword = new String(pf.getPassword());
        if (newPassword.isEmpty()) {
            view.showError("Password cannot be empty.");
            return;
        }

        // 4. Reset
        try {
            authService.resetPassword(email, otp.trim(), newPassword);
            view.showSuccess("Password reset successfully! You can now login.");
        } catch (Exception e) {
            view.showError("Failed to reset password: " + e.getMessage());
        }
    }
}