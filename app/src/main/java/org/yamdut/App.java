package org.yamdut;

import javax.swing.*;

import org.yamdut.core.ScreenManager;
import org.yamdut.utils.UserSession;
import org.yamdut.view.dashboard.AdminDashboard;
import org.yamdut.view.dashboard.DriverDashboard;
import org.yamdut.view.dashboard.PassengerDashboard;
import org.yamdut.view.login.LoginScreen;
import org.yamdut.view.map.MapPanel;
import org.yamdut.view.signup.SignUpScreen;
import org.yamdut.controller.LoginController;
import org.yamdut.controller.SignupController;



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
            DriverDashboard driverDashboard = new DriverDashboard();
            AdminDashboard adminDashboard = new AdminDashboard();

            screenManager.register("PASSENGER_DASHBOARD", passengerDashboard);
            screenManager.register("DRIVER_DASHBOARD", driverDashboard);
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
            //preeti-patch
        });
    }
}