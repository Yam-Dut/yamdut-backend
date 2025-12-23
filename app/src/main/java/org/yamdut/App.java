package org.yamdut;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.yamdut.controller.AdminDashboardController;
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
import org.yamdut.view.signup.OtpScreen;
import org.yamdut.view.signup.SignUpScreen;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Yamdut - Ride Sharing");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null); // Center on screen

            ScreenManager screenManager = new ScreenManager(frame);

            // Create screens without controllers first
            LoginScreen loginScreen = new LoginScreen(null);
            SignUpScreen signupScreen = new SignUpScreen(null);

            // Create controllers, wiring in ScreenManager
            LoginController loginController = new LoginController(loginScreen, screenManager);
            SignupController signupController = new SignupController(signupScreen, screenManager);

            // Let screens know about their controllers
            loginScreen = new LoginScreen(loginController);
            signupScreen = new SignUpScreen(signupController);

            screenManager.register("LOGIN", loginScreen);
            screenManager.register("SIGNUP", signupScreen);

            // OTP Screen placeholder
            screenManager.register("OTP", new JPanel());

            // Dashboards for different roles with controllers
            PassengerDashboard passengerDashboard = new PassengerDashboard();
            PassengerDashboardController passengerController = new PassengerDashboardController(passengerDashboard);
            screenManager.register("PASSENGER_DASHBOARD", passengerDashboard);

            DriverDashboard driverDashboard = new DriverDashboard();
            DriverDashboardController driverController = new DriverDashboardController(driverDashboard);
            screenManager.register("DRIVER_DASHBOARD", driverDashboard);

            AdminDashboard adminDashboard = new AdminDashboard();
            AdminDashboardController adminController = new AdminDashboardController(adminDashboard);
            screenManager.register("ADMIN_DASHBOARD", adminDashboard);

            // Wire logout buttons to clear session and go back to login
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


            // Check if user is already logged in (persistent session)
            if (UserSession.getInstance().isLoggedIn()) {
                Role userRole = UserSession.getInstance().getCurrentUser().getRole();
                screenManager.showDashBoardForRole(userRole);
            } else {
                screenManager.show("LOGIN");
            }

            frame.setVisible(true);
        });
    }
}