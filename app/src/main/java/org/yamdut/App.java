package org.yamdut;

import javax.swing.*;

import org.yamdut.core.ScreenManager;
import org.yamdut.utils.UserSession;
import org.yamdut.view.dashboards.AdminDashboard;
import org.yamdut.view.dashboards.DriverDashboardView;
import org.yamdut.view.auth.LoginScreen;
import org.yamdut.view.components.MapPanel;
import org.yamdut.view.dashboards.PassengerDashboard;
import org.yamdut.view.auth.SignUpScreen;
import org.yamdut.controller.LoginController;
import org.yamdut.controller.SignupController;
import org.yamdut.controller.DriverDashboardController;
import org.yamdut.model.User;



public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Yamdut");
            frame.setSize(1080, 1080);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

            // Dashboards for different roles
            PassengerDashboard passengerDashboard = new PassengerDashboard();
            DriverDashboardView driverDashboardView = new DriverDashboardView("Demo Driver");
            // Minimal driver user (id required by controller)
            User driverUser = new User("Ram Bahadur Tamang", "driver@yamdut.com", "9800000000", "driver", "password", "DRIVER");
            driverUser.setId(1);
            new DriverDashboardController(driverDashboardView, driverUser);
            AdminDashboard adminDashboard = new AdminDashboard();

            screenManager.register("USER_DASHBOARD", passengerDashboard);
            screenManager.register("DRIVER_DASHBOARD", driverDashboardView);

            screenManager.register("ADMIN_DASHBOARD", adminDashboard);

            // Wire logout buttons to clear session and go back to login
            passengerDashboard.getLogoutButton().addActionListener(e -> {
                UserSession.getInstance().logout();
                screenManager.show("LOGIN");
            });
            driverDashboardView.addLogoutListener(e -> {
                UserSession.getInstance().logout();
                screenManager.show("LOGIN");
            });

            adminDashboard.getLogoutButton().addActionListener(e -> {
                UserSession.getInstance().logout();
                screenManager.show("LOGIN");
            });

            // Passenger map button opens a simple map dialog (Swing)
            passengerDashboard.getShowMapButton().addActionListener(e -> {
                JDialog mapDialog = new JDialog(frame, "Yamdut - Map", true);
                mapDialog.setSize(900, 600);
                mapDialog.setLocationRelativeTo(frame);
                mapDialog.add(new MapPanel());
                mapDialog.setVisible(true);
            });

            screenManager.show("LOGIN");
            frame.setVisible(true);
        });
    }
}