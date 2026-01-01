package org.yamdut;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// import org.yamdut.controller.AdminDashboardController;
import org.yamdut.controller.DriverDashboardController;
import org.yamdut.controller.LoginController;
import org.yamdut.controller.PassengerDashboardController;
import org.yamdut.controller.SignupController;
import org.yamdut.core.ScreenManager;
import org.yamdut.model.Role;
import org.yamdut.utils.UserSession;
import org.yamdut.view.dashboard.AdminDashboard;
import org.yamdut.view.dashboard.DriverDashboard;
import org.yamdut.view.dashboard.PassengerDashboard;
import org.yamdut.view.login.LoginScreen;
import org.yamdut.view.signup.SignUpScreen;

public class App {

    public static void main(String[] args) {
        // Fix for HiDPI scaling on Linux/Arch
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("sun.java2d.dpiaware", "true");

        // JavaFX Platform will be initialized automatically when first JFXPanel is
        // created
        // No need to call Platform.startup()

        // Cleanup stale requests logic now handled in RideMatchingService.submitRide

        SwingUtilities.invokeLater(() -> {

            // ───────────────────────── Frame ─────────────────────────
            JFrame frame = new JFrame("Yamdut - Ride Sharing");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            ScreenManager screenManager = new ScreenManager(frame);

            // ───────────────────────── AUTH: LOGIN ─────────────────────────
            LoginScreen loginScreenTemp = new LoginScreen(null);
            LoginController loginController = new LoginController(loginScreenTemp, screenManager);

            LoginScreen loginScreen = new LoginScreen(loginController);

            // ───────────────────────── AUTH: SIGNUP ─────────────────────────
            SignUpScreen signUpScreenTemp = new SignUpScreen(null);
            SignupController signupController = new SignupController(signUpScreenTemp, screenManager);

            SignUpScreen signUpScreen = new SignUpScreen(signupController);

            // ───────────────────────── REGISTER SCREENS ─────────────────────────
            screenManager.register("LOGIN", loginScreen);
            screenManager.register("SIGNUP", signUpScreen);
            screenManager.register("OTP", new JPanel()); // placeholder

            // ───────────────────────── DASHBOARDS ─────────────────────────
            PassengerDashboard passengerDashboard = new PassengerDashboard();
            DriverDashboard driverDashboard = new DriverDashboard();
            AdminDashboard adminDashboard = new AdminDashboard();

            new PassengerDashboardController(passengerDashboard);
            new DriverDashboardController(driverDashboard);
            // new AdminDashboardController(adminDashboard);

            screenManager.register("PASSENGER_DASHBOARD", passengerDashboard);
            screenManager.register("DRIVER_DASHBOARD", driverDashboard);
            screenManager.register("ADMIN_DASHBOARD", adminDashboard);

            // ───────────────────────── LOGOUT HANDLING ─────────────────────────
            passengerDashboard.getLogoutButton().addActionListener(e -> {
                UserSession.getInstance().logout();
                screenManager.show("LOGIN");
            });

            driverDashboard.getLogoutButton().addActionListener(e -> {
                UserSession.getInstance().logout();
                screenManager.show("LOGIN");
            });

            adminDashboard.getLogoutButton().addActionListener(e -> {
                UserSession.getInstance().logout();
                screenManager.show("LOGIN");
            });

            // ───────────────────────── INITIAL ROUTING ─────────────────────────
            if (UserSession.getInstance().isLoggedIn()) {
                Role role = UserSession.getInstance()
                        .getCurrentUser()
                        .getRole();
                screenManager.showDashBoardForRole(role);
            } else {
                screenManager.show("LOGIN");
            }

            frame.setVisible(true);
        });
    }
}
