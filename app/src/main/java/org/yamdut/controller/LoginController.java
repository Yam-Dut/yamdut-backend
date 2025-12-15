package org.yamdut.controller;

import javax.swing.SwingWorker;
import javax.swing.JOptionPane;

import org.yamdut.core.ScreenManager;
import org.yamdut.ui.login.LoginScreen;
import org.yamdut.backend.model.User;
import org.yamdut.backend.service.AuthService;
import org.yamdut.utils.UserSession;

public class LoginController {
    private final AuthService authService;
    private final LoginScreen view;
    private final ScreenManager screenManager;

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
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            private String error;

            @Override
            protected User doInBackground() {
                try {
                    // We use email-as-username; backend returns user with role
                    return authService.login(email, password);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                view.setLoading(false);
                try {
                    User user = get();
                    if (user == null) {
                        view.showError(error != null ? error : "Invalid email or password");
                        return;
                    }

                    // Persist logged in user in client-side session
                    UserSession.getInstance().login(user);

                    // Backend has decided the role for us
                    String role = user.getRole();

                    // This is where we branch behaviour based on the role.
                    // For now we just show a message; you can later wire this
                    // to different dashboards using ScreenManager.
                    // role already computed above
                    switch (role != null ? role.toUpperCase() : "") {
                        case "DRIVER":
                            screenManager.show("DRIVER_DASHBOARD");
                            break;

                        case "PASSENGER":
                        case "USER": // if you ever store USER instead of PASSENGER
                            screenManager.show("USER_DASHBOARD");
                            break;

                        case "ADMIN":
                            screenManager.show("ADMIN_DASHBOARD");
                            break;

                        default:
                            view.showError("Unknown role for this account");
                            break;
                    }
                } catch (Exception e) {
                    view.showError("Unexpected error during login");
                }
            }
        };

        view.setLoading(true);
        worker.execute();
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