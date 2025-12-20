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
        currentWorker = new SwingWorker<User,Void>() {
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
                        // SwingUtilities.invokeLater(() -> screenManager.showDashBoardForRole(user.getRole()));
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
                    //ignore cancelled login
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
        // Minimal placeholder – backend-only decision making can be added later
        JOptionPane.showMessageDialog(
                null,
                "Password reset flow is not implemented yet.\nRequested for: " + email,
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}